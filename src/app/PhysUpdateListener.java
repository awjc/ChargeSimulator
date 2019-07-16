package app;

import model.SimulationModel;

public interface PhysUpdateListener {
  default void onBeforePhysUpdate(SimulationModel currentState) {

  }

  default void onAfterPhysUpdate(SimulationModel currentState) {

  }
}
