package netschool.server;

import javax.swing.SwingUtilities;

import netschool.client.RecieveImageThread;
import netschool.client.ViewFrame;

public class MainServer {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new AdminFrame();
			}
		});
	}
}
