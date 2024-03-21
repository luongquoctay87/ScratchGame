package vn.tayjava;

public class StandardSymbol {
    int column;
    int row;
    String symbol;

    public StandardSymbol(int column, int row, String symbol) {
        this.column = column;
        this.row = row;
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "StandardSymbol{" +
                "column=" + column +
                ", row=" + row +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
