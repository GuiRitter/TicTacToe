package main;

/**
 *
 * @author guir
 */
public class AutomaticTest {
    
    private static int board[][] = new int[3][3];
    
    private static int c = 1;
    
    private static int games = 0;
    
    private static int i = 0;
    
    private static int j = 0;
    
    private static int playedBoard[][] = new int[3][3];
    
    private static boolean team = true;
    
    private static boolean isLost () {
        if (((isOpponent(board[0][0])
         && isOpponent(board[1][1])
         && isOpponent(board[2][2]))
         || (isOpponent(board[0][2])
         && isOpponent(board[1][1])
         && isOpponent(board[2][0])))
         && (board[1][1] != 0)) {
            return true;
        } else {
            for (i = 0; i < 3; i++) {
                if (((isOpponent(board[i][0])
                 && isOpponent(board[i][1])
                 && isOpponent(board[i][2])
                 && (board[i][1] != 0))
                 || (isOpponent(board[0][i])
                 && isOpponent(board[1][i])
                 && isOpponent(board[2][i])
                 && (board[1][i] != 0)))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean isOpponent (int p) {
        return ((team != ((p % 2) == 0)) && (p != 0));
    }
    
    private static boolean isPlayer (int p) {
        return ((team == ((p % 2) == 0)) && (p != 0));
    }
    
    private static boolean isWon () {
        if (((isPlayer(board[0][0])
         && isPlayer(board[1][1])
         && isPlayer(board[2][2]))
         || (isPlayer(board[0][2])
         && isPlayer(board[1][1])
         && isPlayer(board[2][0])))
         && (board[1][1] != 0)) {
            return true;
        } else {
            for (i = 0; i < 3; i++) {
                if (((isPlayer(board[i][0])
                 && isPlayer(board[i][1])
                 && isPlayer(board[i][2])
                 && (board[i][1] != 0))
                 || (isPlayer(board[0][i])
                 && isPlayer(board[1][i])
                 && isPlayer(board[2][i])
                 && (board[1][i] != 0)))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void main (String args[]) {
        games = 0;
        lost:
        while (true) {
            games++;
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; j++) {
                    board[i][j] = 0;
                    playedBoard[i][j] = 0;
                }
            }
            System.out.println("Game " + games + " cleaned");
            for (c = 1; c < 10; c += 2) {
                System.out.println("Game " + games + " will start to look for a move");
                while (true) {
                    i = j = 3;
                    System.out.println("Game " + games + " is looking for a move at " + i + ", " + j);
                    while ((i > 2) || (j > 2)) {
                        i = (int)(10*Math.random());
                        j = (int)(10*Math.random());
                    }
                    if (board[i][j] == 0) {
                        System.out.println("Game " + games + "'s tester made a move at " + i + ", " + j);
                        board[i][j] = c;
                        break;
                    }
                }
                /*
                System.out.println(games);
                for (i = 0; i < 3; i++) {
                    for (j = 0; j < 3; j++) {
                        System.out.print(board[i][j] + " ");
                    }
                    System.out.println();
                }
                //*/
                if (c != 9) {
                    System.out.println("Game " + games + "'s player is about to make a move");
                    playedBoard = Player.play(board, team);
                    System.out.println("Game " + games + "'s player made a move");
                    for (i = 0; i < 3; i++) {
                        for (j = 0; j < 3; j++) {
                            board[i][j] = playedBoard[i][j];
                        }
                    }
                }
                if (isWon()) {
                    System.out.println("Games: " + games);
                    break;
                } else if (isLost()) {
                    System.out.println("Game was lost after: " + games + " rounds");
                    for (j = 0; j < 3; j++) {
                        for (i = 0; i < 3; i++) {
                            System.out.print(board[i][j] + " ");
                        }
                        System.out.println();
                    }
                    break lost;
                } else if (c == 9) {
                    System.out.println("Game " + games + "tied");
                }
            }
        }
        /*
        c = 1;
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                board[i][j] = 0;
            }
        }
        //*/
        /*
        board[1][0] = 1;
        playedBoard = Player.play(board, true);
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                board[i][j] = playedBoard[i][j];
            }
        }
        //*/
        /*
        finish:
        while (true) {
            i = (int)(10*Math.random());
            j = (int)(10*Math.random());
            if ((i < 3) && (j < 3)) {
                if (board[i][j] == 9) {
                    break;
                } else if (board[i][j] == 0) {
                    board[i][j] = c;
                    c += 2;
                    try {
                        playedBoard = Player.play(board, team);
                    } catch (IllegalArgumentException ex) {
                        System.out.println(ex.getMessage());
                        games++;
                        System.out.println(games);
                        if (lost()) {
                            for (j = 0; j < 3; j++) {
                                for (i = 0; i < 3; i++) {
                                    System.out.print(board[i][j] + " ");
                                }
                                System.out.println();
                            }
                            break finish;
                        }
                        c = 1;
                        for (i = 0; i < 3; i++) {
                            for (j = 0; j < 3; j++) {
                                playedBoard[i][j] = 0;
                            }
                        }
                    }
                    for (i = 0; i < 3; i++) {
                        for (j = 0; j < 3; j++) {
                            board[i][j] = playedBoard[i][j];
                        }
                    }
                }
            }
        }
        //*/
    }
}
