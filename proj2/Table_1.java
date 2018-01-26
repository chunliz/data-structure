import java.util.*;

public class Table_1 {

    //private List<Row> tableRow;
    private List<Column> tableColumn;
    public List<List<String>> tableData;

    public Table_1() {

    }

    public Table_1(String[] name, String[] type) {
        tableColumn = new ArrayList<>();
        for (int i = 0; i < name.length - 1; i += 2) {
            Column col = new Column(name[i], type[i]);
            tableColumn.add(col);
        }
        tableData = new ArrayList<>();
    }

    public Table_1(String[] name, String[] type, String[][] data) {
        tableColumn = new ArrayList<>();
        for (int i = 0; i < name.length - 1; i += 2) {
            Column col = new Column(name[i], type[i]);
            tableColumn.add(col);
        }
        tableData = new ArrayList<>();

        for (int i = 0; i < data.length; i += 1) {
            List<String> newRow = new ArrayList<>(Arrays.asList(data[i]));
            tableData.add(newRow);
        }
    }

    public void addRow(String[] row) {
        if (row.length != this.tableColumn.size()) {
            throw new RuntimeException("Row size is wrong!");
        }
        tableData.add(Arrays.asList(row));
    }

    public void addColumn(String columnName, String columnType, String[] columnData) {
        if (columnData.length != tableData.size()) {
            throw new RuntimeException("Column size is wrong!");
        }
        Column newColumn = new Column(columnName, columnType);
        tableColumn.add(newColumn);
        for (int i = 0; i < columnData.length; i += 1) {
            tableData.get(i).add(columnData[i]);
        }
    }

    public String[] getColumn() {
        return null;
    }

    /*
    public Table_1 join(Table_1 tbl) {
        List<String> argsName = new ArrayList<>();
        List<String> argsType = new ArrayList<>();

        Set<Column> name = new HashSet<>();
        Set<Column> commonName = new HashSet<>();

        name.addAll(tbl.tableColumn);

        for (Column col : this.tableColumn) {
            if (name.contains(col)) {
                commonName.add(col);
                argsName.add(col.name);
                argsType.add(col.type);
            }
        }

        for (Column col : this.tableColumn) {
            if (!commonName.contains(col)) {
                argsName.add(col.name);
                argsType.add(col.type);
            }
        }

        for (Column col : tbl.tableColumn) {
            if (!commonName.contains(col)) {
                argsName.add(col.name);
                argsType.add(col.type);
            }
        }
        Table_1 result = new Table_1(argsName.toArray(new String[argsName.size()]), argsType.toArray(new String[argsType.size()]));

        for (Row r1 : this.tableRow) {
            for (Row r2 : tbl.tableRow) {
                if (commonName.isEmpty()) {
                    List<String> newRow = new ArrayList<>();
                    newRow.addAll(r1.rowData);
                    newRow.addAll(r2.rowData);
                    result.tableRow.add(new Row(newRow.toArray(new String[newRow.size()])));
                } else {
                    boolean match = true;
                    List<String> newRow = new ArrayList<>();
                    for (int i = 0; i < commonName.size(); i += 1) {
                        int pos1 = this.tableColumn.indexOf(result.tableColumn.get(i));
                        int pos2 = tbl.tableColumn.indexOf(result.tableColumn.get(i));
                        if (!r1.rowData.get(pos1).equals(r2.rowData.get(pos2))) {
                            match = false;
                            continue;
                        }
                        newRow.add(r1.rowData.get(pos1));
                    }
                    if (match) {
                        for (Column col : this.tableColumn) {
                            if (!commonName.contains(col)) {
                                newRow.add(r1.rowData.get(this.tableColumn.indexOf(col)));
                            }
                        }
                        for (Column col : tbl.tableColumn) {
                            if (!commonName.contains(col)) {
                                newRow.add(r2.rowData.get(tbl.tableColumn.indexOf(col)));
                            }
                        }
                        result.tableRow.add(new Row(newRow.toArray(new String[newRow.size()])));
                    }
                }
            }
        }

        return result;
    }*/

    private void printTable() {
        for (Column col : tableColumn) {
            col.printColumnTitle();
        }
        System.out.println();
        for (List<String> ls : tableData) {
            Row row = new Row(ls.toArray(new String[tableColumn.size()]));
            row.printRow();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Table_1)) {
            return false;
        } else {
            /* Check if the number of columns and rows in two tables are the same. If not, return false. */
            if (this.tableColumn.size() != ((Table_1) obj).tableColumn.size()) {
                return false;
            }

            Set<Column> s1 = new HashSet<>(this.tableColumn);
            Set<Column> s2 = new HashSet<>(this.tableColumn);
            if (!s1.equals(s2)) {
                return false;
            }

            /* Check whether two tables has exactly the same columns and rows. */
            for (int i = 0; i < this.tableColumn.size(); i += 1) {
                if (!this.tableColumn.get(i).equals(((Table_1) obj).tableColumn.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;

        for (Column c : tableColumn) {
            result *= c.hashCode();
        }

        return result;
    }

    /*
    public static void main(String[] args) {
        //String[] str = new String[]{"x", "int", "y", "int"};
        //Table table = new Table(str);
        Table table = new Table("x", "int", "y", "int");
        table.addRow(new Row("1","2"));

        Table table1 = new Table("x","int","z","int");
        table1.addRow(new Row("3","4"));
        table1.addRow(new Row("1","5"));

        table.join(table1).printTable();
    }*/
}
