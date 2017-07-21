package com.myorg.codingexcercise;

/**
 *
 * Created by kamoorr on 7/14/17.
 */
public class Item implements Comparable<Item> {

    private String itemId;
    private int cubicFt;

    public Item(String itemId, int cubicFt) {
        this.itemId = itemId;
        this.cubicFt = cubicFt;
    }

    public String getItemId() {
        return itemId;
    }
    
    public int getCubicFt() {
    	return cubicFt;
    }

    @Override
    public int compareTo(Item o) {
      return this.getCubicFt() - o.getCubicFt();
    }
    
    // for debugging
    public String toString() {
      return itemId + " " + cubicFt;
    }

    
}