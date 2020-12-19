package v2.app;

import v2.model.SimulationModel;

public interface PhysUpdateListener {
  default void onBeforePhysUpdate(SimulationModel currentState) {

  }

  default void onAfterPhysUpdate(SimulationModel currentState) {

  }
}
