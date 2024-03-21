package vn.tayjava;

import java.util.List;
import java.util.Map;

public class JsonModel {
    private int rows;
    private int columns;
    private Map<String, Symbol> symbols;
    private List<StandardSymbol> standardSymbols;
    private Map<String, Integer> bonusSymbol;
    private Map<String, WinCombination> winCombinations;
    private int betAmount;

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(Map<String, Symbol> symbols) {
        this.symbols = symbols;
    }

    public List<StandardSymbol> getStandardSymbols() {
        return standardSymbols;
    }

    public void setStandardSymbols(List<StandardSymbol> standardSymbols) {
        this.standardSymbols = standardSymbols;
    }

    public Map<String, Integer> getBonusSymbol() {
        return bonusSymbol;
    }

    public void setBonusSymbol(Map<String, Integer> bonusSymbol) {
        this.bonusSymbol = bonusSymbol;
    }

    public Map<String, WinCombination> getWinCombinations() {
        return winCombinations;
    }

    public void setWinCombinations(Map<String, WinCombination> winCombinations) {
        this.winCombinations = winCombinations;
    }

    public int getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(int betAmount) {
        this.betAmount = betAmount;
    }

    @Override
    public String toString() {
        return "JsonModel{" +
                "rows=" + rows +
                ", columns=" + columns +
                ", symbols=" + symbols +
                ", standardSymbols=" + standardSymbols +
                ", bonusSymbol=" + bonusSymbol +
                ", winCombinations=" + winCombinations +
                ", betAmount=" + betAmount +
                '}';
    }
}
