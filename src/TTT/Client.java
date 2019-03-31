package TTT;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;

import javax.swing.JComboBox;

public class Client extends Thread {
	//��������ַ
	private InetSocketAddress serverAddr = new InetSocketAddress("10.30.23.134",9527);
	//UDPsocket�����ڷ��ͺͽ������ݱ�
	private DatagramSocket socket; 
	private View view;
	
	public static void main(String[] args) {
		Client newclient = new Client();
		newclient.start();
	}
	
	//ÿ�½�һ���ͻ���ʱ����ʼ��
	public Client() {
		try {	
			socket = new DatagramSocket();
			this.view = new View(socket);
			online();
		} catch(Exception e) {}
	}

	@Override
	public void run() {
		try {
			while(true) {
				//�������ݱ�
				byte[] buffer = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
                //���ݱ�����
				byte[] bytedata = packet.getData();
                ByteArrayInputStream bis = new ByteArrayInputStream(bytedata);
                ObjectInputStream ois = new ObjectInputStream(bis);
	            Object data = ois.readObject();
	            //��ñ��ͻ��˵ĵ�ַ
	            String localIP = InetAddress.getLocalHost().getHostAddress();
            	InetSocketAddress localAddr = new InetSocketAddress(localIP, socket.getLocalPort());
	            if(data instanceof Set) {	//����Ǽ���
	            	//ת�����ϵĸ�ʽ
	            	Set<InetSocketAddress> allSocketAddr = (Set<InetSocketAddress>) data;
	            	//������촰�ڵ�������
	            	view.dropList.removeAllItems();
	            	//�����пͻ��˵ĵ�ַ����������
	            	for(InetSocketAddress clientAddr : allSocketAddr) {
//	            		if(!clientAddr.equals(localAddr)	//�Լ����⣨���������Լ�������Ϣ��
	            		view.dropList.addItem(clientAddr);
	            	}
	            } else if(data instanceof String) {	//������ַ���
	            	//��÷�����Ϣ�Ķ���ĵ�ַ
	            	InetSocketAddress otherAddr = new InetSocketAddress(
	            			packet.getAddress(), packet.getPort());
	            	//�ַ���ת����ʽ
	            	String msg = (String) data;
	            	//�����촰����ʾ��Ϣ
	            	if(otherAddr.equals(serverAddr)) {	//����Ƿ���˷�����Ϣ
	            		view.infoWindow.append("��������" + msg + "\n");
	            	} else if(otherAddr.equals(localAddr)){		//������Լ�������Ϣ
	            		//ʲôҲ����
	            	} else {
	            		view.infoWindow.append("ta��" + msg + "\n");
	            	}
	            } else {
	            	view.infoWindow.append("�Է����͵ļȲ��Ǽ���Ҳ�����ַ���." + "\n");
	            }
	            
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//���ߣ�֪ͨ���пͻ��˸ÿͻ����ߣ���������пͻ��˵ĵ�ַ
	public void online() {
		try {
			//��һ�������ݱ�������server�Լ��Ѿ����� 
			String str = "";
			byte[] buffer = str.getBytes();
			DatagramPacket dp = new DatagramPacket(buffer, buffer.length, serverAddr);
			socket.send(dp);
			//�����촰����ʾ�Լ��Ѿ�����
			InetAddress localIP = socket.getLocalAddress();
			int port = socket.getLocalPort();
			view.infoWindow.append("�������ߣ���ĵ�ַΪ��" + localIP + ":" + port + "\n");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
