package vn.tayjava;

import com.google.gson.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ScratchGame {
    private final int rows;
    private final int columns;
    private final Map<String, Symbol> symbols;
    private final List<SymbolProbability> standardSymbolProbabilities;
    private final Map<String, Integer> bonusSymbolProbabilities;
    private final Map<String, WinCombination> winCombinations;
    private final int betAmount;

    public ScratchGame(String configFile, int betAmount) throws IOException {
        JsonObject configJson = JsonParser.parseReader(new FileReader(configFile)).getAsJsonObject();
        Gson gson = new Gson();

        this.rows = configJson.has("rows") ? configJson.get("rows").getAsInt() : 3;
        this.columns = configJson.has("columns") ? configJson.get("columns").getAsInt() : 3;
        this.symbols = gson.fromJson(configJson.getAsJsonObject("symbols"), Symbols.class);

        JsonObject probabilities = configJson.getAsJsonObject("probabilities");
        JsonArray standardSymbols = probabilities.getAsJsonArray("standard_symbols");
        JsonObject bonusSymbols = probabilities.getAsJsonObject("bonus_symbols");

        List<SymbolProbability> symbolProbabilities = new ArrayList<>();
        if (!standardSymbols.isEmpty()) {
            for (int i = 0; i < standardSymbols.size(); i++) {
                JsonObject standard = standardSymbols.get(i).getAsJsonObject();
                System.out.println("standard_symbols: " + standard);
                int column = standard.get("column").getAsInt();
                int row = standard.get("row").getAsInt();
                String symbol = standard.get("symbols").getAsString();
                SymbolProbability probability = new SymbolProbability(column, row, symbol);
                symbolProbabilities.add(probability);
            }
        }

        this.standardSymbolProbabilities = symbolProbabilities;
        this.bonusSymbolProbabilities = gson.fromJson(bonusSymbols, Map.class);
        this.winCombinations = new Gson().fromJson(configJson.getAsJsonObject("win_combinations"), WinCombinations.class);
        this.betAmount = betAmount;
    }

    public JsonObject playGame() {
        String[][] matrix = generateMatrix();
        return calculateReward(matrix);
    }

    private String [][] generateMatrix() {
        String[][] matrix = new String [rows][columns];
        Random random = new Random();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = getRandomSymbol(random, i, j);
            }
        }
        return matrix;
    }

    private String getRandomSymbol(Random random, int row, int column) {
        double randomNumber = random.nextDouble();
        double cumulativeProbability = 0.0;
        List<SymbolProbability> probabilities = getSymbolProbabilitiesForRowAndColumn(row, column);
        for (SymbolProbability symbolProbability : probabilities) {
            cumulativeProbability += symbolProbability.probability;
            if (randomNumber <= cumulativeProbability) {
                return symbolProbability.symbol;
            }
        }
        return ""; // default to space if no symbol is selected
    }

    private List<SymbolProbability> getSymbolProbabilitiesForRowAndColumn(int row, int column) {
        List<SymbolProbability> probabilities = new ArrayList<>();
        for (SymbolProbability symbolProbability : standardSymbolProbabilities) {
            if (symbolProbability.column == column && symbolProbability.row == row) {
                probabilities.add(symbolProbability);
            }
        }
        return probabilities;
    }

    private JsonObject calculateReward(String [][] matrix) {
        int reward = 0;
        Map<String, List<String>> appliedWinningCombinations = new HashMap<>();
        String appliedBonusSymbol = null;

        // Iterate through the matrix to find winning combinations and apply bonuses
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                String symbol = String.valueOf(matrix[i][j]);
                if (symbols.containsKey(symbol)) {
                    reward += betAmount * symbols.get(symbol).getRewardMultiplier();
                    // Check if this symbol has any winning combinations
                    if (winCombinations.containsKey(symbol)) {
                        WinCombination winCombination = winCombinations.get(symbol);
                        // Apply the win combination to the symbol
                        if (winCombination.isApplicable(matrix, i, j)) {
                            appliedWinningCombinations.computeIfAbsent(symbol, k -> new ArrayList<>())
                                    .add(winCombination.getName());
                        }
                    }
                } else if (bonusSymbolProbabilities.containsKey(symbol)) {
                    // Apply bonus symbol
                    appliedBonusSymbol = symbol;
                    reward += symbols.get(symbol).getExtra();
                }
            }
        }

        JsonObject result = new JsonObject();
        result.addProperty("reward", reward);
        result.addProperty("bet_amount", betAmount);
        return result;
    }


    public static void main(String[] args) throws IOException {
//        if (args.length < 2) {
//            System.out.println("Usage: java ScratchGame <config_file> <betting_amount>");
//            return;
//        }
//        String configFile = args[0];
//        int betAmount = Integer.parseInt(args[1]);

        String configFile = "config.json";
        int betAmount = 100;

        ScratchGame game = new ScratchGame(configFile, betAmount);
        JsonObject result = game.playGame();
        System.out.println(result);
    }
}

