public class Palindrome {

    public static Deque<Character> wordToDeque(String word) {
        Deque<Character> wordDeque = new LinkedListDeque<>();
        for (int i = 0; i < word.length(); i += 1) {
            wordDeque.addLast(word.charAt(i));
        }
        return wordDeque;
    }

    public static boolean isPalindrome(String word) {
        if (word.length() < 2) return true;
        return (word.charAt(0) == word.charAt(word.length()-1)) && isPalindrome(word.substring(1,word.length()-1));
    }

    public static boolean isPalindrome(String word, CharacterComparator cc) {
        if (word.length() < 2) return true;
        return cc.equalChars(word.charAt(0), word.charAt(word.length()-1)) && isPalindrome(word.substring(1,word.length()-1), cc);
    }

}
