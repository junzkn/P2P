package TTT;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import javax.swing.*;


public class View {
	//������Ϣ��ʾ��
	public JTextArea infoWindow = new JTextArea(10,30);
	public JScrollPane scrollPane = new JScrollPane(infoWindow);;
	//������,�ں������ͻ��˵ĵ�ַ������ip�Ͷ˿ڣ�
	public JComboBox dropList = new JComboBox(); 
	//�������ĵ�ַ
	public DatagramSocket socket;
	
	public View(DatagramSocket socket){
		this.socket = socket;
		createWindow();
	}
	
	public void sendMessage(String msg, InetSocketAddress address) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bos);
			os.writeObject(msg);
			os.flush();
			byte[] data = bos.toByteArray();
			DatagramPacket packet = new DatagramPacket(data, data.length, address);
        	//����Ļ����ʾ�Լ�������Ϣ
			infoWindow.append("�㣺" + msg + "\n");
			socket.send(packet);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//�������촰��
	public void createWindow() {
		JFrame frame = new JFrame("���촰��1");
		frame.setLayout(new FlowLayout());
		frame.setSize(500, 400);
		
		dropList.setPreferredSize(new Dimension(139,25));
		infoWindow.setSize(50, 30);
		
		JLabel tips = new JLabel("��ѡ��һ��Ҫͨ�ŵ��û�:");
		tips.setPreferredSize(new Dimension(165,50));
		JLabel label = new JLabel("");
		label.setPreferredSize(new Dimension(500, 25));
		label.setHorizontalAlignment(0);	//������ʾ
		
		JTextField inputText = new JTextField(24); 
		JButton sendButton = new JButton("����");
		
		frame.add(tips);
		frame.add(dropList);
		frame.add(scrollPane);
		frame.add(label);
		frame.add(inputText);
		frame.add(sendButton);
		
        ActionListener listen = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//����������Ϣ
            	String text = inputText.getText();
            	if(!text.equals("")) {
            		//����������ĵ�ip�Ͷ˿�
    				InetSocketAddress desAddr = (InetSocketAddress) dropList.getSelectedItem(); 
    				sendMessage(text, desAddr); 
    				inputText.setText("");
            	}
			}
        };
        
	    inputText.addActionListener(listen);
        sendButton.addActionListener(listen);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(3);

       
	}
}
