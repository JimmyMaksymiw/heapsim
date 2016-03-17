package memory;

import java.util.Collections;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * This memory model allocates memory cells based on the buddy method.
 *
 * @author "Johan Holmberg, Malmö university"
 * @since 1.0
 */
public class Buddy extends Memory {
    private class Block {
        private Pointer pointer;
        private int size;
        private Block right;
        private Block left;
        private boolean empty;
        private Stack<Pointer> relatives;

        public Block(Pointer p, int size) {
            this.pointer = p;
            this.size = size;
            empty = true;
            relatives = new Stack<>();
        }

        public String toString() {
            return String.format("%03d - %03d\t\t%s (Size: %d)", pointer.pointsAt(), pointer.pointsAt() + size - 1, empty ? "Free" : "Allocated", size);
        }

    }

    private final List<Block> blocks = new LinkedList<>();

    /**
     * Initializes an instance of a buddy-based memory.
     *
     * @param size The number of cells.
     */
    public Buddy(int size) {
        super(size);
        blocks.add(new Block(new Pointer(this), size));
    }

    /**
     * Allocates a number of memory cells.
     *
     * @param size the number of cells to allocate.
     * @return The address of the first cell.
     */
    @Override
    public Pointer alloc(int size) {
        // Return null if requested size is larger than the memory
        if (size > this.cells.length)
            return null;

        Pointer p = null;

        // Loop through the blocks
        for (Block b : blocks) {

            if (size == b.size && b.empty) {
                b.empty = false;
                p = b.pointer;
                break;
            } else if (b.empty && b.size >= size) {

                Block currentBlock = b;

                // Find minimum block size
                while (size < currentBlock.size) {
                    if (size <= currentBlock.size / 2) {
                        int halfSize = currentBlock.size / 2;

                        // Split block into two new
                        int startOfBlock = currentBlock.pointer.pointsAt();
                        Pointer p1 = new Pointer(startOfBlock, this);
                        Pointer p2 = new Pointer(p1.pointsAt() + halfSize, this);
                        Block b1 = new Block(p1, halfSize);
                        Block b2 = new Block(p2, currentBlock.size - b1.size);

                        // Set the new block's relatives
                        b1.relatives = (Stack<Pointer>) currentBlock.relatives.clone();
                        b2.relatives = (Stack<Pointer>) currentBlock.relatives.clone();
                        b1.relatives.push(currentBlock.pointer.clone());
                        b2.relatives.push(currentBlock.pointer.clone());

                        // Connect the old block's neighbours with the new blocks, and vice versa
                        if (currentBlock.left != null) {
                            currentBlock.left.right = b1;
                            b1.left = currentBlock.left;
                        }
                        if (currentBlock.right != null) {
                            currentBlock.right.left = b2;
                            b2.right = currentBlock.right;
                        }

                        // Connect the new blocks with each other
                        b1.right = b2;
                        b2.left = b1;

                        // Remove the old block and add the new
                        blocks.remove(currentBlock);
                        blocks.add(b1);
                        blocks.add(b2);

                        // Set b1 as current block
                        currentBlock = b1;

                        sort();
                    } else {
                        // Allocation can't fit into half size, use current
                        break;
                    }

                }

                //
                currentBlock.empty = false;
                p = currentBlock.pointer;
                break;
            }

        }
        sort();
        return p;
    }

    public void sort() {
        Collections.sort(blocks, (b1, b2) -> {
            int p1 = b1.pointer.pointsAt();
            int p2 = b2.pointer.pointsAt();
            return p1 < p2 ? -1 : 1;
        });
    }

    /**
     * Releases a number of data cells
     *
     * @param p The pointer to release.
     */
    @Override
    public void release(Pointer p) {
        for (Block b : blocks) {
            if (!b.pointer.equals(p))
                continue;

            b.empty = true;

            tryMerge(b);

            break;
        }
        sort();
    }

    public void tryMerge(Block b) {
        Block both;
        Pointer relativesPointer;

        do {
            both = null;
            relativesPointer = null;

            // Retrieve pointer of relative
            try {relativesPointer = b.relatives.peek();} catch (EmptyStackException ignored) {}

            // If relativesPointer is null, you're at the top
            if (relativesPointer != null) {

                // Merge right
                if (b.right != null && b.pointer.pointsAt() == relativesPointer.pointsAt() && b.right.empty && b.right.size == b.size) {
                    both = new Block(b.pointer, b.size + b.right.size);

                    // Connect new block with neighbours
                    both.right = b.right.right;
                    both.left = b.left;

                    // Remove closest relative and copy stack to merged block
                    b.relatives.pop();
                    both.relatives = (Stack<Pointer>) b.relatives.clone();

                    // Remove old neighbour
                    blocks.remove(b.right);
                }

                // Merge left
                else if (b.left != null && relativesPointer.pointsAt() == b.left.pointer.pointsAt() && b.left.empty && b.left.size == b.size) {
                    both = new Block(b.left.pointer, b.size + b.left.size);

                    // Connect new block with neighbours
                    both.right = b.right;
                    both.left = b.left.left;

                    // Remove closest relative and copy stack to merged block
                    b.relatives.pop();
                    both.relatives = (Stack<Pointer>) b.relatives.clone();

                    // Remove old neighbour
                    blocks.remove(b.left);
                }
            }

            if (both != null) {
                // Connect neighbours with new block
                Block leftNeighbour = both.left;
                Block rightNeighbour = both.left;
                if (leftNeighbour != null) leftNeighbour.right = both;
                if (rightNeighbour != null) rightNeighbour.left = both;

                // Add new block and remove the old
                blocks.add(both);
                blocks.remove(b);
                b = both;
            }

            sort();

        // Keep going until you can't merge any more
        } while (both != null);
    }

    /**
     * Prints a simple model of the memory. Example:
     * <p>
     * |    0 -  110 | Allocated
     * |  111 -  150 | Free
     * |  151 -  999 | Allocated
     * | 1000 - 1024 | Free
     */
    @Override
    public void printLayout() {
        blocks.forEach(System.out::println);
    }
}
