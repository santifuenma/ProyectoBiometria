package com.example.sprint0_biometria;

import android.os.AsyncTask;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClienteApi {

    // Se define la URL de la API en Plesk donde se enviarán las mediciones
    private static final String API_URL = "https://sfuenma.upv.edu.es/api/index.php?endpoint=mediciones";

    // Metodo público para enviar una medición
    // Se arranca una AsyncTask que hace la operación en segundo plano
    public static void sendMeasurement(int idSensor, double valor) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    // Se abre la conexión HTTP hacia la API
                    URL url = new URL(API_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    // Se configura la conexión para que sea un POST en formato JSON
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setDoOutput(true);

                    // Se prepara el contenido JSON con el id del sensor y el valor
                    String jsonInput = "{\"id_sensor\":" + idSensor + ",\"valor\":" + valor + "}";

                    // Se escribe el JSON en el cuerpo de la petición
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonInput.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Se obtiene el código de respuesta del servidor
                    int code = conn.getResponseCode();
                    System.out.println("Respuesta del servidor: " + code);

                } catch (Exception e) {
                    // Si ocurre un error, se imprime en la consola
                    e.printStackTrace();
                }
                return null; // No se devuelve nada porque es solo envío
            }
        }.execute(); // Se ejecuta la tarea en paralelo
    }
}
