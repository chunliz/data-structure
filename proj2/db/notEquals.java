package db;

public class notEquals implements Comparision {
    private String comparisonCharacter;

    public notEquals() {
        comparisonCharacter = "!=";
    }

    public boolean compare(float f1, float f2) {
        return f1 != f2;
    }
    public boolean compare(String s1, String s2) {
        if (s1.compareTo(s2) != 0) {
            return true;
        } else {
            return false;
        }
    }
}