package vn.tayjava;

public class StandardSymbol {
    int column;
    int row;
    String symbol;

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

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
