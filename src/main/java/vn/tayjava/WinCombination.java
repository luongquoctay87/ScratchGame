package vn.tayjava;

import java.util.List;

public class WinCombination {
    private double reward_multiplier;
    private String when;
    private int count;
    private String group;
    private List<List<String>> covered_areas;

    public double getRewardMultiplier() {
        return reward_multiplier;
    }

    public boolean isApplicable(String[][] matrix, int row, int column) {
        if ("same_symbols".equals(when)) {
            // Check same symbol count
            int sameCount = countSameSymbols(matrix, row, column);
            return sameCount >= count;
        }
        return false;
    }

    private int countSameSymbols(String [][] matrix, int row, int column) {
        String symbol = matrix[row][column];
        int count = 1; // Count itself
        // Check horizontally
        for (int i = column + 1; i < matrix[0].length; i++) {
            if (matrix[row][i].equals(symbol)) {
                count++;
            } else {
                break;
            }
        }
        for (int i = column - 1; i >= 0; i--) {
            if (matrix[row][i] == symbol) {
                count++;
            } else {
                break;
            }
        }
        // Check vertically
        for (int i = row + 1; i < matrix.length; i++) {
            if (matrix[i][column] == symbol) {
                count++;
            } else {
                break;
            }
        }
        for (int i = row - 1; i >= 0; i--) {
            if (matrix[i][column] == symbol) {
                count++;
            } else {
                break;
            }
        }
        // Check diagonally (left to right)
        int i = row + 1, j = column + 1;
        while (i < matrix.length && j < matrix[0].length) {
            if (matrix[i][j] == symbol) {
                count++;
            } else {
                break;
            }
            i++;
            j++;
        }
        i = row - 1;
        j = column - 1;
        while (i >= 0 && j >= 0) {
            if (matrix[i][j] == symbol) {
                count++;
            } else {
                break;
            }
            i--;
            j--;
        }
        // Check diagonally (right to left)
        i = row + 1;
        j = column - 1;
        while (i < matrix.length && j >= 0) {
            if (matrix[i][j] == symbol) {
                count++;
            } else {
                break;
            }
            i++;
            j--;
        }
        i = row - 1;
        j = column + 1;
        while (i >= 0 && j < matrix[0].length) {
            if (matrix[i][j] == symbol) {
                count++;
            } else {
                break;
            }
            i--;
            j++;
        }
        return count;
    }

    public String getName() {
        return group;
    }
}