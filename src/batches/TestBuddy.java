package batches;

import memory.Buddy;
import memory.Pointer;

/**
 * Created by Kalle Bornemark on 2016-03-16.
 */
public class TestBuddy {
    public static void main(String[] args) {
        Buddy b = new Buddy(1024);
        Pointer p1, p2, p3, p4;

        p1 = b.alloc(34);
        p2 = b.alloc(66);
        p3 = b.alloc(35);
        p4 = b.alloc(67);

        b.printLayout();
    }
}
