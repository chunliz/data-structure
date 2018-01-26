import org.junit.Test;
import static org.junit.Assert.*;

public class tableTest {

    @Test
    public void testTable() {
        Table table1 = new Table(new String[]{"x", "y"}, new String[]{"int", "int"});
        table1.addRow(new String[]{"1", "2"});
        table1.addRow(new String[]{"4", "6"});

        Table table2 = new Table(new String[]{"m", "z"}, new String[]{"int", "int"});
        table2.addRow(new String[]{"3", "4"});
        table2.addRow(new String[]{"1", "5"});

        Table expected = new Table(new String[]{"x", "y", "z"}, new String[]{"int", "int", "int"});
        expected.addRow(new String[]{"1", "2", "5"});

        //assertTrue(join(table1, table2).equals(expected));
        Operator op = new Operator();
        Table.join(table1, table2).printTable();
        expected.printTable();

        Table table3 = new Table(expected);
        table3.printTable();

        //assertEquals(expected.columnNames, op.join(table1, table2).columnNames);
        //assertEquals(expected.columnTypes, op.join(table1, table2).columnTypes);
        //assertEquals(expected.tableInfo, op.join(table1, table2).tableInfo);
    }

    @Test
    public void testOperator() {
        Table table1 = new Table(new String[]{"x", "y"}, new String[]{"string", "string"});
        table1.addRow(new String[]{"1", "2"});
        table1.addRow(new String[]{"4", "6"});

        String[] expected = new String[]{"12", "46"};

        Operator op = new Operator(table1, "x", "y", "+");
        assertArrayEquals(expected, op.combineColumns());
    }

    @Test
    public void testCondition() {
        Table table1 = new Table(new String[]{"First", "Last", "Age"}, new String[]{"string", "string", "int"});
        table1.addRow(new String[]{"Chunli", "Zhang", "30"});
        table1.addRow(new String[]{"Zhiling", "Dun", "29"});
        table1.addRow(new String[]{"An", "Li", "60"});

        Comparision comp = new greaterThan();
        Condition cond = new Condition(table1, "Age", "29", comp, true);
        int[] actual = cond.obeysCondition();

        int[] expected = new int[]{0, 2};
        assertArrayEquals(expected, actual);
    }
}
