package Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFileChooser;

import FileMessage.FileMessage;

public class Client {
	
	private Socket socket;
	private ObjectOutputStream outputStream;
	
	public Client() throws IOException{
		this.socket = new Socket("localhost", 5555);
		this.outputStream = new ObjectOutputStream(socket.getOutputStream());
		
		new Thread(new ListenerSocket(socket)).start();
		
		menu();
	}
	
	private void menu() throws IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Digite seu nome: ");
		String nome = scanner.nextLine();
		
		this.outputStream.writeObject(new FileMessage(nome));
		
		int option = 0;
		
		while (option != 1) {
			System.out.println("1 - Sair | 2 - Enviar :");
			option = scanner.nextInt();
			if(option == 2) {
				send(nome);
			}else if(option == 1) {
				System.exit(0);;
			}
		}	
	}
	
	private void send(String nome) throws IOException {
		JFileChooser fileChooser = new JFileChooser();
		int opt = fileChooser.showOpenDialog(null);
		
		if (opt == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			
			this.outputStream.writeObject(new FileMessage(nome, file));
		}
	}
	
	private class ListenerSocket implements Runnable{
		private ObjectInputStream inputStream;
		
		public ListenerSocket(Socket socket) throws IOException {
			this.inputStream = new ObjectInputStream(socket.getInputStream());
		}

		@Override
		public void run() {
			FileMessage message = null;
			try {
				while((message = (FileMessage) inputStream.readObject()) != null) {
					System.out.println("\nVocê recebeu um arquivo de " + message.getCliente());
					System.out.println("O nome do arquivo é: "+ message.getFile().getName());
					
					//imprime(message);
					
					save(message);
					
					System.out.println("1 - Sair | 2 - Enviar :");
				}
			}catch(IOException e) {
				e.printStackTrace();
			}catch(ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	
	private void save(FileMessage message) {
		try {
			
			Thread.sleep(new Random().nextInt(1000));
			
			long time = System.currentTimeMillis();
			
			FileInputStream fileInputStream = new FileInputStream(message.getFile());
			FileOutputStream fileOutputStream = new FileOutputStream("c:\\z\\"+time+"_"+message.getFile().getName());
			
			FileChannel fin = fileInputStream.getChannel();
			FileChannel fout = fileOutputStream.getChannel();
			
			long size = fin.size();
			
			fin.transferTo(0, size, fout);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		
		
//	private void imprime(FileMessage message) throws IOException {
//		try {
//			FileReader fileReader = new FileReader(message.getFile());
//			BufferedReader bufferedReader = new BufferedReader(fileReader);
//			String linha;
//			while((linha = bufferedReader.readLine()) != null) {
//				System.out.println(linha);
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
		
}
	public static void main(String[] args) {
		try {
			new Client();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
