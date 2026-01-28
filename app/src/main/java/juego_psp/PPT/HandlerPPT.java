package juego_psp.PPT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class HandlerPPT implements Runnable {

    private static final String PIEDRA = "PIEDRA";
    private static final String PAPEL = "PAPEL";
    private static final String TIJERAS = "TIJERAS";
    private static final String SALIR = "SALIR";
    private static final String SI = "SI";
    private static final String NO = "NO";
    private static final String JUGAR_OTRA_VEZ = "JUGAR_OTRA_VEZ";
    private static final String FIN_JUEGO = "FIN_JUEGO";

    private final Socket socket;
    private int puntosJugador;
    private int puntosBot;
    private final String[] opciones = { PIEDRA, PAPEL, TIJERAS };
    private int rondasJugadas;
    private final Random random = new Random();

    public HandlerPPT(Socket socket) {
        this.socket = socket;
        this.puntosJugador = 0;
        this.puntosBot = 0;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("BIENVENIDO A PIEDRA, PAPEL O TIJERAS (AL MEJOR DE 2 PUNTOS)");

            boolean jugarDeNuevo = true;
            while (jugarDeNuevo) {
                jugarPartida(in, out);
                out.println("\n¿Quieres jugar otra partida? (SI/NO)");
                String respuesta = in.readLine();
                if (respuesta == null || !respuesta.equalsIgnoreCase(SI)) {
                    jugarDeNuevo = false;
                    out.println("Gracias por jugar. ¡Hasta la próxima!");
                    out.println(FIN_JUEGO);
                }
            }

        } catch (IOException e) {
            System.out.println("Cliente desconectado: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error al cerrar el socket: " + e.getMessage());
            }
            System.out.println("Cliente finalizado: " + socket.getInetAddress());
        }
    }

    private void jugarPartida(BufferedReader in, PrintWriter out) throws IOException {
        puntosJugador = 0;
        puntosBot = 0;
        rondasJugadas = 0;

        while (puntosJugador < 2 && puntosBot < 2) {
            out.println("\n--- RONDA " + (rondasJugadas + 1) + " ---");
            out.println("Ingresa tu jugada (" + PIEDRA + ", " + PAPEL + ", " + TIJERAS + ") o " + SALIR
                    + " para terminar:");

            String jugadaCliente = in.readLine();
            if (jugadaCliente == null || jugadaCliente.equalsIgnoreCase(SALIR)) {
                out.println("Juego terminado.");
                return;
            }

            if (!esJugadaValida(jugadaCliente)) {
                out.println("ERROR: Jugada no válida. Inténtalo de nuevo.");
                continue;
            }

            String jugadaServidor = generarJugadaServidor();
            out.println("El servidor eligió: " + jugadaServidor);

            String resultadoRonda = determinarGanador(jugadaCliente.toUpperCase(), jugadaServidor);
            out.println(resultadoRonda);

            rondasJugadas++;
            out.println("MARCADOR: Jugador " + puntosJugador + " - " + puntosBot + " Servidor");
        }

        anunciarGanadorPartida(out);
    }

    private void anunciarGanadorPartida(PrintWriter out) {
        out.println("\n--- FIN DE LA PARTIDA ---");
        if (puntosJugador > puntosBot) {
            out.println("¡FELICIDADES! Has ganado la partida.");
        } else if (puntosBot > puntosJugador) {
            out.println("El servidor ha ganado la partida. Mejor suerte la próxima vez.");
        } else {
            out.println("¡La partida ha terminado en empate!");
        }
    }

    private String generarJugadaServidor() {
        return opciones[random.nextInt(opciones.length)];
    }

    boolean esJugadaValida(String jugada) {
        for (String opcion : opciones) {
            if (opcion.equalsIgnoreCase(jugada)) {
                return true;
            }
        }
        return false;
    }

    String determinarGanador(String jugador, String servidor) {
        if (jugador.equals(servidor)) {
            return "RONDA: EMPATE";
        }

        switch (jugador) {
            case PIEDRA:
                if (servidor.equals(TIJERAS)) {
                    puntosJugador++;
                    return "RONDA: GANAS (Piedra vence a Tijeras)";
                } else {
                    puntosBot++;
                    return "RONDA: PIERDES (Papel vence a Piedra)";
                }
            case PAPEL:
                if (servidor.equals(PIEDRA)) {
                    puntosJugador++;
                    return "RONDA: GANAS (Papel vence a Piedra)";
                } else {
                    puntosBot++;
                    return "RONDA: PIERDES (Tijeras vence a Papel)";
                }
            case TIJERAS:
                if (servidor.equals(PAPEL)) {
                    puntosJugador++;
                    return "RONDA: GANAS (Tijeras vence a Papel)";
                } else {
                    puntosBot++;
                    return "RONDA: PIERDES (Piedra vence a Tijeras)";
                }
            default:
                return "RONDA: JUGADA INVÁLIDA"; // No debería ocurrir por la validación previa
        }
    }
}