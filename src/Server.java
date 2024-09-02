import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {

	public Map<String, ArrayList<ClientData>> Rooms = new HashMap<String, ArrayList<ClientData>>();

	public static void main(String[] args) {

		// Check args
		if (args.length < 1) {
			System.out.println("Error: You must specify the listening port of the server");
			System.exit(-1);
		}

		Server server = new Server();
		int port = Integer.parseInt(args[0]);
		System.out.println("Listening on port " + port);

		while (true) {

			try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"))) {
				// Wait for clients to join
				System.out.println("Waiting for clients...");
				Socket clientSocket = serverSocket.accept();

				// Client connected -> We create a thread to communicate with him
				System.out.println("Client connected " + clientSocket.getRemoteSocketAddress());
				Thread thread = new ServerThread(server, clientSocket);
				thread.start();

			} catch (IOException exception) {
				System.out.println("IOException: " + exception.toString());
			}
		}
	}
}