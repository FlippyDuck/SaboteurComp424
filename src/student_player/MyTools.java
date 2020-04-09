package student_player;

import java.util.ArrayList;
import java.util.Arrays;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.SaboteurBonus;
import Saboteur.cardClasses.SaboteurCard;
import Saboteur.cardClasses.SaboteurDrop;
import Saboteur.cardClasses.SaboteurMap;
import Saboteur.cardClasses.SaboteurTile;

public class MyTools {
	
    public static double getSomething() {
        return Math.random();
    }

    public static double computeEvaluation(SaboteurMove move, SaboteurBoardState boardState) {
        
        SaboteurCard card = move.getCardPlayed();
        int[] pos = move.getPosPlayed();
        
        return 0;
    }

    public static SaboteurMove moveTowardsGoal(SaboteurBoardState boardState) {
        System.out.println("------------------------------------------");
        System.out.println("Starting moveTowardsGoal");
        
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

        SaboteurTile[][] board = boardState.getHiddenBoard();
        
        ArrayList<SaboteurCard> cards = boardState.getCurrentPlayerCards();

        ArrayList<SaboteurTile> pathTiles = new ArrayList<>();

        if (boardState.getNbMalus(boardState.getTurnPlayer()) > 0) {
            System.out.println("Dealing with malus");
            for (int i = 0; i < cards.size(); i++) {
                if (cards.get(i).getName().equals("Bonus")) {
                    return new SaboteurMove(new SaboteurBonus(), 0, 0, boardState.getTurnPlayer());
                }
            }
            for (int i = 0; i < cards.size(); i++) {
                if (cards.get(i) instanceof SaboteurTile) {
                    SaboteurTile tile = (SaboteurTile) cards.get(i);
                    if (tile.getPath()[1][1] == 0) {
                        return new SaboteurMove(new SaboteurDrop(), i, 0, boardState.getTurnPlayer());
                    }
                }
            }
            return new SaboteurMove(new SaboteurDrop(), 0, 0, boardState.getTurnPlayer());
        }
        System.out.println("No malus - Playing Map or Tile");
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) instanceof SaboteurMap) {
                System.out.println("Playing Map");
                SaboteurMove move;
                // System.out.println("hiddenPos = " + Arrays.deepToString(SaboteurBoardState.hiddenPos));
                if (board[SaboteurBoardState.hiddenPos[0][0]][SaboteurBoardState.hiddenPos[0][1]].getIdx().equals("8")) {
                    move = new SaboteurMove(new SaboteurMap(), SaboteurBoardState.hiddenPos[0][0], SaboteurBoardState.hiddenPos[0][1], boardState.getTurnPlayer());     
                } else if (board[SaboteurBoardState.hiddenPos[1][0]][SaboteurBoardState.hiddenPos[1][1]].getIdx().equals("8")) {
                    move = new SaboteurMove(new SaboteurMap(), SaboteurBoardState.hiddenPos[1][0], SaboteurBoardState.hiddenPos[1][1], boardState.getTurnPlayer());
                } else if (board[SaboteurBoardState.hiddenPos[2][0]][SaboteurBoardState.hiddenPos[2][1]].getIdx().equals("8")) {
                    move = new SaboteurMove(new SaboteurMap(), SaboteurBoardState.hiddenPos[2][0], SaboteurBoardState.hiddenPos[2][1], boardState.getTurnPlayer());
                } else {
                    move = new SaboteurMove(new SaboteurDrop(), i, 0, boardState.getTurnPlayer());
                }
                System.out.println(move.toPrettyString());
                // System.out.println("hiddenPos = " + Arrays.deepToString(SaboteurBoardState.hiddenPos));
                return move;
                // for (int j = 0; j < 3; j++) {
                //     System.out.println(board[boardState.hiddenPos[j][0]][boardState.hiddenPos[j][1]].getIdx());
                                
                // }
            } else if (cards.get(i) instanceof SaboteurTile) {
                SaboteurTile tile = (SaboteurTile) cards.get(i);
                pathTiles.add(tile);
                if (SaboteurTile.canBeFlipped(tile.getIdx())) {
                    pathTiles.add(tile.getFlipped());
                }
            }
        }
        System.out.println("No map. Playing tile");
        
        int minDist = Integer.MAX_VALUE;
        SaboteurTile bestTile = null;
        int[] bestPos = new int[2];

        // SaboteurTile[][] hiddenBoard = boardState.getHiddenBoard();
        // printHiddenBoard(hiddenBoard);
        int[][] intBoard = boardState.getHiddenIntBoard();    
        // printIntBoard(intBoard);
        
        System.out.println("Placing one of " + pathTiles.size() + " tiles");
        for (int i = 0; i < pathTiles.size(); i++) {
            System.out.println("iteration " + i);
            System.out.println(pathTiles.get(i).getName());
            ArrayList<int[]> positions = boardState.possiblePositions(pathTiles.get(i));
            System.out.println("Found " + positions.size() + " possible positions");
            if (positions.size() == 0) {
                continue;
            }
            
            for (int j = 0; j < positions.size(); j++) {
                System.out.println(Arrays.toString(positions.get(j)));
            }
            for (int j = 0; j < positions.size(); j++) {
                // System.out.println("Trying (" + positions.get(i)[0] + ", " + positions.get(i)[1] + ")");
                insertTile(intBoard, pathTiles.get(i), positions.get(j));
                // printIntBoard(intBoard);
                int dist = continuousPathDistanceFromGoal(intBoard);
                System.out.println("dist = " + dist);
                if (dist < minDist) {
                    minDist = dist;
                    bestTile = pathTiles.get(i);
                    bestPos = positions.get(j);
                }
                removeTile(intBoard, positions.get(j));
                // printIntBoard(intBoard);
            }

        }

        if (bestTile == null) {
            return boardState.getRandomMove();
        }
        System.out.println("Best dist = " + minDist);
        System.out.println("Playing tile " + bestTile.getName() + " at " + Arrays.toString(bestPos));
        return new SaboteurMove(bestTile, bestPos[0], bestPos[1], boardState.getTurnPlayer());
    }

    public static void printIntBoard(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j] == -1 ? "-" : board[i][j]);
            }
            System.out.println();
        }
    }

    public static void printHiddenBoard(SaboteurTile[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == null) {
                    System.out.print("null ");
                } else {
                    System.out.print(board[i][j].getName() + " ");
                }
                
            }
            System.out.println();
        }
    }

    public static void insertTile(int[][] board, SaboteurTile tile, int[] position) {
        int[][] path = tile.getPath();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[position[0] * 3 + i][position[1] * 3 + j] = path[j][2 - i];
            }
        }
    }

    public static void removeTile(int[][] board, int[] position) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[position[0] * 3 + i][position[1] * 3 + j] = SaboteurBoardState.EMPTY;
            }
        }
    }

    public static int continuousPathDistanceFromGoal(int[][] board) {
        // System.out.println("Computing distance from goal");
        // printIntBoard(board);
        // int startPos = SaboteurBoardState.originPos * 3;
        
        int[] startPos = {SaboteurBoardState.originPos * 3 + 1, SaboteurBoardState.originPos * 3 + 1};
        int[] endPos = {SaboteurBoardState.hiddenPos[1][0] * 3 + 1, SaboteurBoardState.hiddenPos[1][1] * 3 + 1};

        boolean[][] reachableArr = new boolean[SaboteurBoardState.BOARD_SIZE * 3][SaboteurBoardState.BOARD_SIZE * 3];
        for (int i = 0; i < reachableArr.length; i++) {
            for (int j = 0; j < reachableArr[i].length; j++) {
                reachableArr[i][j] = false;
            }
        }

        reachableArr[startPos[0]][startPos[1]] = true;

        ArrayList<int[]> frontier = new ArrayList<>();
        frontier.add(startPos);
        int[][] moves = {{0, -1},{0, 1},{1, 0},{-1, 0}};

        int minDist = Integer.MAX_VALUE;

        while (frontier.size() > 0) {
            // printReachable(reachableArr);
            int[] pos = frontier.remove(0);
            
            for (int i = 0; i < moves.length; i++) {
                int x = pos[0] + moves[i][0];
                int y = pos[1] + moves[i][1];
                if (x >= board.length || x < 0 || y >= board[0].length || y < 0) {
                    continue;
                }
                if (board[x][y] == SaboteurBoardState.TUNNEL && reachableArr[x][y] == false) {
                    reachableArr[x][y] = true;
                    frontier.add(new int[]{x, y});
                    int tmpDist = Math.abs(x - endPos[0]) + Math.abs(y - endPos[1]);
                    if (tmpDist < minDist) {
                        minDist = tmpDist;
                    }
                }
            }    
        }
        // printReachable(reachableArr);
        // System.out.println("minDist = " + minDist);
        return minDist;
    }

    public static void printReachable(boolean[][] reachableArr) {
        for (int i = 0; i < reachableArr.length; i++) {
            for (int j = 0; j < reachableArr[i].length; j++) {
                System.out.print(reachableArr[i][j] ? "1" : "0");
            }
            System.out.println();
        }
        System.out.println("\n\n");
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