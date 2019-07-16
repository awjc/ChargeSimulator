package ui.infopanel;

import java.awt.Color;
import ui.SimulatorWindowState;

public interface InfoSubPanel {
  void onDrawUpdate(SimulatorWindowState currentState);

  void onPhysUpdate(SimulatorWindowState currentState);

  String getDisplayString();

  Color getDisplayStringColor();
}
