
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * UDP P2P测试通信转发服务器：接收，转发消息
 * @author hujunhua
 *
 */
public class Server {
    //存放所有客户机地址的队列
    private Set<InetSocketAddress> clientAddSet = new HashSet<>();
    //启动接收的UDP端口服务器
    private void startServer() throws Exception{
        DatagramSocket socket=new DatagramSocket(10000);
        System.out.println("UDP服务器等待接收数据："+socket.getLocalSocketAddress());
        while(true){
            //指定接收缓冲区大小
            byte[] buffer=new byte[1024];
            //创建接收数据包对象
            DatagramPacket packet=new DatagramPacket(buffer, buffer.length);
            //阻塞等待数据到来，如果数据，存入packet中的缓冲区
            socket.receive(packet);
            //得到发送方得IP地址和端口号
            InetAddress clientAdd=packet.getAddress();
            int cientPort=packet.getPort();
            InetSocketAddress address=new InetSocketAddress(clientAdd, cientPort);
            //将这个地址加入到队列中
            clientAddSet.add(address);
            byte[] recvData=packet.getData();//取得数据
            //去掉空格
            String s=new String(recvData).trim();
            //接收到后，打印出收到的数据长度
            System.out.println("服务器收到数据："+s+"form:"+address);
            for(InetSocketAddress dclient:clientAddSet){
                String temf=address+"，到服务器取数据！";
                //转发服务器的地址和端口列表数据
                ByteArrayOutputStream bous=new ByteArrayOutputStream();
                ObjectOutputStream oos=new ObjectOutputStream(bous);
                oos.writeObject(temf);
                oos.flush();
                byte[] data=bous.toByteArray();
                DatagramPacket mp=new DatagramPacket(data, data.length);
                mp.setSocketAddress(dclient);//发送给客户端地址
                socket.send(mp);
            }
            //转发服务器断的地址列表数据
            ByteArrayOutputStream bous=new ByteArrayOutputStream();
            ObjectOutputStream oos=new ObjectOutputStream(bous);
            oos.writeObject(clientAddSet);
            oos.flush();
            byte[] data=bous.toByteArray();
            //发送服务器段保存的各个客户机的地址信息
            DatagramPacket sendp=new DatagramPacket(data, data.length);
            sendp.setSocketAddress(address);
            socket.send(sendp);
        }
    }
    /**
     * 启动主函数
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Server reciver=new Server();
        reciver.startServer();
    }


}