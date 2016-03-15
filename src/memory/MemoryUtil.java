package memory;

import java.util.*;

/**
 * @author Jimmy Maksymiw
 */
public class MemoryUtil {

    public final static class NoFreeMemoryException extends Exception {}

    public enum Status {FREE, ALLOCATED}

    public static void printLayout(Status[] memory) {
        System.out.println("\nMemory status:");
        Status s = memory[0];
        int first = 0;
        int last = 0;
        for (int i = 1; i < memory.length; i++) {
            if (memory[i].equals(s)) {
                last++;
            } else {
                System.out.println(first + " - " + last + " = " + s + "(" + (last - first + 1) + ")");
                last++;
                s = memory[i];
                first = last;
            }
        }
        System.out.println(first + " - " + last + " = " + s + "(" + (last - first + 1) + ")");
    }

    public static void printPointerPos(HashMap<Pointer, Integer> pointers) {
        LinkedList<Pointer> keys = new LinkedList(pointers.keySet());
        keys.sort((p1, p2) -> p1.pointsAt() - p2.pointsAt());

        System.out.println("\nPointer positions");
        for (Pointer p : keys) {
            System.out.println("pointsAt: " + p.pointsAt() + ", size: " + pointers.get(p));
        }
    }

    /**
     *
     * @param from
     * @param to
     * @param s
     */
    public static void updateMemoryStatus(Status[] memStatus, int from, int to, Status s) {
        for (int i = from; i < to; i++) {
            memStatus[i] = s;
        }
    }
}

