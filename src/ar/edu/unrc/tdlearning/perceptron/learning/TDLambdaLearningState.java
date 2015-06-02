/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unrc.tdlearning.perceptron.learning;

import ar.edu.unrc.tdlearning.perceptron.interfaces.IAction;
import ar.edu.unrc.tdlearning.perceptron.interfaces.IPerceptronInterface;
import ar.edu.unrc.tdlearning.perceptron.interfaces.IProblem;
import ar.edu.unrc.tdlearning.perceptron.interfaces.IState;
import ar.edu.unrc.tdlearning.perceptron.interfaces.IsolatedComputation;
import java.util.List;

/**
 * Esta clase implementa TD lambda learning (lambda = trazas de elegibilidad),
 * con la tecnica por estados, la cual hace que el perceptron recuerde los
 * estados del sigueinte turno (la parte deterministica de las acciones mas lo
 * no deterministico que pueda suceder luego) para hacer las predicciones
 * <p>
 * @author lucia bressan, franco pellegrini, renzo bianchini
 */
public class TDLambdaLearningState extends TDLambdaLearning {

    /**
     *
     * @param perceptron              implementacion de la interfaz entre
     *                                nuestra red neuronal y el
     *                                perceptronInterface que utilizara el
     *                                problema. Este puede estar implementado
     *                                con cualquier libreria o codigo.
     * @param lamdba                  constante que se encuentra en el intervalo
     *                                [0,1]
     * @param alpha                   constante de tasa de aprendizaje
     * @param accumulativePredicition true si se esta utilizando el metodo
     *                                acumulativo de prediccion en TDLearning
     * @param gamma                   tasa de descuento
     * @param momentum                0 <= m < 1
     */
    public TDLambdaLearningState(IPerceptronInterface perceptron, double[] alpha, double lamdba, boolean accumulativePredicition, double gamma, double momentum) {
        super(perceptron, alpha, lamdba, accumulativePredicition, gamma, momentum);
    }

    @Override
    protected IsolatedComputation<ActionPrediction> evaluate(IProblem problem, IState turnInitialState, IAction action) {
        // aplicamos la accion 'bestAction' al estado actual 'currentState',
        // y obtenemos su estado de transicion deterministico.
        IState afterState = problem.computeAfterState(turnInitialState, action);

        //computamos todos los posibles futuros estados a los que podemos alcanzar desde el afterstate, y la probabilidad de que estos ocurran
        List<StateProbability> allPossibleNextTurnState = problem.listAllPossibleNextTurnStateFromAfterstate(afterState);

        //FIXME asegurar no efectos colaterales si activamos pralelismo
//        IPrediction fullPrediction = allPossibleNextTurnState.stream()
//                .map(possibleNextState -> {
//                    IPrediction prediction = problem.evaluateBoardWithPerceptron(possibleNextState.getNextTurnState());
//                    prediction.multiplyBy(possibleNextState.getProbability());
//                    return prediction;
//                })
//                .collect(IPrediction::new, IPrediction::accept, IPrediction::add);
//        if ( this.accumulativePredicition ) {
//            fullPrediction.addReword(afterState.getReward());
//        }
//        return new ActionPrediction(action, fullPrediction);
        return null;
    }

    @Override
    protected void learnEvaluation(IProblem problem, IState turnInitialState, IAction action, IState afterstate, IState nextTurnState, boolean isARandomMove) {
        //V (s') ← V (s') + α(rnext + V (s'next) − V (s'))      -> matematica sin trazas de elegibilidad
        trainer.train(turnInitialState, nextTurnState, getCurrentAlpha(), lamdba, isARandomMove, gamma, momentum);
    }

}