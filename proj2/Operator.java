import java.util.*;
public class Operator {
    public Table t;
    public int columnNum1;
    public int columnNum2;
    public String arithmeticSymbol;

    public Operator() {

    }

    public Operator(Table t1, String columnName1, String columnName2, String symbol) {
        t = new Table(t1);
        columnNum1 = getColumnNumber(t1, columnName1);
        columnNum2 = getColumnNumber(t1, columnName2);
        arithmeticSymbol = symbol;
    }


    public static int getColumnNumber(Table a, String columnName) {
        for (int i = 0; i < a.columnNames.size(); i += 1) {
            if (a.columnNames.get(i).equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isTheSameType() {
        if (t.columnTypes.get(columnNum1).equals("string") && t.columnTypes.get(columnNum2).equals("string")) {
            return true;
        } else if (!t.columnTypes.get(columnNum1).equals("string") && !t.columnTypes.get(columnNum2).equals("string")) { {
                return true;
            }
        } else {
            return false;
        }
    }

    // TODO, returns the combination of the two columns using the column number and combinationSymbol in the operator
    public String[] combineColumns() {
        if (!isTheSameType()) {
            throw new RuntimeException("Cannot combine rows with different types. ");
        }

        String[] retStr = new String[t.tableInfo.size()];

        if (arithmeticSymbol.equals("+")) {
            if (t.columnTypes.get(columnNum1).equals("string")) {
                for (int i = 0; i < t.tableInfo.size(); i += 1) {
                    retStr[i] = t.tableInfo.get(i).get(columnNum1) + t.tableInfo.get(i).get(columnNum2);
                }
            } else {
                for (int i = 0; i < t.tableInfo.size(); i += 1) {
                    float f = Float.valueOf(t.tableInfo.get(i).get(columnNum1)) + Float.valueOf(t.tableInfo.get(i).get(columnNum2));
                    if (t.columnTypes.get(columnNum1).equals("int") && t.columnTypes.get(columnNum2).equals("int")) {
                        retStr[i] = String.valueOf((int) f);
                    } else {
                        retStr[i] = String.valueOf(f);
                    }
                }
            }
        } else if (arithmeticSymbol.equals("-")) {
            if (t.columnTypes.get(columnNum1).equals("string")) {
                throw new RuntimeException("Operator - is not allowed for strings. ");
            } else {
                for (int i = 0; i < t.tableInfo.size(); i += 1) {
                    float f = Float.valueOf(t.tableInfo.get(i).get(columnNum1)) - Float.valueOf(t.tableInfo.get(i).get(columnNum2));
                    if (t.columnTypes.get(columnNum1).equals("int") && t.columnTypes.get(columnNum2).equals("int")) {
                        retStr[i] = String.valueOf((int) f);
                    } else {
                        retStr[i] = String.valueOf(f);
                    }
                }
            }
        } else if (arithmeticSymbol.equals("*")) {
            if (t.columnTypes.get(columnNum1).equals("string")) {
                throw new RuntimeException("Operator * is not allowed for strings. ");
            } else {
                for (int i = 0; i < t.tableInfo.size(); i += 1) {
                    float f = Float.valueOf(t.tableInfo.get(i).get(columnNum1)) * Float.valueOf(t.tableInfo.get(i).get(columnNum2));
                    if (t.columnTypes.get(columnNum1).equals("int") && t.columnTypes.get(columnNum2).equals("int")) {
                        retStr[i] = String.valueOf((int) f);
                    } else {
                        retStr[i] = String.valueOf(f);
                    }
                }
            }
        } else if (arithmeticSymbol.equals("/")) {
            if (t.columnTypes.get(columnNum1).equals("string")) {
                throw new RuntimeException("Operator / is not allowed for strings. ");
            } else {
                for (int i = 0; i < t.tableInfo.size(); i += 1) {
                    float f = Float.valueOf(t.tableInfo.get(i).get(columnNum1)) / Float.valueOf(t.tableInfo.get(i).get(columnNum2));
                    if (t.columnTypes.get(columnNum1).equals("int") && t.columnTypes.get(columnNum2).equals("int")) {
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
