package v2.ui;

import v2.app.Simulation;

public class SimulatorWindowState {
  final Simulation sim;
  final Viewport viewport;

  private SimulatorWindowState(Simulation sim) {
    this.sim = sim;
    this.viewport = Viewport.create();
  }

  static SimulatorWindowState create(Simulation sim) {
    return new SimulatorWindowState(sim);
  }
}
