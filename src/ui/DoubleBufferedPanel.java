package ui;

import static ui.UiUtils.setAntiAlias;

import java.awt.Graphics;
import java.awt.Image;
import java.util.function.Consumer;
import javax.swing.JPanel;
import ui.infopanel.InfoPanel;

public class DoubleBufferedPanel extends JPanel {
  private Image offScrImg;
  private Graphics offG;
  private final Consumer<Graphics> drawFn;
  private final InfoPanel infoPanel;

  DoubleBufferedPanel(InfoPanel infoPanel, Consumer<Graphics> drawFn) {
    this.infoPanel = infoPanel;
    this.drawFn = drawFn;
  }

  @Override
  public void paintComponent(Graphics g) {
    //// Useless because we are pasting over the whole graphics with the offscreen image
    // super.paintComponent(g);

    if (offScrImg == null || offScrImg.getWidth(null) != getWidth()
        || offScrImg.getHeight(null) != getHeight()) {
      offScrImg = createImage(getWidth(), getHeight());
      if (offScrImg == null) {
        return;
      }

      offG = getGraphicsForImage(offScrImg);
    }

    if (offScrImg == null) {
      return;
    }

    if (offG == null) {
      offG = getGraphicsForImage(offScrImg);
    }


    offG.setColor(getBackground());
    offG.fillRect(0, 0, getWidth(), getHeight());

    drawFn.accept(offG);

    infoPanel.draw(offG);

    if (g != null) {
      g.drawImage(offScrImg, getX(), getY(), null);
    }
  }

  private static Graphics getGraphicsForImage(Image offScrImg) {
    Graphics offG = offScrImg.getGraphics();
    setAntiAlias(offG, true);
    return offG;
  }
}
