import static org.junit.Assert.*;
import org.junit.Test;

public class TestArrayDeque1B {
    @Test
    public void randomTest() {
        ArrayDequeSolution<Integer> ads = new ArrayDequeSolution<>();
        StudentArrayDeque<Integer> sad = new StudentArrayDeque<>();
        OperationSequence os = new OperationSequence();

        for (int i = 0; i < 10; i++) {
            double randomNumber = StdRandom.uniform();

            if (randomNumber < 0.5) {
                ads.addFirst(i);
                sad.addFirst(i);
                os.addOperation(new DequeOperation("addFirst", i));
                assertEquals(os.toString(), ads.get(0), sad.get(0));
            } else {
                ads.addLast(i);
                sad.addLast(i);
                os.addOperation(new DequeOperation("addLast", i));
                assertEquals(os.toString(), ads.get(ads.size()-1), sad.get(sad.size()-1));
            }
        }

        sad.printDeque();
        for (int i = 0; i < 10; i++) {
            double randomNumber1 = StdRandom.uniform();

            if (randomNumber1 < 0.5) {
                os.addOperation(new DequeOperation("removeFirst"));
                assertEquals(os.toString(), ads.removeFirst(), sad.removeFirst());
            } else {
                os.addOperation(new DequeOperation("removeLast"));
                assertEquals(os.toString(), ads.removeLast(), sad.removeLast());
            }
        }
    }
}
