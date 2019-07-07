package original.main;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public strictfp class ChargeSimulatorAparapi extends JPanel {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				new ChargeSimulatorAparapi().run();
			}
		});
	}

	private static final int REPEAT_DELAY = 250;

	private final int FRAME_WIDTH;
	private final int FRAME_HEIGHT;
	private static final double FRAME_WIDTH_SCREEN_PERCENTAGE = 0.85;
	private static final double FRAME_HEIGHT_SCREEN_PERCENTAGE = 0.9;

	private static final double drawingFPS = 25;
	// private static final int updateFPS = 30;

	private boolean update = true;
	private long lastUpdateTime = -1;

	private Image offScrImg;
	private Graphics offG;

	private JFrame frame;

	private List<Charge> negCharges;
	private List<Charge> posCharges;
	private List<TestCharge> testCharges;

	private int prevButton = 0;
	private int prevX = 0;
	private int prevY = 0;
	private int mouseX = 0;
	private int mouseY = 0;

	private int moveX = 0;
	private int moveY = 0;
	private Dimension centerPos = null;
	private Dimension movePivot = null;

	private int drawingFreq = 1;

	private double scaleFactor = 1;
	private static final double MIN_SCALING_FACTOR = 0.001;
	private static final double MAX_SCALING_FACTOR = 100;

	private boolean repeating = false;
	private boolean moving = true;
	private boolean drawing = true;
	private boolean drawPotential = false;
	private boolean doLogPotential = false;

	private List<Charge> mostRecent = new ArrayList<>();
	private Stack<List<Charge>> mostRecentStack = new Stack<>();
	private Stack<List<Charge>> mostRecentUndone = new Stack<>();

	private int[] xs;
	private int[] ys;

	private float[] arr;
	private int pdx = 8;
	private int pdy = 8;

	private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);;

	public ChargeSimulatorAparapi() {
		Dimension scrDim = Toolkit.getDefaultToolkit().getScreenSize();
		FRAME_WIDTH = (int) (scrDim.width * FRAME_WIDTH_SCREEN_PERCENTAGE);
		FRAME_HEIGHT = (int) (scrDim.height * FRAME_HEIGHT_SCREEN_PERCENTAGE);
		frame = new CenteredJFrame("Charge Simulator", FRAME_WIDTH, FRAME_HEIGHT);
		frame.setResizable(false);
		frame.getContentPane().add(this);
		// frame.setUndecorated(true);
		// frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		negCharges = new ArrayList<Charge>();
		posCharges = new ArrayList<Charge>();
		testCharges = new ArrayList<TestCharge>();
		Random r = new Random();
		for(int i = 0; i < 100; i++){
			posCharges.add(new Charge(r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2, r.nextInt(FRAME_HEIGHT) - FRAME_HEIGHT
					/ 2, 50 + r.nextInt(100)));
			negCharges.add(new Charge(r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2, r.nextInt(FRAME_HEIGHT) - FRAME_HEIGHT
					/ 2, -200 + r.nextInt(100)));

			if(i % 2 == 0)
				testCharges.add(new TestCharge(r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2, r.nextInt(FRAME_HEIGHT)
						- FRAME_HEIGHT / 2, true));
		}

		setCursor(new Cursor(Cursor.HAND_CURSOR));
		MouseListener l = new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if(moving){
					moveX = e.getX();
					moveY = e.getY();
					movePivot = new Dimension(centerPos.width, centerPos.height);
					setCursor(new Cursor(Cursor.MOVE_CURSOR));
					return;
				}

				prevButton = e.getButton();
				prevX = (int) ((e.getX() - centerPos.width) / scaleFactor);
				prevY = (int) ((e.getY() - centerPos.height) / scaleFactor);

				Charge c = null;

				if(e.getButton() == MouseEvent.BUTTON1){
					c = new Charge((e.getX() - centerPos.width) / scaleFactor, (e.getY() - centerPos.height)
							/ scaleFactor, 100);
				} else if(e.getButton() == MouseEvent.BUTTON3){
					c = new Charge((e.getX() - centerPos.width) / scaleFactor, (e.getY() - centerPos.height)
							/ scaleFactor, -100);
				} else{
					synchronized(testCharges){
						testCharges.add(new TestCharge((e.getX() - centerPos.width) / scaleFactor,
								(e.getY() - centerPos.height) / scaleFactor, true));
					}
				}

				if(c != null){
					if(c.getCharge() < 0){
						synchronized(negCharges){
							negCharges.add(c);
							mostRecent.add(c);
							if(!mostRecentUndone.isEmpty()){
								mostRecentUndone.clear();
							}
						}
					} else{
						synchronized(posCharges){
							posCharges.add(c);
							mostRecent.add(c);
							if(!mostRecentUndone.isEmpty()){
								mostRecentUndone.clear();
							}
						}
					}
					
					updateKCharges();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e){
				if(moving){
					setCursor(new Cursor(Cursor.HAND_CURSOR));
					return;
				}

				mostRecentStack.push(mostRecent);
				mostRecent = new ArrayList<>();

				prevButton = -1;
				repeating = false;
			}
		};
		frame.getContentPane().addMouseListener(l);
		frame.getContentPane().addMouseMotionListener(new MouseMotionAdapter(){
			@Override
			public void mouseMoved(MouseEvent e){
				mouseX = e.getX();
				mouseY = e.getY();
			}

			@Override
			public void mouseDragged(MouseEvent e){
				if(moving){
					int dx = e.getX() - moveX;
					int dy = e.getY() - moveY;
					centerPos.width = movePivot.width + dx;
					centerPos.height = movePivot.height + dy;
					return;
				}

				Charge c = null;
				if(prevButton == MouseEvent.BUTTON1){
					c = new Charge((e.getX() - centerPos.width) / scaleFactor, (e.getY() - centerPos.height)
							/ scaleFactor, 100);
				} else if(prevButton == MouseEvent.BUTTON3){
					c = new Charge((e.getX() - centerPos.width) / scaleFactor, (e.getY() - centerPos.height)
							/ scaleFactor, -100);
				} else{
					synchronized(testCharges){
						testCharges.add(new TestCharge((e.getX() - centerPos.width) / scaleFactor,
								(e.getY() - centerPos.height) / scaleFactor, true));
					}
				}

				if(c != null){
					if(c.getCharge() < 0){
						synchronized(negCharges){
							negCharges.add(c);
							mostRecent.add(c);
							if(!mostRecentUndone.isEmpty()){
								mostRecentUndone.clear();
							}
						}
					} else{
						synchronized(posCharges){
							posCharges.add(c);
							mostRecent.add(c);
							if(!mostRecentUndone.isEmpty()){
								mostRecentUndone.clear();
							}
						}
					}
					
					updateKCharges();
				}
			}
		});

		frame.getContentPane().addMouseWheelListener(new MouseWheelListener(){
			private static final double SCALING_FACTOR_PER_NOTCH = 1.05;

			@Override
			public void mouseWheelMoved(MouseWheelEvent e){
				if(e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL){
					int notches = e.getWheelRotation();
					int scrollAmount = e.getScrollAmount();
					// double prevScale = scaleFactor;
					if(notches < 0){
						for(int i = 0; i < scrollAmount; i++){
							double newScaleFactor = scaleFactor * SCALING_FACTOR_PER_NOTCH;
							if(newScaleFactor < MAX_SCALING_FACTOR && newScaleFactor > MIN_SCALING_FACTOR){
								scaleFactor = newScaleFactor;
							}
						}
					} else{
						for(int i = 0; i < scrollAmount; i++){
							double newScaleFactor = scaleFactor / SCALING_FACTOR_PER_NOTCH;
							if(newScaleFactor < MAX_SCALING_FACTOR && newScaleFactor > MIN_SCALING_FACTOR){
								scaleFactor = newScaleFactor;
							}
						}
					}
					// double dScale = scaleFactor - prevScale;
					// int mx = (e.getX() - centerPos.width);
					// int my = (e.getY() - centerPos.height);
					//
					// centerPos.width -= mx*dScale;
					// centerPos.height -= my*dScale;
				}
			}
		});

		frame.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_X){
					synchronized(testCharges){
						testCharges.clear();
					}

					synchronized(negCharges){
						negCharges.clear();
					}

					synchronized(posCharges){
						posCharges.clear();
					}
					
					updateKCharges();
				}

				if(e.getKeyCode() == KeyEvent.VK_G){
					synchronized(testCharges){
						testCharges.clear();
					}
				}

				if(e.getKeyCode() == KeyEvent.VK_D){
					drawing = !drawing;
				}

				if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
					System.exit(0);
				}

				if(e.getKeyCode() == KeyEvent.VK_B){
					synchronized(posCharges){
						posCharges.clear();
					}
					updateKCharges();
				}

				if(e.getKeyCode() == KeyEvent.VK_R){
					synchronized(negCharges){
						negCharges.clear();
					}
					updateKCharges();
				}

				if(e.getKeyCode() == KeyEvent.VK_HOME){
					scaleFactor = 1;
					centerPos = new Dimension(frame.getWidth() / 2, frame.getHeight() / 2);
				}

				if(e.getKeyCode() == KeyEvent.VK_1){
					Random r = new Random();
					for(int i = 0; i < 50; i++){
						synchronized(testCharges){
							testCharges.add(new TestCharge((r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2) / scaleFactor, (r
									.nextInt(FRAME_HEIGHT) - FRAME_HEIGHT / 2) / scaleFactor, true));
						}
					}
					updateKCharges();
				}

				if(e.getKeyCode() == KeyEvent.VK_2){
					int nCols = 50;
					int nRows = 30;
					for(int i = 0; i < FRAME_WIDTH; i += FRAME_WIDTH / nCols){
						for(int j = 0; j < FRAME_HEIGHT; j += FRAME_HEIGHT / nRows){
							synchronized(posCharges){
								posCharges.add(new Charge((i - FRAME_WIDTH / 2) / scaleFactor, (j - FRAME_HEIGHT / 2)
										/ scaleFactor, 400));
							}
						}
					}
					updateKCharges();
				}

				if(e.getKeyCode() == KeyEvent.VK_3){
					Random r = new Random();
					for(int i = 0; i < 1000; i++){
						synchronized(testCharges){
							testCharges.add(new TestCharge((r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2) / scaleFactor, (r
									.nextInt(FRAME_HEIGHT) - FRAME_HEIGHT / 2) / scaleFactor, true));
						}
					}

					for(int i = 0; i < 1000; i++){
						synchronized(posCharges){
							posCharges.add(new Charge((r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2) / scaleFactor, (r
									.nextInt(FRAME_HEIGHT) - FRAME_HEIGHT / 2) / scaleFactor, 200));
						}
					}

					for(int i = 0; i < 1000; i++){
						synchronized(negCharges){
							negCharges.add(new Charge((r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2) / scaleFactor, (r
									.nextInt(FRAME_HEIGHT) - FRAME_HEIGHT / 2) / scaleFactor, -200));
						}
					}
					updateKCharges();
				}

				if(e.getKeyCode() == KeyEvent.VK_4){
					double radius = 100;
					int nCharges = 100;
					for(int i = 0; i < nCharges; i++){
						double theta = i * Math.PI * 2 / nCharges;
						synchronized(negCharges){
							negCharges.add(new Charge((radius * Math.cos(theta)) / scaleFactor, (radius * Math
									.sin(theta)) / scaleFactor, -100));
						}
					}
					updateKCharges();
				}

				if(e.getKeyCode() == KeyEvent.VK_5){
					double dx = 50;
					double dy = 50;
					for(int i = 0; i < FRAME_WIDTH; i += dx){
						for(int j = 0; j < FRAME_HEIGHT; j += dy){
							testCharges.add(new TestCharge((i - FRAME_WIDTH / 2) / scaleFactor, (j - FRAME_HEIGHT / 2)
									/ scaleFactor, true));
						}
					}
					updateKCharges();
				}

				if(e.getKeyCode() == KeyEvent.VK_SPACE){
					update = !update;
				}

				if(e.getKeyCode() == KeyEvent.VK_A){
					if(prevButton != -1 && !repeating){
						final Timer t = new Timer();
						t.schedule(new TimerTask(){
							@Override
							public void run(){
								if(prevButton == -1 || !repeating){
									repeating = false;
									t.cancel();
									return;
								}

								if(prevButton == MouseEvent.BUTTON1){
									synchronized(posCharges){
										posCharges.add(new Charge(prevX, prevY, 100));
									}
								} else if(prevButton == MouseEvent.BUTTON3){
									synchronized(negCharges){
										negCharges.add(new Charge(prevX, prevY, -100));
									}
								} else{
									synchronized(testCharges){
										TestCharge tc = new TestCharge(prevX, prevY, true);
										tc.first = testCharges.isEmpty();
										testCharges.add(tc);
									}
								}
							}
						}, 0, REPEAT_DELAY);
						repeating = true;
					}
				}

				if(e.getKeyCode() == KeyEvent.VK_S){
					repeating = false;
				}

				if(e.getKeyCode() == KeyEvent.VK_M){
					if(!moving){
						setCursor(new Cursor(Cursor.HAND_CURSOR));
						moving = true;
					} else{
						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						moving = false;
					}
				}

				if(e.getKeyCode() == KeyEvent.VK_C){
					// consolidateCharges();
				}

				if(e.getKeyCode() == KeyEvent.VK_T){
					List<Charge> newPosCharges = new ArrayList<Charge>();
					List<Charge> newNegCharges = new ArrayList<Charge>();
					synchronized(negCharges){
						for(Charge c : negCharges){
							c.setCharge(-c.getCharge());
							newPosCharges.add(c);
						}
					}

					synchronized(posCharges){
						for(Charge c : posCharges){
							c.setCharge(-c.getCharge());
							newNegCharges.add(c);
						}
					}

					synchronized(posCharges){
						posCharges.clear();
						posCharges.addAll(newPosCharges);
					}

					synchronized(negCharges){
						negCharges.clear();
						negCharges.addAll(newNegCharges);
					}
				}

				if(e.getKeyCode() == KeyEvent.VK_Z){
					if(!mostRecentStack.isEmpty()){
						List<Charge> c = mostRecentStack.pop();
						mostRecentUndone.push(c);

						synchronized(negCharges){
							negCharges.removeAll(c);
						}
						synchronized(posCharges){
							posCharges.removeAll(c);
						}
					}
				}

				if(e.getKeyCode() == KeyEvent.VK_Y){
					if(!mostRecentUndone.isEmpty()){
						List<Charge> c = mostRecentUndone.pop();
						mostRecentStack.push(c);
						for(Charge cc : c){
							if(cc.getCharge() > 0){
								synchronized(posCharges){
									posCharges.add(cc);
								}
							} else{
								synchronized(negCharges){
									negCharges.add(cc);
								}
							}
						}
					}
				}

				if(e.getKeyCode() == KeyEvent.VK_EQUALS){
					if(drawingFreq > 1){
						drawingFreq -= 1;
					}
				}

				if(e.getKeyCode() == KeyEvent.VK_MINUS){
					if(drawingFreq < 10){
						drawingFreq += 1;
					}
				}

				if(e.getKeyCode() == KeyEvent.VK_P){
					drawPotential = !drawPotential;
					updateKCharges();
				}

				if(e.getKeyCode() == KeyEvent.VK_OPEN_BRACKET){
					if(pdx > 1){
						pdx /= 2;
					}
					if(pdy > 1){
						pdy /= 2;
					}

					arr = new float[(getWidth() / pdx + 1) * (getHeight() / pdy + 1)];
					k.arr = arr;
				}

				if(e.getKeyCode() == KeyEvent.VK_CLOSE_BRACKET){
					if(pdx < 256){
						pdx *= 2;
					}
					if(pdy < 256){
						pdy *= 2;
					}

					arr = new float[(getWidth() / pdx + 1) * (getHeight() / pdy + 1)];
					k.arr = arr;
				}
				
				if (e.getKeyCode() == KeyEvent.VK_L) {
					doLogPotential = !doLogPotential;
				}
			}
		});

		setBackground(new Color(0, 10, 20));
//		setBackground(new Color(200, 200, 200));
	}

	// private void consolidateCharges(){
	// List<Charge> newCharges = new ArrayList<>();
	// newCharges.addAll(charges);
	// double DISTANCE_THRESHOLD = 1;
	// for(int i = 0; i < newCharges.size(); i++){
	// for(int j = 0; j < newCharges.size(); j++){
	// if(i != j && newCharges.get(i).distanceTo(newCharges.get(j)) <
	// DISTANCE_THRESHOLD){
	// if(i < j){
	// Charge c = newCharges.get(j);
	// newCharges.get(i).setCharge(newCharges.get(i).getCharge() +
	// c.getCharge());
	// newCharges.remove(j);
	// j--;
	// } else{
	// Charge c = newCharges.get(i);
	// newCharges.get(j).setCharge(newCharges.get(j).getCharge() +
	// c.getCharge());
	// newCharges.remove(i);
	// i--;
	// }
	// }
	// }
	// }
	//
	// List<Charge> toRemove = new ArrayList<>();
	// for(Charge c : newCharges){
	// if(Math.abs(c.getCharge()) <= 0.00001){
	// toRemove.add(c);
	// }
	// }
	//
	// newCharges.removeAll(toRemove);
	// charges.clear();
	// charges.addAll(newCharges);
	//
	// // for(Charge c : charges){
	// // if(!newCharges.contains(c)){
	// // newCharges.add(c);go
	//
	// // }
	// // }
	//
	// // System.out.println(charges.size() + " : " + charges);
	// }

	public void run(){
		frame.setVisible(true);

		Timer drawTimer = new Timer();
		drawTimer.schedule(new TimerTask(){
			private int i = 0;

			@Override
			public void run(){
				if(drawing){
					Container c = frame.getContentPane();
					if(c != null && c.getGraphics() != null && ++i % drawingFreq == 0){
						paintComponent(c.getGraphics());
						i = 0;
					}
				}

				if(update)
					update();
				else
					lastUpdateTime = System.currentTimeMillis();
				// repaint();
			}
		}, 0, (int) (1000 / drawingFPS));

		// Timer updateTimer = new Timer();
		// updateTimer.schedule(new TimerTask(){
		// @Override
		// public void run(){
		// if(update)
		// update();
		// else
		// lastUpdateTime = System.currentTimeMillis();
		// }
		// }, 0, 1000 / updateFPS);
	}

	@Override
	public void paintComponent(Graphics g){
		if(centerPos == null){
			// System.out.println(getHeight() + " : " + frame.getHeight() +
			// " : " + frame.getContentPane().getHeight());
			centerPos = new Dimension(frame.getWidth() / 2, frame.getHeight() / 2);
		}

		// System.out.println("DRAW " + i++);
		if(offScrImg == null || offScrImg.getWidth(null) != getWidth() || offScrImg.getHeight(null) != getHeight()){
			offScrImg = createImage(getWidth(), getHeight());
			if(offScrImg == null){
				return;
			}

			offG = offScrImg.getGraphics();
		}

		if(offScrImg == null){
			return;
		}

		if(offG == null){
			offG = offScrImg.getGraphics();
		}

		setAntiAlias(offG, true);

		offG.setColor(getBackground());
		offG.fillRect(0, 0, getWidth(), getHeight());

		if (!drawPotential) {
			offG.setColor(Color.RED);
			doRedBalls();
	
			offG.setColor(Color.BLUE);
			doBlueBalls();
	
			offG.setColor(Color.GREEN);
			doGreenBalls();
		}
		
		offG.setColor(Color.WHITE);
		FontMetrics fm = offG.getFontMetrics();
		offG.drawString(String.format("Zoom: %.2f%%", scaleFactor * 100), 10, 10 + fm.getAscent());

		float mag = getPotentialAt(mouseX, mouseY);
		offG.drawString(String.format("Mouse: (%d, %d), Potential: %.2f", mouseX, mouseY, mag), 10,
				20 + fm.getAscent() * 2);

		if(update){
			xs = new int[]{ getWidth() - 20, getWidth() - 20, getWidth() - 5 };
			ys = new int[]{ 7, 23, 15 };
			offG.fillPolygon(xs, ys, xs.length);
		} else{
			offG.fillRect(getWidth() - 20, 7, 3, 15);
			offG.fillRect(getWidth() - 14, 7, 3, 15);
		}

		if(drawPotential){
			drawPotential(offG);
		}

		if(g != null){
			g.drawImage(offScrImg, getX(), getY(), null);
		}
	}

	private void updateKCharges(){
		if (posCharges.size() > 0) {
			k.posX = new float[posCharges.size()];
			k.posY = new float[posCharges.size()];
			k.posQ = new float[posCharges.size()];
	
			for(int i = 0; i < posCharges.size(); i++){
				Charge c = posCharges.get(i);
				k.posX[i] = (float) c.getX();
				k.posY[i] = (float) c.getY();
				k.posQ[i] = (float) c.getCharge();
			}
		} else {
			k.posX = new float[1];
			k.posY = new float[1];
			k.posQ = new float[1];
		}
		
		if (negCharges.size() > 0) {
			k.negX = new float[negCharges.size()];
			k.negY = new float[negCharges.size()];
			k.negQ = new float[negCharges.size()];
	
			for(int i = 0; i < negCharges.size(); i++){
				Charge c = negCharges.get(i);
				k.negX[i] = (float) c.getX();
				k.negY[i] = (float) c.getY();
				k.negQ[i] = (float) c.getCharge();
			}
		} else {
			k.negX = new float[1];
			k.negY = new float[1];
			k.negQ = new float[1];
		}

		k.PBOUNDS = posCharges.size();
		k.NBOUNDS = negCharges.size();
	}

	private PotentialKernel k = new PotentialKernel();

	private class PotentialKernel extends Kernel {
		private int pdxp;
		private int pdyp;
		private float[] arr;
		private float[] posX;
		private float[] posY;
		private float[] posQ;
		private float[] negX;
		private float[] negY;
		private float[] negQ;

		private int centerPosWidth;
		private int centerPosHeight;
		private float scaleFactor;
		private int PBOUNDS;
		private int NBOUNDS;

		@Override
		public void run(){
			int gid0 = getGlobalId(0) * pdxp;
			int gid1 = getGlobalId(1) * pdyp;

			float mag = this.getPotentialAt(gid0, gid1);
			arr[getGlobalId(1) * getGlobalSize(0) + getGlobalId(0)] = mag;
		}

		private float getPotentialAt(int xPos, int yPos){
			float potential = 0;
			final float closeBound = 50;
			if (PBOUNDS > 0 ){
				for(int i = 0; i < PBOUNDS; i++){
					float x = (xPos - centerPosWidth) / scaleFactor - posX[i];
					float y = (yPos - centerPosHeight) / scaleFactor - posY[i];
					float dist = x * x + y * y;
					if(dist < closeBound){
						dist = closeBound;
					}
					float pot = posQ[i] / dist;
					potential += pot;
				}
			}
			if (NBOUNDS > 0) {
				for(int i = 0; i < NBOUNDS; i++){
					float x = (xPos - centerPosWidth) / scaleFactor - negX[i];
					float y = (yPos - centerPosHeight) / scaleFactor - negY[i];
					float dist = x * x + y * y;
					if(dist < closeBound){
						dist = closeBound;
					}
					float pot = negQ[i] / dist;
	
					potential -= pot;
				}
			}

			return potential;
		}
	}
	
	private class DrawerKernel extends Kernel {
		private int[] cols; 
		private float[] arr;
		private float maxValue;
		private int pdx;
		private int pdy;
		private boolean doLog;
		
		@Override
		public void run(){
			//int gid = ((getGlobalId(1)/pdy)*pdy) * ((getGlobalSize(0)/pdx)*pdx) + ((getGlobalId(0)/pdx)*pdx);
			int gid = (getGlobalId(1)/pdy)*(getGlobalSize(0)/pdx+1) + (getGlobalId(0)/pdx);
			float val = arr[gid];
//			float val = gid;
//			val = (float) Math.pow(val/maxValue, 1);
			if (doLog) {
				val = (float)(Math.log(val+1) / Math.log(maxValue+1));
			} else {
				val = (float) val / maxValue;
			}
//			val = (float)(Math.log(val*(Math.E-1)+1));
			if (val > 1.0f) { val = 1.0f; }
			if (val < 0.0f) { val = 0.0f; }
			int colVal = (int) (val * 255);
			cols[getGlobalId(1)*getGlobalSize(0) + getGlobalId(0)] = 0x99000000 | (colVal << 16) | (colVal << 8);
		}
	}
	private DrawerKernel drawerKernel = new DrawerKernel();

	private void drawPotential(Graphics g){

		int w = getWidth();
		int h = getHeight();

		if(k.arr == null){
			k.arr = new float[(w / pdx + 1) * (h / pdy + 1)];
		}

		try{

			k.pdxp = pdx;
			k.pdyp = pdy;
			k.scaleFactor = (float) scaleFactor;
			k.centerPosWidth = centerPos.width;
			k.centerPosHeight = centerPos.height;

//			System.out.println(k.getExecutionMode());
			k.execute(Range.create2D(w / pdx + 1, h / pdy + 1));
			arr = k.arr;
			double magMax = Double.NEGATIVE_INFINITY;
			for(int i = 0, ii = 0; i < w; i += pdx, ii++){
				for(int j = 0, jj = 0; j < h; j += pdy, jj++){
					int idx = jj * (w / pdx + 1) + ii;
					if(idx < arr.length){
						if(arr[idx] > magMax){
							magMax = arr[idx];
						}
					}
				}
			}
			
			drawerKernel.maxValue = (float)magMax;
			drawerKernel.arr = arr;
			drawerKernel.pdx = pdx;
			drawerKernel.pdy = pdy;
			drawerKernel.doLog = doLogPotential;
			if (drawerKernel.cols == null) {
				drawerKernel.cols = new int[w * h];
			}
			drawerKernel.execute(Range.create2D(w, h));
			System.out.println(drawerKernel.getExecutionMode());

//			for(int i = 0, ii = 0; i < w; i += pdx, ii++){
//				for(int j = 0, jj = 0; j < h; j += pdy, jj++){
//					float col = (float) (Math.log((float) (arr[jj * (w / pdx + 1) + ii]) / Math.log(magMax)));
//					if(col > 1.0f)
//						col = 1.0f;
//					if(col < 0.0f)
//						col = 0.0f;
//
					g.setColor(new Color(0, 0.5f, 0, 0.7f));
					g.fillRect(5 - pdx / 2, 5 - pdy / 2, pdx, pdy);
//				}
//			}
			BufferedImage res = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			final int[] imgBuf = ( (DataBufferInt) res.getRaster().getDataBuffer() ).getData();
//			int[] cols = new int[w*h];
//			for(int i=0;i<w*h;i++){
//				cols[i] = 0xFF00FFFF;
//			}
			System.arraycopy(drawerKernel.cols, 0, imgBuf, 0, drawerKernel.cols.length);
//			res = ImageIO.read(new File("testimage.png"));
			int colVal = 0xFF009999;
			res.setRGB(10, 14, colVal);
			res.setRGB(10, 15, colVal);
			res.setRGB(11, 14, colVal);
			res.setRGB(11, 15, colVal);
			g.drawImage(res, 0, 0, null);
			
		} catch(Exception e){
			e.printStackTrace();
			return;
		}
	}

	private float getPotentialAt(int xPos, int yPos){
		float potential = 0;
		synchronized(posCharges){
			for(Charge c : posCharges){
				float pot = (float) c.getPotentialAt((xPos - centerPos.width) / scaleFactor, (yPos - centerPos.height)
						/ scaleFactor);
				// double x = (xPos - centerPos.width)/scaleFactor - c.getX();
				// double y = (yPos - centerPos.height)/scaleFactor - c.getY();

				potential += pot;
			}
		}

		synchronized(negCharges){
			for(Charge c : negCharges){
				float pot = (float) c.getPotentialAt((xPos - centerPos.width) / scaleFactor, (yPos - centerPos.height)
						/ scaleFactor);
				// double x = (xPos - centerPos.width)/scaleFactor - c.getX();
				// double y = (yPos - centerPos.height)/scaleFactor - c.getY();

				potential -= pot;
			}
		}

		return potential;
	}

	private void doBlueBalls(){
		final int NTASKS = 4;
		int nCharges = posCharges.size() / NTASKS;
		List<Runnable> work = new ArrayList<>();
		List<Future<?>> futures = new ArrayList<>();
		for(int i = 0; i < NTASKS; i++){
			final int startBalls = i * nCharges;
			final int endBalls = i < NTASKS - 1 ? (i + 1) * nCharges - 1 : posCharges.size() - 1;
			work.add(new Runnable(){
				@Override
				public void run(){
					for(int i = startBalls; i <= endBalls; i++){
						if(i >= posCharges.size()){
							return;
						}

						Charge c = posCharges.get(i);
						if(c != null){
							c.draw(offG, scaleFactor, centerPos);
						}
					}
				}
			});
		}

		for(Runnable r : work){
			futures.add(executor.submit(r));
		}

		for(Future<?> f : futures){
			try{
				f.get(100, TimeUnit.SECONDS);
			} catch(InterruptedException | ExecutionException | TimeoutException e){
				e.printStackTrace();
			}
		}
	}

	private void doRedBalls(){
		final int NTASKS = 4;
		int nCharges = negCharges.size() / NTASKS;
		List<Runnable> work = new ArrayList<>();
		List<Future<?>> futures = new ArrayList<>();
		for(int i = 0; i < NTASKS; i++){
			final int startBalls = i * nCharges;
			final int endBalls = i < NTASKS - 1 ? (i + 1) * nCharges - 1 : negCharges.size() - 1;
			work.add(new Runnable(){
				@Override
				public void run(){
					for(int i = startBalls; i <= endBalls; i++){
						if(i >= negCharges.size()){
							return;
						}

						Charge c = negCharges.get(i);
						if(c != null){
							c.draw(offG, scaleFactor, centerPos);
						}
					}
				}
			});
		}

		for(Runnable r : work){
			futures.add(executor.submit(r));
		}

		for(Future<?> f : futures){
			try{
				f.get(100, TimeUnit.SECONDS);
			} catch(InterruptedException | ExecutionException | TimeoutException e){
				e.printStackTrace();
			}
		}
	}

	private void doGreenBalls(){
		final int NTASKS = 4;

		List<Future<?>> futures = new ArrayList<>();
		List<Runnable> work = new ArrayList<>();
		int nCharges = testCharges.size() / NTASKS;
		for(int i = 0; i < NTASKS; i++){
			final int startBalls = i * nCharges;
			final int endBalls = i < NTASKS - 1 ? (i + 1) * nCharges - 1 : testCharges.size() - 1;
			work.add(new Runnable(){
				@Override
				public void run(){
					for(int i = startBalls; i <= endBalls; i++){
						if(i >= testCharges.size()){
							return;
						}

						TestCharge tc = testCharges.get(i);
						if(tc != null){
							tc.draw(offG, scaleFactor, centerPos);
						}
					}
				}
			});
		}

		for(Runnable r : work){
			futures.add(executor.submit(r));
		}

		for(Future<?> f : futures){
			try{
				f.get(100, TimeUnit.SECONDS);
			} catch(InterruptedException | ExecutionException | TimeoutException e){
				e.printStackTrace();
			}
		}
	}

	private void update(){
		if(lastUpdateTime < 0){
			lastUpdateTime = System.currentTimeMillis();
			return;
		}

		List<Runnable> work = new ArrayList<>();
		final int NTASKS = 4;
		int nCharges = testCharges.size() / NTASKS;
		// if(posChargesArr == null){
		// posChargesArr = posCharges.toArray(new Charge[posCharges.size()]);
		// }
		// if(negChargesArr == null){
		// negChargesArr = negCharges.toArray(new Charge[negCharges.size()]);
		// }
		for(int i = 0; i < NTASKS; i++){
			final int startBalls = i * nCharges;
			final int endBalls = i < NTASKS - 1 ? (i + 1) * nCharges - 1 : testCharges.size() - 1;
			work.add(new Runnable(){
				@Override
				public void run(){
					for(int i = startBalls; i <= endBalls; i++){
						if(i >= testCharges.size()){
							return;
						}

						// for(int j=0; j < 10; j++)
						testCharges.get(i).update(posCharges, negCharges, 1.0 / drawingFPS);
					}
				}
			});
		}

		List<Future<?>> futures = new ArrayList<>();
		for(Runnable r : work){
			futures.add(executor.submit(r));
		}

		for(Future<?> f : futures){
			try{
				f.get(100, TimeUnit.SECONDS);
			} catch(InterruptedException | ExecutionException e){
				e.printStackTrace();
			} catch(TimeoutException e){
				System.err.println("Timeout!");
			}
		}

		lastUpdateTime = System.currentTimeMillis();
	}

	public static void setAntiAlias(Graphics g, boolean isAntiAliased){
		Graphics2D g2d = (Graphics2D) g;
		RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				isAntiAliased ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
		renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		g2d.setRenderingHints(renderHints);
	}
}
