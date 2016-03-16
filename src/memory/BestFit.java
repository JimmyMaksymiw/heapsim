package memory;

import memory.MemoryUtil.NoFreeMemoryException;
import memory.MemoryUtil.Status;

import java.util.TreeMap;

/**
 * This memory model allocates memory cells based on the best-fit method.
 *
 * @author "Johan Holmberg, Malmö university"
 * @since 1.0
 */
public class BestFit extends Memory {
    private TreeMap<Pointer, Integer> pointers;
    private Status[] memoryStatus;

    /**
     * Initializes an instance of a best fit-based memory.
     * @param size The number of cells.
     */
    public BestFit(int size) {
        super(size);
        pointers = MemoryUtil.getTreeMap();
        memoryStatus = MemoryUtil.getStatusArray(size);
    }

    /**
     * Allocates a number of memory cells.
     * @param size the number of cells to allocate.
     * @return The address of the first cell.
     */
    @Override
    public Pointer alloc(int size) {
        // if there is no space left in the memory to allocate in the first loop it calls compact() and tries again.
        for (int i = 0; i < 2; i++) {
            try {
                int address = checkBestFit(size);
                Pointer p = new Pointer(address, this);
                pointers.put(p, size);
                MemoryUtil.updateMemoryStatus(memoryStatus, address, address + size, Status.ALLOCATED);
                return p;
            } catch (NoFreeMemoryException e) {
                if (i == 0) compact();
                else System.err.println("No free memory");
            }
        }
        return null;
    }

    /**
     * Checks for a block that has the smallest overhead with the provided value.
     * Where the size has the best fit in the memory.
     * @param size The size of the block.
     * @return the starting address to the first free block.
     * @throws NoFreeMemoryException
     */
    private int checkBestFit(int size) throws NoFreeMemoryException {
        int address = -1;
        int minSpace = Integer.MAX_VALUE;

        int first = 0, last = 0;
        Status s = memoryStatus[0];

        for (int i = 1; i < memoryStatus.length; i++) {
            if (memoryStatus[i].equals(s)) {
                last++;
            } else {
                if (s.equals(Status.FREE) && ((last - first + 1) >= size)) {
                    if ((last - first + 1) - size < minSpace) {
                        address = first;
                        minSpace = (last - first + 1) - size;
                    }
                }
                last++;
                s = memoryStatus[i];
                first = last;
            }
        }
        if (s.equals(Status.FREE) && ((last - first + 1) >= size)) {
            if ((last - first + 1) - size < minSpace) {
                address = first;
            }
        }

        if (address >= 0) return address;
        else throw new NoFreeMemoryException();
    }

    /**
     * Releases a number of data cells
     * @param p The pointer to release.
     */
    @Override
    public void release(Pointer p) {
        try {
            int point = p.pointsAt();
            int size = pointers.get(p);
            MemoryUtil.updateMemoryStatus(memoryStatus, point, point + size, Status.FREE);
            pointers.remove(p);
        } catch (NullPointerException npe) {
            System.err.println("Pointer not in memory");
        }
    }

    /**
     * Prints a simple model of the memory, and the pointers.
     */
    @Override
    public void printLayout() {
        MemoryUtil.printLayout(memoryStatus);
        MemoryUtil.printPointerPos(pointers);
    }

    /**
     * Compacts the memory space.
     */
    public void compact() {
        MemoryUtil.compact(pointers, memoryStatus);
    }
}
