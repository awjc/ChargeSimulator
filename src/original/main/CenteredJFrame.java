package original.main;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

class CenteredJFrame extends JFrame {

  CenteredJFrame(String title, int width, int height) {
    super(title);
    setSize(width, height);
    Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation((scrSize.width - width) / 2, (scrSize.height - height) / 2);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}
