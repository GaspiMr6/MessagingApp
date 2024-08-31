import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println("Error: You must specify the listening port of the server");
			System.exit(-1);
		}

		Server server = new Server();
		int port = Integer.parseInt(args[0]);
		ServerSocket serverSocket;
		System.out.println("Listening to the port " + port);

		try {
			serverSocket = new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"));

			System.out.println("Waiting for clients...");
			Socket clientSocket = serverSocket.accept();
			System.out.println("Serving a client " + clientSocket.getPort());
			String clientData = server.ReadSocketData(clientSocket);
			System.out.println("I've received " + clientData);
			serverSocket.close();

		} catch (IOException exception) {
			System.out.println("IOException: " + exception.toString());
		}

        System.out.println();
	}

	private String ReadSocketData(Socket socket) {

		try {
			InputStream inputStream = socket.getInputStream();
			DataInputStream dataInStream = new DataInputStream(inputStream);
			String input = dataInStream.readUTF();
			return input;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}