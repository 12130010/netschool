package netschool.server;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import netschool.client.RecieveCommandThread;
import netschool.client.RecieveImageThread;
import netschool.client.ViewFrame;

public class AdminFrame extends JFrame {
	public static RecieveImageThread recieveImageThread;
	public static ViewFrame v;
	//
	SendImageThread sendImageThread;
	RecieveReplyThread recieveReplyThread; // running thread

	public AdminFrame() {

		recieveReplyThread = new RecieveReplyThread();
		recieveReplyThread.start();

		createView();
		this.setTitle("NetSchool - Server - © Hoang Nhuoc Quy");
		this.setSize(700, 400);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	private void createView() {
		this.getContentPane().setLayout(new BorderLayout());
		JPanel root = new JPanel(new BorderLayout());
		this.getContentPane().add(root);

		JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
		// -- Header --
		// Start/stop screen
		JButton button = new JButton("Start screen");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton btn = (JButton) e.getSource();
				System.out.println(btn.getText());
				switch (btn.getText()) {
				case "Start screen":
					if (startScreen())
						btn.setText("Stop screen");
					break;
				case "Stop screen":
					if (stopScreen())
						btn.setText("Start screen");
					break;

				default:
					break;
				}
			}
		});
		header.add(button);

		// Who are you
		button = new JButton("List client");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					sendUDP(RecieveCommandThread.WHO_ARE_YOU);
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, e1.getMessage());
				}
			}
		});
		header.add(button);

		final JTextField jtClientName = new JTextField(10);
		header.add(jtClientName);

		// client start
		button = new JButton("Client start screen");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					sendUDP(RecieveCommandThread.RUN_SHARE_SCREEN + jtClientName.getText());
					startClientScreen();
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, e1.getMessage());
				}
			}
		});
		header.add(button);

		// client stop
		button = new JButton("Client stop screen");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					sendUDP(RecieveCommandThread.STOP_SHARE_SCREEN);
					stopClientScreen();
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, e1.getMessage());
				}
			}
		});
		header.add(button);

		// -- header end --

		JPanel left = new JPanel(new BorderLayout());
		final JTextArea jtArea = new JTextArea(10, 20);
		left.add(jtArea);

		button = new JButton("Refresh");
		left.add(button, BorderLayout.NORTH);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jtArea.setText("");
				for (String s : recieveReplyThread.getListName()) {
					jtArea.append(s);
					jtArea.append("\n");
				}
			}
		});

		root.add(header, BorderLayout.NORTH);
		root.add(left, BorderLayout.WEST);
	}

	public boolean startScreen() {
		if (sendImageThread == null) {
			sendImageThread = new SendImageThread();
			sendImageThread.start();
			return true;
		}
		return false;
	}

	public boolean stopScreen() {
		if (sendImageThread != null && sendImageThread.isAlive()) {
			sendImageThread.myStop();
			sendImageThread = null;
			return true;
		}
		return false;
	}

	public void sendUDP(String mes) throws IOException {
		String host = "224.5.6.7";
		int port = 9001;

		byte[] message = mes.getBytes();

		// Get the internet address of the specified host
		InetAddress address = InetAddress.getByName(host);

		// Initialize a datagram packet with data and address
		DatagramPacket packet = new DatagramPacket(message, message.length, address, port);

		// Create a datagram socket, send the packet through it, close it.
		DatagramSocket dsocket = new DatagramSocket();
		dsocket.send(packet);
		dsocket.close();
	}

	public static void startClientScreen() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				v = new ViewFrame();
				v.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				v.setTitle("NetSchool - Server - © Hoang Nhuoc Quy");
				recieveImageThread = new RecieveImageThread(v);
				recieveImageThread.execute();
			}
		});
	}

	public static void stopClientScreen() {
		if (recieveImageThread != null && v != null) {
			recieveImageThread.cancel(true);
			v.dispose();
			System.out.println("Main Stop");
		}
	}

	public static void main(String[] args) {
		new AdminFrame();
	}
}
