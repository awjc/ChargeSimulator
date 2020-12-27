package v3;

import static v3.ChargeSimulator.ClearParticles.ALL;
import static v3.ChargeSimulator.ClearParticles.HALF;
import static v3.ChargeSimulator.ClearParticles.NONE;

import java.awt.event.KeyEvent;
import v3.ChargeSimulator.ClearParticles;

public class CommandUtils {

  private ChargeSimulator sim;

  public CommandUtils(ChargeSimulator sim) {
    this.sim = sim;
  }

  private void handleClear(KeyEvent e) {
    ClearParticles clearParticles = e.isShiftDown() ? HALF : ALL;
    if (e.getKeyCode() == KeyEvent.VK_X) {
      sim.clearParticles(clearParticles, clearParticles, clearParticles);
    }
    if (e.getKeyCode() == KeyEvent.VK_R) {
      sim.clearParticles(clearParticles, NONE, NONE);
    }
    if (e.getKeyCode() == KeyEvent.VK_B) {
      sim.clearParticles(NONE, clearParticles, NONE);
    }
    if (e.getKeyCode() == KeyEvent.VK_G) {
      sim.clearParticles(NONE, NONE, clearParticles);
    }
  }

  public void processKeyEvent(KeyEvent e) {
    if (sim.maybeIgnoreFrame()) {
      return;
    }

    handleClear(e);
  }
}
