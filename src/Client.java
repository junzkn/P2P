import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;

import javax.swing.*;

/**
 * UDP P2P测试客户机：发送消息，接收服务器发来的消息
 *
 * @author hujunhua
 *
 */
public class Client extends Thread {
    // 公网服务器地址  
    private SocketAddress destAdd = new InetSocketAddress("192.168.155.3",10000);
    private DatagramSocket sendSocket;// 发送Socket对象  
    // 显示接收到消息的组件  
    private JTextArea jta_receive = new JTextArea(10, 25);
    private JComboBox jcb_addList = new JComboBox();// 其他客户机的地址显示  

    private Client() {
        try {
            sendSocket = new DatagramSocket();
        } catch (Exception e) {
        }
    }

    public void run() {
        try {
            while (true) {
                byte[] recvData = new byte[1024];
                // 创建接收数据包对象  
                DatagramPacket recvPacket = new DatagramPacket(recvData,recvData.length);
                System.out.println("等待接收数据到来。。。");
                sendSocket.receive(recvPacket);
                byte[] data = recvPacket.getData();
                // 读到信息
                ByteArrayInputStream bins = new ByteArrayInputStream(data);
                ObjectInputStream oins = new ObjectInputStream(bins);
                Object dataO = oins.readObject();
                if (dataO instanceof Set) {// 服务器端的地址列表  
                    Set<InetSocketAddress> othersAdds = (Set<InetSocketAddress>) dataO;
                    jcb_addList.removeAllItems();
                    // 将收到的地址列表加入到界面下拉框中  
                    for (InetSocketAddress it : othersAdds) {
                        jcb_addList.addItem(it);
                    }
                } else if (dataO instanceof String) {
                    String s = (String) dataO;
                    // 显示你到界面  
                    jta_receive.append(s + "\r\n");
                } else {
                    String s = "不知道发了什么鬼:" + dataO;
                    jta_receive.append(s + "\r\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendP2PMsg(String msg, InetSocketAddress dest) {
        try {
            ByteArrayOutputStream bous = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bous);
            oos.writeObject(msg);
            oos.flush();
            byte[] data = bous.toByteArray();
            DatagramPacket dp = new DatagramPacket(data, data.length, dest);
            sendSocket.send(dp);
            System.out.println("已发送一条点对点消息给：" + dest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendRequestMsg(String msg) {
        try {
            byte[] buffer = msg.getBytes();
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length,destAdd);
            sendSocket.send(dp);
            System.out.println("已发送给服务器：" + msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 显示主界面  
    private void setUpUI() {

        JFrame frame = new JFrame("P2P测试——客户端");
        frame.setLayout(new FlowLayout());
        frame.setSize(500, 400);

        JButton button = new JButton("获取其他客户及地址");
        button.setPreferredSize(new Dimension(200,25));
        jcb_addList.setPreferredSize(new Dimension(200,25));
        frame.add(button);
        frame.add(jcb_addList);


        // 发送请求给服务器端的其他客户及列表信息  
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendRequestMsg("取得地址");

            }
        });


        JLabel jl_receiver = new JLabel("接收到的消息");
        jl_receiver.setPreferredSize(new Dimension(400,25));
        jl_receiver.setHorizontalAlignment(0) ;
        JLabel jl_send = new JLabel("发送的消息：");
        jl_send.setPreferredSize(new Dimension(400,25));
        jl_send.setHorizontalAlignment(0) ;
        final JTextField jtf_send = new JTextField(20);// 发送输入框
        JButton bu_send = new JButton("发送");

        jta_receive.setLineWrap(true);        //激活自动换行功能

        frame.add(jl_receiver);
        frame.add(jta_receive);
        frame.add(jl_send);
        frame.add(jtf_send);
        frame.add(bu_send);

        // 发送事件监听  
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String msg = jtf_send.getText();
                // 得到选中的目标地址  
                InetSocketAddress dest = (InetSocketAddress) jcb_addList.getSelectedItem();
                sendP2PMsg(msg, dest);
                jtf_send.setText("");

            }
        };


        bu_send.addActionListener(al);
        jtf_send.addActionListener(al);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(3);

    }

    // 主函数  
    public static void main(String[] args) {
        Client sender = new Client();
        sender.start();
        sender.setUpUI();
    }

}  