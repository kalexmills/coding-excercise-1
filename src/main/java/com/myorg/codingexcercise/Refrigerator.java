package com.myorg.codingexcercise;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

/**
 *
 * You are about to build a Refrigerator which has SMALL, MEDIUM, and LARGE
 * sized shelves.
 *
 * Method signature are given below. You need to implement the logic to
 *
 * 1. To keep track of items put in to the Refrigerator (add or remove) 2. Make
 * sure enough space available before putting it in 3. Make sure space is used
 * as efficiently as possible 4. Make sure code runs efficiently
 *
 *
 * Created by kamoorr on 7/14/17.
 */
public class Refrigerator {

  /**
   * Maximum number of passes to make through the refrigerator when rearranging
   * before giving up.
   */
  private static final int REARRANGE_AGGRESIVENESS = 3;

  private int usedCapacity;

  /**
   * Refrigerator Total Cubic Feet (CuFt)
   */
  private int cubicFt;

  /**
   * Large size shelf count and size of one shelf
   */
  private int largeShelfCount;
  private int largeShelfCuFt;

  /**
   * Medium size shelf count and size of one shelf
   */
  private int mediumShelfCount;
  private int mediumShelfCuFt;

  /**
   * Medium size shelf count and size of one shelf
   */
  private int smallShelfCount;
  private int smallShelfCuFt;

  /**
   * Stores the shelves in this refrigerator, sorted in order of the free space
   * available. Guarantees O(log n) time access.
   */
  private TreeSet<FreeSpaceable> shelvesByFreeSpace;

  /**
   * Stores a list of items by their size, to facilitate quickly rearranging
   * items.
   */
  private TreeSet<Item> itemsBySize;

  /**
   * Stores the shelf in which an item lies, to facilitate O(1) lookup of the
   * right shelf.
   */
  private HashMap<String, Shelf> shelfByItemId;

  /**
   *
   * Create a new refrigerator by specifying shelfSize and count for SMALL,
   * MEDIUM, LARGE shelves
   * 
   * @param largeShelfCount
   * @param largeShelfCuFt
   * @param mediumShelfCount
   * @param mediumShelfCuFt
   * @param smallShelfCount
   * @param smallShelfCuFt
   */
  public Refrigerator(int largeShelfCount, int largeShelfCuFt, int mediumShelfCount, int mediumShelfCuFt,
      int smallShelfCount, int smallShelfCuFt) {

    /**
     * Calculating total cuft as local variable to improve performance. Assuming
     * no vacant space in the refrigerator
     *
     */
    this.cubicFt = (largeShelfCount * largeShelfCuFt) + (mediumShelfCount * mediumShelfCuFt)
        + (smallShelfCount * smallShelfCuFt);

    this.largeShelfCount = largeShelfCount;
    this.largeShelfCuFt = largeShelfCuFt;

    this.mediumShelfCount = mediumShelfCount;
    this.mediumShelfCuFt = mediumShelfCuFt;

    this.smallShelfCount = smallShelfCount;
    this.smallShelfCuFt = smallShelfCuFt;

    /**
     * Create the list of shelves sorted by their free space, using Java's
     * Red-Black tree implementation to maintain their sorted order in O(log n)
     * time.
     */
    shelvesByFreeSpace = new TreeSet<FreeSpaceable>(new FreeSpaceableComparator());

    for (int i = 0; i < smallShelfCount; i++)
      shelvesByFreeSpace.add(new Shelf(this.smallShelfCuFt));

    for (int i = 0; i < mediumShelfCount; i++)
      shelvesByFreeSpace.add(new Shelf(this.mediumShelfCuFt));

    for (int i = 0; i < largeShelfCount; i++)
      shelvesByFreeSpace.add(new Shelf(this.largeShelfCuFt));

    /**
     * Instantiate in-memory item/shelf indices.
     */
    itemsBySize = new TreeSet<>();
    shelfByItemId = new HashMap<>();
  }

  /**
   * Implement logic to put an item to this refrigerator. Make sure -- You have
   * enough vacant space in the refrigerator -- Make this action efficient in a
   * way to increase maximum utilization of the space, re-arrange items when
   * necessary
   *
   * Return true if put is successful false if put is not successful, for
   * example, if you don't have enough space any shelf, even after re-arranging
   *
   *
   * @param item
   */
  public boolean put(Item item) {
    if (item == null)
      return false;
    FreeSpaceIndex spaceIndex = new FreeSpaceIndex(item.getCubicFt());
    Shelf shelf = null;
    // get the shelf with at least enough free space to handle the new item.
    // if there are no shelves with enough free space, make several passes 
    // through the refrigerator and attempt to free up some space
    for (int i = 0; i < REARRANGE_AGGRESIVENESS + 1; i++) {
      shelf = (Shelf) shelvesByFreeSpace.ceiling(spaceIndex);
      if (shelf == null)
        rearrangeToFreeSpace(item.getCubicFt());
      else
        break;
    }

    if (shelf == null)
      return false;
    else {
      addItemToShelf(item, shelf);
      return true;
    }
  }

  /**
   * Makes a single pass through the refrigerator and attempts to free up at
   * least desiredSpace. Takes O(s log k) time where s is the number of shelves,
   * and k is the max number of items in any shelf.
   * 
   * @param desiredSpace
   *          int amount of space desired in any shelf
   * @return Shelf a shelf which has at least desiredSpace, or null if the
   *         refrigerator could not be rearranged in a single pass.
   * 
   */
  private Shelf rearrangeToFreeSpace(int desiredSpace) {
    Shelf[] shelves = shelvesByFreeSpace.toArray(new Shelf[shelvesByFreeSpace.size()]);

    /**
     * Iterate over shelves in ascending order of free space. For shelves[i],
     * try to find an item in another shelf which will fit into shelves[i]. If
     * you find one, move it. Stop when you reach a shelf which has the desired
     * amount of free space and return it.
     */
    Shelf result = null;
    for (int i = 0; i < shelves.length; i++) {  // O(s) where s is num shelves
      /**
       * If we ever find a
       */
      if (result == null && shelves[i].getFreeSpace() >= desiredSpace) {
        result = shelves[i];
        break;
      }
      /**
       * Iterates over all items which have at most size equal to the free space
       * in shelves[i]. Once we find an item which lies on some shelf other than
       * shelves[i], we will swap it to this shelf.
       */
      Iterator<Item> itemIt = itemsBySize.headSet(itemsBySize.ceiling(new Item(null, shelves[i].getFreeSpace())), true)
          .descendingIterator();
      Item item = null;
      Shelf other = null;
      while (itemIt.hasNext()) {
        item = itemIt.next();
        other = shelfByItemId.get(item.getItemId());
        if (other != shelves[i])
          break;
      }
      if (other != null)
        swapItemToShelf(item, shelves[i], other);  // O(log k) where k is max number of items in any shelf
    }
    return result;
  }

  /**
   * remove and return the requested item Return null when not available
   * 
   * @param itemId
   * @return
   */
  public Item get(String itemId) {
    if (itemId != null) {
      Shelf shelf = (Shelf) shelfByItemId.get(itemId);
      if (shelf == null)
        return null;
      else
        return removeItemFromShelf(itemId, shelf);
    } else
      return null; // TODO: consider redesigning API to throw an Exception
  }

  /**
   * Return current utilization of the space
   * 
   * @return
   */
  public float getUtilizationPercentage() {
    return usedCapacity / (float) cubicFt;
  }

  /**
   * Return current utilization in terms of cuft
   * 
   * @return
   */
  public int getUsedSpace() {
    return usedCapacity;
  }

  /**
   * Adds item to shelf, updating all secondary indices. Takes O(log k) time
   * 
   * @param item
   * @param shelf
   */
  private void addItemToShelf(Item item, Shelf shelf) {
    shelvesByFreeSpace.remove(shelf);
    shelf.put(item);
    shelvesByFreeSpace.add(shelf);
    shelfByItemId.put(item.getItemId(), shelf);
    itemsBySize.add(item);
    usedCapacity += item.getCubicFt();
  }

  /**
   * Removes item from shelf, updating all secondary indices. Takes O(log k) time
   * 
   * @param itemId
   * @param shelf
   * @return
   */
  private Item removeItemFromShelf(String itemId, Shelf shelf) {
    if (shelf.get(itemId) != null) {
      shelvesByFreeSpace.remove(shelf);
      Item result = shelf.remove(itemId);
      shelvesByFreeSpace.add(shelf);
      shelfByItemId.remove(itemId);
      itemsBySize.remove(result);
      usedCapacity -= result.getCubicFt();
      return result;
    }
    return null;
  }

  /**
   * Swaps item from one shelf to another, updating all secondary indices. 
   * Takes O(log k) time.
   * 
   * @param item
   * @param from
   * @param to
   */
  private void swapItemToShelf(Item item, Shelf from, Shelf to) {
    if (from.get(item.getItemId()) != null) {
      shelvesByFreeSpace.remove(from);  // O(log k) where k is num items in a shelf
      shelvesByFreeSpace.remove(to);    // O(log k)
      from.remove(item.getItemId());    // O(1)
      to.put(item);                     // O(1)
      shelvesByFreeSpace.add(from);     // O(log k)
      shelvesByFreeSpace.add(to);       // O(log k)
      shelfByItemId.put(item.getItemId(), to); // O(1)
    }
  }

  /**
   * Sorts in Ascending order. Needed for correct operation in TreeSet.
   * 
   * @author K. Alex Mills
   */
  private static final class FreeSpaceableComparator implements Comparator<FreeSpaceable> {

    @Override
    public int compare(FreeSpaceable o1, FreeSpaceable o2) {
      if (o1 == o2)
        return 0;
      else {
        int result = o1.getFreeSpace() - o2.getFreeSpace();
        if (result == 0)
          // removing this line causes problems
          return o1.hashCode() - o2.hashCode();
        return result;
      }
    }

  }

}
