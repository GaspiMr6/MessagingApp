import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketUtilities {
    
    public enum EMessageHeader {
        CLIENT_DATA,
        CLIENT_DISCONNECTED,

        SERVER_DATA,
        SERVER_DISCONNECTED,
    }

    public static class Message {
        public Message(EMessageHeader msgHeader, String stringData){
            messageHeader = msgHeader; 
            data = stringData;
        };
        public EMessageHeader messageHeader;
        public String data;
    }


	public static Message ReadSocketData(Socket socket) {

		try {
            InputStream inputStream = socket.getInputStream();
			DataInputStream dataInStream = new DataInputStream(inputStream);
            EMessageHeader msgHeader = EMessageHeader.values()[dataInStream.readInt()];
            String msgData = dataInStream.readUTF();
			return new Message(msgHeader, msgData);
        } catch (IOException e) {
            if (!Thread.currentThread().isInterrupted())
			    System.out.println("Error Reading: " + e + " " + e.getMessage() + " " + e.getCause());
		}
        
		return null;
	}

    public static void WriteSocketData(Socket socket, Message message) {
        try {
            OutputStream outStream = socket.getOutputStream();
            DataOutputStream dataOutStream = new DataOutputStream(outStream);
            dataOutStream.writeInt(message.messageHeader.ordinal());
            dataOutStream.writeUTF(message.data);
        } catch (Exception e) {
            if (!Thread.currentThread().isInterrupted())
                System.out.println("Error Writing: " + e.getMessage() + e.getStackTrace().toString() + " " + e.getCause());
        }
    }

}
