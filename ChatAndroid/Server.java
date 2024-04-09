import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private boolean connected = false;

	public Server() {
		try {
			serverSocket = new ServerSocket(5000);
			System.out.println("Servidor establecido");
			System.out.println("Esperando conexiones...");

			clientSocket = serverSocket.accept();
			System.out.println("Conexión establecida con cliente: " + clientSocket.getInetAddress());
			outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			inputStream = new ObjectInputStream(clientSocket.getInputStream());
			connected = true;

			ExecutorService executorService = Executors.newFixedThreadPool(2);
			executorService.execute(new ReceptorMensajes());
			executorService.execute(new EnviadorMensajes());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class ReceptorMensajes implements Runnable {
		@Override
		public void run() {
			try {
				while (connected) {
					String mensajeEntrada = (String) inputStream.readObject();
					System.out.print("\nMENSAJE DE CLIENTE: " + mensajeEntrada + "YO SERVIDOR: ");
					if (mensajeEntrada.equals("fin")) {
						outputStream.close();
						inputStream.close();
						clientSocket.close();
						serverSocket.close();
						connected = false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class EnviadorMensajes implements Runnable {
		@Override
		public void run() {
			try {
				while (connected) {
					System.out.print("YO SERVIDOR: ");
					Scanner scanner = new Scanner(System.in);
					String mensajeSalida = scanner.nextLine();
					outputStream.writeObject(mensajeSalida);
					outputStream.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new Server();
	}
}