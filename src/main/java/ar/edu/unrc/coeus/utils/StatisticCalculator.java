package ar.edu.unrc.coeus.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author lucia bressan, franco pellegrini, renzo bianchini
 */
public
class StatisticCalculator {
    private final int             capacity;
    private final DecimalFormat   formatter;
    private final Queue< Double > history;
    private       double          average;
    private       int             itemCounter;

    public
    StatisticCalculator(
            final int capacity,
            final int outputDecimals
    ) {

        average = 0.0d;
        itemCounter = 0;
        if ( capacity > 0 ) {
            this.capacity = capacity;
            history = new ArrayDeque<>(capacity);
        } else {
            this.capacity = 0;
            history = null;
        }
        if ( outputDecimals > 0 ) {
            final StringBuilder pattern = new StringBuilder(outputDecimals);
            for ( int i = 0; i < outputDecimals; i++ ) {
                pattern.append('#');
            }
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
            otherSymbols.setDecimalSeparator(',');
            otherSymbols.setGroupingSeparator('.');
            formatter = new DecimalFormat("#." + pattern, otherSymbols);
        } else if ( outputDecimals == 0 ) {
            formatter = new DecimalFormat("#");
        } else {
            formatter = null;
        }
    }

    public
    StatisticCalculator() {
        this(0, -1);
    }

    public synchronized
    void addSample( final double sample ) {
        if ( history != null ) {
            if ( history.size() >= capacity ) {
                average -= history.remove();
                itemCounter--;
            }
            history.add(sample);
            average += sample;
            itemCounter++;
            assert itemCounter <= capacity;
        } else {
            average += sample;
            itemCounter++;
        }
    }

    public synchronized
    double getAverage() {
        return average / ( itemCounter * 1.0d );
    }

    public synchronized
    Double getFullCapacityAverage() {
        return ( itemCounter == capacity ) ? getAverage() : null;
    }

    public synchronized
    int getItemCounter() {
        return itemCounter;
    }

    public synchronized
    String printableAverage() {
        if ( itemCounter < 1 ) {
            return "?";
        } else {
            return ( formatter != null ) ? formatter.format(getAverage()) : Double.toString(getAverage());
        }
    }

    public synchronized
    String printableFullCapacityAverage() {
        if ( itemCounter < capacity ) {
            return "?";
        } else {
            return ( formatter != null ) ? formatter.format(getAverage()) : Double.toString(getAverage());
        }
    }

    public synchronized
    void reset() {
        average = 0.0d;
        itemCounter = 0;
        if ( history != null ) {
            history.clear();
        }
    }
}
