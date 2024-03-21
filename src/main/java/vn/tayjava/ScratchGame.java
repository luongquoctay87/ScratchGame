package vn.tayjava;

import com.google.gson.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ScratchGame {

    public static void main(String[] args) throws IOException {
        ScratchGame game = new ScratchGame();
       JsonModel jsonModel = game.convertJson("config.json", 100);
        String[][] matrix = game.generateMatrix(jsonModel);
        System.out.println(matrix);

    }

    private String[][] generateMatrix(JsonModel jsonModel) {
        int rows = jsonModel.getRows();
        int columns = jsonModel.getColumns();

        String[][] matrix = new String[rows][columns];
        Random random = new Random();

        // Generate matrix with symbols based on probabilities
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // Get probabilities for standard symbols in this cell
                String cellProbabilities = getCellProbabilities(jsonModel.getStandardSymbols(), i, j);

                // Choose a symbol based on probabilities
               // String symbol = chooseSymbol(cellProbabilities, random.nextDouble());
                matrix[i][j] = cellProbabilities;
            }
        }

        return matrix;
    }

    private String getCellProbabilities(List<StandardSymbol> standardSymbols, int row, int column) {
        String symbol = "";
        for (StandardSymbol standardSymbol : standardSymbols) {
            if (standardSymbol.column == column && standardSymbol.row == row) {
                symbol = standardSymbol.symbol;
                System.out.println("==========> symbol: " + symbol);
                break;
            }
        }

        return symbol;
    }

    private String chooseSymbol(JSONObject cellProbabilities, double randomNumber) {
        TreeMap<Double, String> symbolMap = new TreeMap<>();
        double cumulativeProbability = 0;

        // Build cumulative probabilities map
        for (String symbol : cellProbabilities.keySet()) {
            double probability = cellProbabilities.getDouble(symbol);
            cumulativeProbability += probability;
            symbolMap.put(cumulativeProbability, symbol);
        }

        // Choose symbol based on random number
        double randomValue = randomNumber * cumulativeProbability;
        return symbolMap.higherEntry(randomValue).getValue();
    }

    private JsonModel convertJson(String configFile, int betAmount) throws IOException {
        JsonObject configJson = JsonParser.parseReader(new FileReader(configFile)).getAsJsonObject();
        Gson gson = new Gson();

        int rows = configJson.has("rows") ? configJson.get("rows").getAsInt() : 3;
        int columns = configJson.has("columns") ? configJson.get("columns").getAsInt() : 3;
        Symbols symbols = gson.fromJson(configJson.getAsJsonObject("symbols"), Symbols.class);

        JsonObject probabilityJson = configJson.getAsJsonObject("probabilities");
        JsonArray standardSymbolJson = probabilityJson.getAsJsonArray("standard_symbols");
        JsonObject bonusSymbolJson = probabilityJson.getAsJsonObject("bonus_symbols");

        List<StandardSymbol> standardSymbols = new ArrayList<>();
        if (!standardSymbolJson.isEmpty()) {
            for (int i = 0; i < standardSymbolJson.size(); i++) {
                JsonObject standard = standardSymbolJson.get(i).getAsJsonObject();
                StandardSymbol probability = new StandardSymbol(standard.get("column").getAsInt(),
                        standard.get("row").getAsInt(), standard.get("symbols").getAsString());
                standardSymbols.add(probability);
            }
        }

        Map<String, Integer> bonusSymbol = gson.fromJson(bonusSymbolJson, Map.class);
        WinCombinations winCombinations = new Gson().fromJson(configJson.getAsJsonObject("win_combinations"), WinCombinations.class);

        JsonModel jsonModel = new JsonModel();
        jsonModel.setColumns(columns);
        jsonModel.setRows(rows);
        jsonModel.setSymbols(symbols);
        jsonModel.setStandardSymbols(standardSymbols);
        jsonModel.setBonusSymbol(bonusSymbol);
        jsonModel.setWinCombinations(winCombinations);
        jsonModel.setBetAmount(betAmount);

        // System.out.println(jsonModel);

        return jsonModel;
    }
}

