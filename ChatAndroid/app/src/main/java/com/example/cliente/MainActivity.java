package com.example.cliente;

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

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private EditText etLibro; private EditText et; private TextView tv; private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etLibro = ((EditText) findViewById(R.id.et2));
        et = ((EditText) findViewById(R.id.et));
        tv =((TextView) findViewById(R.id.textView));
        context = MainActivity.this;
    }

    public void llamada(View view) {
        if (et.getText().toString().length() > 0) {
            MyATaskCliente myATaskYW = new MyATaskCliente();
            myATaskYW.execute(et.getText().toString());
        } else {
            Toast.makeText(context, "Escribe la URL a atacar", Toast.LENGTH_LONG).show();
        }
    }

    public void publicar(View view) {
        if (et.getText().toString().length() > 0 && etLibro.getText().toString().length() > 0) {
            MyATFichar myATaskYW = new MyATFichar();
            myATaskYW.execute(et.getText().toString(), etLibro.getText().toString());
        } else {
            Toast.makeText(context, "Escribe URL o contendio a guardar", Toast.LENGTH_LONG).show();
        }
    }

    public void modificar(View view) {
        if (et.getText().toString().length() > 0 && etLibro.getText().toString().length() > 0) {
            MyATModificar myATaskYW = new MyATModificar();
            myATaskYW.execute(et.getText().toString(), etLibro.getText().toString());
        } else {
            Toast.makeText(context, "Escribe URL o contendio a modificar", Toast.LENGTH_LONG).show();
        }
    }

    public void eliminar(View view) {
        if (et.getText().toString().length() > 0) {
            MyATEliminar myATaskYW = new MyATEliminar();
            myATaskYW.execute(et.getText().toString());
        } else {
            Toast.makeText(context, "Escribe la URL", Toast.LENGTH_LONG).show();
        }
    }
    class MyATaskCliente extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... values) {
            String pp = values[0];
            StringBuffer sb = new StringBuffer();
            try {
                URL url = new URL(pp);
                HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
                if (myConnection.getResponseCode() == 200) {
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "utf-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    try{
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            jsonReader.beginObject();
                            while (jsonReader.hasNext()) {
                                String key = jsonReader.nextName();
                                if (key.equals("id")) {
                                    sb.append("{id: " + jsonReader.nextString() + ", ");
                                } else if (key.equals("name")) {
                                    sb.append("name: '" + jsonReader.nextString() + "'}\n");
                                } else {
                                    jsonReader.skipValue();
                                }
                            }
                            jsonReader.endObject();
                        }
                    } catch (Exception e) {
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            String key = jsonReader.nextName();
                            if (key.equals("id")) {
                                sb.append("{id: " + jsonReader.nextString() + ", ");
                            } else if (key.equals("name")) {
                                sb.append("name: '" + jsonReader.nextString() + "'}\n");
                            } else {
                                jsonReader.skipValue();
                            }
                        }
                        jsonReader.endObject();
                    }

                } else {
                    tv.setText("no ha ido");
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String value) {
            tv.setText(value);
        }
    }

    public class MyATFichar extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... values) {
            String pp = values[0];
            String libro = values[1];
            StringBuffer sb = new StringBuffer();
            try {
                URL url = new URL(pp);
                HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
                myConnection.setReadTimeout(10000);
                myConnection.setConnectTimeout(15000);
                myConnection.setRequestMethod("POST");
                myConnection.setDoInput(true);
                myConnection.setDoOutput(true);
                myConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", libro);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String query = jsonObject.toString();

                OutputStream os = myConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                myConnection.connect();
                if (myConnection.getResponseCode() == 200) {
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "utf-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        String key = jsonReader.nextName();
                        if (key.equals("id")) {
                            sb.append("{" + jsonReader.nextString() + ", ");
                        } else if (key.equals("name")) {
                            sb.append(jsonReader.nextString() + "}\n");
                        } else {
                            jsonReader.skipValue();
                        }
                    }
                    jsonReader.endObject();
                } else {
                    tv.setText("no ha ido");
                }
            } catch (Exception e) {
                tv.setText(e.getMessage());
            }
            return sb.toString();
        }
        @Override
        protected void onPostExecute(String value) {
            tv.setText("SE HA INTRODUCIDO:\n "+ value);
        }

    }

    public class MyATModificar extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... values) {
            String pp = values[0];
            String libro = values[1];
            StringBuffer sb = new StringBuffer();
            try {
                URL url = new URL(pp);
                HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
                myConnection.setReadTimeout(10000);
                myConnection.setConnectTimeout(15000);
                myConnection.setRequestMethod("PUT");
                myConnection.setDoInput(true);
                myConnection.setDoOutput(true);
                myConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", libro);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String query = jsonObject.toString();

                OutputStream os = myConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                myConnection.connect();
                if (myConnection.getResponseCode() == 200) {
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "utf-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        String key = jsonReader.nextName();
                        if (key.equals("id")) {
                            sb.append("{" + jsonReader.nextString() + ", ");
                        } else if (key.equals("name")) {
                            sb.append(jsonReader.nextString() + "}\n");
                        } else {
                            jsonReader.skipValue();
                        }
                    }
                    jsonReader.endObject();
                } else {
                    tv.setText("no ha ido");
                }
            } catch (Exception e) {
                tv.setText(e.getMessage());
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String value) {
            tv.setText(value);
        }


    }

    public class MyATEliminar extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... values) {
            StringBuffer sb = new StringBuffer();
            try {
                // Primera conexión para recoger datos
                URL url = new URL(values[0]);
                HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
                try {
                    if (myConnection.getResponseCode() == 200) {
                        InputStream responseBody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "utf-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);
                        try {
                            jsonReader.beginObject();
                            while (jsonReader.hasNext()) {
                                String key = jsonReader.nextName();
                                if (key.equals("id")) {
                                    sb.append("con id: " + jsonReader.nextString() + "\n");
                                } else if (key.equals("name")) {
                                    sb.append("con nombre: " + jsonReader.nextString());
                                } else {
                                    jsonReader.skipValue();
                                }
                            }
                            jsonReader.endObject();
                        } finally {
                            jsonReader.close();
                        }
                    }
                } finally {
                    myConnection.disconnect();
                }


                HttpURLConnection deleteConnection = (HttpURLConnection) url.openConnection();
                try {
                    deleteConnection.setRequestMethod("DELETE");
                    deleteConnection.connect();
                    if (deleteConnection.getResponseCode() == 200) {}
                } finally {
                    deleteConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sb.toString();
        }
        @Override
        protected void onPostExecute(String value) {
            tv.setText("SE HA ELIMINADO LIBRO:\n "+ value);
        }
    }


}
**/
