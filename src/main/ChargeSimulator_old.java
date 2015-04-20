package main;

import javax.swing.JPanel;
//
//import gui.CenteredJFrame;
//
//import java.awt.Color;
//import java.awt.Container;
//import java.awt.Cursor;
//import java.awt.Dimension;
//import java.awt.FontMetrics;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.Image;
//import java.awt.RenderingHints;
//import java.awt.Toolkit;
//import java.awt.event.KeyAdapter;
//import java.awt.event.KeyEvent;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.awt.event.MouseMotionAdapter;
//import java.awt.event.MouseWheelEvent;
//import java.awt.event.MouseWheelListener;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Random;
//import java.util.Stack;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import javax.swing.SwingUtilities;
//
//@SuppressWarnings("serial")
public strictfp class ChargeSimulator_old extends JPanel {
//	public static void main(String[] args){
//		SwingUtilities.invokeLater(new Runnable(){
//			@Override
//			public void run(){
//				new ChargeSimulator_old().run();
//			}
//		});
//	}
//
//	private static final int REPEAT_DELAY = 250;
//
//	private final int FRAME_WIDTH;
//	private final int FRAME_HEIGHT;
//	private static final double FRAME_WIDTH_SCREEN_PERCENTAGE = 0.85;
//	private static final double FRAME_HEIGHT_SCREEN_PERCENTAGE = 0.9;
//
//	private static final int drawingFPS = 30;
//	// private static final int updateFPS = 30;
//
//	private boolean update = true;
//	private long lastUpdateTime = -1;
//
//	private Image offScrImg;
//	private Graphics offG;
//
//	private JFrame frame;
//
//	private List<Charge> charges;
//	private List<TestCharge> testCharges;
//
//	private int prevButton = 0;
//	private int prevX = 0;
//	private int prevY = 0;
//
//	private int moveX = 0;
//	private int moveY = 0;
//	private Dimension centerPos = null;
//	private Dimension movePivot = null;
//
//	private double scaleFactor = 1;
//	private static final double MIN_SCALING_FACTOR = 0.01;
//	private static final double MAX_SCALING_FACTOR = 10;
//
//	private boolean repeating = false;
//	private boolean moving = true;
//	private boolean drawing = true;
//
//	private List<Charge> mostRecent = new ArrayList<>();
//	private Stack<List<Charge>> mostRecentStack = new Stack<>();
//	private Stack<List<Charge>> mostRecentUndone = new Stack<>();
//
//	public ChargeSimulator_old() {
//		Dimension scrDim = Toolkit.getDefaultToolkit().getScreenSize();
//		FRAME_WIDTH = (int) (scrDim.width * FRAME_WIDTH_SCREEN_PERCENTAGE);
//		FRAME_HEIGHT = (int) (scrDim.height * FRAME_HEIGHT_SCREEN_PERCENTAGE);
//		frame = new CenteredJFrame("Charge Simulator", FRAME_WIDTH, FRAME_HEIGHT);
//		frame.setResizable(false);
//		frame.getContentPane().add(this);
//		// frame.setUndecorated(true);
//		// frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//
//		charges = Collections.synchronizedList(new ArrayList<Charge>());
//		testCharges = Collections.synchronizedList(new ArrayList<TestCharge>());
//		Random r = new Random();
//		for(int i = 0; i < 100; i++){
//			charges.add(new Charge(r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2,
//					r.nextInt(FRAME_HEIGHT) - FRAME_HEIGHT / 2, 50 + r.nextInt(100)));
//			charges.add(new Charge(r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2,
//					r.nextInt(FRAME_HEIGHT) - FRAME_HEIGHT / 2, -200 + r.nextInt(100)));
//
//			if(i % 2 == 0)
//				testCharges.add(new TestCharge(r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2, r.nextInt(FRAME_HEIGHT)
//						- FRAME_HEIGHT / 2, true));
//		}
//
//		setCursor(new Cursor(Cursor.HAND_CURSOR));
//		MouseListener l = new MouseAdapter(){
//			@Override
//			public void mousePressed(MouseEvent e){
//				if(moving){
//					moveX = e.getX();
//					moveY = e.getY();
//					movePivot = new Dimension(centerPos.width, centerPos.height);
//					setCursor(new Cursor(Cursor.MOVE_CURSOR));
//					return;
//				}
//
//				prevButton = e.getButton();
//				prevX = (int) ((e.getX() - centerPos.width) / scaleFactor);
//				prevY = (int) ((e.getY() - centerPos.height) / scaleFactor);
//
//				Charge c = null;
//
//				if(e.getButton() == MouseEvent.BUTTON1){
//					c = new Charge((e.getX() - centerPos.width) / scaleFactor, (e.getY() - centerPos.height)
//							/ scaleFactor, 100);
//				} else if(e.getButton() == MouseEvent.BUTTON3){
//					c = new Charge((e.getX() - centerPos.width) / scaleFactor, (e.getY() - centerPos.height)
//							/ scaleFactor, -100);
//				} else{
//					synchronized(testCharges){
//						testCharges.add(new TestCharge((e.getX() - centerPos.width) / scaleFactor,
//								(e.getY() - centerPos.height) / scaleFactor, true));
//					}
//				}
//
//				if(c != null){
//					synchronized(charges){
//						charges.add(c);
//						mostRecent.add(c);
//						if(!mostRecentUndone.isEmpty()){
//							mostRecentUndone.clear();
//						}
//					}
//				}
//			}
//
//			@Override
//			public void mouseReleased(MouseEvent e){
//				if(moving){
//					setCursor(new Cursor(Cursor.HAND_CURSOR));
//					return;
//				}
//
//				mostRecentStack.push(mostRecent);
//				mostRecent = new ArrayList<>();
//
//				prevButton = -1;
//				repeating = false;
//			}
//		};
//		frame.getContentPane().addMouseListener(l);
//		frame.getContentPane().addMouseMotionListener(new MouseMotionAdapter(){
//			@Override
//			public void mouseDragged(MouseEvent e){
//				if(moving){
//					int dx = e.getX() - moveX;
//					int dy = e.getY() - moveY;
//					centerPos.width = movePivot.width + dx;
//					centerPos.height = movePivot.height + dy;
//					return;
//				}
//
//				Charge c = null;
//				if(prevButton == MouseEvent.BUTTON1){
//					c = new Charge((e.getX() - centerPos.width) / scaleFactor, (e.getY() - centerPos.height)
//							/ scaleFactor, 100);
//				} else if(prevButton == MouseEvent.BUTTON3){
//					c = new Charge((e.getX() - centerPos.width) / scaleFactor, (e.getY() - centerPos.height)
//							/ scaleFactor, -100);
//				} else{
//					synchronized(testCharges){
//						testCharges.add(new TestCharge((e.getX() - centerPos.width) / scaleFactor,
//								(e.getY() - centerPos.height) / scaleFactor, true));
//					}
//				}
//
//				if(c != null){
//					synchronized(charges){
//						charges.add(c);
//						mostRecent.add(c);
//						if(!mostRecentUndone.isEmpty()){
//							mostRecentUndone.clear();
//						}
//					}
//				}
//			}
//		});
//
//		frame.getContentPane().addMouseWheelListener(new MouseWheelListener(){
//			private static final double SCALING_FACTOR_PER_NOTCH = 1.05;
//
//			@Override
//			public void mouseWheelMoved(MouseWheelEvent e){
//				if(e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL){
//					int notches = e.getWheelRotation();
//					int scrollAmount = e.getScrollAmount();
//					// double prevScale = scaleFactor;
//					if(notches < 0){
//						for(int i = 0; i < scrollAmount; i++){
//							double newScaleFactor = scaleFactor * SCALING_FACTOR_PER_NOTCH;
//							if(newScaleFactor < MAX_SCALING_FACTOR && newScaleFactor > MIN_SCALING_FACTOR){
//								scaleFactor = newScaleFactor;
//							}
//						}
//					} else{
//						for(int i = 0; i < scrollAmount; i++){
//							double newScaleFactor = scaleFactor / SCALING_FACTOR_PER_NOTCH;
//							if(newScaleFactor < MAX_SCALING_FACTOR && newScaleFactor > MIN_SCALING_FACTOR){
//								scaleFactor = newScaleFactor;
//							}
//						}
//					}
//					// double dScale = scaleFactor - prevScale;
//					// int mx = (e.getX() - centerPos.width);
//					// int my = (e.getY() - centerPos.height);
//					//
//					// centerPos.width -= mx*dScale;
//					// centerPos.height -= my*dScale;
//				}
//			}
//		});
//
//		frame.addKeyListener(new KeyAdapter(){
//			@Override
//			public void keyPressed(KeyEvent e){
//				if(e.getKeyCode() == KeyEvent.VK_X){
//					synchronized(testCharges){
//						testCharges.clear();
//					}
//
//					synchronized(charges){
//						charges.clear();
//					}
//				}
//
//				if(e.getKeyCode() == KeyEvent.VK_G){
//					synchronized(testCharges){
//						testCharges.clear();
//					}
//				}
//
//				if(e.getKeyCode() == KeyEvent.VK_D){
//					drawing = !drawing;
//				}
//
//				if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
//					System.exit(0);
//				}
//
//				if(e.getKeyCode() == KeyEvent.VK_B){
//					synchronized(charges){
//						for(int i = 0; i < charges.size(); i++){
//							if(charges.get(i).getCharge() >= 0){
//								charges.remove(i--);
//							}
//						}
//					}
//				}
//
//				if(e.getKeyCode() == KeyEvent.VK_R){
//					synchronized(charges){
//						for(int i = 0; i < charges.size(); i++){
//							if(charges.get(i).getCharge() < 0){
//								charges.remove(i--);
//							}
//						}
//					}
//				}
//
//				if(e.getKeyCode() == KeyEvent.VK_1){
//					Random r = new Random();
//					for(int i = 0; i < 50; i++){
//						synchronized(testCharges){
//							testCharges.add(new TestCharge((r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2) / scaleFactor, (r
//									.nextInt(FRAME_HEIGHT) - FRAME_HEIGHT / 2) / scaleFactor, true));
//						}
//					}
//				}
//
//				if(e.getKeyCode() == KeyEvent.VK_2){
//					int nCols = 50;
//					int nRows = 30;
//					for(int i = 0; i < FRAME_WIDTH; i += FRAME_WIDTH / nCols){
//						for(int j = 0; j < FRAME_HEIGHT; j += FRAME_HEIGHT / nRows){
//							synchronized(charges){
//								charges.add(new Charge((i - FRAME_WIDTH / 2) / scaleFactor, (j - FRAME_HEIGHT / 2)
//										/ scaleFactor, 400));
//							}
//						}
//					}
//				}
//
//				if(e.getKeyCode() == KeyEvent.VK_3){
//					Random r = new Random();
//					for(int i = 0; i < 1000; i++){
//						synchronized(testCharges){
//							testCharges.add(new TestCharge((r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2) / scaleFactor, (r
//									.nextInt(FRAME_HEIGHT) - FRAME_HEIGHT / 2) / scaleFactor, true));
//						}
//					}
//
//					for(int i = 0; i < 2000; i++){
//						synchronized(charges){
//							charges.add(new Charge((r.nextInt(FRAME_WIDTH) - FRAME_WIDTH / 2) / scaleFactor, (r
//									.nextInt(FRAME_HEIGHT) - FRAME_HEIGHT / 2) / scaleFactor, 200 - r.nextInt(400)));
//						}
//					}
//				}
//
//				if(e.getKeyCode() == KeyEvent.VK_SPACE){
//					update = !update;
//				}
//
//				if(e.getKeyCode() == KeyEvent.VK_A){
//					if(prevButton != -1 && !repeating){
//						final Timer t = new Timer();
//						t.schedule(new TimerTask(){
//							@Override
//							public void run(){
//								if(prevButton == -1 || !repeating){
//									repeating = false;
//									t.cancel();
//									return;
//								}
//
//								if(prevButton == MouseEvent.BUTTON1){
//									synchronized(charges){
//										charges.add(new Charge(prevX, prevY, 100));
//									}
//								} else if(prevButton == MouseEvent.BUTTON3){
//									synchronized(charges){
//										charges.add(new Charge(prevX, prevY, -100));
//									}
//								} else{
//									synchronized(testCharges){
//										TestCharge tc = new TestCharge(prevX, prevY, true);
//										tc.first = testCharges.isEmpty();
//										testCharges.add(tc);
//									}
//								}
//							}
//						}, 0, REPEAT_DELAY);
//						repeating = true;
//					}
//				}
//
//				if(e.getKeyCode() == KeyEvent.VK_S){
//					repeating = false;
//				}
//
//				if(e.getKeyCode() == KeyEvent.VK_M){
//					if(!moving){
//						setCursor(new Cursor(Cursor.HAND_CURSOR));
//						moving = true;
//					} else{
//						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//						moving = false;
//					}
//				}
//
//				if(e.getKeyCode() == KeyEvent.VK_C){
//					synchronized(charges){
//						consolidateCharges();
//					}
//				}
//
//				if(e.getKeyCode() == KeyEvent.VK_T){
//					synchronized(charges){
//						for(Charge c : charges){
//							c.setCharge(-c.getCharge());
//						}
//					}
//				}
//
//				if(e.getKeyCode() == KeyEvent.VK_Z){
//					synchronized(charges){
//						if(!mostRecentStack.isEmpty()){
//							List<Charge> c = mostRecentStack.pop();
//							charges.removeAll(c);
//							mostRecentUndone.push(c);
//						}
//					}
//				}
//
//				if(e.getKeyCode() == KeyEvent.VK_Y){
//					synchronized(charges){
//						if(!mostRecentUndone.isEmpty()){
//							List<Charge> c = mostRecentUndone.pop();
//							charges.addAll(c);
//							mostRecentStack.push(c);
//						}
//					}
//				}
//			}
//		});
//
//		setBackground(new Color(0, 10, 20));
//	}
//
//	private void consolidateCharges(){
//		List<Charge> newCharges = new ArrayList<>();
//		newCharges.addAll(charges);
//		double DISTANCE_THRESHOLD = 1;
//		for(int i = 0; i < newCharges.size(); i++){
//			for(int j = 0; j < newCharges.size(); j++){
//				if(i != j && newCharges.get(i).distanceTo(newCharges.get(j)) < DISTANCE_THRESHOLD){
//					if(i < j){
//						Charge c = newCharges.get(j);
//						newCharges.get(i).setCharge(newCharges.get(i).getCharge() + c.getCharge());
//						newCharges.remove(j);
//						j--;
//					} else{
//						Charge c = newCharges.get(i);
//						newCharges.get(j).setCharge(newCharges.get(j).getCharge() + c.getCharge());
//						newCharges.remove(i);
//						i--;
//					}
//				}
//			}
//		}
//
//		List<Charge> toRemove = new ArrayList<>();
//		for(Charge c : newCharges){
//			if(Math.abs(c.getCharge()) <= 0.00001){
//				toRemove.add(c);
//			}
//		}
//
//		newCharges.removeAll(toRemove);
//		charges.clear();
//		charges.addAll(newCharges);
//
//		// for(Charge c : charges){
//		// if(!newCharges.contains(c)){
//		// newCharges.add(c);go
//
//		// }
//		// }
//
//		// System.out.println(charges.size() + " : " + charges);
//	}
//
//	public void run(){
//		frame.setVisible(true);
//
//		Timer drawTimer = new Timer();
//		drawTimer.schedule(new TimerTask(){
//			@Override
//			public void run(){
//				if(drawing){
//					Container c = frame.getContentPane();
//					if(c != null && c.getGraphics() != null){
//						paintComponent(c.getGraphics());
//					}
//				}
//
//				if(update)
//					update();
//				else
//					lastUpdateTime = System.currentTimeMillis();
//				// repaint();
//			}
//		}, 0, 1000 / drawingFPS);
//
//		// Timer updateTimer = new Timer();
//		// updateTimer.schedule(new TimerTask(){
//		// @Override
//		// public void run(){
//		// if(update)
//		// update();
//		// else
//		// lastUpdateTime = System.currentTimeMillis();
//		// }
//		// }, 0, 1000 / updateFPS);
//	}
//
//	@Override
//	public void paintComponent(Graphics g){
//		if(centerPos == null){
//			// System.out.println(getHeight() + " : " + frame.getHeight() +
//			// " : " + frame.getContentPane().getHeight());
//			centerPos = new Dimension(frame.getWidth() / 2, frame.getHeight() / 2);
//		}
//
//		// System.out.println("DRAW " + i++);
//		if(offScrImg == null || offScrImg.getWidth(null) != getWidth() || offScrImg.getHeight(null) != getHeight()){
//			offScrImg = createImage(getWidth(), getHeight());
//			if(offScrImg == null){
//				return;
//			}
//
//			offG = offScrImg.getGraphics();
//		}
//
//		if(offScrImg == null){
//			return;
//		}
//
//		if(offG == null){
//			offG = offScrImg.getGraphics();
//		}
//
//		setAntiAlias(offG, true);
//
//		offG.setColor(getBackground());
//		offG.fillRect(0, 0, getWidth(), getHeight());
//
//		synchronized(charges){
//			for(Charge c : charges){
//				if(c != null){
//					c.draw(offG, scaleFactor, centerPos);
//				}
//			}
//		}
//
//		synchronized(testCharges){
//			for(TestCharge tc : testCharges){
//				if(tc != null){
//					tc.draw(offG, scaleFactor, centerPos);
//				}
//			}
//		}
//
//		offG.setColor(Color.WHITE);
//		FontMetrics fm = offG.getFontMetrics();
//		offG.drawString(String.format("Zoom: %.2f%%", scaleFactor * 100), 10, 10 + fm.getAscent());
//		// offG.drawLine(centerPos.width, 0, centerPos.width, getHeight());
//		// offG.drawLine(0, centerPos.height, getWidth(), centerPos.height);
//
//		if(g != null){
//			g.drawImage(offScrImg, getX(), getY(), null);
//		}
//	}
//
//	private void update(){
//		// System.out.println("UPDATE " + j++);
//		if(lastUpdateTime < 0){
//			lastUpdateTime = System.currentTimeMillis();
//			return;
//		}
//		// double deltaT = (System.currentTimeMillis() - lastUpdateTime) /
//		// 1000.0;
//		// synchronized(charges){
//		// for(Charge c : charges){
//		// c.update(deltaT);
//		// }
//		// }
//
//		synchronized(testCharges){
//			for(TestCharge tc : testCharges){
//				// tc.update(charges, deltaT);
//				tc.update(charges, 1.0 / drawingFPS);
//			}
//		}
//
//		lastUpdateTime = System.currentTimeMillis();
//	}
//
//	public static void setAntiAlias(Graphics g, boolean isAntiAliased){
//		Graphics2D g2d = (Graphics2D) g;
//		RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
//				isAntiAliased ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
//		renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
//
//		g2d.setRenderingHints(renderHints);
//	}
}
