package v3;

import static v3.ChargeSimulator.ClearParticlesOption.ALL;
import static v3.ChargeSimulator.ClearParticlesOption.HALF;
import static v3.ChargeSimulator.ClearParticlesOption.NONE;

import java.awt.event.KeyEvent;
import v3.ChargeSimulator.ClearParticlesOption;

public class CommandUtils {

  private ChargeSimulator sim;

  public CommandUtils(ChargeSimulator sim) {
    this.sim = sim;
  }

  private void handleClear(KeyEvent e) {
    ClearParticlesOption option = e.isShiftDown() ? HALF : ALL;
    if (e.getKeyCode() == KeyEvent.VK_X) {
      sim.clearParticles(option, option, option);
    }
    if (e.getKeyCode() == KeyEvent.VK_R) {
      sim.clearParticles(option, NONE, NONE);
    }
    if (e.getKeyCode() == KeyEvent.VK_B) {
      sim.clearParticles(NONE, option, NONE);
    }
    if (e.getKeyCode() == KeyEvent.VK_G) {
      sim.clearParticles(NONE, NONE, option);
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
