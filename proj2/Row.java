import java.util.*;
class Row {
    public List<String> rowData;

    public Row(String[] row) {
        rowData = new ArrayList<>();
        Collections.addAll(rowData, row);
    }

    public void printRow() {
        for (String str : rowData) {
            System.out.print(str);
            System.out.print(" ");
        }
        System.out.println();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Row)) {
            return false;
        } else {
            if (this.rowData.size() != ((Row) obj).rowData.size()) {
                return false;
            } else {
                for (int i = 0; i < this.rowData.size(); i += 1) {
                    if (!this.rowData.get(i).equals(((Row) obj).rowData.get(i))) {
                        return false;
                    }
                }
            }

        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (String s : rowData) {
            result += s.hashCode();
        }
        return result;
    }
}
