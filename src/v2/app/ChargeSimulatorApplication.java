package v2.app;

import java.lang.reflect.InvocationTargetException;
import v2.ui.SimulatorWindow;

/**
 * Runs the simulation app
 */
public class ChargeSimulatorApplication {
  private static final String VERSION_STRING = "v2.1";

  private static final String WINDOW_TITLE =
      "Charge Simulator - by awjc - " + VERSION_STRING;


  public static void main(String[] args) throws InvocationTargetException, InterruptedException {
    SimulatorWindow window = new SimulatorWindow(WINDOW_TITLE);
    Simulation sim = Simulation.create(window);
    window.initializeAndShow(sim);
  }
}
