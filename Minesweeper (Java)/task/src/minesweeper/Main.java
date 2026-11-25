package minesweeper;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // write your code here
        Scanner sc = new Scanner(System.in);
        System.out.print("How many mines do you want on the field? ");
        int minesCount = sc.nextInt();

        Minesweeper minesweeper = new Minesweeper();
        minesweeper.populateMinesRandomly(minesCount);
        System.out.println("\n" + minesweeper + "\n");
        
        boolean firstFreeDone = false;

        while (minesweeper.isGameOn()) {
            System.out.print("Set/unset mines marks or claim a cell as free: ");
            int col = sc.nextInt() - 1;
            int row = sc.nextInt() - 1;
            String input = sc.next();
            if(!firstFreeDone && input.equals("free")) {
                firstFreeDone = true;
                minesweeper.processFirstFree(row, col);
            }

            minesweeper.processUserInput(row, col, input);
        }
    }
}
