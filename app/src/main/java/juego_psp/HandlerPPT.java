package juego_psp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HandlerPPT implements Runnable {

    // esta clase actualmente es un copia y pega del ejercicio usado en clase 
  
    private final Socket socket;

    public HandlerPPT(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // Usamos Try-with-resources para asegurar el cierre del socket de
        // ESTE cliente
        try (socket; // Java 9+ permite referenciar la variable final aquí
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("BIENVENIDO A LA CALCULADORA. Formato: OPERACION NUM1 NUM2 (Ej: SUMAR 5 10)");

            // Leemos líneas completas (más fácil que leer bytes fijos)
            String linea;
            while ((linea = in.readLine()) != null) {
                if (linea.equalsIgnoreCase("SALIR")) break;

                try {
                    // Protocolo simple basado en texto: "SUMAR 5 5"
                    String[] partes = linea.split(" ");

                    if (partes.length != 3) {
                        out.println("ERROR: Formato incorrecto. Usa: OPERACION NUM1 NUM2");
                        continue;
                    }

                    String operacion = partes[0].toUpperCase();
                    int n1 = Integer.parseInt(partes[1]);
                    int n2 = Integer.parseInt(partes[2]);
                    int resultado = 0;

                    switch (operacion) {
                        case "SUMAR": resultado = n1 + n2; break;
                        case "RESTAR": resultado = n1 - n2; break;
                        case "MULT":  resultado = n1 * n2; break;
                        default:
                            out.println("ERROR: Operación desconocida (Usa SUMAR, RESTAR, MULT)");
                            continue;
                    }
                    out.println("RESULTADO: " + resultado);

                } catch (NumberFormatException e) {
                    out.println("ERROR: Los argumentos deben ser números enteros.");
                }
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado abruptamente.");
        } finally {
            System.out.println("Cliente finalizado: " + socket.getInetAddress());
        }
    }

}
