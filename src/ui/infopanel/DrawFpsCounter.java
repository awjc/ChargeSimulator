package ui.infopanel;

import java.awt.Color;
import ui.SimulatorWindowState;

public class DrawFpsCounter extends SingleColorInfoSubPanel {
  private long lastUpdateTimeMs;

  public DrawFpsCounter() {
    super(Color.WHITE);
  }


  @Override
  public void onDrawUpdate(SimulatorWindowState currentState) {

  }

  @Override
  public void onPhysUpdate(SimulatorWindowState currentState) {

  }

  int i;
  @Override
  public String getDisplayString() {
    i++;
    return String.format("Draw FPS Counter - %04d", i);
  }
}
