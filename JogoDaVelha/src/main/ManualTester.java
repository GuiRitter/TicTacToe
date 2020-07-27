package main;

/**
 *
 * @author guir
 */
public class ManualTester {
    
    private static int board[][] = new int[3][3];
    
    private static int c = 1;
    
    private static int games = 0;
    
    private static int i = 0;
    
    private static int j = 0;
    
    private static int playedBoard[][] = new int[3][3];
    
    private static boolean team = false;
    
    private static boolean winnableColumn = false;
    
    private static boolean winnableLine = false;
    
    private static boolean winnablePrimaryDiagonal = false;
    
    private static boolean winnableSecondaryDiagonal = false;
    
    private static int x = 0;
    
    private static void getRandomBlankPosition () {
        while (true) {
            i = j = 3;
            while (i > 2) {
                i = (int)(10*Math.random());
            }
            while (j > 2) {
                j = (int)(10*Math.random());
            }
            if (board[i][j] == 0) {
               break;
            }
        }
    }
    
    private static boolean isBlank () {
        return (board[i][j] == 0)?true:false;
    }
    
    private static boolean isBlockingPositionReversed () {
        // secondary diagonal
        winnableSecondaryDiagonal = false;
        if (i == j) {
            winnableSecondaryDiagonal = true;
            for (x = 0; x < 3; x++) {
                if ((x != i) && (!isPlayer(board[x][x]))) {
                    winnableSecondaryDiagonal = false;
                    break;
                }
            }
        }
        // primary diagonal
        winnablePrimaryDiagonal = false;
        if ((2 - i) == j) {
            winnablePrimaryDiagonal = true;
            for (x = 0; x < 3; x++) {
                if ((x != j) && (!isPlayer(board[2 - x][x]))) {
                    winnablePrimaryDiagonal = false;
                    break;
                }
            }
        }
        // line
        winnableLine = true;
        for (x = 0; x < 3; x++) {
            if ((x != i) && (!isPlayer(board[x][j]))) {
                winnableLine = false;
                break;
            }
        }
        // column
        winnableColumn = true;
        for (x = 0; x < 3; x++) {
            if ((x != j) && (!isPlayer(board[i][x]))) {
                winnableColumn = false;
                break;
            }
        }
        return ((winnableSecondaryDiagonal || winnablePrimaryDiagonal
         || winnableColumn || winnableLine) && isBlank());
    }
    
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
    
    private static boolean isPositionAtCenter () {
        return ((i == 1) && (j == 1))?true:false;
    }
    
    private static boolean isPositionAtCorner () {
        return (((i == 0) && (j == 0)) || ((i == 2) && (j == 0))
         || ((i == 0) && (j == 2)) || ((i == 2) && (j == 2)))?true:false;
    }
    
    private static boolean isPositionAtEdge () {
        return (((i == 1) && (j == 0)) || ((i == 0) && (j == 1))
         || ((i == 2) && (j == 1)) || ((i == 1) && (j == 2)))?true:false;
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
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                board[i][j] = 0;
            }
        }
        try {
            // first move
            playedBoard = Player.play(board, false);
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; j++) {
                    board[i][j] = playedBoard[i][j];
                }
            }
            // second move
            while (true) {
                getRandomBlankPosition();
                if (isPositionAtCenter()) {
                    board[i][j] = 2;
                    break;
                }
            }
            // third move
            playedBoard = Player.play(board, false);
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; j++) {
                    board[i][j] = playedBoard[i][j];
                }
            }
            // fourth move
            while (true) {
                getRandomBlankPosition();
                if (isPositionAtEdge()) {
                    board[i][j] = 4;
                    break;
                }
            }
            // fifth move
            playedBoard = Player.play(board, false);
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; j++) {
                    board[i][j] = playedBoard[i][j];
                }
            }
            // sixth move
            getRandomBlankPosition();
            board[i][j] = 6;
            // seventh move
            playedBoard = Player.play(board, false);
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; j++) {
                    board[i][j] = playedBoard[i][j];
                }
            }
            // eighth move
            getRandomBlankPosition();
            board[i][j] = 8;
            // ninth move
            playedBoard = Player.play(board, false);
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; j++) {
                    board[i][j] = playedBoard[i][j];
                }
            }
            // output
            throw new IllegalArgumentException("Nothing.");
        } catch (IllegalArgumentException ex) {
            // output
            System.out.println(ex.getMessage());
            for (j = 0; j < 3; j++) {
                for (i = 0; i < 3; i++) {
                    System.out.print(board[i][j] + " ");
                }
                System.out.println();
            }
        }
    }
}
