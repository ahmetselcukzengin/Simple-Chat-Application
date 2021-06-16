import java.net.*;
import java.util.*;
import java.io.*;

public class MyChatServer {
	private static int PORT_NUMBER = 4242;
	
	private ServerSocket listeningSocket;
	private ArrayList<MyChatServerConnection> conns;
	
	public MyChatServer(ServerSocket listen) {
		this.listeningSocket = listen;
		conns = new ArrayList<MyChatServerConnection>();
	}

	/**
	 * Accept new client connections and start new threads 
	 */
	public void getConnections() throws IOException {
		while (true) {
			Socket clientSocket = listeningSocket.accept();
			MyChatServerConnection conn = new MyChatServerConnection(clientSocket, this);
			conn.setDaemon(true);
			conn.start();
			addConnection(conn);
		}
	}

	public synchronized void addConnection(MyChatServerConnection conn) {
		conns.add(conn);
	}

	public synchronized void removeConnection(MyChatServerConnection conn) {
		conns.remove(conn);
	}

	/**
	 * Makinamizdaki tum IP adreslerini dinler (ornegin hem public IP adresimizi, hem de localhost'u).
	 * Istemciler sunucuya baglanmak icin makinamizdaki IP adreslerinden herhangi birini kullanabilir.  
	 * @param portNumber Uygulamanin dinleyecegi port numarasi (TCP)
	 * @return yaratilan ServerSocket nesnesini dondurur
	 * @throws Exception
	 */
	public static ServerSocket initializeServerSocket_AllAddresses(int portNumber) throws Exception {
		ServerSocket serverSocket = new ServerSocket(portNumber);  
		System.out.printf("Chat server listening on port [%d]\n", portNumber);
		return serverSocket;
	}

	/**
	 * Makinamizdaki IP adresleri arasindan yalnizca verilen adresi dinler (ornegin yalnizca public IP adresimizi).
	 * Istemciler sunucuya baglanmak icin yalnizca verilen IP adresini kullanabilir.  
	 * @param listenAddress Uygulamanin dinleyecegi IP adresi
	 * @param portNumber Uygulamanin dinleyecegi port numarasi (TCP)
	 * @return yaratilan ServerSocket nesnesini dondurur
	 * @throws Exception
	 */
	public static ServerSocket initializeServerSocket_SingleAddress(String listenAddress, int portNumber) throws Exception {
		ServerSocket serverSocket = new ServerSocket();  
		InetAddress bindAddress = InetAddress.getByName(listenAddress);
		SocketAddress endPoint = new InetSocketAddress(bindAddress, portNumber);  
		System.out.printf("Chat server listening on address [%s] port [%d]\n", listenAddress, portNumber);
		serverSocket.bind(endPoint);  
		return serverSocket;
	}

	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket;
		serverSocket = initializeServerSocket_AllAddresses(PORT_NUMBER);
		new MyChatServer(serverSocket).getConnections();
	}
}
