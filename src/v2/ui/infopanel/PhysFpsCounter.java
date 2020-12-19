package v2.ui.infopanel;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import v2.model.SimulationModel;

public class PhysFpsCounter extends SingleColorInfoSubPanel {

  private final List<FpsAverager> physFpsAveragers;

  public PhysFpsCounter() {
    super(Color.WHITE);
    physFpsAveragers = Arrays.asList(
        new FpsAverager(TimeUnit.SECONDS.toMillis(1)),
        new FpsAverager(TimeUnit.SECONDS.toMillis(5)));
  }

  @Override
  public void onAfterPhys(SimulationModel currentModel) {
    long currentTimeMs = System.currentTimeMillis();
    physFpsAveragers.forEach(a -> a.onFrame(currentTimeMs));
  }

  @Override
  public String getDisplayString() {
    StringBuilder displayString = new StringBuilder("Phys FPS Counter ~ ");

    displayString.append(physFpsAveragers.stream()
        .map(a -> String.format("%.1f", a.getFpsAverageBySecond()))
        .collect(Collectors.joining(" - ")));

    return displayString.toString();
  }
}
