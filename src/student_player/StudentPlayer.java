package student_player;

import boardgame.Move;

import Saboteur.SaboteurPlayer;

import Saboteur.cardClasses.SaboteurMap;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {

    private enum Phase {OPENING, MIDGAME, ENDGAME}

    private Phase currentPhase;
    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260714814");
        currentPhase = Phase.OPENING;
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    @Override
    public Move chooseMove(SaboteurBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...

        // MyTools.getSomething();

        // Is random the best you can do?
        // Move myMove = boardState.getRandomMove();

        SaboteurMove result = null;

        switch(currentPhase) {
            case OPENING:
                // try {
                result = MyTools.moveTowardsGoal(boardState);
                // } catch (Exception e) {
                //     System.out.println("Error occurred");
                //     System.out.println(e.getMessage());
                // }
                
                if (MyTools.continuousPathDistanceFromGoal(boardState.getHiddenIntBoard()) < 7) {
                    currentPhase = Phase.MIDGAME;
                }
                break;
            case MIDGAME:
                SaboteurMove myMove = boardState.getRandomMove();
                double max = 0;
                for(SaboteurMove move : boardState.getAllLegalMoves()) {
                    //Prioritize map
                    if(move.getCardPlayed() instanceof SaboteurMap) {
                        myMove = move;
                        break;
                    }
                    BoardCopy board;
                    double utility = 0;
                    //Number of random runs per legal move
                    int numRuns = 10;
                    for(int i = 0; i < numRuns; i++) {
                        board = new BoardCopy(boardState.getHiddenBoard(), boardState.getHiddenIntBoard(), boardState.getCurrentPlayerCards(), player_id);
                        //Process your move, then start the random run
                        board.processMove(move);
                        utility += board.run();
                    }
                    if(utility > max) {
                        myMove = move;
                        max = utility;
                    }
                    //System.out.println(max);
                }
                MyTools.discard.add(myMove.getCardPlayed());
                result = myMove;
            case ENDGAME:
                break;
        }
    
        // Return your move to be processed by the server.
        return result;
    	
    }
}