package juego_psp.ppt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Cliente de red para el juego de Piedra, Papel o Tijera.
 * Gestiona la conexión con el servidor y la comunicación asíncrona recibiendo
 * mensajes en un hilo separado.
 */
public class ClientePPT {

    /** Nombre del host o dirección IP del servidor */
    private final String host;
    /** Puerto TCP del servidor */
    private final int puerto;
    /** Socket de conexión */
    private Socket socket;
    /** Canal de salida de datos hacia el servidor */
    private PrintWriter out;
    /** Canal de entrada de datos desde el servidor */
    private BufferedReader in;
    /**
     * Callback para procesar mensajes recibidos y enviarlos a la interfaz de
     * usuario
     */
    private final Consumer<String> onMessageReceived; // Callback para mensajes del servidor

    /**
     * Inicializa el cliente con los datos de conexión necesarios.
     * 
     * @param host              Dirección del servidor (ej. "localhost").
     * @param puerto            Puerto del servidor (ej. 9999).
     * @param onMessageReceived Función que recibe los mensajes del servidor para
     *                          actualizar la UI.
     */
    public ClientePPT(String host, int puerto, Consumer<String> onMessageReceived) {
        this.host = host;
        this.puerto = puerto;
        this.onMessageReceived = onMessageReceived;
    }

    /**
     * Establece la conexión con el servidor e inicia el hilo para escuchar
     * mensajes.
     */
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

    /**
     * Escucha continuamente los mensajes del servidor en un bucle mientras la
     * conexión esté abierta.
     */
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

    /**
     * Envía una jugada o mensaje de control al servidor.
     * 
     * @param mensaje El texto a enviar (ej. "PIEDRA", "SI", "NO").
     */
    public void enviarMensaje(String mensaje) {
        if (out != null) {
            out.println(mensaje);
        }
    }

    /**
     * Cierra el socket y libera los recursos de red de forma segura.
     */
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
