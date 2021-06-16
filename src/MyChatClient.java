import java.io.IOException;
import java.net.Socket;

public class MyChatClient {
	public static MyChatGui gui;
	public MyChatClientConnection comm;		// communication with the server
	private static boolean hungup = false;				// has the server hung up on us?

	public MyChatClient(Socket sock) throws IOException {		

		comm = new MyChatClientConnection(sock, this);
		comm.setDaemon(true);
		comm.start();

	}
	public void hangUp() {
		hungup = true;
	}
	
	public static void main(String args[]) {
		try {
			MyChatClient chatClient = new MyChatClient(new Socket("localhost", 4242));
			gui = new MyChatGui(chatClient);
			while (!hungup) {
				chatClient.comm.send("POP_SMS");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}