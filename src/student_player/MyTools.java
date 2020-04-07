package student_player;

import java.util.ArrayList;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurBonus;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurDrop;
import Saboteur.cardClasses.SaboteurMap;
import Saboteur.cardClasses.SaboteurTile;

public class MyTools {
	
	public static ArrayList<SaboteurCard> discard = new ArrayList<SaboteurCard>();
	
    public static double getSomething() {
        return Math.random();
    }

    public static double computeEvaluation(SaboteurMove move, SaboteurBoardState boardState) {
        
        SaboteurCard card = move.getCardPlayed();
        int[] pos = move.getPosPlayed();
        
        return 0;
    }

    public static SaboteurMove moveTowardsGoal(SaboteurBoardState boardState) {
        // ArrayList<SaboteurMove> moves = boardState.getAllLegalMoves();
        // SaboteurMove bestMove = null;
        // double bestEval = 0.0;
        // for (int i = 0; i < moves.size(); i++) {
        //     double tmp = MyTools.computeEvaluation(moves.get(i), boardState);
        //     if (tmp > bestEval) {
        //         bestMove = moves.get(i);
        //         bestEval = tmp;
        //     }
        // }

        SaboteurTile[][] board = boardState.getBoardForDisplay();
        
        ArrayList<SaboteurCard> cards = boardState.getCurrentPlayerCards();

        ArrayList<SaboteurTile> pathTiles = new ArrayList<>();

        if (boardState.getNbMalus(boardState.getTurnPlayer()) > 0) {
            for (int i = 0; i < cards.size(); i++) {
                if (cards.get(i).getName().equals("Bonus")) {
                    return new SaboteurMove(new SaboteurBonus(), 0, 0, boardState.getTurnPlayer());
                }
            }
            
        }
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getName().equals("Map")) {
                SaboteurMove move;
                if (!board[SaboteurBoardState.hiddenPos[1][0]][SaboteurBoardState.hiddenPos[1][1]].getIdx().equals("goalTile")) {
                    move = new SaboteurMove(new SaboteurMap(), SaboteurBoardState.hiddenPos[1][0], SaboteurBoardState.hiddenPos[1][1], boardState.getTurnPlayer());     
                } else if (!board[SaboteurBoardState.hiddenPos[0][0]][SaboteurBoardState.hiddenPos[0][1]].getIdx().equals("goalTile")) {
                    move = new SaboteurMove(new SaboteurMap(), SaboteurBoardState.hiddenPos[0][0], SaboteurBoardState.hiddenPos[0][1], boardState.getTurnPlayer());
                } else if (!board[SaboteurBoardState.hiddenPos[2][0]][SaboteurBoardState.hiddenPos[2][1]].getIdx().equals("goalTile")) {
                    move = new SaboteurMove(new SaboteurMap(), SaboteurBoardState.hiddenPos[2][0], SaboteurBoardState.hiddenPos[2][1], boardState.getTurnPlayer());
                } else {
                    move = new SaboteurMove(new SaboteurDrop(), i, 0, boardState.getTurnPlayer());
                }
                return move;
                // for (int j = 0; j < 3; j++) {
                //     System.out.println(board[boardState.hiddenPos[j][0]][boardState.hiddenPos[j][1]].getIdx());
                                
                // }
            } else if (cards.get(i).getName().startsWith("Tile")) {
                pathTiles.add((SaboteurTile) cards.get(i));
            }
        }
        
        for (int i = 0; i < pathTiles.size(); i++) {
            ArrayList<int[]> positions = boardState.possiblePositions(pathTiles.get(i));
            
        }
        
        return null;

    }
    
    public static int distanceToNearestGoal(int[] pos) {
    	int distance = Integer.MAX_VALUE;
    	for(int i = 0; i < 3; i++) {
    		int dx = Math.abs(pos[0] - SaboteurBoardState.hiddenPos[i][0]);
    		int dy = Math.abs(pos[1] - SaboteurBoardState.hiddenPos[i][1]);
    		if(dx + dy < distance)
    			distance = dx + dy;
    	}
    	return distance;
    }
}