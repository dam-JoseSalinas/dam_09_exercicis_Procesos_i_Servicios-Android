package com.example.cliente;

<<<<<<< HEAD
//para la app
import android.content.Context; import android.os.Bundle; import androidx.annotation.Nullable; import androidx.appcompat.app.AppCompatActivity;

import android.util.Log; import android.widget.Toast;

import android.view.View; import android.widget.Button; import android.widget.EditText;

//para hilo android
import android.os.AsyncTask; import android.app.ProgressDialog;

//comunicacion por socket 
import java.net.Socket; import java.io.ObjectInputStream; import java.io.ObjectOutputStream;
import java.io.IOException;
//import java.io.InputStream;
//import java.io.PrintStream;
//import java.net.InetAddress;
//import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
	//Grafico
	Button btnEnviar;
	EditText cajaEnviar;
	EditText cajaRecibir;
	Context context = this;

	//Cliente
	ObjectOutputStream streamSalida;
	String mensajeSalida;
	Socket socketCliente;
	
	//Comunicacion
	boolean comunicacionEstablecida = false;
	boolean finalizarComunicacion;
	
	//Server
    ObjectInputStream streamEntrada;
    static final int SERVERPORT = 5000;
	static final String ADDRESS = "10.0.2.2";
	String mensajeEntrada;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//establecer grafico
		btnEnviar = ((Button) findViewById(R.id.button));
		cajaEnviar = ((EditText) findViewById(R.id.editText));
		cajaRecibir = ((EditText) findViewById(R.id.editText2));


        Thread conexionThread = new Thread() {
            @Override
            public void run() {
                //establecer comunicacion
                while(!comunicacionEstablecida) {
                    try {
                        socketCliente = new Socket(ADDRESS, SERVERPORT);
                        String miServer = socketCliente.getInetAddress().getHostName();
                        Log.i("tag", "Comunicacion establecida con: " + miServer);
                        streamSalida = new ObjectOutputStream(socketCliente.getOutputStream());
                        streamEntrada = new ObjectInputStream(socketCliente.getInputStream());
                        comunicacionEstablecida = true;
                        finalizarComunicacion = false;
                    } catch (Exception e) {e.printStackTrace();}
                }
            }
        };
        conexionThread.start();
        //establecer recibido de mensajes
        Thread recibirThread = new Thread() {
            @Override
            public void run() {
                //establecer comunicacion
                try {
                    do {
                        mensajeEntrada = (String) streamEntrada.readObject();
                        cajaRecibir.append("\nSERVIDOR: " + mensajeEntrada);
                        if(mensajeEntrada.equals("fin\n")) {
                            finalizarComunicacion = true;
                            comunicacionEstablecida = false;
                            streamSalida.close();
                            streamEntrada.close();
                            socketCliente.close();
                        }
                    } while (!finalizarComunicacion);
                } catch (Exception e) {e.printStackTrace();}
            }
        };
        recibirThread.start();
		//establecer envio
		btnEnviar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if(cajaEnviar.getText().toString().length()>0){
					EnviarMensajesAsyncTask enviarThread = new EnviarMensajesAsyncTask();
					enviarThread.execute(cajaEnviar.getText().toString());
				}else{
					Toast.makeText(context, "Escribe mensaje", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	class EnviarMensajesAsyncTask extends AsyncTask<String,Void,String>{

        @Override
		protected String doInBackground(String... values) {
            try {
                streamSalida.writeObject(values[0] + "\n");
                streamSalida.flush();
                cajaEnviar.setText("");
            } catch (Exception e) {
            }
            return  null;
        }
	}
}
/**
import androidx.appcompat.app.AppCompatActivity;

=======
>>>>>>> ae050a6 (funciona)
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    Context context = this;
    Button btnEnviar;
    EditText cajaEnviar;
    EditText cajaRecibir;
    static final String ADDRESS = "10.0.2.2";
    static final int SERVERPORT = 5000;
    ObjectOutputStream streamSalida;
    ObjectInputStream streamEntrada;
    boolean conectado = false;

    RecibirMensajes recibirThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEnviar = findViewById(R.id.button);
        cajaEnviar = findViewById(R.id.editText);
        cajaRecibir = findViewById(R.id.editText2);

        recibirThread = new RecibirMensajes();
        recibirThread.execute();

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(cajaEnviar.getText().toString().length()>0){
                    EnviarMensajes enviarThread = new EnviarMensajes();
                    enviarThread.execute(cajaEnviar.getText().toString());
                }else{
                    Toast.makeText(context, "Escribe mensaje", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    class EnviarMensajes extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... values) {
            try {
                streamSalida.writeObject(values[0] + "\n");
                streamSalida.flush();
                cajaEnviar.setText("");
            } catch (Exception e) {
            }
            return  null;
        }
    }

    class RecibirMensajes extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Socket socketCliente = new Socket(ADDRESS, SERVERPORT);
                String miServer = socketCliente.getInetAddress().getHostName();
                Log.i("tag", "Comunicacion establecida con: " + miServer);
                streamSalida = new ObjectOutputStream(socketCliente.getOutputStream());
                streamEntrada = new ObjectInputStream(socketCliente.getInputStream());
                conectado = true;
                while (conectado) {

                    String mensajeRecibido = (String) streamEntrada.readObject();
                    publishProgress(mensajeRecibido);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... mensajes) {
            for (String mensaje : mensajes) {
                cajaRecibir.append(mensaje + "\n");
            }
        }
    }
}
