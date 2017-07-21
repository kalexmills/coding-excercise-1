package com.myorg.codingexcercise;

import java.util.HashMap;
import java.util.Map;

public class Shelf implements FreeSpaceable {

  /**
   * Scales better when there are a large number of items. A more efficient
   * implementation would just use an array for small numbers of items and
   * switch to a HashMap when the number of items becomes large.
   */
  private Map<String, Item> items;
  private int capacity;
  private int usedCapacity;

  public Shelf(int capacityCuFt) {
    this.items = new HashMap<>();
    this.capacity = capacityCuFt;
    this.usedCapacity = 0;

  }

  public boolean put(Item item) {
    int itemCuFt = item.getCubicFt();
    if (itemCuFt <= getFreeSpace()) {
      usedCapacity += itemCuFt;
      items.put(item.getItemId(), item);
      return true;
    } else
      return false;
  }

  public Item get(String itemId) {
    return items.get(itemId);
  }

  public Item remove(String itemId) {
    if (items.containsKey(itemId)) {
      Item result = items.remove(itemId);
      usedCapacity -= result.getCubicFt();
      return result;
    }
    return null;
  }

  public int getUsedSpace() {
    return usedCapacity;
  }

  public int getFreeSpace() {
    return capacity - usedCapacity;
  }

  public float getUtilizationPercentage() {
    return (float) (usedCapacity / (float) capacity);
  }

  /**
   * Two shelves are equal only if they represent the exact same object.
   */
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    else
      return false;
  }

  
  public String toString() {
    return items.toString() + " capacity: " + usedCapacity + "/" + capacity;
  }
}
