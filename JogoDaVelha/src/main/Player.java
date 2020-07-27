package main;

/**
 * Tic Tac Toe player that never loses. It doesn't use objects, since the
 * objective is to test and port this code to Arduino. Attempt 2.
 * @author guir
 */
public class Player {
    
    /**
     * Counter for the amount of blank cells in a line/column/diagonal.
     */
    private static int blanks = 0;
    
    /*
     * Whether there is one.
     */
    private static boolean blockingPosition = false;
    
    /**
     * Where the game is played. Must contain numbers from 0 to 9, where 0 is a
     * free cell, odd numbers are a team and even numbers are the other. The
     * sequence of the numbers equals the sequence the moves.
     */
    private static int[][] board = new int[3][3];
    
    /**
     * Whether the player can choose which move to make.
     */
    private static boolean choice = false;
    
    /**
     * Argument check.
     */
    private static boolean found[] = new boolean[9];
    
    /**
     * Iterator.
     */
    private static int i = 0;
    
    /**
     * Iterator.
     */
    private static int j = 0;
    
    /**
     * The last turn played.
     */
    private static int lastMove = 0;
    
    /**
     * Biggest priority.
     */
    private static int maximum = 0;
    
    /**
     * Biggest priority's column.
     */
    private static int maximumI = 0;
    
    /**
     * Biggest priority's line.
     */
    private static int maximumJ = 0;
    
    /**
     * Counter for the amount of opponent cells in a line/column/diagonal.
     */
    private static int opponents = 0;
    
    /**
     * Calculated priorities of the moves on every board position.
     */
    private static int priorities[][] = new int[3][3];
    
    /**
     * Argument check.
     */
    private static enum State {
        
        /**
         * Blank cells in the board.
         */
        BLANK,
        
        /**
         * Used cells in the board.
         */
        CURRENT,
        
        /**
         * Initial state.
         */
        INITIAL};
    
    private static State state = State.INITIAL;
    
    /**
     * <code>false</code> if this player uses odd numbers, <code>true</code>
     * otherwise.
     */
    private static boolean team = false;
    
    private static boolean winnableColumn = false;
    
    private static boolean winnableLine = false;
    
    private static boolean winnablePrimaryDiagonal = false;
    
    private static boolean winnableSecondaryDiagonal = false;
    
    /**
     * Iterator.
     */
    private static int x = 0;
    
    private static void getBlockingPosition () {
        blockingPosition = false;
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                if (isBlockingPosition()) {
                    blockingPosition = true;
                    return;
                }
            }
        }
    }
    
    /**
     * Configures the coordinates of the first blank corner square. Calling this
     * method means that the desired square is actually the last corner. This is
     * preferable to ramdomly find a square when there's only one available.
     */
    private static void getFirstBlankCornerPosition () {
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                if (isPositionAtCorner() && isBlank()) {
                    return;
                }
            }
        }
    }
    
    /**
     * Configures the coordinates of the square that is on the opposite side of
     * the square that contains the passed number.
     * @param p the number on the reference square
     */
    private static void getOppositePosition (int p) {
        found:
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                if (board[i][j] == p) {
                    break found;
                }
            }
        }
        i = 2 - i;
        j = 2 - j;
    }
    
    private static void getRandomBlankCornerPosition () {
        do {
            getRandomBlankPosition();
        } while (!isPositionAtCorner());
    }
    
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
    
    /**
     * @return whether there's an opponent double at a column or line that is
     * composed of an edge square
     */
    private static boolean haveDoubleOpponentAtColumnOrLineUsingEdge () {
        for (x = 0; x < 3; x++) {
            if (isOpponent(board[x][0]) && isOpponent(board[x][1])) {
                return true;
            } else if (isOpponent(board[x][1]) && isOpponent(board[x][2])) {
                return true;
            } else if (isOpponent(board[0][x]) && isOpponent(board[1][x])) {
                return true;
            } else if (isOpponent(board[1][x]) && isOpponent(board[2][x])) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return whether the game can be won on the next move
     */
    private static boolean haveWinnablePosition () {
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                if (isWinnablePosition()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * @return whether the square is blank
     */
    private static boolean isBlank () {
        return (board[i][j] == 0)?true:false;
    }

    /**
     * @param p a number representing a move in a board
     * @return whether the move haven't been made
     */
    private static boolean isBlank (int p) {
        return (p == 0)?true:false;
    }
    
    /**
     * @return whether the position will block the opponent's win
     */
    private static boolean isBlockingPosition () {
        // secondary diagonal
        winnableSecondaryDiagonal = false;
        if (i == j) {
            winnableSecondaryDiagonal = true;
            for (x = 0; x < 3; x++) {
                if ((x != i) && (!isOpponent(board[x][x]))) {
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
                if ((x != j) && (!isOpponent(board[2 - x][x]))) {
                    winnablePrimaryDiagonal = false;
                    break;
                }
            }
        }
        // line
        winnableLine = true;
        for (x = 0; x < 3; x++) {
            if ((x != i) && (!isOpponent(board[x][j]))) {
                winnableLine = false;
                break;
            }
        }
        // column
        winnableColumn = true;
        for (x = 0; x < 3; x++) {
            if ((x != j) && (!isOpponent(board[i][x]))) {
                winnableColumn = false;
                break;
            }
        }
        return ((winnableSecondaryDiagonal || winnablePrimaryDiagonal
         || winnableColumn || winnableLine) && isBlank());
    }
    
    private static boolean isBorderWithOpponent () {
        if (isOpponent(board[1][j])) {
            return true;
        } else if (isOpponent(board[i][1])) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * @return whether the position lies at a diagonal
     */
    private static boolean isDiagonalPosition () {
        return ((i == j) || (2 - i == j))?true:false;
    }
    
    /**
     * @return whether the board is empty
     */
    private static boolean isEmpty () {
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                if (board[i][j] != 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * @return whether someone won
     */
    private static boolean isGameOver () {
        // player won at secondary diagonal
        if (isPlayer(board[0][0])
         && isPlayer(board[1][1])
         && isPlayer(board[2][2])) {
            return true;
        // opponent won at secondary diagonal
        } else if (isOpponent(board[0][0])
         && isOpponent(board[1][1])
         && isOpponent(board[2][2])) {
            return true;
        // player won at primary diagonal
        } else if (isPlayer(board[2][0])
         && isPlayer(board[1][1])
         && isPlayer(board[0][2])) {
            return true;
        // opponent won at primary diagonal
        } else if (isOpponent(board[2][0])
         && isOpponent(board[1][1])
         && isOpponent(board[0][2])) {
            return true;
        } else {
            for (x = 0; x < 3; x++) {
                // player won at column
                if (isPlayer(board[x][0])
                 && isPlayer(board[x][1])
                 && isPlayer(board[x][2])) {
                    return true;
                // opponent won at column
                } else if (isOpponent(board[x][0])
                 && isOpponent(board[x][1])
                 && isOpponent(board[x][2])) {
                    return true;
                // player won at line
                } else if (isPlayer(board[0][x])
                 && isPlayer(board[1][x])
                 && isPlayer(board[2][x])) {
                    return true;
                // opponent won at line
                } else if (isOpponent(board[0][x])
                 && isOpponent(board[1][x])
                 && isOpponent(board[2][x])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * @param p a move number
     * @return whether the number is at the board's center
     */
    private static boolean isNumberAtCenter (int p) {
        return ((board[1][1] == p)?true:false);
    }
    
    /**
     * @param p a move number
     * @return whether the number is at the board's corner
     */
    private static boolean isNumberAtCorner (int p) {
        return (((board[0][0] == p) || (board[2][0] == p)
         || (board[0][2] == p) || (board[2][2] == p))?true:false);
    }
    
    /**
     * @param p a move number
     * @return whether the number is at the board's edge
     */
    private static boolean isNumberAtEdge (int p) {
        return (((board[1][0] == p) || (board[0][1] == p)
         || (board[2][1] == p) || (board[1][2] == p))?true:false);
    }
    
    /**
     * @param p a number representing a move in the board
     * @return whether the given number represents the numbers used by the
     * opponent
     */
    private static boolean isOpponent (int p) {
        return ((team ^ ((p % 2) == 0)) && (p != 0));
    }
    
    /**
     * @param p a number representing a move in the board
     * @return whether the given number represents the numbers used by the
     * player
     */
    private static boolean isPlayer (int p) {
        return ((team == ((p % 2) == 0)) && (p != 0));
    }
    
    /**
     * @return whether the position is at the board's center
     */
    private static boolean isPositionAtCenter () {
        return ((i == 1) && (j == 1))?true:false;
    }
    
    /**
     * @return whether the position is at the board's corner
     */
    private static boolean isPositionAtCorner () {
        return (((i == 0) && (j == 0)) || ((i == 2) && (j == 0))
         || ((i == 0) && (j == 2)) || ((i == 2) && (j == 2)))?true:false;
    }
    
    /**
     * @return whether the position is at the board's edge
     */
    private static boolean isPositionAtEdge () {
        return (((i == 1) && (j == 0)) || ((i == 0) && (j == 1))
         || ((i == 2) && (j == 1)) || ((i == 1) && (j == 2)))?true:false;
    }
    
    /**
     * @return whether the position will make a win when played
     */
    private static boolean isWinnablePosition () {
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
    
    /**
     * Computes the last turn played.
     */
    private static void lastMove () {
        lastMove = 0;
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                if (lastMove < board[i][j]) {
                    lastMove = board[i][j];
                }
            }
        }
    }
    
    private static void play () {
        lastMove();
        if (haveWinnablePosition()) {
            while (true) {
                getRandomBlankPosition();
                if (isWinnablePosition()) {
                    board[i][j] = lastMove + 1;
                    break;
                }
            }
        } else {
            // player is O, those who play second
            if (team) {
                // TODO
            // player is X, those who play first
            } else {
                // first move; X at a corner
                if (lastMove == 0) {
                    getRandomBlankCornerPosition();
                    board[i][j] = 1;
                // second move; O at the center
                } else if (isNumberAtCenter(2)) {
                    // third move; X opposite to the first move
                    if (lastMove == 2) {
                        getOppositePosition(1);
                        board[i][j] = 3;
                    // fourth move; O at a corner
                    } else if (isNumberAtCorner(4)) {
                        // fifth move; X at the remaining corner
                        if (lastMove == 4) {
                            getFirstBlankCornerPosition();
                            board[i][j] = 5;
                        }
                    // fourth move; O at an edge
                    } else if (isNumberAtEdge(4)) {
                        // fifth, seventh and ninth move; X blocking
                        getBlockingPosition();
                        board[i][j] = lastMove + 1;
                    }
                // second move; O at a corner
                } else if (isNumberAtCorner(2)) {
                    // third move; X at a corner
                    if (lastMove == 2) {
                        getRandomBlankCornerPosition();
                        board[i][j] = 3;
                    // fifth move; X at the remaining corner
                    } else {
                        getFirstBlankCornerPosition();
                        board[i][j] = 5;
                    }
                // second move; O at an edge
                } else {
                    // third move; X at the center
                    if (lastMove == 2) {
                        board[1][1] = 3;
                    } else {
                        // fourth move; O blocked and made a double
                        if (haveDoubleOpponentAtColumnOrLineUsingEdge()) {
                            // fifth move, X blocking
                            getBlockingPosition();
                            board[i][j] = 5;
                        // fourth move; O just blocked
                        } else {
                            // fifth move; X at the corner not bordered by an O
                            for (i = 0; i < 3; i++) {
                                for (j = 0; j < 3; j++) {
                                    if (isPositionAtCorner() && isBlank()
                                     && (!isBorderWithOpponent())) {
                                        board[i][j] = 5;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Plays a move in a board.
     * @param board must contain numbers from 0 to 9. 0 is a free cell, odd
     * numbers are a team and even numbers are the other. The sequence of the
     * numbers equals the sequence the moves.
     * @param team <code>false</code> if this player uses odd numbers,
     * <code>true</code> otherwise.
     * @return a new board with the move played included or the same board if
     * it's full or it isn't this player's turn.
     * @throws IllegalArgumentException if the board is not a 3×3
     * <code>int</code> matrix, if it contains a number outside of the 
     * acceptable range, or if there are gaps between the numbers.
     */
    public static int[][] play (int[][] board, boolean team)
     throws IllegalArgumentException {
        if (board.length != 3 || board[0].length != 3) {
            throw new IllegalArgumentException("Board is not correctly sized.");
        } else {
            for (i = 0; i < 9; i++) {
                found[i] = false;
            }
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; j++) {
                    if ((board[i][j] < 0) || (board[i][j] > 9)) {
                        throw new IllegalArgumentException(
                         "Board contains out of range number.");
                    } else {
                        if (board[i][j] != 0) {
                            if (found[board[i][j] - 1]) {
                                throw new IllegalArgumentException(
                                 "Board contains repeated number.");
                            }
                            found[board[i][j] - 1] = true;
                        }
                    }
                }
            }
            state = State.INITIAL;
            for (i = 0; i < 9; i++) {
                if (state == State.INITIAL) {
                    if (found[i]) {
                        state = State.CURRENT;
                    } else {
                        state = State.BLANK;
                    }
                } else if (state == State.BLANK) {
                    if (found[i]) {
                        throw new IllegalArgumentException(
                         "Board contains illegal sequence of moves.");
                    }
                } else {
                    if (!found[i]) {
                        state = State.BLANK;
                        // i is previously analyzed number
                        // if team is true then player uses even numbers
                        // if last move was in an even turn
                        // then it's not the player's turn
                        // or
                        // if team is false then player uses odd numbers]
                        // if last move was in an odd turn
                        // then it's not the player's turn
                        // simplifying this results in an xnor
                        // which can be simplified by an equals
                        if (team == ((i % 2) == 0)) {
                            throw new IllegalArgumentException(
                             "This is not this player's move.");
                        }
                    } else if (i == 8) {
                        // game is finished
                        throw new IllegalArgumentException(
                         "Game is already finished.");
                    }
                }
            }
            if (isGameOver()) {
                throw new IllegalArgumentException(
                 "Game is already finished.");
            } else {
                for (i = 0; i < 3; i++) {
                    for (j = 0; j < 3; j++) {
                        Player.board[i][j] = board[i][j];
                    }
                }
                Player.team = team;
                play();
            }
        }
        return Player.board;
    }
}
