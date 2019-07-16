package app;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import ui.SimulatorWindow;

/**
 * Runs the simulation app
 */
public class ChargeSimulatorApplication {
  private static final String VERSION_STRING = "1.001";

  private static final String WINDOW_TITLE =
      "Charge Simulator - by awjc - version " + VERSION_STRING;


  public static void main(String[] args) throws InvocationTargetException, InterruptedException {
    SimulatorWindow window = new SimulatorWindow(WINDOW_TITLE);
    Simulation sim = Simulation.create(window);
    window.initializeAndShow(sim);
  }
}
