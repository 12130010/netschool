package netschool.client;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;

public class ViewFrame extends JFrame{
	ViewPanel vPanel;
	public ViewFrame() {
		vPanel = new ViewPanel();
		this.getContentPane().add(new JScrollPane(vPanel));
		setSize(700, 700);
		setTitle("NetSchool - Client - © Hoang Nhuoc Quy");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	public void setIm(BufferedImage im) {
		vPanel.setIm(im);
	}
	public static void main(String[] args) throws AWTException {
		ViewFrame vFrame = new ViewFrame();
		Robot r = new Robot();
//		Rectangle rec = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		Rectangle rec = new Rectangle(800,200);
		BufferedImage im = r.createScreenCapture(rec);
		vFrame.setIm(im);
		
		 rec = new Rectangle(500,500);
		 im = r.createScreenCapture(rec);
		vFrame.setIm(im);
	}
	public void showMessage(String mes){
		JOptionPane.showMessageDialog(null, mes);
	}
}
