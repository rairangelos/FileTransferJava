package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import FileMessage.FileMessage;

public class Server {

	private ServerSocket serverSocket;
	private Socket socket;
	private Map<String, ObjectOutputStream> streamMap = new HashMap<String, ObjectOutputStream>();
	
	public Server() {
		try {
			serverSocket = new ServerSocket(5555);
			System.out.println("Servidor engaged!");
			
			while (true) {
				socket = serverSocket.accept();
				
				new Thread(new ListenerSocket(socket)).start();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public class ListenerSocket implements Runnable {
		private ObjectOutputStream outputStream;
		private ObjectInputStream inputStream;
		
		public ListenerSocket(Socket socket) throws IOException {
			this.outputStream = new ObjectOutputStream(socket.getOutputStream());
			this.inputStream = new ObjectInputStream(socket.getInputStream());
		}

		@Override
		public void run() {
			FileMessage message = null;
			try {
				while((message = (FileMessage) inputStream.readObject()) != null) {
					streamMap.put(message.getCliente(), outputStream);
					if(message.getFile() != null) {
						for (Map.Entry<String, ObjectOutputStream> kv : streamMap.entrySet()) {
							if(!message.getCliente().equals(kv.getKey())) {
								kv.getValue().writeObject(message);
							}
						}
					}
				}
			} catch (Exception e) {
				streamMap.remove(message.getCliente());
				System.out.println(message.getCliente() + "desconectado!");
			}
			
		}
		
	}
	
	public static void main(String[] args) {
		new Server();
	}
	
	
}
