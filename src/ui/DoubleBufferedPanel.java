package ui;

import static ui.UiUtils.setAntiAlias;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.function.Consumer;
import javax.swing.JPanel;

public class DoubleBufferedPanel extends JPanel {
  private Image offScrImg;
  private Graphics offG;
  private Consumer<Graphics> drawFn;

  public DoubleBufferedPanel(Consumer<Graphics> drawFn) {
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

      offG = offScrImg.getGraphics();
    }

    if (offScrImg == null) {
      return;
    }

    if (offG == null) {
      offG = offScrImg.getGraphics();
    }

    setAntiAlias(offG, true);

    offG.setColor(getBackground());
    offG.fillRect(0, 0, getWidth(), getHeight());

    offG.setColor(Color.WHITE);
    offG.drawString("TEXT", 100, 100);

    // offG.setColor(Color.WHITE);
    // FontMetrics fm = offG.getFontMetrics();
    // offG.drawString(String.format("Zoom: %.2f%%", scaleFactor * 100), 10, 10 + fm.getAscent());
    // offG.drawString(String.format("Physics Speed: %.2f", physicsSpeedFactor), 10, 10 + fm.getAscent() * 2);
    //
    // float mag = getPotentialAt(mouseX, mouseY);
    // offG.drawString(String.format("Mouse: (%d, %d), Potential: %.2f", mouseX, mouseY, mag), 10,
    //     10 + fm.getAscent() * 3);
    //
    // if (update) {
    //   xs = new int[]{getWidth() - 20, getWidth() - 20, getWidth() - 5};
    //   ys = new int[]{7, 23, 15};
    //   offG.fillPolygon(xs, ys, xs.length);
    // } else {
    //   offG.fillRect(getWidth() - 20, 7, 3, 15);
    //   offG.fillRect(getWidth() - 14, 7, 3, 15);
    // }
    //
    // if (drawPotential) {
    //   drawPotential(offG);
    // }

    drawFn.accept(offG);

    if (g != null) {
      g.drawImage(offScrImg, getX(), getY(), null);
    }
  }

}
