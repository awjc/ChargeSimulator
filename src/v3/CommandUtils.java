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

  public void addRing() {

  }

  public void processKeyEvent(KeyEvent e) {

    handleClear(e);

    if (e.getKeyCode() == KeyEvent.VK_S && e.isControlDown()) {
      sim.saveState("latest.snapshot");
    }

    if (e.getKeyCode() == KeyEvent.VK_L && e.isControlDown()) {
      sim.loadState(e.isShiftDown() ? "autosave.snapshot" : "latest.snapshot");
    }
  }
}
