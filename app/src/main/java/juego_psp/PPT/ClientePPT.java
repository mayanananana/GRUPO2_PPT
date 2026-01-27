package juego_psp.PPT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class ClientePPT {

    private final String host;
    private final int puerto;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final Consumer<String> onMessageReceived; // Callback para mensajes del servidor

    public ClientePPT(String host, int puerto, Consumer<String> onMessageReceived) {
        this.host = host;
        this.puerto = puerto;
        this.onMessageReceived = onMessageReceived;
    }

    public void conectar() {
        try {
            socket = new Socket(host, puerto);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Inicia un hilo para escuchar los mensajes del servidor de forma asíncrona
            Thread listenerThread = new Thread(this::escucharAlServidor);
            listenerThread.setDaemon(true); // El hilo no impedirá que la app se cierre
            listenerThread.start();

        } catch (IOException e) {
            // Notifica al GUI sobre el error de conexión
            onMessageReceived.accept("Error al conectar con el servidor: " + e.getMessage());
        }
    }

    private void escucharAlServidor() {
        try {
            String mensajeDelServidor;
            while ((mensajeDelServidor = in.readLine()) != null) {
                // Usa el callback para enviar el mensaje al controlador de la UI
                onMessageReceived.accept(mensajeDelServidor);
            }
        } catch (IOException e) {
            if (!socket.isClosed()) {
                onMessageReceived.accept("Se ha perdido la conexión con el servidor.");
            }
        } finally {
            desconectar();
        }
    }

    public void enviarMensaje(String mensaje) {
        if (out != null) {
            out.println(mensaje);
        }
    }

    public void desconectar() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                onMessageReceived.accept("Desconectado del servidor.");
            }
        } catch (IOException e) {
            onMessageReceived.accept("Error al desconectar: " + e.getMessage());
        }
    }
}
