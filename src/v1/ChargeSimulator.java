// package v1;

// import java.awt.Color;
// import java.awt.Container;
// import java.awt.Cursor;
// import java.awt.Dimension;
// import java.awt.FontMetrics;
// import java.awt.Graphics;
// import java.awt.Graphics2D;
// import java.awt.Image;
// import java.awt.RenderingHints;
// import java.awt.Toolkit;
// import java.awt.event.KeyAdapter;
// import java.awt.event.KeyEvent;
// import java.awt.event.MouseAdapter;
// import java.awt.event.MouseEvent;
// import java.awt.event.MouseListener;
// import java.awt.event.MouseMotionAdapter;
// import java.awt.event.MouseWheelEvent;
// import java.awt.event.MouseWheelListener;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Random;
// import java.util.Stack;
// import java.util.Timer;
// import java.util.TimerTask;
// import java.util.concurrent.ExecutionException;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.Future;
// import java.util.concurrent.TimeUnit;
// import java.util.concurrent.TimeoutException;

// import javax.swing.JFrame;
// import javax.swing.JPanel;
// import javax.swing.SwingUtilities;

// public class ChargeSimulator extends JPanel {

// private static final String VERSION_STRING = "ChargeSimulator - by awjc -
// v1.5";

// public static double chargeSize = 1.0;

// private final List<Double> chargeSizes = Arrays.asList(1.0, 0.333, 0.1,
// 0.3333, 1.001, 3.33, 10.0, 3.333);


// private List<Runnable> updatesToRunNext = new ArrayList<>();

// private static final long serialVersionUID = 1L;

// /**
// * The geometric factor for each step of the speed scale factor
// */
// private static final double SPEED_SCALE_FACTOR_ADJUSTMENT_FACTOR = 1.1;

// /**
// * Relative multiplier for physics update speed
// */
// private double physicsSpeedFactor = 1.0;

// public static void main(String[] args) {
// SwingUtilities.invokeLater(new Runnable() {
// @Override
// public void run() {
// new ChargeSimulator().run();
// }
// });
// }

// private static final int REPEAT_DELAY = 250;

// private final int FRAME_WIDTH;
// private final int FRAME_HEIGHT;
// private static final double FRAME_WIDTH_SCREEN_PERCENTAGE = 0.85;
// private static final double FRAME_HEIGHT_SCREEN_PERCENTAGE = 0.9;
// {
// Dimension scrDim = Toolkit.getDefaultToolkit().getScreenSize();
// FRAME_WIDTH = (int) (scrDim.width * FRAME_WIDTH_SCREEN_PERCENTAGE);
// FRAME_HEIGHT = (int) (scrDim.height * FRAME_HEIGHT_SCREEN_PERCENTAGE);
// }

// private static final double drawingFPS = 25;
// // private static final int updateFPS = 30;

// private boolean update = true;
// private long lastUpdateTime = -1;

// private Image offScrImg;
// private Graphics offG;

// private JFrame frame;

// private final List<Charge> negCharges;
// private final List<Charge> posCharges;
// private final List<TestCharge> testCharges;

// private int prevButton = 0;
// private int prevX = 0;
// private int prevY = 0;
// private int mouseX = 0;
// private int mouseY = 0;

// private int moveX = 0;
// private int moveY = 0;
// private Dimension centerPos = null;
// private Dimension movePivot = null;

// private int drawingFreq = 1;

// private double scaleFactor = 1;
// private static final double MIN_SCALING_FACTOR = 0.001;
// private static final double MAX_SCALING_FACTOR = 100;

// private volatile boolean repeating = false;
// private boolean moving = true;
// private boolean drawing = true;
// private boolean drawPotential = false;

// private Timer repeatTimer = new Timer();

// private List<Charge> mostRecent = new ArrayList<>();
// private Stack<List<Charge>> mostRecentStack = new Stack<>();
// private Stack<List<Charge>> mostRecentUndone = new Stack<>();

// private int[] xs;
// private int[] ys;

// private float[][] arr;
// private int pdx = 8;
// private int pdy = 8;

// private ExecutorService executor = Executors
// .newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
// ;

// public ChargeSimulator() {
// frame = new CenteredJFrame(VERSION_STRING, FRAME_WIDTH, FRAME_HEIGHT);
// frame.setResizable(false);
// frame.getContentPane().add(this);
// // frame.setUndecorated(true);
// // frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

// negCharges = new ArrayList<Charge>();
// posCharges = new ArrayList<Charge>();
// testCharges = new ArrayList<TestCharge>();
// Random r = new Random();
// for (int i = 0; i < 100; i++) {
// posCharges.add(new Charge(r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2,
// r.nextInt(FRAME_HEIGHT) - FRAME_HEIGHT / 2, 50 + r.nextInt(100)));
// negCharges.add(new Charge(r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2,
// r.nextInt(FRAME_HEIGHT) - FRAME_HEIGHT / 2, -200 + r.nextInt(100)));

// if (i % 2 == 0) {
// testCharges
// .add(new TestCharge(r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2,
// r.nextInt(FRAME_HEIGHT)
// - FRAME_HEIGHT / 2, true));
// }
// }

// setCursor(new Cursor(Cursor.HAND_CURSOR));
// MouseListener l = new MouseAdapter() {
// @Override
// public void mousePressed(MouseEvent e) {
// if (moving) {
// moveX = e.getX();
// moveY = e.getY();
// movePivot = new Dimension(centerPos.width, centerPos.height);
// setCursor(new Cursor(Cursor.MOVE_CURSOR));
// return;
// }

// prevButton = e.getButton();
// prevX = (int) ((e.getX() - centerPos.width) / scaleFactor);
// prevY = (int) ((e.getY() - centerPos.height) / scaleFactor);

// Charge c = null;

// double x = (e.getX() - centerPos.width) / scaleFactor;
// double y = (e.getY() - centerPos.height) / scaleFactor;
// System.out.println(">>> " + String.format("(%d, %d)", e.getX(), e.getY()));
// System.out.println(String.format("Placing charge at (%.0f, %.0f)", x, y));
// if (e.getButton() == MouseEvent.BUTTON1) {
// c = new Charge(x, y, 100);
// } else if (e.getButton() == MouseEvent.BUTTON3) {
// c = new Charge(x, y, -100);
// } else {
// synchronized (testCharges) {
// testCharges.add(new TestCharge(x, y, true));
// }
// }

// if (c != null) {
// if (c.getCharge() < 0) {
// synchronized (negCharges) {
// negCharges.add(c);
// mostRecent.add(c);
// if (!mostRecentUndone.isEmpty()) {
// mostRecentUndone.clear();
// }
// }
// } else {
// synchronized (posCharges) {
// posCharges.add(c);
// mostRecent.add(c);
// if (!mostRecentUndone.isEmpty()) {
// mostRecentUndone.clear();
// }
// }
// }
// }
// }

// @Override
// public void mouseReleased(MouseEvent e) {
// if (moving) {
// setCursor(new Cursor(Cursor.HAND_CURSOR));
// return;
// }

// mostRecentStack.push(mostRecent);
// mostRecent = new ArrayList<>();

// repeating = false;
// }
// };
// frame.getContentPane().addMouseListener(l);
// frame.getContentPane().addMouseMotionListener(new MouseMotionAdapter() {
// @Override
// public void mouseMoved(MouseEvent e) {
// mouseX = e.getX();
// mouseY = e.getY();
// }

// @Override
// public void mouseDragged(MouseEvent e) {
// if (moving) {
// int dx = e.getX() - moveX;
// int dy = e.getY() - moveY;
// centerPos.width = movePivot.width + dx;
// centerPos.height = movePivot.height + dy;
// return;
// }

// Charge c = null;
// if (prevButton == MouseEvent.BUTTON1) {
// c = new Charge((e.getX() - centerPos.width) / scaleFactor, (e.getY() -
// centerPos.height)
// / scaleFactor, 100);
// } else if (prevButton == MouseEvent.BUTTON3) {
// c = new Charge((e.getX() - centerPos.width) / scaleFactor, (e.getY() -
// centerPos.height)
// / scaleFactor, -100);
// } else {
// synchronized (testCharges) {
// testCharges.add(new TestCharge((e.getX() - centerPos.width) / scaleFactor,
// (e.getY() - centerPos.height) / scaleFactor, true));
// }
// }

// if (c != null) {
// if (c.getCharge() < 0) {
// synchronized (negCharges) {
// negCharges.add(c);
// mostRecent.add(c);
// if (!mostRecentUndone.isEmpty()) {
// mostRecentUndone.clear();
// }
// }
// } else {
// synchronized (posCharges) {
// posCharges.add(c);
// mostRecent.add(c);
// if (!mostRecentUndone.isEmpty()) {
// mostRecentUndone.clear();
// }
// }
// }
// }
// }
// });

// frame.getContentPane().addMouseWheelListener(new MouseWheelListener() {
// private static final double SCALING_FACTOR_PER_NOTCH = 1.05;

// @Override
// public void mouseWheelMoved(MouseWheelEvent e) {
// if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
// double oldScaleFactor = scaleFactor;
// int notches = e.getWheelRotation();
// int scrollAmount = e.getScrollAmount();
// // double prevScale = scaleFactor;
// if (notches < 0) {
// for (int i = 0; i < scrollAmount; i++) {
// double newScaleFactor = scaleFactor * SCALING_FACTOR_PER_NOTCH;
// if (newScaleFactor < MAX_SCALING_FACTOR && newScaleFactor >
// MIN_SCALING_FACTOR) {
// scaleFactor = newScaleFactor;
// }
// }
// } else {
// for (int i = 0; i < scrollAmount; i++) {
// double newScaleFactor = scaleFactor / SCALING_FACTOR_PER_NOTCH;
// if (newScaleFactor < MAX_SCALING_FACTOR && newScaleFactor >
// MIN_SCALING_FACTOR) {
// scaleFactor = newScaleFactor;
// }
// }
// }

// if (oldScaleFactor > scaleFactor) {
// double change = scaleFactor / oldScaleFactor - 1;
// // double change = 1 - scaleFactor / oldScaleFactor;
// int xx = (FRAME_WIDTH / 2 - centerPos.width);
// int yy = (FRAME_HEIGHT / 2 - centerPos.height);
// // centerPos = new Dimension((int)(centerPos.width * change),
// (int)(centerPos.height * change));
// centerPos = new Dimension((int) (centerPos.width - xx * change),
// (int) (centerPos.height - yy * change));
// } else {
// double change = scaleFactor / oldScaleFactor - 1;
// // double change = 1 - scaleFactor / oldScaleFactor;
// int xx = (FRAME_WIDTH / 2 - centerPos.width);
// int yy = (FRAME_HEIGHT / 2 - centerPos.height);
// // centerPos = new Dimension((int)(centerPos.width * change),
// (int)(centerPos.height * change));
// centerPos = new Dimension((int) (centerPos.width - xx * change),
// (int) (centerPos.height - yy * change));
// }
// // double dScale = scaleFactor - prevScale;
// // int mx = (e.getX() - centerPos.width);
// // int my = (e.getY() - centerPos.height);
// //
// // centerPos.width -= mx*dScale;
// // centerPos.height -= my*dScale;
// }
// }
// });

// frame.addKeyListener(new KeyAdapter() {
// @Override
// public void keyPressed(KeyEvent e) {
// if (ignoreNext) {
// ignoreNext = false;
// } else {
// if (e.getKeyCode() == KeyEvent.VK_X) {
// synchronized (testCharges) {
// testCharges.clear();
// }

// synchronized (negCharges) {
// negCharges.clear();
// }

// synchronized (posCharges) {
// posCharges.clear();
// }
// }

// if (e.getKeyCode() == KeyEvent.VK_G) {
// if (e.isShiftDown()) {
// updatesToRunNext.add(() -> {
// List<TestCharge> newTestCharges = new ArrayList<>();

// synchronized (testCharges) {
// for (int i = 0; i < testCharges.size(); i += 2) {
// newTestCharges.add(testCharges.get(i));
// }
// }
// synchronized (testCharges) {
// testCharges.clear();
// testCharges.addAll(newTestCharges);
// }
// });
// } else {
// synchronized (testCharges) {
// testCharges.clear();
// }
// }
// }

// if (e.getKeyCode() == KeyEvent.VK_D) {
// drawing = !drawing;
// }

// if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
// System.exit(0);
// }

// if (e.getKeyCode() == KeyEvent.VK_B) {
// synchronized (posCharges) {
// posCharges.clear();
// }
// }

// if (e.getKeyCode() == KeyEvent.VK_R) {
// synchronized (negCharges) {
// negCharges.clear();
// }
// }

// if (e.getKeyCode() == KeyEvent.VK_HOME) {
// scaleFactor = 1;
// centerPos = new Dimension(frame.getWidth() / 2, frame.getHeight() / 2);
// radialCount = 100;
// }

// if (e.getKeyCode() == KeyEvent.VK_O) {
// chargeSize = chargeSizes.get((chargeSizes.indexOf(chargeSize) + 1) %
// chargeSizes.size());
// System.out.println(chargeSize);
// }

// if (e.getKeyCode() == KeyEvent.VK_1) {
// updatesToRunNext.add(() -> {
// Random r = new Random();
// for (int i = 0; i < 50; i++) {
// synchronized (testCharges) {
// testCharges
// .add(new TestCharge((r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2) / scaleFactor,
// (r
// .nextInt(FRAME_HEIGHT) - FRAME_HEIGHT / 2) / scaleFactor, true));
// }
// }
// });
// }

// if (e.getKeyCode() == KeyEvent.VK_2) {
// updatesToRunNext.add(() -> {
// int nCols = 50;
// int nRows = 30;
// for (int i = 0; i < FRAME_WIDTH; i += FRAME_WIDTH / nCols) {
// for (int j = 0; j < FRAME_HEIGHT; j += FRAME_HEIGHT / nRows) {
// synchronized (posCharges) {
// posCharges
// .add(new Charge((i - FRAME_WIDTH / 2) / scaleFactor, (j - FRAME_HEIGHT / 2)
// / scaleFactor, 400));
// }
// }
// }
// });
// }

// if (e.getKeyCode() == KeyEvent.VK_3) {
// updatesToRunNext.add(() -> {
// Random r = new Random();
// for (int i = 0; i < 1000; i++) {
// synchronized (testCharges) {
// testCharges
// .add(new TestCharge((r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2) / scaleFactor,
// (r
// .nextInt(FRAME_HEIGHT) - FRAME_HEIGHT / 2) / scaleFactor, true));
// }
// }

// for (int i = 0; i < 1000; i++) {
// synchronized (posCharges) {
// posCharges
// .add(new Charge((r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2) / scaleFactor, (r
// .nextInt(FRAME_HEIGHT) - FRAME_HEIGHT / 2) / scaleFactor, 200));
// }
// }

// for (int i = 0; i < 1000; i++) {
// synchronized (negCharges) {
// negCharges
// .add(new Charge((r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2) / scaleFactor, (r
// .nextInt(FRAME_HEIGHT) - FRAME_HEIGHT / 2) / scaleFactor, -200));
// }
// }
// });
// }

// if (e.getKeyCode() == KeyEvent.VK_4) {
// updatesToRunNext.add(() -> {
// double radius = 100 / scaleFactor;
// int nCharges = radialCount;
// List<Charge> charges = new ArrayList<>();
// // double xx = centerPos.getWidth();
// // double yy = centerPos.getHeight();
// int viewportCenterX = (int)((FRAME_WIDTH / 2 - centerPos.width) /
// scaleFactor);
// int viewportCenterY = (int)((FRAME_HEIGHT / 2 - centerPos.height) /
// scaleFactor);

// for (int i = 0; i < nCharges; i++) {
// double theta = i * Math.PI * 2 / nCharges;
// charges.add(new Charge(
// viewportCenterX + radius * Math.cos(theta),
// viewportCenterY + radius * Math.sin(theta),
// e.isShiftDown() ? 100 : -100));
// }
// if (e.isShiftDown()) {
// synchronized (posCharges) {
// posCharges.addAll(charges);
// }
// } else {
// synchronized (negCharges) {
// negCharges.addAll(charges);
// }
// }
// mostRecentStack.push(charges);
// });
// }

// if (e.getKeyCode() == KeyEvent.VK_5) {
// int viewportCenterX = (int)((FRAME_WIDTH / 2 - centerPos.width) /
// scaleFactor);
// int viewportCenterY = (int)((FRAME_HEIGHT / 2 - centerPos.height) /
// scaleFactor);

// updatesToRunNext.add(() -> {
// double dx = e.isShiftDown() ? 25 : e.isControlDown() ? 15 : e.isAltDown() ?
// 100 : 50;
// double dy = e.isShiftDown() ? 25 : e.isControlDown() ? 15 : e.isAltDown() ?
// 100 : 50;
// for (int i = -FRAME_WIDTH/2; i < FRAME_WIDTH/2; i += dx) {
// for (int j = -FRAME_HEIGHT/2; j < FRAME_HEIGHT/2; j += dy) {
// testCharges.add(new TestCharge(
// viewportCenterX + i / scaleFactor,
// viewportCenterY + j / scaleFactor,
// true));
// }
// }
// });
// }

// if (e.getKeyCode() == KeyEvent.VK_6) {
// int viewportCenterX = (int)((FRAME_WIDTH / 2 - centerPos.width) /
// scaleFactor);
// int viewportCenterY = (int)((FRAME_HEIGHT / 2 - centerPos.height) /
// scaleFactor);

// updatesToRunNext.add(() -> {
// double radius = 100 / scaleFactor;
// int nCharges = radialCount;
// List<TestCharge> charges = new ArrayList<>();
// for (int i = 0; i < nCharges; i++) {
// double theta = i * Math.PI * 2 / nCharges;
// charges.add(new TestCharge(
// viewportCenterX + radius * Math.cos(theta),
// viewportCenterY + radius * Math.sin(theta),
// true));
// }
// synchronized (testCharges) {
// testCharges.addAll(charges);
// }
// });
// // mostRecentStack.push(charges);
// }

// if (e.getKeyCode() == KeyEvent.VK_7) {
// System.out.println("Frame size: " + FRAME_WIDTH + " x " + FRAME_HEIGHT + " @
// " + String.format("%.4f", scaleFactor));
// System.out.println(String.format("center: (%d, %d)", centerPos.width,
// centerPos.height));
// }

// if (e.getKeyCode() == KeyEvent.VK_SPACE) {
// update = !update;
// }

// if (e.getKeyCode() == KeyEvent.VK_A) {
// if (!repeating) {
// repeatTimer.schedule(new TimerTask() {
// @Override
// public void run() {
// if (repeating) {
// synchronized (testCharges) {
// TestCharge tc = new TestCharge(prevX, prevY, true);
// tc.first = testCharges.isEmpty();
// testCharges.add(tc);
// }
// }
// }
// }, 0, REPEAT_DELAY);
// } else {
// repeatTimer.cancel();
// repeatTimer = new Timer();
// }

// repeating = !repeating;
// }

// if (e.getKeyCode() == KeyEvent.VK_S) {
// ignoreNext = true;
// }

// if (e.getKeyCode() == KeyEvent.VK_M) {
// if (!moving) {
// setCursor(new Cursor(Cursor.HAND_CURSOR));
// moving = true;
// } else {
// setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
// moving = false;
// }
// }

// if (e.getKeyCode() == KeyEvent.VK_C) {
// // consolidateCharges();
// }

// if (e.getKeyCode() == KeyEvent.VK_T) {
// updatesToRunNext.add(() -> {
// List<Charge> newPosCharges = new ArrayList<Charge>();
// List<Charge> newNegCharges = new ArrayList<Charge>();

// synchronized (negCharges) {
// for (Charge c : negCharges) {
// c.setCharge(-c.getCharge());
// newPosCharges.add(c);
// }
// }
// synchronized (posCharges) {
// for (Charge c : posCharges) {
// c.setCharge(-c.getCharge());
// newNegCharges.add(c);
// }
// }
// synchronized (posCharges) {
// posCharges.clear();
// posCharges.addAll(newPosCharges);
// }
// synchronized (negCharges) {
// negCharges.clear();
// negCharges.addAll(newNegCharges);
// }
// });
// }

// if (e.getKeyCode() == KeyEvent.VK_U) {
// Timer t = new Timer();
// t.schedule(new TimerTask() {
// @Override
// public void run() {
// if (update) {
// synchronized (posCharges) {
// rotateRad(posCharges, Math.toRadians(1.0));
// }
// }

// if (update) {
// synchronized (negCharges) {
// rotateRad(negCharges, Math.toRadians(-1.0));
// }
// }
// }
// }, 0, 1000 / 60);
// }

// if (e.getKeyCode() == KeyEvent.VK_Z) {
// if (!mostRecentStack.isEmpty()) {
// List<Charge> c = mostRecentStack.pop();
// mostRecentUndone.push(c);

// synchronized (negCharges) {
// negCharges.removeAll(c);
// }
// synchronized (posCharges) {
// posCharges.removeAll(c);
// }
// }
// }

// if (e.getKeyCode() == KeyEvent.VK_Y) {
// if (!mostRecentUndone.isEmpty()) {
// List<Charge> c = mostRecentUndone.pop();
// mostRecentStack.push(c);
// for (Charge cc : c) {
// if (cc.getCharge() > 0) {
// synchronized (posCharges) {
// posCharges.add(cc);
// }
// } else {
// synchronized (negCharges) {
// negCharges.add(cc);
// }
// }
// }
// }
// }

// if (e.getKeyCode() == KeyEvent.VK_EQUALS) {
// if (drawingFreq > 1) {
// drawingFreq -= 1;
// }
// }

// if (e.getKeyCode() == KeyEvent.VK_MINUS) {
// if (drawingFreq < 10) {
// drawingFreq += 1;
// }
// }

// if (e.getKeyCode() == KeyEvent.VK_P) {
// drawPotential = !drawPotential;
// }

// if (e.getKeyCode() == KeyEvent.VK_OPEN_BRACKET) {
// if (e.isShiftDown()) {
// if (pdx > 1) {
// pdx /= 2;
// }
// if (pdy > 1) {
// pdy /= 2;
// }

// arr = new float[getWidth() / pdx + 1][getHeight() / pdy + 1];
// } else {
// physicsSpeedFactor /= SPEED_SCALE_FACTOR_ADJUSTMENT_FACTOR;
// }
// }

// if (e.getKeyCode() == KeyEvent.VK_CLOSE_BRACKET) {
// if (e.isShiftDown()) {
// if (pdx < 128) {
// pdx *= 2;
// }
// if (pdy < 128) {
// pdy *= 2;
// }

// arr = new float[getWidth() / pdx + 1][getHeight() / pdy + 1];
// } else {
// physicsSpeedFactor *= SPEED_SCALE_FACTOR_ADJUSTMENT_FACTOR;
// }
// }

// if (e.getKeyCode() == KeyEvent.VK_COMMA) {
// double factor = e.isShiftDown()
// ? Math.pow(SPEED_SCALE_FACTOR_ADJUSTMENT_FACTOR, 5)
// : SPEED_SCALE_FACTOR_ADJUSTMENT_FACTOR;
// radialCount = (int) (radialCount / factor);
// }

// if (e.getKeyCode() == KeyEvent.VK_PERIOD) {
// double factor = e.isShiftDown()
// ? Math.pow(SPEED_SCALE_FACTOR_ADJUSTMENT_FACTOR, 5)
// : SPEED_SCALE_FACTOR_ADJUSTMENT_FACTOR;
// radialCount = (int) (radialCount * factor);
// }

// if (e.getKeyCode() == KeyEvent.VK_SLASH) {
// radialCount = 100;
// }

// if (e.getKeyCode() == KeyEvent.VK_BACK_SLASH) {
// physicsSpeedFactor = 1.0;
// }
// }
// }
// });

// setBackground(new Color(0, 10, 20));
// }

// private void rotateRad(List<Charge> charges, double rad) {
// for (int i = 0; i < charges.size(); i++) {
// Charge c = charges.get(i);
// double newX = c.getX() * Math.cos(rad) - c.getY() * Math.sin(rad);
// double newY = c.getX() * Math.sin(rad) + c.getY() * Math.cos(rad);
// c.changePosition(newX, newY);
// }
// }

// private int radialCount = 100;

// private boolean ignoreNext = false;

// // private void consolidateCharges(){
// // List<Charge> newCharges = new ArrayList<>();
// // newCharges.addAll(charges);
// // double DISTANCE_THRESHOLD = 1;
// // for(int i = 0; i < newCharges.size(); i++){
// // for(int j = 0; j < newCharges.size(); j++){
// // if(i != j && newCharges.get(i).distanceTo(newCharges.get(j)) <
// DISTANCE_THRESHOLD){
// // if(i < j){
// // Charge c = newCharges.get(j);
// // newCharges.get(i).setCharge(newCharges.get(i).getCharge() +
// c.getCharge());
// // newCharges.remove(j);
// // j--;
// // } else{
// // Charge c = newCharges.get(i);
// // newCharges.get(j).setCharge(newCharges.get(j).getCharge() +
// c.getCharge());
// // newCharges.remove(i);
// // i--;
// // }
// // }
// // }
// // }
// //
// // List<Charge> toRemove = new ArrayList<>();
// // for(Charge c : newCharges){
// // if(Math.abs(c.getCharge()) <= 0.00001){
// // toRemove.add(c);
// // }
// // }
// //
// // newCharges.removeAll(toRemove);
// // charges.clear();
// // charges.addAll(newCharges);
// //
// // // for(Charge c : charges){
// // // if(!newCharges.contains(c)){
// // // newCharges.add(c);go
// //
// // // }
// // // }
// //
// // // System.out.println(charges.size() + " : " + charges);
// // }

// public void run() {
// frame.setVisible(true);

// Timer drawTimer = new Timer();
// drawTimer.schedule(new TimerTask() {
// private int i = 0;

// @Override
// public void run() {
// if (drawing) {
// Container c = frame.getContentPane();
// if (c != null && c.getGraphics() != null && ++i % drawingFreq == 0) {
// paintComponent(c.getGraphics());
// i = 0;
// }
// }

// if (update) {
// update();
// } else {
// lastUpdateTime = System.currentTimeMillis();
// }
// // repaint();
// }
// }, 0, (int) (1000 / drawingFPS));

// // Timer updateTimer = new Timer();
// // updateTimer.schedule(new TimerTask(){
// // @Override
// // public void run(){
// // if(update)
// // update();
// // else
// // lastUpdateTime = System.currentTimeMillis();
// // }
// // }, 0, 1000 / updateFPS);
// }

// int linesDone = 0;

// int nextLineY(FontMetrics fm) {
// return (10 + fm.getAscent() * (linesDone++));
// }

// @Override
// public void paintComponent(Graphics g) {
// if (centerPos == null) {
// // System.out.println(getHeight() + " : " + frame.getHeight() +
// // " : " + frame.getContentPane().getHeight());
// centerPos = new Dimension(frame.getWidth() / 2, frame.getHeight() / 2);
// }

// // System.out.println("DRAW " + i++);
// if (offScrImg == null || offScrImg.getWidth(null) != getWidth()
// || offScrImg.getHeight(null) != getHeight()) {
// offScrImg = createImage(getWidth(), getHeight());
// if (offScrImg == null) {
// return;
// }

// offG = offScrImg.getGraphics();
// }

// if (offScrImg == null) {
// return;
// }

// if (offG == null) {
// offG = offScrImg.getGraphics();
// }

// setAntiAlias(offG, true);

// offG.setColor(getBackground());
// offG.fillRect(0, 0, getWidth(), getHeight());

// offG.setColor(Color.RED);
// doRedBalls();

// offG.setColor(Color.BLUE);
// doBlueBalls();

// offG.setColor(Color.GREEN);
// doGreenBalls();

// offG.setColor(Color.WHITE);

// FontMetrics fm = offG.getFontMetrics();
// int xOffset = 10;
// offG.drawString(String.format("Zoom: %.2f%%", scaleFactor * 100), xOffset,
// nextLineY(fm));
// offG.drawString(String.format("Physics Speed: %.2f", physicsSpeedFactor),
// xOffset, nextLineY(fm));

// offG.drawString(String.format("Radial Count: %d", radialCount), xOffset,
// nextLineY(fm));

// float mag = getPotentialAt(mouseX, mouseY);
// offG.drawString(String.format("Mouse: (%d, %d), Potential: %.2f", mouseX,
// mouseY, mag),
// xOffset, nextLineY(fm));
// if (update) {
// xs = new int[]{getWidth() - 20, getWidth() - 20, getWidth() - 5};
// ys = new int[]{7, 23, 15};
// offG.fillPolygon(xs, ys, xs.length);
// } else {
// offG.fillRect(getWidth() - 20, 7, 3, 15);
// offG.fillRect(getWidth() - 14, 7, 3, 15);
// }

// if (drawPotential) {
// drawPotential(offG);
// }

// if (g != null) {
// g.drawImage(offScrImg, getX(), getY(), null);
// }
// }

// private void drawPotential(Graphics g) {
// int w = getWidth();
// int h = getHeight();

// if (arr == null) {
// arr = new float[w / pdx + 1][h / pdy + 1];
// }

// try {
// double magMax = -1;
// for (int i = 0, ii = 0; i < w; i += pdx, ii++) {
// for (int j = 0, jj = 0; j < h; j += pdy, jj++) {
// float mag = getPotentialAt(i, j);
// arr[ii][jj] = mag;
// if (mag > magMax) {
// magMax = mag;
// }
// }
// }

// for (int i = 0, ii = 0; i < w; i += pdx, ii++) {
// for (int j = 0, jj = 0; j < h; j += pdy, jj++) {
// float col = (float) (arr[ii][jj] / magMax);
// if (col > 1.0f) {
// col = 1.0f;
// }
// if (col < 0.0f) {
// col = 0.0f;
// }

// g.setColor(new Color(col, col, col, 0.7f));
// g.fillRect(i - pdx / 2, j - pdy / 2, pdx, pdy);
// }
// }
// } catch (Exception e) {
// return;
// }
// }

// private float getPotentialAt(int xPos, int yPos) {
// float potential = 0;
// synchronized (posCharges) {
// for (Charge c : posCharges) {
// float pot = (float) c.getPotentialAt((xPos - centerPos.width) / scaleFactor,
// (yPos - centerPos.height) / scaleFactor);
// // double x = (xPos - centerPos.width)/scaleFactor - c.getX();
// // double y = (yPos - centerPos.height)/scaleFactor - c.getY();

// potential += pot;
// }
// }

// synchronized (negCharges) {
// for (Charge c : negCharges) {
// float pot = (float) c.getPotentialAt((xPos - centerPos.width) / scaleFactor,
// (yPos - centerPos.height) / scaleFactor);
// // double x = (xPos - centerPos.width)/scaleFactor - c.getX();
// // double y = (yPos - centerPos.height)/scaleFactor - c.getY();

// potential -= pot;
// }
// }

// return potential;
// }

// private void doBlueBalls() {
// final int NTASKS = 4;
// int nCharges = posCharges.size() / NTASKS;
// List<Runnable> work = new ArrayList<>();
// List<Future<?>> futures = new ArrayList<>();
// for (int i = 0; i < NTASKS; i++) {
// final int startBalls = i * nCharges;
// final int endBalls = i < NTASKS - 1 ? (i + 1) * nCharges - 1 :
// posCharges.size() - 1;
// work.add(new Runnable() {
// @Override
// public void run() {
// for (int i = startBalls; i <= endBalls; i++) {
// if (i >= posCharges.size()) {
// return;
// }

// Charge c = posCharges.get(i);
// if (c != null) {
// c.draw(offG, scaleFactor, centerPos);
// }
// }
// }
// });
// }

// for (Runnable r : work) {
// futures.add(executor.submit(r));
// }

// for (Future<?> f : futures) {
// try {
// f.get(100, TimeUnit.SECONDS);
// } catch (InterruptedException | ExecutionException | TimeoutException e) {
// e.printStackTrace();
// }
// }
// }

// private void doRedBalls() {
// final int NTASKS = 4;
// int nCharges = negCharges.size() / NTASKS;
// List<Runnable> work = new ArrayList<>();
// List<Future<?>> futures = new ArrayList<>();
// for (int i = 0; i < NTASKS; i++) {
// final int startBalls = i * nCharges;
// final int endBalls = i < NTASKS - 1 ? (i + 1) * nCharges - 1 :
// negCharges.size() - 1;
// work.add(new Runnable() {
// @Override
// public void run() {
// for (int i = startBalls; i <= endBalls; i++) {
// if (i >= negCharges.size()) {
// return;
// }

// Charge c = negCharges.get(i);
// if (c != null) {
// c.draw(offG, scaleFactor, centerPos);
// }
// }
// }
// });
// }

// for (Runnable r : work) {
// futures.add(executor.submit(r));
// }

// for (Future<?> f : futures) {
// try {
// f.get(100, TimeUnit.SECONDS);
// } catch (InterruptedException | ExecutionException | TimeoutException e) {
// e.printStackTrace();
// }
// }
// }

// private void doGreenBalls() {
// final int NTASKS = 1;

// List<Future<?>> futures = new ArrayList<>();
// List<Runnable> work = new ArrayList<>();
// int nCharges = testCharges.size() / NTASKS;
// for (int i = 0; i < NTASKS; i++) {
// final int startBalls = i * nCharges;
// final int endBalls = i < NTASKS - 1 ? (i + 1) * nCharges - 1 :
// testCharges.size() - 1;
// work.add(new Runnable() {
// @Override
// public void run() {
// for (int i = startBalls; i <= endBalls; i++) {
// if (i >= testCharges.size()) {
// return;
// }

// TestCharge tc = testCharges.get(i);
// if (tc != null) {
// tc.draw(offG, scaleFactor, centerPos);
// }
// }
// }
// });
// }

// for (Runnable r : work) {
// futures.add(executor.submit(r));
// }

// for (Future<?> f : futures) {
// try {
// f.get(100, TimeUnit.SECONDS);
// } catch (InterruptedException | ExecutionException | TimeoutException e) {
// e.printStackTrace();
// }
// }
// }

// private void update() {
// if (lastUpdateTime < 0) {
// lastUpdateTime = System.currentTimeMillis();
// return;
// }

// updatesToRunNext.forEach(Runnable::run);
// updatesToRunNext.clear();

// List<Runnable> work = new ArrayList<>();
// final int NTASKS = 4;
// int nCharges = testCharges.size() / NTASKS;
// // if(posChargesArr == null){
// // posChargesArr = posCharges.toArray(new Charge[posCharges.size()]);
// // }
// // if(negChargesArr == null){
// // negChargesArr = negCharges.toArray(new Charge[negCharges.size()]);
// // }
// for (int i = 0; i < NTASKS; i++) {
// final int startBalls = i * nCharges;
// final int endBalls = i < NTASKS - 1 ? (i + 1) * nCharges - 1 :
// testCharges.size() - 1;
// work.add(new Runnable() {
// @Override
// public void run() {
// for (int i = startBalls; i <= endBalls; i++) {
// if (i >= testCharges.size()) {
// return;
// }

// // for(int j=0; j < 10; j++)
// testCharges.get(i).update(posCharges, negCharges, physicsSpeedFactor /
// drawingFPS);
// }
// }
// });
// }

// List<Future<?>> futures = new ArrayList<>();
// for (Runnable r : work) {
// futures.add(executor.submit(r));
// }

// for (Future<?> f : futures) {
// try {
// f.get(100, TimeUnit.SECONDS);
// } catch (InterruptedException | ExecutionException e) {
// e.printStackTrace();
// } catch (TimeoutException e) {
// System.err.println("Timeout!");
// }
// }

// lastUpdateTime = System.currentTimeMillis();
// }

// private static void setAntiAlias(Graphics g, boolean isAntiAliased) {
// Graphics2D g2d = (Graphics2D) g;
// RenderingHints renderHints = new
// RenderingHints(RenderingHints.KEY_ANTIALIASING,
// isAntiAliased ? RenderingHints.VALUE_ANTIALIAS_ON :
// RenderingHints.VALUE_ANTIALIAS_OFF);
// renderHints.put(RenderingHints.KEY_RENDERING,
// RenderingHints.VALUE_RENDER_SPEED);

// g2d.setRenderingHints(renderHints);
// }
// }
