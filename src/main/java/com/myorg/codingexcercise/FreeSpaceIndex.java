package com.myorg.codingexcercise;

/**
 * A light-weight index into a NavigableSet of FreeSpaceable objects. Only contains
 * the minimum logic needed by wrapping a simple int.
 * 
 * @author K. Alex Mills
 *
 */
public class FreeSpaceIndex implements FreeSpaceable, Comparable<FreeSpaceable> {

  private int freeSpaceCuFt;
  
  public FreeSpaceIndex(int freeSpaceCuFt) {
    this.freeSpaceCuFt = freeSpaceCuFt;
  }
  
  
  public int getFreeSpace() {
    return freeSpaceCuFt;
  }


  @Override
  public int compareTo(FreeSpaceable o) {
    return this.getFreeSpace() - o.getFreeSpace();
  }
  
  
  // for debugging
  public String toString() { return ""+freeSpaceCuFt;} 
}
