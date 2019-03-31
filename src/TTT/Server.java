package TTT;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

public class Server {
	  //������пͻ��˵�ַ�ļ���
    private Set<InetSocketAddress> clientAddrSet = new HashSet<>();
    private DatagramSocket socket;
    
    public static void main(String[] args) throws Exception {
    	Server main = new Server();
    	main.startServer();
    }
    
    private void startServer() throws Exception {
    	//�������UDPЭ���socket
    	socket = new DatagramSocket(9527);
    	System.out.println("�������ѽ������˿ڣ�9527");
    	while(true) {
    		 //ָ�����ջ�������С
            byte[] buffer=new byte[1024];
            //�����������ݱ�����
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            //�������ݱ�
            socket.receive(packet);
            //�õ��ͻ��˵�ip�Ͷ˿ں�
            InetAddress clientIP = packet.getAddress();
            int clientPort = packet.getPort();
            //���õ�ַ������ip�Ͷ˿ڣ����뼯����
            InetSocketAddress clientAddr = new InetSocketAddress(clientIP, clientPort);
            clientAddrSet.add(clientAddr);
            String inform = clientAddr + " �Ѿ�����";
            System.out.println(inform);
            //�������������ͻ��ˣ��ÿͻ���������
            for(InetSocketAddress client:clientAddrSet) {
            	if(!client.equals(clientAddr)) sendPacket(inform, client);
                sendPacket(clientAddrSet, client);
            }
    	}
    }
    
    //��Ŀ���ַ�������ݱ�
    private void sendPacket(Object object, InetSocketAddress desAddr) throws Exception {
    	 ByteArrayOutputStream bous = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(bous);
         oos.writeObject(object);
         oos.flush();
         byte[] data=bous.toByteArray();
         DatagramPacket dp = new DatagramPacket(data, data.length);
         dp.setSocketAddress(desAddr);
         socket.send(dp);
    }
}
