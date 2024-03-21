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
        ScratchGame game = new ScratchGame();
        game.loadConfig("config.json", 100); // Load config and set betting amount
        String[][] matrix = game.generateMatrix();
        // Print the generated matrix
        System.out.println("====[Matrix]====");
        for (String[] row : matrix) {
            System.out.println("---" + Arrays.toString(row) + "---");
        }
        System.out.println("================");
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
}


