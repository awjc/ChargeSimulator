package v2.ui.infopanel;

import java.util.ArrayDeque;
import java.util.Deque;

public class FpsAverager {
  final Deque<Long> fpsLog;
  final long numMsToKeepTrackOf;

  FpsAverager(long numMsToKeepTrackOf) {
    this.fpsLog = new ArrayDeque<>();
    this.numMsToKeepTrackOf = numMsToKeepTrackOf;
  }

  /**
   * Adds one to the count of frames that have happened in this unique second
   */
  synchronized void onFrame(long currentTimestampMs) {
    fpsLog.addLast(currentTimestampMs);
    long lastTimeWeShouldKeep = currentTimestampMs - numMsToKeepTrackOf;
    while (fpsLog.getFirst() < lastTimeWeShouldKeep) {
      // Since the canonical ms ids are strictly ascending in time, just removing the first
      // key makes this a LRU-eviction cache
      fpsLog.removeFirst();
    }
  }

  synchronized int getTotalFpsCount() {
    return fpsLog.size();
  }

  synchronized double getFpsAverageBySecond() {
    if (fpsLog.size() == 0) {
      return 0;
    } else {
      double measurementPeriodSeconds = (fpsLog.getLast() - fpsLog.getFirst()) / 1000.0;
      return measurementPeriodSeconds > 0 ? fpsLog.size() / measurementPeriodSeconds : 0;
    }
  }
}
