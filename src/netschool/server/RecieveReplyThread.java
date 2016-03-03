package netschool.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import netschool.client.MainClient;
import netschool.client.RecieveCommandThread;

public class RecieveReplyThread extends Thread{
	Set<String> listName ;
	int port = 9001;
	String groupIP = "224.5.6.7";
	MulticastSocket socket;
	DatagramPacket packet;
	byte[] data;
	
	public RecieveReplyThread() {
		listName = new TreeSet<String>();
	}
	
	@Override
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
		while(!stop){
			try {
				socket.receive(packet);
				command = new String(packet.getData(),0, packet.getLength());
				if (command != null) {
					if(command.startsWith(RecieveCommandThread.I_AM)){
						StringTokenizer stk = new StringTokenizer(command);
						if(stk.countTokens() == 2){
							stk.nextToken();
							listName.add(stk.nextToken());
							System.out.println(listName);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				stop = true;
			}
		}
		//TODO lỗi
	}
	
	public Set<String> getListName() {
		return listName;
	}
	public void clearListName(){
		listName.clear();
	}
	public static void main(String[] args) {
		new RecieveReplyThread().start();
	}
}
