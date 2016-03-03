package netschool.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import netschool.server.SendImageThread;

public class RecieveCommandThread extends Thread {
	public static final String I_AM = "iAm";
	public static final String WHO_ARE_YOU = "whoAreYou";
	public static final String RUN_SHARE_SCREEN = "runShareScreen";
	public static final String STOP_SHARE_SCREEN = "stopShareScreen";

	int port = 9001;
	String groupIP = "224.5.6.7";
	MulticastSocket socket;
	DatagramPacket packet;
	byte[] data;

	String myHostName;

	SendImageThread sendImageThread;

	ViewFrame vFrame;

	public RecieveCommandThread(ViewFrame v) {
		this.vFrame = v;
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			myHostName = inetAddress.getHostName().trim();
		} catch (UnknownHostException e) {
			myHostName = "NoName";
		}
	}

	public void run() {
		boolean stop = false;
		try {
			socket = new MulticastSocket(port);
			InetAddress inet = InetAddress.getByName(groupIP);
			socket.joinGroup(inet);
			data = new byte[6500];
			packet = new DatagramPacket(data, 6500);
		} catch (IOException e) {
			e.printStackTrace();
			stop = true;
		}
		String command;
		while (!stop) {
			try {
				socket.receive(packet);
				command = new String(packet.getData(), 0, packet.getLength());
				System.out.println(command);
				if (command != null) {
					if (command.startsWith(WHO_ARE_YOU)) {
						sendUDP(I_AM, myHostName);
					}
					if (command.startsWith(RUN_SHARE_SCREEN)) {
						if (command.endsWith(myHostName)) {
							if (sendImageThread == null) {
								sendImageThread = new SendImageThread();
								sendImageThread.start();
								MainClient.stop();
							}
						}
					}
					if (command.startsWith(STOP_SHARE_SCREEN)) {
						if (sendImageThread != null && sendImageThread.isAlive()) {
							sendImageThread.myStop();
							sendImageThread = null;
							MainClient.run();
						}

					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				stop = true;
			}
		}
		vFrame.showMessage("Lỗi! Vui lòng tắt ứng dụng và chạy lại!");
	}

	public void sendUDP(String com, String mes) {
		try {
			DatagramPacket packet = new DatagramPacket(new byte[200], 200, InetAddress.getByName(groupIP), port);
			String s = com + " " + mes;
			packet.setData(s.getBytes(), 0, s.length());
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setvFrame(ViewFrame vFrame) {
		this.vFrame = vFrame;
	}

	public static void main(String[] args) {
		new RecieveCommandThread(null).start();
	}
}
