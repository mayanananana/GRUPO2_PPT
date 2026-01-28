package juego_psp.PPT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class ClientePPT {
    public static void main(String[] args) {
        String host = "localhost";
        int puerto = 9999;

        System.out.println("--------- Iniciando Cliente de PPT ---------");

        try (Socket socket = new Socket(host, puerto);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Scanner teclado = new Scanner(System.in)) {

            String lineaServidor;
            // Bucle principal para leer mensajes del servidor
            while ((lineaServidor = in.readLine()) != null) {
                System.out.println("SERVIDOR: " + lineaServidor);

                // Si el servidor pide una jugada
                if (lineaServidor.contains("Ingresa tu jugada (PIEDRA, PAPEL, TIJERAS) o SALIR para terminar")) {
                    System.out.print("Tu jugada > ");
                    String jugada = teclado.nextLine();
                    out.println(jugada);
                }
                // Si el servidor pregunta si jugar de nuevo
                else if (lineaServidor.contains("¿Quieres jugar otra partida?")) {
                    System.out.print("Respuesta (SI/NO) > ");
                    String respuesta = teclado.nextLine();
                    out.println(respuesta);
                    if (!respuesta.equalsIgnoreCase("SI")) {
                        // El servidor enviará un mensaje de despedida, que se imprimirá
                        // en la siguiente iteración del bucle y luego el bucle terminará
                        // porque el servidor cerrará la conexión (readLine devolverá null).
                    }
                }
                // Si el servidor nos dice que nos vayamos
                else if (lineaServidor.contains("Hasta pronto!")) {
                    break;
                } else if (lineaServidor.contains("DESEMPATE")) {
                    System.out.println("Ingresa tu jugada (PIEDRA, PAPEL, TIJERAS) o SALIR para terminar");
                    System.out.print("Tu jugada >");
                    String respuesta = teclado.nextLine();
                    out.println(respuesta);
                }
            }

        } catch (ConnectException e) {
            System.err.println("Error de conexión. ¿Está el servidor en marcha? " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de comunicación: " + e.getMessage());
        }

        System.out.println("--------- Cliente de PPT Finalizado ---------");
    }
}
