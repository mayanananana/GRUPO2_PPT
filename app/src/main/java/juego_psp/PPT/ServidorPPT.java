package juego_psp.PPT;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Servidor multihilo para el juego de Piedra, Papel o Tijera.
 * Escucha conexiones entrantes y delega cada cliente a un {@link HandlerPPT}.
 * Su rol es gestionar las conexiones de los jugadores, emparejarlos y coordinar
 * las partidas.
 */
public class ServidorPPT implements Runnable {
    /** Puerto en el que escuchará el servidor */
    private final int puerto;
    /** Flag para controlar el ciclo de vida del servidor */
    private volatile boolean activo = true;

    /**
     * Crea una instancia del servidor.
     * 
     * @param puerto Puerto TCP en el que se abrirá el servidor.
     */
    public ServidorPPT(int puerto) {
        this.puerto = puerto;
    }

    /**
     * Ciclo principal del servidor. Acepta conexiones en un bucle mientras esté
     * activo.
     * Cada nueva conexión de cliente es manejada en un hilo separado por un
     * {@link HandlerPPT}.
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            serverSocket.setSoTimeout(1000); // Revisa el flag activo cada segundo
            System.out.println("Servidor de PPT iniciado en el puerto " + puerto);

            while (activo) {
                try {
                    Socket jugadorSocket = serverSocket.accept();
                    System.out.println("NUEVO JUGADOR: " + jugadorSocket.getInetAddress());

                    // Cada cliente es manejado en su propio hilo por un HandlerPPT
                    new Thread(new HandlerPPT(jugadorSocket)).start();

                } catch (SocketTimeoutException e) {
                    // Timeout para poder comprobar el bucle 'while(activo)'
                }
            }
        } catch (IOException e) {
            if (activo) {
                System.err.println("Error en el servidor: " + e.getMessage());
            }
        } finally {
            System.out.println("Servidor detenido.");
        }
    }

    /**
     * Detiene el bucle principal del servidor de forma segura.
     * Establece el flag 'activo' a falso para que el bucle principal termine.
     */
    public void detener() {
        activo = false;
    }
}
