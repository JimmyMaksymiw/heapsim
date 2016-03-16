package memory;

import memory.MemoryUtil.NoFreeMemoryException;
import memory.MemoryUtil.Status;

import java.util.TreeMap;

/**
 * This memory model allocates memory cells based on the first-fit method.
 *
 * @author "Johan Holmberg, Malmö university"
 * @since 1.0
 */
public class FirstFit extends Memory {
    private Status[] memoryStatus;
    private TreeMap<Pointer, Integer> pointers;

    /**
     * Initializes an instance of a first fit-based memory.
     * @param size The number of cells.
     */
    public FirstFit(int size) {
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
                int address = checkFistFreeSpace(size);
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
     * Checks for the first free block with the provided value.
     * @param size The size of the block.
     * @return the starting address to the first free block.
     * @throws NoFreeMemoryException
     */
    private int checkFistFreeSpace(int size) throws NoFreeMemoryException {
        int address = 0;
        int tmp = 0;
        for (int i = 0; i < cells.length; i++) {
            if (memoryStatus[i].equals(Status.ALLOCATED)) {
                address = i + 1;
                tmp = 0;
            } else if (memoryStatus[i].equals(Status.FREE)) {
                tmp++;
                if (tmp == size) {
                    return address;
                }
            }
        }
        throw new NoFreeMemoryException();
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
