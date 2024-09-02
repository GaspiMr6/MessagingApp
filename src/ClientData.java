import java.net.Socket;
import java.net.SocketAddress;

public class ClientData {
    public ClientData(SocketAddress socketAddress, Socket socket){
        ClientID = socketAddress;
        ClientSocket = socket;
    }
    public SocketAddress ClientID;
    public Socket ClientSocket;
}