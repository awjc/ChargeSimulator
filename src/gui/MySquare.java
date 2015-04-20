package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MySquare {
   private static class Square {
      private int x;
      private int y;
      private int sideLength;
      private Color color;
      
      private Square(int x, int y, int sideLength, Color color) {
          this.x = x;
          this.y = y;
          this.sideLength = sideLength;
          this.color = color;
      }
   }

   private static Square square = new Square(100, 100, 50, Color.RED);

   public static void main(String[] args) {
      JFrame f = new JFrame("Square game!");
      f.setSize(800, 600);
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      final MyPanel myPanel = new MyPanel();
      f.addKeyListener(new KeyAdapter() {
          @Override
          public void keyPressed(KeyEvent e) {
             if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                 square.x -= 50;
             } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                 square.x += 50;
             } else if(e.getKeyCode() == KeyEvent.VK_UP) {
                 square.y -= 50;
             } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                 square.y += 50;
             }

             myPanel.repaint();
          }
      });

      f.add(myPanel);
      f.setVisible(true);
   }

   private static class MyPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	@Override
      public void paintComponent(Graphics g) {
         g.setColor(Color.BLACK);
         g.fillRect(0, 0, getWidth(), getHeight());

         g.setColor(square.color);
         g.fillRect(square.x, square.y, square.sideLength, square.sideLength);
      }
   }
}