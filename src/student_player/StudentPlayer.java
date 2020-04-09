package student_player;

import boardgame.Move;

import Saboteur.SaboteurPlayer;
import Saboteur.cardClasses.*;

import java.util.ArrayList;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {

    private enum Phase {OPENING, MIDGAME, ENDGAME}

	private Phase currentPhase;
	private ArrayList<SaboteurCard> discard = new ArrayList<SaboteurCard>();
    private SaboteurTile[][] previousBoard;
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
                
                break;
            case MIDGAME:
                break;
            case ENDGAME:
                break;
        }
    
        // Return your move to be processed by the server.
		// return result;

		//Getting opponent's last move
		if (previousBoard != null) {
			SaboteurTile[][] board = boardState.getHiddenBoard();
			for(int i = 0; i < SaboteurBoardState.BOARD_SIZE; i++) {
				for(int j = 0; j < SaboteurBoardState.BOARD_SIZE; j++) {
					if(previousBoard[i][j] != board[i][j]) {
						//Tile was added
						if(previousBoard[i][j] == null) {
							discard.add(board[i][j]);
						}
						//Tile was destroyed
						else if(board[i][j] == null) {
							discard.add(new SaboteurDestroy());
						}
						//TODO: enemy map, bonus, malus
						//Don't know what cards enemy has dropped
					}
				}
			}
		}
		//Getting rest of deck
		ArrayList<SaboteurCard> myCards = boardState.getCurrentPlayerCards();
		ArrayList<SaboteurCard> deck = SaboteurCard.getDeck();
		//Remove cards from discard pile
        for(int i = 0; i < deck.size(); i++) {
            for(int j = 0; j < discard.size(); j++) {
                if(deck.get(i).getName().equals(discard.get(j).getName())) {
                    deck.remove(i);
                    i--;
                    break;
                }
            }
		}
		//Remove cards from your hand
        for(int i = 0; i < deck.size(); i++) {
            for(int j = 0; j < myCards.size(); j++) {
                if(deck.get(i).getName().equals(myCards.get(j).getName())) {
                    deck.remove(i);
                    i--;
                    break;
                }
            }
		}
		//Monte carlo
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
    		int numRuns = 100;
    		for(int i = 0; i < numRuns; i++) {
    			board = new BoardCopy(boardState.getHiddenBoard(), boardState.getHiddenIntBoard(), boardState.getCurrentPlayerCards(), deck, player_id);
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
		discard.add(myMove.getCardPlayed());
		previousBoard = boardState.getHiddenBoard();
        return myMove;
    }
}