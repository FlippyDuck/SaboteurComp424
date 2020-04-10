package student_player;

import boardgame.Move;

import Saboteur.SaboteurPlayer;
import Saboteur.cardClasses.*;

import Saboteur.cardClasses.SaboteurMap;
import Saboteur.cardClasses.SaboteurTile;
import java.util.ArrayList;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {

    private enum Phase {OPENING, MIDGAME, ENDGAME}

	private Phase currentPhase;
	public static long start;
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
		start = System.currentTimeMillis();

        SaboteurMove result = null;

        switch(currentPhase) {
            case OPENING:
                // try {
                result = MyTools.moveTowardsGoal(boardState);
                // } catch (Exception e) {
                //     System.out.println("Error occurred");
                //     System.out.println(e.getMessage());
                // }
                if (result.getCardPlayed() instanceof SaboteurTile) {
                    int[][] board = boardState.getHiddenIntBoard();
                    MyTools.insertTile(board, (SaboteurTile) result.getCardPlayed(), result.getPosPlayed());
                    if (MyTools.continuousPathDistanceFromGoal(board) < 10) {
                        currentPhase = Phase.MIDGAME;
                        System.out.println("Moving to midgame phase");
                    }
                }
                
                break;
            case MIDGAME:
                result = MyTools.monteCarlo(boardState, player_id);
            case ENDGAME:
                break;
        }
    
        // Return your move to be processed by the server.
		return result;		
    }
}