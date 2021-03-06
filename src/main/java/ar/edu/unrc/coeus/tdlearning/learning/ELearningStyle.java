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
package ar.edu.unrc.coeus.tdlearning.learning;

/**
 * Tipo de estilo de aprendizaje utilizado.
 *
 * @author lucia bressan, franco pellegrini, renzo bianchini
 */
public
enum ELearningStyle {

    /**
     * Utiliza el estado inicial para predecir la recompensa final del problema.
     */
    STATE,

    /**
     * Utiliza el estado intermedio (luego de las acciones determinísticas) para predecir la recompensa final del problema.
     */
    AFTER_STATE
}
