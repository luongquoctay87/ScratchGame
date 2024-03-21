package vn.tayjava;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ScratchGame {
    private JsonModel jsonModel;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Lack of parameter: --config or --betting-amount");
            return;
        }

        String configFile = args[0];
        int betAmount = Integer.parseInt(args[1]);

        ScratchGame game = new ScratchGame();

        game.loadConfig(configFile, betAmount);

        String[][] matrix = game.generateMatrix();

        // Print the generated matrix
        System.out.println("====[Matrix]====");
        for (String[] row : matrix) {
            System.out.println("---" + Arrays.toString(row) + "---");
        }
        System.out.println("================\n\n");

        System.out.println("========[ Apply Winning ]========");
        int reward = game.applyWinningCombinations(matrix);
        // reward = game.applyBonusSymbols(matrix, reward);
        // reward = game.calculateFinalReward(matrix);
        System.out.println("Final reward: " + reward);
    }

    public void loadConfig(String configFile, int betAmount) {
        try {
            Gson gson = new Gson();
            JsonObject configJson = gson.fromJson(new FileReader(configFile), JsonObject.class);
            jsonModel = convertJson(configJson, betAmount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonModel convertJson(JsonObject configJson, int betAmount) {
        Gson gson = new Gson();
        int rows = configJson.has("rows") ? configJson.get("rows").getAsInt() : 3;
        int columns = configJson.has("columns") ? configJson.get("columns").getAsInt() : 3;
        Symbols symbols = gson.fromJson(configJson.getAsJsonObject("symbols"), Symbols.class);
        JsonArray standardSymbolJson = configJson.getAsJsonObject("probabilities").getAsJsonArray("standard_symbols");
        Map<String, Integer> bonusSymbol = gson.fromJson(configJson.getAsJsonObject("probabilities").getAsJsonObject("bonus_symbols"), Map.class);
        WinCombinations winCombinations = gson.fromJson(configJson.getAsJsonObject("win_combinations"), WinCombinations.class);

        List<StandardSymbol> standardSymbols = new ArrayList<>();
        for (int i = 0; i < standardSymbolJson.size(); i++) {
            JsonObject standard = standardSymbolJson.get(i).getAsJsonObject();
            StandardSymbol probability = new StandardSymbol(standard.get("column").getAsInt(),
                    standard.get("row").getAsInt(), standard.get("symbols").getAsString());
            standardSymbols.add(probability);
        }

        JsonModel jsonModel = new JsonModel();
        jsonModel.setColumns(columns);
        jsonModel.setRows(rows);
        jsonModel.setSymbols(symbols);
        jsonModel.setStandardSymbols(standardSymbols);
        jsonModel.setBonusSymbol(bonusSymbol);
        jsonModel.setWinCombinations(winCombinations);
        jsonModel.setBetAmount(betAmount);

        return jsonModel;
    }

    public String[][] generateMatrix() {
        String[][] matrix = new String[jsonModel.getRows()][jsonModel.getColumns()];
        Random random = new Random();

        // Calculate total reward multiplier for standard symbols
        int totalRewardMultiplier = jsonModel.getSymbols().values().stream()
                .filter(symbol -> symbol.getType().equals("standard"))
                .mapToInt(Symbol::getRewardMultiplier)
                .sum();

        // Generate matrix with symbols based on reward multipliers
        for (int i = 0; i < jsonModel.getRows(); i++) {
            for (int j = 0; j < jsonModel.getColumns(); j++) {
                double randomNumber = random.nextDouble() * totalRewardMultiplier;
                double cumulativeProbability = 0;

                for (Map.Entry<String, Symbol> entry : jsonModel.getSymbols().entrySet()) {
                    Symbol symbol = entry.getValue();
                    if (symbol.getType().equals("standard")) {
                        cumulativeProbability += symbol.getRewardMultiplier();
                        if (randomNumber <= cumulativeProbability) {
                            matrix[i][j] = entry.getKey();
                            break;
                        }
                    }
                }
            }
        }

        return matrix;
    }

    public int applyWinningCombinations(String[][] matrix) {
        int totalReward = 0;

        // Iterate over each symbol in the matrix
        for (int i = 0; i < jsonModel.getRows(); i++) {
            for (int j = 0; j < jsonModel.getColumns(); j++) {
                String symbol = matrix[i][j];
                if (symbol != null) {
                    // Check if the symbol has any winning combinations
                    List<String> appliedCombinations = new ArrayList<>();
                    for (Map.Entry<String, WinCombination> entry : jsonModel.getWinCombinations().entrySet()) {
                        WinCombination winCombination = entry.getValue();
                        // System.out.println("winCombination: " + winCombination);
                        if (winCombination.getWhen().equals("same_symbols")) {
                            int count = countSameSymbols(matrix, i, j);
                            if (count >= winCombination.getCount()) {
                                totalReward += (int) (calculateReward(symbol, winCombination) * jsonModel.getBetAmount());
                                appliedCombinations.add(entry.getKey());
                            }
                        } else if (winCombination.getWhen().equals("linear_symbols")) {
                            if (isLinearCombination(matrix, i, j, winCombination)) {
                                totalReward += (int) (calculateReward(symbol, winCombination) * jsonModel.getBetAmount());
                                appliedCombinations.add(entry.getKey());
                            }
                        }
                    }
                    // Output applied winning combinations for the symbol
                    if (!appliedCombinations.isEmpty()) {
                        System.out.printf("Applied winning combinations for symbol %s\n", symbol);
                    }
                }
            }
        }

        return totalReward;
    }

    public int applyBonusSymbols(String[][] matrix, int reward) {
        int finalReward = reward;

        // Iterate over each symbol in the matrix
        for (int i = 0; i < jsonModel.getRows(); i++) {
            for (int j = 0; j < jsonModel.getColumns(); j++) {
                String symbol = matrix[i][j];
                if (symbol != null && jsonModel.getBonusSymbol().containsKey(symbol)) {
                    int multiplier = jsonModel.getBonusSymbol().get(symbol);
                    switch (symbol) {
                        case "10x":
                            finalReward *= 10;
                            break;
                        case "5x":
                            finalReward *= 5;
                            break;
                        case "+1000":
                            finalReward += 1000;
                            break;
                        case "+500":
                            finalReward += 500;
                            break;
                        // Handle other bonus symbols if needed
                        default:
                            break;
                    }
                    System.out.println("Applied bonus symbol: " + symbol);
                }
            }
        }

        return finalReward;
    }

    public int calculateFinalReward(String[][] matrix) {
        int reward = applyWinningCombinations(matrix);
        reward = applyBonusSymbols(matrix, reward);
        return reward;
    }

    private int countSameSymbols(String[][] matrix, int row, int column) {
        String targetSymbol = matrix[row][column];
        int count = 1; // Start with 1 for the current symbol

        // Check horizontally
        for (int i = column + 1; i < jsonModel.getColumns(); i++) {
            if (matrix[row][i] != null && matrix[row][i].equals(targetSymbol)) {
                count++;
            } else {
                break;
            }
        }

        // Check vertically
        for (int i = row + 1; i < jsonModel.getRows(); i++) {
            if (matrix[i][column] != null && matrix[i][column].equals(targetSymbol)) {
                count++;
            } else {
                break;
            }
        }

        return count;
    }

    private boolean isLinearCombination(String[][] matrix, int row, int column, WinCombination winCombination) {
        for (List<String> area : winCombination.getCovered_areas()) {
            boolean matched = true;
            for (String cell : area) {
                String[] indices = cell.split(":");
                int r = Integer.parseInt(indices[0]);
                int c = Integer.parseInt(indices[1]);
                if (!matrix[r][c].equals(matrix[row][column])) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                return true;
            }
        }
        return false;
    }

    private double calculateReward(String symbol, WinCombination winCombination) {
        double rewardMultiplier = jsonModel.getSymbols().get(symbol).getRewardMultiplier();
        return rewardMultiplier * winCombination.getReward_multiplier();
    }

}


