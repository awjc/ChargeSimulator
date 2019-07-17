package ui.infopanel;

import java.awt.Color;
import model.SimulationModel;
import ui.SimulatorWindowState;

public interface InfoSubPanel {
  void onBeforeDraw(SimulatorWindowState currentState);

  void onAfterDraw(SimulatorWindowState currentState);

  void onBeforePhys(SimulationModel currentModel);

  void onAfterPhys(SimulationModel currentModel);

  String getDisplayString();

  Color getDisplayStringColor();
}
