package v2.ui.infopanel;

import java.awt.Color;
import v2.model.SimulationModel;
import v2.ui.SimulatorWindowState;

public interface InfoSubPanel {
  void onBeforeDraw(SimulatorWindowState currentState);

  void onAfterDraw(SimulatorWindowState currentState);

  void onBeforePhys(SimulationModel currentModel);

  void onAfterPhys(SimulationModel currentModel);

  String getDisplayString();

  Color getDisplayStringColor();
}
