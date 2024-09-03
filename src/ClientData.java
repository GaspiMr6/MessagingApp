import java.net.Socket;
import java.net.SocketAddress;

public class ClientData {
    public ClientData(SocketAddress socketAddress, Socket socket, String Nickname){
        ClientID = socketAddress;
        ClientSocket = socket;
        ClientNickname = Nickname;
    }
    public SocketAddress ClientID;
    public Socket ClientSocket;
    public String ClientNickname;
}