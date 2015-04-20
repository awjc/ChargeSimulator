package gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class CenteredJFrame extends JFrame {
	public CenteredJFrame(String title, int width, int height) {
		super(title);
		setSize(width, height);
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((scrSize.width - width) / 2, (scrSize.height - height) / 2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args){
		String s = "515-868-7421.";
//		char c;
		int pos = 0;
		int[] arr = new int[10];
		for(int i = 0; i < s.length(); i++){
			if(Character.isDigit(s.charAt(i))){
				arr[pos] = s.charAt(i);
				pos++;
			}
		}
	}
}
