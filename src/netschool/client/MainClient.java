package netschool.client;

import javax.swing.SwingUtilities;

public class MainClient {
	public static RecieveImageThread recieveImageThread;
	public static ViewFrame v;
	public static RecieveCommandThread recieveCommandThread;

	public static void run() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				v = new ViewFrame();
				recieveImageThread = new RecieveImageThread(v);
				recieveImageThread.execute();
				recieveCommandThread.setvFrame(v);
			}
		});
	}

	public static void stop() {
		recieveImageThread.cancel(true);
		v.dispose();
		System.out.println("Main Stop");
	}

	public static void main(String[] args) {
		recieveCommandThread = new RecieveCommandThread(v);
		recieveCommandThread.start();
		run();
	}

}