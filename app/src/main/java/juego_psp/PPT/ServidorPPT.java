package juego_psp.PPT;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServidorPPT implements Runnable {
    private final int puerto;
    private volatile boolean activo = true;

    public ServidorPPT(int puerto) {
        this.puerto = puerto;
    }

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

    public void detener() {
        activo = false;
    }
}
