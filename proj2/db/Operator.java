package db;
import java.util.*;
import java.util.regex.Pattern;

public class Operator {

    private static final String INTEGER = "\\s*(\\d+)\\s*";
    private static final String FLOAT = "^\\s*([+-]?\\d*\\.\\d*)\\s*";
    private static final String STRING = "\'.*\'";

    private Table t;
    private String columnName;
    private String columnNameOrLiteral;
    private String columnType;
    private String arithmeticSymbol;
    private boolean isUnary;

    public Operator() {

    }

    public Operator(Table t1, String columnName1, String columnName2, String symbol, boolean isUnary) {
        t = new Table(t1);
        columnName = columnName1;
        columnNameOrLiteral = columnName2;
        columnType = Table.getColumnType(t1, columnName1);
        arithmeticSymbol = symbol;
        this.isUnary = isUnary;
    }

    public boolean isTheSameType() {

        String colType2 = Table.getColumnType(t, columnNameOrLiteral);
        if (columnType.equals("string") && colType2.equals("string")) {
            return true;
        } else if (columnType.equals("int") || columnType.equals("float")) {
            if (colType2.equals("int") || colType2.equals("float")) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    // TODO, returns the combination of the two columns using the column number and combinationSymbol in the operator
    public String[] combineColumns() {
        if (!isTheSameType()) {
            throw new RuntimeException("Cannot combine rows with different types. ");
        }

        String[] retStr = new String[t.tableInfo.size()];
        int colNum1 = Table.getColumnNumber(t, columnName);
        int colNum2 = isUnary? -1 : Table.getColumnNumber(t, columnNameOrLiteral);
        String type2 = Table.getColumnType(t, columnNameOrLiteral);

        if (arithmeticSymbol.equals("+")) {
            if (columnType.equals("string")) {

                for (int i = 0; i < t.tableInfo.size(); i += 1) {
                    retStr[i] = t.tableInfo.get(i).get(colNum1) + (isUnary? columnNameOrLiteral : t.tableInfo.get(i).get(colNum2));
                }
            } else {
                for (int i = 0; i < t.tableInfo.size(); i += 1) {
                    float f = Float.valueOf(t.tableInfo.get(i).get(colNum1)) + Float.valueOf(isUnary? columnNameOrLiteral : t.tableInfo.get(i).get(colNum2));
                    if (columnType.equals("int") && type2.equals("int")) {
                        retStr[i] = String.valueOf((int) f);
                    } else {
                        retStr[i] = String.valueOf(f);
                    }
                }
            }
        } else if (arithmeticSymbol.equals("-")) {
            if (columnType.equals("string")) {
                throw new RuntimeException("Operator - is not allowed for strings. ");
            } else {
                for (int i = 0; i < t.tableInfo.size(); i += 1) {
                    float f = Float.valueOf(t.tableInfo.get(i).get(colNum1)) - Float.valueOf(isUnary? columnNameOrLiteral : t.tableInfo.get(i).get(colNum2));
                    if (columnType.equals("int") && type2.equals("int")) {
                        retStr[i] = String.valueOf((int) f);
                    } else {
                        retStr[i] = String.valueOf(f);
                    }
                }
            }
        } else if (arithmeticSymbol.equals("*")) {
            if (columnType.equals("string")) {
                throw new RuntimeException("Operator * is not allowed for strings. ");
            } else {
                for (int i = 0; i < t.tableInfo.size(); i += 1) {
                    float f = Float.valueOf(t.tableInfo.get(i).get(colNum1)) * Float.valueOf(isUnary? columnNameOrLiteral : t.tableInfo.get(i).get(colNum2));
                    if (columnType.equals("int") && type2.equals("int")) {
                        retStr[i] = String.valueOf((int) f);
                    } else {
                        retStr[i] = String.valueOf(f);
                    }
                }
            }
        } else if (arithmeticSymbol.equals("/")) {
            if (columnType.equals("string")) {
                throw new RuntimeException("Operator / is not allowed for strings. ");
            } else {
                for (int i = 0; i < t.tableInfo.size(); i += 1) {
                    float f = Float.valueOf(t.tableInfo.get(i).get(colNum1)) / Float.valueOf(isUnary? columnNameOrLiteral : t.tableInfo.get(i).get(colNum2));
                    if (columnType.equals("int") && type2.equals("int")) {
                        retStr[i] = String.valueOf((int) f);
                    } else {
                        retStr[i] = String.valueOf(f);
                    }
                }
            }
        } else {
            throw new RuntimeException("Invalid arithmetic operator! ");
        }
        return retStr;
    }
}
