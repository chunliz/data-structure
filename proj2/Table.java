import java.util.*;

public class Table {
    /** Stores table data, names and types of the columns. */
    public List<List<String>> tableInfo;
    public List<String> columnNames;
    public List<String> columnTypes;

    /** Constructors. */
    public Table() {

    }

    public Table(String[] colNames, String[] colTypes) {
        columnNames = new ArrayList<>();
        columnTypes = new ArrayList<>();
        Collections.addAll(columnNames, colNames);
        Collections.addAll(columnTypes, colTypes);
        tableInfo = new ArrayList<>();
    }

    public Table(String[] colNames, String[] colTypes, String[][] tableData) {
        columnNames = new ArrayList<>();
        columnTypes = new ArrayList<>();
        Collections.addAll(columnNames, colNames);
        Collections.addAll(columnTypes, colTypes);
        tableInfo = new ArrayList<>();
        for (int i = 0; i < tableData.length; i += 1) {
            List<String> newRow = new ArrayList<>(Arrays.asList(tableData[i]));
            tableInfo.add(newRow);
        }
    }

    public Table(Table a) {
        columnNames = new ArrayList<>(a.columnNames);
        columnTypes = new ArrayList<>(a.columnTypes);
        tableInfo = new ArrayList<>(a.tableInfo);
    }

    public void addRow(String[] row) {
        tableInfo.add(new ArrayList<>(Arrays.asList(row)));
    }

    public void addColumn(String columnName, String columnType, String[] columnData) {
        columnNames.add(columnName);
        columnTypes.add(columnType);
        for (int i = 0; i < tableInfo.size(); i += 1) {
            tableInfo.get(i).add(columnData[i]);
        }
    }

    // TODO, gets the row indices corresponding to the given condition.
    public List<Integer> getRows(Condition a) {
        return null;
    }

    public String[] getColumn(Table a, int columnNum) {
        String[] ret = new String[a.tableInfo.size()];
        for (int i = 0; i < a.tableInfo.size(); i += 1) {
            ret[i] = a.tableInfo.get(i).get(columnNum);
        }
        return ret;
    }

    public static String[] getCommonColumnNames(Table a, Table b) {
        Set<String> set = new HashSet<>();
        set.addAll(b.columnNames);

        List<String> ret = new ArrayList<>();
        for (String s : a.columnNames) {
            if (set.contains(s)) {
                ret.add(s);
            }
        }
        return ret.toArray(new String[ret.size()]);
    }

    public static Table join(Table a, Table b) {
        List<String> cmnColName = new ArrayList<>(Arrays.asList(getCommonColumnNames(a, b)));
        List<String> colName = new ArrayList(cmnColName);
        List<String> colType = new ArrayList<>();
        for (String s : cmnColName) {
            colType.add(a.columnTypes.get(Operator.getColumnNumber(a, s)));
        }
        for (int i = 0; i < a.columnNames.size(); i += 1) {
            if (!colName.contains(a.columnNames.get(i))) {
                colName.add(a.columnNames.get(i));
                colType.add(a.columnTypes.get(i));
            }
        }
        for (int i = 0; i < b.columnNames.size(); i += 1) {
            if (!colName.contains(b.columnNames.get(i))) {
                colName.add(b.columnNames.get(i));
                colType.add(b.columnTypes.get(i));
            }
        }

        Table retTable = new Table(colName.toArray(new String[colName.size()]), colType.toArray(new String[colType.size()]));

        for (int i = 0; i < a.tableInfo.size(); i += 1) {
            for (int j = 0; j < b.tableInfo.size(); j += 1) {
                if (isRowMatch(a, b, i, j)) {
                    List<String> ls1 = new ArrayList<>();
                    List<String> ls2 = new ArrayList<>();
                    for (int k = 0; k < a.columnNames.size(); k += 1) {
                        if (cmnColName.contains(a.columnNames.get(k))) {
                            ls1.add(a.tableInfo.get(i).get(k));
                        } else {
                            ls2.add(a.tableInfo.get(i).get(k));
                        }
                    }
                    for (int l = 0; l < b.columnNames.size(); l += 1) {
                        if (!cmnColName.contains(b.columnNames.get(l))) {
                            ls2.add(b.tableInfo.get(j).get(l));
                        }
                    }
                    ls1.addAll(ls2);
                    retTable.addRow(ls1.toArray(new String[ls1.size()]));
                }
            }
        }
        return retTable;
    }

    /** Returns true if the ith row of table a matches the jth row of table b.
     *  Same columns of two tables have same value, then we say two rows match. */
    private static boolean isRowMatch(Table a, Table b, int i, int j) {
        String[] cmnColName = getCommonColumnNames(a, b);
        if (cmnColName.length != 0) {
            for (String s : cmnColName) {
                int idxInA = Operator.getColumnNumber(a, s);
                int idxInB = Operator.getColumnNumber(b, s);
                if (!a.tableInfo.get(i).get(idxInA).equals(b.tableInfo.get(j).get(idxInB))) {
                    return false;
                }
            }
        }
        return true;
    }



    public void printTable() {
        for (int i = 0; i < columnNames.size(); i += 1) {
            System.out.print(columnNames.get(i) + " " + columnTypes.get(i) + " ");
        }
        System.out.println();
        for (int i = 0; i < tableInfo.size(); i += 1) {
            System.out.println(tableInfo.get(i));
        }
    }
/*
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Table)) {
            return false;
        } else {
            // Check if the number of columns and rows in two tables are the same. If not, return false.
            if (this.columnNames.size() != ((Table) obj).columnNames.size()) {
                return false;
            } else {

            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;

        for (String s : columnNames) {
            result *= s.hashCode();
        }

        return result;
    }*/

}
