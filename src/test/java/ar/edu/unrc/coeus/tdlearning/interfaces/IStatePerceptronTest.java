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
package ar.edu.unrc.coeus.tdlearning.interfaces;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author lucia bressan, franco pellegrini, renzo bianchini
 */
public
class IStatePerceptronTest {

    private final boolean[][] board = { { false, false }, { false, false }, { false, false }, { false, false } };

    /**
     * Test of translateToPerceptronInput method, of class IStatePerceptron.
     */
    @Test
    public
    void testTranslateToPerceptronInput() {
        System.out.println("translateToPerceptronInput");
        final int              neuronIndex = 2;
        final IStatePerceptron instance    = new IStatePerceptronImpl();
        final Double           expResult   = 0.0d;
        final Double           result      = instance.translateToPerceptronInput(neuronIndex);
        assertThat(result, is(expResult));
    }

    /**
     *
     */
    public
    class IStatePerceptronImpl
            implements IStatePerceptron {

        @Override
        public
        IState getCopy() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public
        double getStateReward( final int outputNeuron ) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public
        boolean isTerminalState() {
            int count = 0;
            count += ( board[0][0] ) ? 1 : 0;
            count += ( board[0][1] ) ? 1 : 0;
            count += ( board[1][0] ) ? 1 : 0;
            count += ( board[1][1] ) ? 1 : 0;
            assert count != 4;
            return count == 3;
        }

        @Override
        public
        Double translateToPerceptronInput( final int neuronIndex ) {
            if ( board[0][0] && ( neuronIndex == 0 ) ) {
                return 1.0d;
            }
            if ( board[0][1] && ( neuronIndex == 1 ) ) {
                return 1.0d;
            }
            if ( board[1][0] && ( neuronIndex == 2 ) ) {
                return 1.0d;
            }
            if ( board[1][1] && ( neuronIndex == 3 ) ) {
                return 1.0d;
            }
            return 0.0d;
        }

    }

}
