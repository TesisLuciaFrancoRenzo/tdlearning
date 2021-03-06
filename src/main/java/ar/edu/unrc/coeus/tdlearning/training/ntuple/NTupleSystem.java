/*
 * Copyright (C) 2016  Lucia Bressan <lucyluz333@gmial.com>,
 *                     Franco Pellegrini <francogpellegrini@gmail.com>,
 *                     Renzo Bianchini <renzobianchini85@gmail.com
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ar.edu.unrc.coeus.tdlearning.training.ntuple;

import ar.edu.unrc.coeus.tdlearning.interfaces.IStateNTuple;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Red neuronal para usos de NTuplas.
 *
 * @author lucia bressan, franco pellegrini, renzo bianchini
 */
public
class NTupleSystem {

    private final Function< Double, Double >       activationFunction;
    private final boolean                          concurrency;
    private final Function< Double, Double >       derivedActivationFunction;
    private final Map< SamplePointValue, Integer > mapSamplePointValuesIndex;
    private final int[]                            nTuplesLength;
    private final int[]                            nTuplesWeightQuantity;
    private final int[]                            nTuplesWeightQuantityIndex;
    private       double[]                         lut;

    /**
     * Red neuronal optimizada para usos de NTuplas.
     *
     * @param allSamplePointPossibleValues todos los posibles valores dentro de un punto muestral de las NTupla.
     * @param nTuplesLength                longitudes de las NTuplas.
     * @param activationFunction           función de activación.
     * @param derivedActivationFunction    derivada de la función de activación.
     * @param concurrency                  true si se permite concurrencia en los cálculos.
     */
    public
    NTupleSystem(
            final List< SamplePointValue > allSamplePointPossibleValues,
            final int[] nTuplesLength,
            final Function< Double, Double > activationFunction,
            final Function< Double, Double > derivedActivationFunction,
            final boolean concurrency
    ) {
        mapSamplePointValuesIndex = new HashMap<>(allSamplePointPossibleValues.size());
        for ( int spvIndex = 0; spvIndex < allSamplePointPossibleValues.size(); spvIndex++ ) {
            mapSamplePointValuesIndex.put(allSamplePointPossibleValues.get(spvIndex), spvIndex);
        }
        nTuplesWeightQuantity = new int[nTuplesLength.length];
        nTuplesWeightQuantityIndex = new int[nTuplesLength.length];
        nTuplesWeightQuantityIndex[0] = 0;
        final int nTuplesLengthLength = nTuplesLength.length;
        int       lutSize             = 0;
        for ( int nTupleIndex = 0; nTupleIndex < nTuplesLengthLength; nTupleIndex++ ) {
            nTuplesWeightQuantity[nTupleIndex] = (int) Math.pow(mapSamplePointValuesIndex.size(), nTuplesLength[nTupleIndex]);
            lutSize += nTuplesWeightQuantity[nTupleIndex];
            if ( nTupleIndex > 0 ) {
                nTuplesWeightQuantityIndex[nTupleIndex] = nTuplesWeightQuantityIndex[nTupleIndex - 1] + nTuplesWeightQuantity[nTupleIndex - 1];
            }
        }
        lut = new double[lutSize];
        this.nTuplesLength = nTuplesLength;
        this.activationFunction = activationFunction;
        this.derivedActivationFunction = derivedActivationFunction;
        this.concurrency = concurrency;
    }

    /**
     * Dado la NTupla número {@code nTupleIndex} extraída de {@code state}, esta función calcula a que peso corresponde dentro de la red neuronal.
     *
     * @param nTupleIndex               NTupla observada en {@code state}.
     * @param nTuplesLength             longitudes de las NTuplas.
     * @param state                     estado del cual se extrae la NTupla.
     * @param mapSamplePointValuesIndex mapa de los posibles valores dentro de una muestra de NTupla, asociado a su índice.
     *
     * @return peso correspondiente en la red neuronal de la NTupla numero {@code nTupleIndex} dentro de {@code state}.
     */
    public static
    int calculateLocalIndex(
            final int nTupleIndex,
            final int[] nTuplesLength,
            final IStateNTuple state,
            final Map< SamplePointValue, Integer > mapSamplePointValuesIndex
    ) {
        final SamplePointValue[] nTuple = state.getNTuple(nTupleIndex);
        int                      index  = 0;
        for ( int nIndex = 0; nIndex < nTuplesLength[nTupleIndex]; nIndex++ ) {
            index += mapSamplePointValuesIndex.get(nTuple[nIndex]) * (int) Math.pow(mapSamplePointValuesIndex.size(), nIndex);
        }
        return index;
    }

    /**
     * Dado la NTupla número {@code nTupleIndex} extraída de {@code state}, esta función calcula a que peso corresponde dentro de la red neuronal.
     *
     * @param nTupleIndex               NTupla observada en {@code state}.
     * @param nTuplesLength             longitudes de las NTuplas.
     * @param nTuple                    nTupla del estado.
     * @param mapSamplePointValuesIndex mapa de los posibles valores dentro de una muestra de NTupla, asociado a su índice.
     *
     * @return peso correspondiente en la red neuronal de la NTupla numero {@code nTupleIndex} dentro de {@code state}.
     */
    public static
    int calculateLocalIndex(
            final int nTupleIndex,
            final int[] nTuplesLength,
            final SamplePointValue[] nTuple,
            final Map< SamplePointValue, Integer > mapSamplePointValuesIndex
    ) {
        int index = 0;
        for ( int nIndex = 0; nIndex < nTuplesLength[nTupleIndex]; nIndex++ ) {
            index += mapSamplePointValuesIndex.get(nTuple[nIndex]) * (int) Math.pow(mapSamplePointValuesIndex.size(), nIndex);
        }
        return index;
    }

    /**
     * Función experimental para mezclar redes neuronales
     *
     * @param nTupleSystems lista de redes neuronales a fusionar
     */
    public static
    void fuseLut( final List< NTupleSystem > nTupleSystems ) {
        if ( nTupleSystems.isEmpty() ) {
            throw new IllegalArgumentException("la lista no puede ser vacía");
        }
        final double[] currentLut = nTupleSystems.get(0).lut;
        IntStream.range(0, currentLut.length).parallel().forEach(index -> {
            for ( int j = 1; j < nTupleSystems.size(); j++ ) {
                currentLut[index] += nTupleSystems.get(j).lut[index];
            }
            currentLut[index] = currentLut[index] / nTupleSystems.size();
        });
        for ( int i = 1; i < nTupleSystems.size(); i++ ) {
            nTupleSystems.get(i).lut = currentLut.clone();
        }
    }

    /**
     * Añade {@code correction} al peso con el índice {@code currentWeightIndex} dentro de la red neuronal.
     *
     * @param currentWeightIndex índice del peso en la red neuronal.
     * @param correction         valor a añadir.
     */
    public
    void addCorrectionToWeight(
            final int currentWeightIndex,
            final double correction
    ) {
        lut[currentWeightIndex] += correction;
    }

    /**
     * @return función de activación.
     */
    public
    Function< Double, Double > getActivationFunction() {
        return activationFunction;
    }

    /**
     * Se utiliza la red neuronal para evaluar un {@code state} y obtener una predicción.
     *
     * @param state estado a evaluar.
     *
     * @return predicción de a red neuronal y los cálculos temporales utilizados en el proceso.
     */
    public
    ComplexNTupleComputation getComplexComputation( final IStateNTuple state ) {
        IntStream stream = IntStream.range(0, nTuplesLength.length);
        stream = concurrency ? stream.parallel() : stream.sequential();
        final int[] indexes = new int[nTuplesLength.length];
        final double sum = stream.mapToDouble(nTupleIndex -> {
            indexes[nTupleIndex] =
                    nTuplesWeightQuantityIndex[nTupleIndex] + calculateLocalIndex(nTupleIndex, nTuplesLength, state, mapSamplePointValuesIndex);
            return lut[indexes[nTupleIndex]];
        }).sum();
        final ComplexNTupleComputation output = new ComplexNTupleComputation();
        output.setIndexes(indexes);
        output.setOutput(activationFunction.apply(sum));
        output.setDerivedOutput(derivedActivationFunction.apply(output.getOutput()));
        return output;
    }

    /**
     * Se utiliza la red neuronal para evaluar un {@code state} y obtener una predicción.
     *
     * @param state estado a evaluar.
     *
     * @return predicción de a red neuronal.
     */
    public
    Double getComputation( final IStateNTuple state ) {
        IntStream stream = IntStream.range(0, nTuplesLength.length);
        stream = concurrency ? stream.parallel() : stream.sequential();
        return activationFunction.apply(stream.mapToDouble(nTupleIndex -> lut[nTuplesWeightQuantityIndex[nTupleIndex] + calculateLocalIndex(
                nTupleIndex,
                nTuplesLength,
                state,
                mapSamplePointValuesIndex)]).sum());
    }

    /**
     * @return derivada de la función de activación.
     */
    public
    Function< Double, Double > getDerivedActivationFunction() {
        return derivedActivationFunction;
    }

    /**
     * @return tabla lut.
     */
    public
    double[] getLut() {
        return lut;
    }

    /**
     * @return mapa de los posibles valores dentro de una muestra de NTupla, asociado a su índice.
     */
    public
    Map< SamplePointValue, Integer > getMapSamplePointValuesIndex() {
        return mapSamplePointValuesIndex;
    }

    /**
     * @return las longitudes de las NTuplas.
     */
    public
    int[] getNTuplesLength() {
        return nTuplesLength;
    }

    /**
     * @return cantidad de pesos en cada NTupla posible.
     */
    public
    int[] getNTuplesWeightQuantity() {
        return nTuplesWeightQuantity;
    }

    /**
     * @return the nTuplesWeightQuantityIndex
     */
    public
    int[] getNTuplesWeightQuantityIndex() {
        return nTuplesWeightQuantityIndex;
    }

    /**
     * Carga una red neuronal desde un archivo.
     *
     * @param weightsFile archivo con los pesos de la red neuronal.
     *
     * @throws IOException            no se puede leer el archivo.
     * @throws ClassNotFoundException no se encuentran las clases adecuadas para cargar la red.
     */
    public
    void load( final File weightsFile )
            throws IOException, ClassNotFoundException {
        try ( FileInputStream input = new FileInputStream(weightsFile) ) {
            load(input);
        }
    }

    /**
     * Carga una red neuronal desde un archivo.
     *
     * @param inputStream stream con los pesos de la red neuronal.
     *
     * @throws IOException            no se puede leer el archivo.
     * @throws ClassNotFoundException no se encuentran las clases adecuadas para cargar la red.
     */
    public
    void load( final InputStream inputStream )
            throws IOException, ClassNotFoundException {
        if ( inputStream == null ) {
            throw new IllegalArgumentException("weightsFile can't be null");
        }
        // descomprimimos
        final GZIPInputStream gz = new GZIPInputStream(inputStream);
        // leemos le objeto utilizando ObjectInputStream
        final Object obj;
        try ( ObjectInputStream obj_in = new ObjectInputStream(gz) ) {
            // creamos el objeto
            obj = obj_in.readObject();
        }
        //intentamos cargarlo en la variable correspondiente
        if ( obj instanceof double[] ) {
            lut = (double[]) obj;
        } else {
            throw new IllegalArgumentException("Unsupported file format");
        }
    }

    /**
     * Inicializa los valores de los pesos de la red neuronal con numero al azar.
     */
    @SuppressWarnings( "UnclearExpression" )
    public
    void randomize() {
        final Random random = new Random();
        IntStream.range(0, lut.length).parallel().forEach(weightIndex -> lut[weightIndex] = ( random.nextDouble() * 2.0d - 1.0d ));
    }

    /**
     * Salva en un archivo {@code outputFile} los pesos de la red neuronal.
     *
     * @param outputFile archivo destino.
     *
     * @throws IOException no se puede guardar en ese archivo.
     */
    public
    void save( final File outputFile )
            throws IOException {
        //definimos el stream de salida
        try ( FileOutputStream output = new FileOutputStream(outputFile) ) {
            save(output);
        }
    }

    /**
     * Salva en un stream {@code outputStream} los pesos de la red neuronal.
     *
     * @param outputStream stream destino.
     *
     * @throws IOException no se puede guardar en ese archivo.
     */
    public
    void save( final OutputStream outputStream )
            throws IOException {
        //comprimimos
        final GZIPOutputStream gz = new GZIPOutputStream(outputStream);
        //escribimos el archivo el objeto
        try ( ObjectOutputStream oos = new ObjectOutputStream(gz) ) {
            oos.writeObject(lut);
        }
    }

    /**
     * Cambia el valor del peso de la red neuronal con el índice {@code index} al valor {@code value}.
     *
     * @param index índice del peso a modificar.
     * @param value nuevo valor del peso a modificar.
     */
    public
    void setWeight(
            final int index,
            final double value
    ) {
        lut[index] = value;
    }

    /**
     * Cambia todos los pesos de la red neuronal con los valores de {@code value}
     *
     * @param value nuevos valores para los pesos de la red neuronal.
     */
    public
    void setWeights( final double[] value ) {
        lut = value;
    }

}
