package student_player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.cardClasses.*;

public class MyTools {

	private static ArrayList<SaboteurCard> discard = new ArrayList<SaboteurCard>();
    private static SaboteurTile[][] previousBoard;
	
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

        int nuggetPos = -1;
        int countRevealed = 0;
        for (int i = 0; i < 3; i++) {
            if (!board[SaboteurBoardState.hiddenPos[i][0]][SaboteurBoardState.hiddenPos[i][1]].getIdx().equals("8")) {
                countRevealed++;
            }
            if (board[SaboteurBoardState.hiddenPos[i][0]][SaboteurBoardState.hiddenPos[i][1]].getIdx().equals("nugget")) {
                nuggetPos = i;
            }
        }
        if (nuggetPos == -1 && countRevealed == 2) {
            for (int i = 0; i < 3; i++) {
                if (board[SaboteurBoardState.hiddenPos[i][0]][SaboteurBoardState.hiddenPos[i][1]].getIdx().equals("8")) {
                    nuggetPos = i;
                }
            } 
        }

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
                int dist = continuousPathDistanceFromGoal(intBoard, nuggetPos);
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

    public static int continuousPathDistanceFromGoal(int[][] board, int nuggetPos) {
        // System.out.println("Computing distance from goal");
        // printIntBoard(board);
        // int startPos = SaboteurBoardState.originPos * 3;
        
        int[] startPos = {SaboteurBoardState.originPos * 3 + 1, SaboteurBoardState.originPos * 3 + 1};
        int[] endPos;
        if (nuggetPos == -1) {
            endPos = new int[]{SaboteurBoardState.hiddenPos[1][0] * 3 + 1, SaboteurBoardState.hiddenPos[1][1] * 3 + 1};   
        } else {
            endPos = new int[]{SaboteurBoardState.hiddenPos[nuggetPos][0] * 3 + 1, SaboteurBoardState.hiddenPos[nuggetPos][1] * 3 + 1};   
        }

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
                    int tmpDist = Math.abs(x - endPos[0]) + Math.abs(y - endPos[1]) / 5; // prioritize vertical distance
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

    public static SaboteurMove monteCarlo(SaboteurBoardState boardState, int player_id) {
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

        ArrayList<SaboteurMove> moves = boardState.getAllLegalMoves();
        HashMap<SaboteurMove, Integer> map = new HashMap<>();
        BoardCopy board;
		int numRuns = 1000;
        for(int i = 0; i < numRuns; i++) {
            //Allow 2 seconds
            if(System.currentTimeMillis() - StudentPlayer.start >= 2000){
                discard.add(myMove.getCardPlayed());
                previousBoard = boardState.getHiddenBoard();
                System.out.println(myMove.toPrettyString() + " -> Winrate: " + (map.get(myMove) * 100.0 / i) + "%, NumRuns: " + i + " =======================================================================");
                return myMove;
            }
            for(SaboteurMove move : moves) {
                map.putIfAbsent(move, 0);
                //Prioritize map
                if(move.getCardPlayed() instanceof SaboteurMap) {
                    myMove = move;
                    break;
                }
                board = new BoardCopy(boardState.getHiddenBoard(), boardState.getHiddenIntBoard(), boardState.getCurrentPlayerCards(), deck, player_id);
    			//Process your move, then start the random run
                board.processMove(move);
                int utility = board.run();
                map.put(move, map.get(move) + utility);
            }
            //Get best move
            myMove = map.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
        }
        /*double max = 0;
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
        */
        return myMove;
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