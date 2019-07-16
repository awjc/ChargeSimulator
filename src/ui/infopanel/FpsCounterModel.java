package ui.infopanel;

import java.util.TreeMap;

public class FpsCounterModel {
  private CappedSizeFpsMap fpsCountByUniqueSecond;

  FpsCounterModel() {
    // Keep the last 30 seconds worth of FPS's
    fpsCountByUniqueSecond = new CappedSizeFpsMap(30);
  }

  private class CappedSizeFpsMap {
    final TreeMap<Long, Integer> map;
    final int maxSize;

    CappedSizeFpsMap(int maxSize) {
      this.maxSize = maxSize;
      this.map = new TreeMap<>();
    }

    /** Adds one to the count of frames that have happened in this unique second */
    synchronized void onFrame(long timestampMs) {
      long uniqueSecondId = timestampMs / 1000;
      synchronized (map) {
        map.put(uniqueSecondId, map.getOrDefault(uniqueSecondId, 0) + 1);
      }
    }
  }
}
