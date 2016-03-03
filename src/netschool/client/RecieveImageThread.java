package netschool.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

public class RecieveImageThread extends SwingWorker<Boolean, byte[]> {
	int port = 9000;
	String groupIP = "224.5.6.7";
	MulticastSocket socket;
	DatagramPacket packet;
	byte[] data, data2;
	byte[] tmp = new byte[65003];
	int off1 = 65000, off2 = 65000 * 2, off3 = 65000 * 3;
	int dataLength = 250000, dataLength2;
	int cnum = 1;
	int sum = 0;
	int count = 0;

	BufferedImage im;
	ViewFrame vFrame;

	public RecieveImageThread(ViewFrame v) {
		vFrame = v;
	}

	@Override
	protected Boolean doInBackground() throws Exception {
		boolean stop = false;
		try {
			socket = new MulticastSocket(port);
			InetAddress inet = InetAddress.getByName(groupIP);
			socket.joinGroup(inet);
			packet = new DatagramPacket(tmp, 65003);
			data = new byte[dataLength];
			data2 = new byte[dataLength];
		} catch (IOException e) {
			e.printStackTrace();
			stop = true;
		}
		while (!stop) {
			try {
				socket.receive(packet);
				if (tmp[0] != cnum) {
					if (tmp[0] > cnum || tmp[0] == 0) {
						reset();
						cnum = tmp[0];
					} else if (tmp[0] < cnum)
						continue;
				}
				sum = tmp[1];
				if (tmp[2] == 0) {
					off1 = packet.getLength() - 3;
					dataLength2 += off1;
					System.arraycopy(tmp, 3, data, 0, off1);
				}
				if (tmp[2] == 1) {
					off2 = packet.getLength() - 3;
					dataLength2 += off2;
					System.arraycopy(tmp, 3, data, off1, off2);
					off2 += off1;
				}
				if (tmp[2] == 2) {
					off3 = packet.getLength() - 3;
					dataLength2 += off3;
					System.arraycopy(tmp, 3, data, off2, off3);
					off3 += off2;
				}
				if (tmp[2] == 3) {
					dataLength2 += packet.getLength() - 3;
					System.arraycopy(tmp, 3, data, off3, packet.getLength() - 3);

				}
				count++;
				if (count == sum) {
					System.arraycopy(data, 0, data2, 0, dataLength2);
					publish(data2);
					reset();
				}
			} catch (IOException e) {
				e.printStackTrace();
				stop = true;
			}
		}
		return false;
	}

	@Override
	protected void process(List<byte[]> chunks) {
		if (!chunks.isEmpty()) {
			try {
				im = ImageIO.read(new ByteArrayInputStream(data));
				if (im != null)
					vFrame.setIm(im);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void done() {
		super.done();
		try {
			if (!isCancelled()&&!get()) {
				vFrame.showMessage("Lỗi! Vui lòng tắt ứng dụng và chạy lại!");
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	private void reset() {
		Arrays.fill(data2, (byte) 0);
		off1 = 65000;
		off2 = 65000 * 2;
		off3 = 65000 * 3;
		dataLength2 = 0;
		count = 0;
	}

	
}

