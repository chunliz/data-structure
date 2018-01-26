package db;

import java.util.ArrayList;
import java.util.List;

public class Condition {

    private static final String INTEGER = "\\s*(\\d+)\\s*";
    private static final String FLOAT = "^\\s*([+-]?\\d*\\.\\d*)\\s*";
    private static final String STRING = "\'.*\'";

    private Table t;
    private boolean isUnary;
    private String columnName;
    private String columnNameOrLiteral;
    private String columnType;
    private Comparision comp;

    public Condition(Table t, String colName, String s, Comparision cp, boolean isUnary) {
        this.t = new Table(t);
        this.isUnary = isUnary;
        columnName = colName;
        columnNameOrLiteral = s;
        columnType = t.columnTypes.get(t.columnNames.indexOf(colName));
        comp = cp;
    }
    public int[] obeysCondition() {
        List<Integer> retRows = new ArrayList<>();
        for (int i = 0; i < t.tableInfo.size(); i += 1) {
            String compStr1 = t.tableInfo.get(i).get(t.columnNames.indexOf(columnName));
            String compStr2 = columnNameOrLiteral;
            if (!isUnary)  {
                compStr2 = t.tableInfo.get(i).get(t.columnNames.indexOf(columnNameOrLiteral));
            }
            boolean compRes = false;
            if (columnType.equals("string")) {
                compRes = comp.compare(compStr1, compStr2);
            } else {
                compRes = comp.compare(Float.valueOf(compStr1), Float.valueOf(compStr2));
            }
            if (compRes) {
                retRows.add(i);
            }
        }
        int[] result = new int[retRows.size()];
        for (int i = 0; i < retRows.size(); i += 1) {
            result[i] = retRows.get(i);
        }
        return result;
    }
}
