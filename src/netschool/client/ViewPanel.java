package netschool.client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ViewPanel extends JPanel {
	int width = 1366;
	int height = 768;
	BufferedImage im;

	public ViewPanel() {
		this.setPreferredSize(new Dimension(width, height));
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(im, 0, 0, null);
	}
	public void setIm(BufferedImage im) {
		this.im = im;
		if (width != im.getWidth() || height != im.getHeight()) {
			width = im.getWidth();
			height = im.getHeight();
			this.setPreferredSize(new Dimension(width, height));
		}
		this.repaint();
	}
}
