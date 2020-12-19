package v2.ui.infopanel;

import java.awt.Graphics;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;
import v2.model.SimulationModel;
import v2.ui.SimulatorWindowState;

public class InfoPanel extends JPanel {
  private List<InfoSubPanel> subPanels;

  private InfoPanel(List<InfoSubPanel> subPanels) {
    this.subPanels = subPanels;
  }

  public static InfoPanel create(InfoSubPanel... subPanels) {
    return new InfoPanel(Arrays.asList(subPanels));
  }

  public static InfoPanel createDefault() {
    return create(new DrawFpsCounter(), new PhysFpsCounter());
  }

  public void draw(Graphics g) {
    final int baseVerticalOffsetPx = 15;
    final int lineHeightPx = g.getFontMetrics().getAscent() + 5;
    final int horizontalOffsetPx = 5;
    for (int i = 0; i < subPanels.size(); i++) {
      final int verticalOffsetPx = baseVerticalOffsetPx + i * lineHeightPx;
      InfoSubPanel subPanel = subPanels.get(i);
      g.setColor(subPanel.getDisplayStringColor());
      g.drawString(subPanel.getDisplayString(), horizontalOffsetPx, verticalOffsetPx);
    }
  }

  public void onBeforeDraw(SimulatorWindowState currentState) {
    subPanels.forEach(p -> p.onBeforeDraw(currentState));
  }

  public void onAfterDraw(SimulatorWindowState currentState) {
    subPanels.forEach(p -> p.onAfterDraw(currentState));
  }

  public void onBeforePhys(SimulationModel currentModel) {
    subPanels.forEach(p -> p.onBeforePhys(currentModel));
  }

  public void onAfterPhys(SimulationModel currentModel) {
    subPanels.forEach(p -> p.onAfterPhys(currentModel));
  }
}
