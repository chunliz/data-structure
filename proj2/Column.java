import java.util.*;
class Column {
    public String name;
    public String type;
    //public int size;
    //public List<String> columnData;

    public Column(String name, String type) {
        this.name = name;
        this.type = type;
        //size = 0;
    }
    /*
    public Column(String name, String type, String[] data) {
        this.name = name;
        this.type = type;
        ollections.addAll(columnData, data);
        size = columnData.size();
    }*/

    public void printColumnTitle() {
        System.out.print(name + " " + type + " ");
    }

    /*
    public void printColumn() {
        for (String s : columnData) {
            System.out.println(s);
        }
    }*/

    public boolean equals(Object obj) {
        if (!(obj instanceof Column)) {
            return false;
        } else {
            return this.name.equals(((Column) obj).name) && this.type.equals(((Column) obj).type);
        }
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() * this.type.hashCode();
    }
}
