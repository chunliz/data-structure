package db;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Database {

    private static String DATAADDRESS = "out/production/proj2/examples/";

    // Various common constructs, simplifies parsing.
    private static final String REST  = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*",
            AND   = "\\s+and\\s+";

    private static final String INTEGER = "\\s*(\\d+)\\s*";
    private static final String FLOAT = "^\\s*([+-]?\\d*\\.\\d*)\\s*";
    private static final String STRING = "\'.*\'";

    private static final Pattern INTEGER_TYPE = Pattern.compile(INTEGER);
    private static final Pattern FLOAT_TYPE = Pattern.compile(FLOAT);
    private static final Pattern STRING_TYPE = Pattern.compile(STRING);
    private static final Pattern COLUMNEXPR = Pattern.compile("(\\S+)\\s*([+\\-*/])\\s*(\\S+)\\s+as\\s+(\\S+)");
    private static final Pattern CONDITIONEXPR = Pattern.compile("(\\S+)\\s*([<>!=][=]?)\\s*(\\S+)");

    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
            LOAD_CMD   = Pattern.compile("load " + REST),
            STORE_CMD  = Pattern.compile("store " + REST),
            DROP_CMD   = Pattern.compile("drop table " + REST),
            INSERT_CMD = Pattern.compile("insert into " + REST),
            PRINT_CMD  = Pattern.compile("print " + REST),
            SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW  = Pattern.compile("(\\S+)\\s+\\(\\s*(\\S+\\s+\\S+\\s*" +
            "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS  = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+" +
                    "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+" +
                    "([\\w\\s+\\-*/'<>=!.]+?(?:\\s+and\\s+" +
                    "[\\w\\s+\\-*/'<>=!.]+?)*))?"),
            CREATE_SEL  = Pattern.compile("(\\S+)\\s+as select\\s+" +
                    SELECT_CLS.pattern()),
            INSERT_CLS  = Pattern.compile("(\\S+)\\s+values\\s+(.+?" +
                    "\\s*(?:,\\s*.+?\\s*)*)");

    private static Map<String, Table> tableMap;

    public Database() {
        tableMap = new HashMap<>();
    }

    public String transact(String query) throws Exception{
        String result = new String();
        eval(query);
        return result;
    }

    private void eval(String query) throws Exception {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            createTable(m.group(1));
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            loadTable(m.group(1));
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            storeTable(m.group(1));
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            dropTable(m.group(1));
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            insertRow(m.group(1));
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            printTable(m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            select(m.group(1));
        } else {
            System.err.printf("Malformed query: %s\n", query);
        }
    }

    private static void createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            createNewTable(m.group(1), m.group(2).split(COMMA));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4));
        } else {
            System.err.printf("Malformed create: %s\n", expr);
        }
    }

    private static void createNewTable(String name, String[] cols) {
        StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < cols.length-1; i++) {
            joiner.add(cols[i]);
        }

        String colSentence = joiner.toString() + " and " + cols[cols.length-1];
        // System.out.printf("You are trying to create a table named %s with the columns %s\n", name, colSentence);

        String[] colNames = new String[cols.length];
        String[] colTypes = new String[cols.length];
        for (int i = 0; i < cols.length; i += 1) {
            String[] s = cols[i].split(" ");
            colNames[i] = s[0];
            colTypes[i] = s[1];
        }
        Table newTable = new Table(colNames, colTypes);
        tableMap.put(name, newTable);
    }

    private static void createSelectedTable(String name, String exprs, String tables, String conds) {
        //System.out.printf("You are trying to create a table named %s by selecting these expressions:" +
        //        " '%s' from the join of these tables: '%s', filtered by these conditions: '%s'\n", name, exprs, tables, conds);
        Table newTable = select(exprs, tables, conds);
        tableMap.put(name, newTable);
    }

    private static void loadTable(String name) {
        // System.out.printf("You are trying to load the table named %s\n", name);
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(DATAADDRESS + name + ".tbl"));
            String line;
            line = in.readLine();
            String[] cols = line.split(",");
            createNewTable(name, cols);

            Table table = tableMap.get(name);
            while ((line = in.readLine()) != null) {
                String[] row = line.split(",");
                try {
                    table.addRow(row);
                } catch (Exception e) {
                    System.err.printf("Error: cannot add new row.");
                }
            }
        } catch (IOException e) {
            System.err.printf("ERROR: TBL file not found: %s.tbl", name);
        }
    }

    private static void storeTable(String name) {
        // System.out.printf("You are trying to store the table named %s\n", name);
        try {
            PrintWriter pw = new PrintWriter(DATAADDRESS + name + ".tbl", "UTF-8");
            if (tableMap.containsKey(name)) {
                Table table = tableMap.get(name);
                List<String> lines = new ArrayList<>();
                for (int i = 0; i < table.columnNames.size(); i += 1) {
                    lines.add(String.join(" ", table.columnNames.get(i), table.columnTypes.get(i)));
                }

                pw.println(String.join(",", lines));
                for (List<String> ls : table.tableInfo) {
                    pw.println(String.join(",", ls));
                }
                pw.close();
            } else {
                System.err.printf("ERROR: Table not found: %s", name);
            }
        } catch (IOException e) {
            System.err.printf("ERROR: TBL file cannot be written: %s.tbl", name);
        }
    }

    private static void dropTable(String name) {
        // System.out.printf("You are trying to drop the table named %s\n", name);
        if (tableMap.containsKey(name)) {
            tableMap.remove(name);
        } else {
            System.err.printf("ERROR: Table not found: %s", name);
        }
    }

    private static void insertRow(String expr) {
        Matcher m = INSERT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed insert: %s\n", expr);
            return;
        }

        // System.out.printf("You are trying to insert the row \"%s\" into the table %s\n", m.group(2), m.group(1));
        Table table;
        if (tableMap.containsKey(m.group(1))) {
            table = tableMap.get(m.group(1));
            String[] row = m.group(2).split(",");
            String[] types = table.columnTypes.toArray(new String[table.columnTypes.size()]);

            if (matchType(row, types)) {
                table.addRow(row);
            }
        } else {
            System.err.printf("ERROR: Table not found: %s", m.group(1));
        }
    }

    private static boolean matchType(String[] row, String[] types) {
        if (row.length != types.length) {
            return false;
        }

        for (int i = 0; i < row.length; i += 1) {
            if ((types[i].equals("string") && Pattern.matches(STRING, row[i])) ||
                    (types[i].equals("int") && Pattern.matches(INTEGER, row[i])) ||
                    (types[i].equals("float") && Pattern.matches(FLOAT, row[i]))) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    private static void printTable(String name) {
        // System.out.printf("You are trying to print the table named %s\n", name);
        if (tableMap.containsKey(name)) {
            tableMap.get(name).printTable();
        } else {
            System.err.printf("ERROR: Table not found: %s", name);
        }
    }

    private static void select(String expr) throws Exception {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed select: %s\n", expr);
            return;
        }
        select(m.group(1), m.group(2), m.group(3));
    }

    private static Table select(String exprs, String tables, String conds) {
        // System.out.printf("You are trying to select these expressions:" +
        //        " '%s' from the join of these tables: '%s', filtered by these conditions: '%s'\n", exprs, tables, conds);

        String[] cols = exprs.split(COMMA);
        String[] tbls = tables.split(COMMA);
        String[] cds;
        if (conds == null) {
            cds = null;
        } else {
            cds = conds.split(AND);
        }
        List<Table> tableList = new ArrayList<>();
        for (String s : tbls) {
            if (tableMap.containsKey(s)) {
                tableList.add(tableMap.get(s));
            } else {
                System.err.printf("ERROR: table %s does not exist.", s);
                return null;
            }
        }
        Table newTable;
        if (tableList.size() >= 1) {
            newTable = tableList.remove(0);
        } else {
            System.err.printf("ERROR: didn't select tables.");
            return null;
        }

        for (Table  t : tableList) {
            newTable = Table.join(newTable, t);
        }

        if (newTable == null) {
            System.err.printf("ERROR: no match items in the selected tables.");
            return null;
        }

        Table retTable;
        retTable = exprsEval(newTable, cols);
        if (conds != null) {
            retTable = conditionEval(retTable, cds);
        }
        retTable.printTable();
        return retTable;
    }

    private static Table exprsEval(Table t, String[] colExprs) {
        Table retTable;
        if (colExprs.length == 1 && colExprs[0].equals("*")) {
            retTable = new Table(t);
            return retTable;
        }

        retTable = new Table();
        for (String s : colExprs) {
            Matcher m = COLUMNEXPR.matcher(s);
            if (m.matches()) {
                String colLeft = m.group(1);
                String op = m.group(2);
                String colRight = m.group(3);
                String colName = m.group(4);
                String colType = Table.getColumnType(t, colLeft);
                boolean isUnary = false;

                int colNum1 = Table.getColumnNumber(t, colLeft);
                int colNum2 = Table.getColumnNumber(t, colLeft);

                if (colNum1 == -1) {
                    System.err.printf("ERROR: invalid column expression, column does not exist: %s", colLeft);
                    return null;
                }

                if (colNum2 == -1) {
                    isUnary = true;
                }
                Operator oper = new Operator(t, colLeft, colRight, op, isUnary);
                String[] colData = oper.combineColumns();

                if (Table.getColumnType(t, colRight).equals("float")) {
                    colType = "float";
                }
                retTable.addColumn(colName, colType, colData);

            } else {
                int colNum = Table.getColumnNumber(t, s);
                if (colNum == -1) {
                    System.err.printf("ERROR: invalid column expression, column does not exist: %s", s);
                    return null;
                }
                retTable.addColumn(s, t.columnTypes.get(colNum), Table.getColumn(t, colNum));
            }
        }
        return retTable;
    }

    private static Table conditionEval(Table t, String[] conds) {
        Table retTable;
        Set<Integer> exclRows = new HashSet<>();

        for (String s : conds) {
            Matcher m = CONDITIONEXPR.matcher(s);
            if (m.matches()) {
                String colLeft = m.group(1);
                String comOper = m.group(2);
                String colRight = m.group(3);

                System.out.println(colRight);
                boolean isUnary = false;

                int colNum1 = Table.getColumnNumber(t, colLeft);
                int colNum2 = Table.getColumnNumber(t, colRight);

                if (colNum1 == -1) {
                    System.err.printf("ERROR: invalid condition, column does not exist: %s", colLeft);
                    return null;
                }

                if (colNum2 == -1) {
                    isUnary = true;
                }

                Comparision cmp;
                if (comOper.equals("==")) {
                    cmp = new equals();
                } else if (comOper.equals("!=")) {
                    cmp = new notEquals();
                } else if (comOper.equals(">")) {
                    cmp = new greaterThan();
                } else if (comOper.equals("<")) {
                    cmp = new lessThan();
                } else if (comOper.equals(">=")) {
                    cmp = new greaterThanOrEquals();
                } else if (comOper.equals("<=")) {
                    cmp = new lessThanOrEquals();
                } else {
                    System.err.printf("ERROR: invalid operator in condition: %s", comOper);
                    return null;
                }

                Condition condition = new Condition(t, colLeft, colRight, cmp, isUnary);
                int[] selectedRows = condition.obeysCondition();
                Set<Integer> set = new HashSet<>();
                for (int i : selectedRows) {
                    set.add(i);
                }

                for (int i = 0; i < t.tableInfo.size(); i += 1) {
                    if (set.contains(i)) {
                        continue;
                    }
                    exclRows.add(i);
                }
            } else {
                System.err.printf("ERROR: invalid select condition: %s", s);
                return null;
            }
        }

        String[] colNames = t.columnNames.toArray(new String[t.columnNames.size()]);
        String[] colTypes = t.columnTypes.toArray(new String[t.columnTypes.size()]);
        retTable = new Table(colNames, colTypes);

        for (int i = 0; i < t.tableInfo.size(); i += 1) {
            if (exclRows.contains(i)) {
                continue;
            }
            String[] row = t.tableInfo.get(i).toArray(new String[t.columnNames.size()]);
            retTable.addRow(row);
        }

        return retTable;
    }
}
