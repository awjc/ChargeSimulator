package ui.infopanel;

import java.awt.Color;

public abstract class SingleColorInfoSubPanel implements InfoSubPanel {
  private final Color textColor;

  SingleColorInfoSubPanel(Color textColor) {
    this.textColor = textColor;
  }

  @Override
  public Color getDisplayStringColor() {
    return textColor;
  }
}
