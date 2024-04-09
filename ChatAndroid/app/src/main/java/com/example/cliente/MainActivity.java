package com.example.cliente;
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
    boolean recibirMensajes = true; // Controla la ejecución del bucle de recibir mensajes

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEnviar = findViewById(R.id.button);
        cajaEnviar = findViewById(R.id.editText);
        cajaRecibir = findViewById(R.id.editText2);

        // Iniciar el hilo para recibir mensajes
        recibirThread = new RecibirMensajes();
        recibirThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(cajaEnviar.getText().toString().length() > 0) {
                    // Enviar el mensaje ingresado en el EditText
                    EnviarMensajes enviarThread = new EnviarMensajes();
                    enviarThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, cajaEnviar.getText().toString());
                } else {
                    Toast.makeText(context, "Escribe mensaje", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // AsyncTask para enviar mensajes al servidor
    class EnviarMensajes extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... values) {
            try {
                streamSalida.writeObject(values[0] + "\n");
                streamSalida.flush();
                cajaEnviar.setText(""); // Limpiar el EditText después de enviar el mensaje
            } catch (Exception e) {
                // Manejar la excepción, por ejemplo, mostrar un mensaje de error
            }
            return  null;
        }
    }

    // AsyncTask para recibir mensajes del servidor
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

                // Bucle para recibir mensajes hasta que se cambie el valor de recibirMensajes
                while (conectado && recibirMensajes) {
                    String mensajeRecibido = (String) streamEntrada.readObject();
                    publishProgress(mensajeRecibido);
                }

                // Cerrar los flujos y el socket
                streamEntrada.close();
                streamSalida.close();
                socketCliente.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... mensajes) {
            for (String mensaje : mensajes) {
                cajaRecibir.append(mensaje + "\n"); // Mostrar el mensaje recibido en el EditText de recibir
            }
        }
    }
}

