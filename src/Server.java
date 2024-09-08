import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {

	public Map<String, ArrayList<ClientData>> Rooms = new HashMap<String, ArrayList<ClientData>>();

	private ArrayList<Socket> sockets = new ArrayList<>();

	public static void main(String[] args) {

		CheckArguments(args);
		Server server = new Server();
		int port = GetListeningPort(args);
		server.ListenToClients(port);
	}

	private void ListenToClients(int port){
		while (true) {

			try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"))) {
				// Wait for clients to join
				System.out.println("Waiting for clients...");
				Socket clientSocket = serverSocket.accept();
				sockets.add(clientSocket);

				// Client connected -> We create a thread to communicate with him
				System.out.println("Client connected " + clientSocket.getRemoteSocketAddress());
				Thread thread = new ServerThread(this, clientSocket);
				thread.start();

			} catch (IOException exception) {
				System.out.println("IOException: " + exception.toString());
			}
		}
	}

	private static void CheckArguments(String[] args){
		if (args.length < 1) {
			System.out.println("Error: You must specify the listening port of the server");
			System.exit(-1);
		}
	}

	private static int GetListeningPort(String[] args){
		int port = Integer.parseInt(args[0]);
		System.out.println("Listening on port " + port);
		return port;
	}
}