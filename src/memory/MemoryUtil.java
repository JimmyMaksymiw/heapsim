package memory;

import java.util.*;

/**
 * @author Jimmy Maksymiw
 */
public class MemoryUtil {

    public final static class NoFreeMemoryException extends Exception {}

    public enum Status {FREE, ALLOCATED}

    /**
     * Returns a TreeMap<> that implements a Comparator to have the Pointer-objects sorted.
     * @return The TreeMap<Pointer, Integer>.
     */
    public static TreeMap<Pointer, Integer> getTreeMap() {
        return new TreeMap<>((Comparator<Pointer>) (p1, p2) -> {
            if (p1.pointsAt() > p2.pointsAt()) return 1;
            else if (p1.pointsAt() < p2.pointsAt()) return -1;
            else return 0;
        });
    }

    /**
     * @param size The size of the array
     * @return a Status-array filled with FREE.
     */
    public static Status[] getStatusArray(int size) {
        Status[] s = new Status[size];
        Arrays.fill(s, Status.FREE);
        return s;
    }

    /**
     * Prints a simple model of the memory:
     * 0 - 9 = ALLOCATED(10)
     * 10 - 19 = FREE(10)
     * 20 - 39 = ALLOCATED(20)
     * 40 - 54 = FREE(15)
     * 55 - 69 = ALLOCATED(15)
     * 70 - 99 = FREE(30)
     * @param memory The Status-array of the memory.
     */
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

    /**
     * Prints all information about the pointers in ascending order.
     * @param pointers The pointers.
     */
    public static void printPointerPos(TreeMap<Pointer, Integer> pointers) {
        System.out.println("\nPointer positions:");
        for(Map.Entry<Pointer,Integer> entry : pointers.entrySet()) {
            Pointer p = entry.getKey();
            Integer size = entry.getValue();
            System.out.println("pointsAt: " + p.pointsAt() + ", size: " + size + ", data: " + Arrays.toString(p.read(size)));
        }
    }

    /**
     * Updates the provided status-array with the provided values.
     * @param from Starting index.
     * @param to End index.
     * @param s status to update the selected
     */
    public static void updateMemoryStatus(Status[] memStatus, int from, int to, Status s) {
        for (; from < to; from++) {
            memStatus[from] = s;
        }
    }

    /**
     * Compacts the memory space.
     */
    public static void compact(TreeMap<Pointer,Integer> pointers, Status[] memoryStatus){
        int counter = 0, pointerLength, pointerStart;
        for (Map.Entry<Pointer, Integer> entry : pointers.entrySet()) {
            // Get pointer info
            Pointer p = entry.getKey();
            pointerStart = p.pointsAt();
            pointerLength = entry.getValue();

            // Update memory statuses
            if (pointerStart != 0) {
                for (int i = pointerStart; i < pointerStart + pointerLength; i++) {
                    memoryStatus[i] = Status.FREE;
                }
            }
            for (int i = counter; i < counter + pointerLength; i++) {
                memoryStatus[i] = Status.ALLOCATED;
            }

            // Save the data written to the cells.
            int[] data = p.read(pointerLength);

            // Redirect pointer
            p.pointAt(counter);

            // Update the cells in the memory.
            p.write(data);

            // Set counter at first free slot
            counter = p.pointsAt() + pointerLength;
        }
    }
}

