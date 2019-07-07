package app;

import ui.SimulatorWindow;

/**
 * Runs the simulation app
 */
public class ChargeSimulatorApplication {
  private static final String VERSION_STRING = "0.003";

  private static final String WINDOW_TITLE =
      "Charge Simulator - by awjc - version " + VERSION_STRING;


  public static void main(String[] args) {
    Simulation sim = new Simulation();
    SimulatorWindow window = new SimulatorWindow(sim, WINDOW_TITLE).show();
  }
}
