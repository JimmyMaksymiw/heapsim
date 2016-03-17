package batches;

import memory.Buddy;
import memory.Pointer;

/**
 * Created by Kalle Bornemark on 2016-03-17.
 */
public class TestBuddy2 {
    public static void main(String[] args) {
        Buddy b = new Buddy(1024);
        Pointer p_a, p_b, p_c, p_d, p_e, p_f;

        p_a = b.alloc(64);
        p_b = b.alloc(64);
        p_c = b.alloc(64);
        p_d = b.alloc(128);
        p_e = b.alloc(32);
        p_f = b.alloc(256);

        b.printLayout();

        b.release(p_d);
        System.out.println("\nRemoved 128");
        b.printLayout();

        b.release(p_f);
        System.out.println("\nRemoved 256");
        b.printLayout();

        b.release(p_a);
        b.release(p_b);
        b.release(p_c);
        System.out.println("\nRemoved all 64s");
        b.printLayout();

        b.release(p_e);
        System.out.println("\nRemoved 32");
        b.printLayout();
    }
}
