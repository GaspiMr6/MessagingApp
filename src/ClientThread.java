import java.net.Socket;

public class ClientThread extends Thread {

    Socket clientSocket;

    public ClientThread(Socket clientSocketReceived){
        clientSocket = clientSocketReceived;
    }

    public void run (){

        String response;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                response = SocketUtilities.ReadSocketData(clientSocket);
                switch (response) {
                    case "SERVER_SHUTDOWN":
                        System.out.println("The server has been shutdown");
                        System.exit(0);
                        break;
                    default:
                        System.out.println(response);
                        break;
                }

            }
        } catch (Exception e){
            System.out.println("Client Thread Exception: " + e.getMessage());
        }
    }
}
