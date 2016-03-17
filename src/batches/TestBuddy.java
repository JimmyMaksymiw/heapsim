package batches;

import memory.Buddy;
import memory.Pointer;

/**
 * Buddy system test case based on Wikipedia's "In practice" example
 * found on https://en.wikipedia.org/wiki/Buddy_memory_allocation
 * Created by Kalle Bornemark on 2016-03-16.
 */
public class TestBuddy {
    public static void main(String[] args) {
        Buddy b = new Buddy(1024);
        Pointer p_a, p_b, p_c, p_d, p_e, p_f;

        p_a = b.alloc(34);
        p_b = b.alloc(66);
        p_c = b.alloc(35);
        p_d = b.alloc(67);

        b.printLayout();

        b.release(p_b);
        System.out.println("\nRemoved 34");
        b.printLayout();

        b.release(p_d);
        System.out.println("\nRemoved D (67)");
        b.printLayout();

        b.release(p_a);
        System.out.println("\nRemoved A (34)");
        b.printLayout();

        b.release(p_c);
        System.out.println("\nRemoved C (35)");
        b.printLayout();
    }
}
