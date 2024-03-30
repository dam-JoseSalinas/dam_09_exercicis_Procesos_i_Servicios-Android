import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class MainCliente {
	//Server
	private final static int PORT = 5000;
	private final static String SERVER = "localhost";

	public static void main(String[] args) {
		Socket socketServidor;
		//Comunicacion
		boolean finalizarComunicacion = false;
		try { 
			System.out.println("Cliente> Inicio");
			while( !finalizarComunicacion ){
				socketServidor = new Socket(SERVER, PORT);
				//lector
				InputStreamReader streamEntrada = new InputStreamReader(socket.getInputStream();
				BufferedReader readerStreamEntrada = new BufferedReader(streamEntrada));
				//escritor
				PrintStream streamSalida = new PrintStream(socketServidor.getOutputStream());
				
				//enviar
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("Cliente> Escriba mensaje:");
				String mensajeSalida = reader.readLine();
				streamSalida.println(mensajeSalida);
				
				//recibir
				String mensajeEntrada = readerStreamEntrada.readLine();
				if( mensajeEntrada != null ) System.out.println("Servidor> " + mensajeEntrada );
				
				if(mensajeEntrada.equals("exit")){
					finalizarComunicacion=true;
					System.out.println("Cliente> Fin de programa");
				}  
				socketServidor.close();
			}
		} catch (IOException ex) {
		System.err.println("Cliente> " + ex.getMessage());   
		}
	}

}
