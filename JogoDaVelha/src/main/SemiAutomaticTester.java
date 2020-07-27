package main;

import javax.swing.JOptionPane;

public class SemiAutomaticTester {
    
    private static int board[][] = {{0,0,0},{0,0,0},{0,0,0}};
    
    private static int c = 2;
    
    private static int i = 0;
    
    private static int j = 0;
    
    private static int playedBoard[][] = {{0,0,0},{0,0,0},{0,0,0}};
    
    public static void main (String args[]) {
        c = 2;
        while (true) {
            playedBoard = Player.play(board, false);
            for (j = 0; j < 3; j++) {
                for (i = 0; i < 3; i++) {
                    board[i][j] = playedBoard[i][j];
                }
            }
            for (j = 0; j < 3; j++) {
                for (i = 0; i < 3; i++) {
                    System.out.print(board[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println();
            i = Integer.parseInt(JOptionPane.showInputDialog("i"));
            j = Integer.parseInt(JOptionPane.showInputDialog("j"));
            board[i][j] = c;
            for (j = 0; j < 3; j++) {
                for (i = 0; i < 3; i++) {
                    System.out.print(board[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println();
            c += 2;
        }
    }
}
