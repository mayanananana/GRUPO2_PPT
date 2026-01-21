package juego_psp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServidorPPT {
    private static final int PUERTO= 9999; // el servidor espera conexiones en este puerto
    private static volatile boolean active= true;
    public static void main(String[] args) {

        // Permite el cierre de la app con Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        System.out.println("\n--- Solicitud de parada detectada (Ctrl+C) ---");
        active = false;
        }));

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)){

            serverSocket.setSoTimeout(2000); // Revisa el flag activo, cada 2 segundos 

        System.out.println("Bienvenido a Piedra, Papel y Tijera arrancando en el puerto "+PUERTO);
        System.out.println("Conecta varios jugadores a la vez");

        while(active){
            try {

                // ACEPTAR 
                Socket jugador = serverSocket.accept();
                System.out.println("NUEVO JUGADOR: "+jugador.getInetAddress());
                
                // DELEGAR 
                // Libera al main para volver al bucle
                new Thread(new HandlerPPT(jugador)).start(); // TODO implementar constructor delHandler 
                
            } catch (SocketTimeoutException e) {
                System.out.print("."); 
            }
        }
            
        } catch (IOException e) {
            System.err.println("Error en el servidor: "+e.getMessage() );
        }
        
    }
    
}
