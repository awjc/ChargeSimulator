package v2.ui.infopanel;

import java.awt.Color;
import v2.model.SimulationModel;
import v2.ui.SimulatorWindowState;

public class SingleColorInfoSubPanel implements InfoSubPanel {

  private final Color textColor;

  SingleColorInfoSubPanel(Color textColor) {
    this.textColor = textColor;
  }

  @Override
  public void onBeforeDraw(SimulatorWindowState currentState) {
    // Intentionally left blank
  }

  @Override
  public void onAfterDraw(SimulatorWindowState currentState) {
    // Intentionally left blank
  }

  @Override
  public void onBeforePhys(SimulationModel currentModel) {
    // Intentionally left blank
  }

  @Override
  public void onAfterPhys(SimulationModel currentModel) {
    // Intentionally left blank
  }

  @Override
  public String getDisplayString() {
    return null;
  }

  @Override
  public Color getDisplayStringColor() {
    return textColor;
  }
}
