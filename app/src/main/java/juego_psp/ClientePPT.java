package juego_psp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class ClientePPT {
    public static void main(String[] args) {
        String host= "localhost";
        int puerto= 9999; // el cliente intentará conectarse a este puerto

        boolean conectado= true;

        System.out.println("---------Iniciando partida----------");
        
        // se debe definir en el try-with-resources:
        //socket, printwriter, bufferedreader y scanner 

        try (Socket socket = new Socket(host, puerto);
            // 1. Canales de comunicación
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             // 2. Escáner para leer del teclado
             Scanner teclado = new Scanner(System.in)){
           
        // PASO 1: Mensaje de bienvenida del juego
            // El servidor envía "BIENVENIDO..." nada más conectarnos.
            String mensajeBienvenida = in.readLine();
            System.out.println("SERVIDOR DICE: " + mensajeBienvenida);

            System.out.println("Introduce tu jugada"+"Piedra -- Papel -- Tijeras");

            while(conectado){
                System.out.print(">--");
                String comando = teclado.nextLine();

                out.println(comando);

                // Recibir respuesta del servidor

                String respuestaServidor= in.readLine();

                if(respuestaServidor==null){
                    System.out.println("el servidor cerró la conexión");
                } else {
                    System.out.println("SERVIDOR: "+respuestaServidor);
                }

                // Salida del bucle:
                 if (comando.equalsIgnoreCase("SALIR")) {
                    conectado = false;
                }
            }
            
            
            
        } catch (ConnectException e) {
            System.err.println("Error (de conexión al servidor): "+e.getMessage());
        } catch(IOException e){
            System.err.println("Error (de comunicación"+e.getMessage());
        }
            System.out.println("Partida finalizada????");

        
    }

}
