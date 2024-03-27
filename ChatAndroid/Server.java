import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private final static int PORT = 5000;
	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			System.out.println("Servidor establecido");
			Socket clienteSocket;
			
			while(true) {
				clienteSocket = serverSocket.accept();
				//entrada
				BufferedReader input = new BufferedReader(
					new InputStreamReader(clienteSocket.getInputStream())
				)
				//salida
				PrintStream output = new PrintStream(clienteSocket.getOutputStream());
				
				//lectura
				String request = input.readLine();
				System.out.println("Cliente > peticion [" + request + "]");
				
				//escritura
				String strOutput = process(request);
				System.out.println("Servidor > peticion procesada [" + strOutput + "]");
				
				//limpiar buffer
				output.flush();
				output.println(strOutput);
				clienteSocket.close();
			}
		} catch (IOException e) {
			System.err.println(ex.getMessage());
		}
	}
}
