package ui.infopanel;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import model.SimulationModel;
import ui.SimulatorWindowState;

public class DrawFpsCounter extends SingleColorInfoSubPanel {

  private final List<FpsAverager> drawFpsAveragers;

  public DrawFpsCounter() {
    super(Color.WHITE);
    drawFpsAveragers = Arrays.asList(
        new FpsAverager(TimeUnit.SECONDS.toMillis(1)),
        new FpsAverager(TimeUnit.SECONDS.toMillis(10)),
        new FpsAverager(TimeUnit.SECONDS.toMillis(30)));
  }


  @Override
  public void onBeforeDraw(SimulatorWindowState currentState) {

  }

  @Override
  public void onAfterDraw(SimulatorWindowState currentState) {
    long currentTimeMs = System.currentTimeMillis();
    drawFpsAveragers.forEach(a -> a.onFrame(currentTimeMs));
  }

  @Override
  public void onBeforePhys(SimulationModel currentModel) {

  }

  @Override
  public void onAfterPhys(SimulationModel currentModel) {

  }

  @Override
  public String getDisplayString() {
    StringBuilder displayString = new StringBuilder("Draw FPS Counter - ");

    displayString.append(drawFpsAveragers.stream()
        .map(a -> String.format("%.1f [%04d]", a.getFpsAverageBySecond(), a.getTotalFpsCount()))
        .collect(
            Collectors.joining(" ; ")));

    return displayString.toString();
  }
}
