package v1;

import static v1.ChargeSimulator.ClearParticles.ALL;
import static v1.ChargeSimulator.ClearParticles.HALF;
import static v1.ChargeSimulator.ClearParticles.NONE;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import v1.ChargeSimulator.ClearParticles;

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
