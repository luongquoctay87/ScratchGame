import vn.tayjava.ScratchGame;

import java.util.Arrays;

public class ScratchGameTest {
    public static void main(String[] args) {
        ScratchGame game = new ScratchGame();

        game.loadConfig("config.json", 100);

        String[][] matrix = game.generateMatrix();

        // Print the generated matrix
        System.out.println("====[Matrix]====");
        for (String[] row : matrix) {
            System.out.println("---" + Arrays.toString(row) + "---");
        }
        System.out.println("================\n\n");

        System.out.println("========[ Apply Winning ]========");
        int reward = game.applyWinningCombinations(matrix);

        int bunus = game.applyBonusSymbols(matrix, reward);
        int finalReward = game.calculateFinalReward(matrix);
        System.out.println("Final reward: " + reward);
    }
}
