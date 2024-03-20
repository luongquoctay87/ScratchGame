package vn.tayjava;

import java.util.Map;

public class SymbolProbability {
    int column;
    int row;
    Map<String, Integer> symbols;
    String symbol;
    double probability;

    public SymbolProbability(int column, int row, String symbol) {
        this.column = column;
        this.row = row;
        this.symbol = symbol;
    }

    public String getSymbol() {
        for (Map.Entry<String, Integer> entry : symbols.entrySet()) {
            symbol = entry.getKey();
            probability = (double) entry.getValue() / symbols.values().stream().mapToInt(Integer::intValue).sum();
            break; // Take the first symbol and its probability
        }
        return symbol;
    }
}
