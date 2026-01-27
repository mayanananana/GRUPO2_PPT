package juego_psp.PPT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

/**
 * Gestiona la lógica de una partida de Piedra, Papel o Tijera para un cliente
 * específico.
 * Esta clase implementa Runnable para ser ejecutada en un hilo separado por el
 * servidor.
 */
public class HandlerPPT implements Runnable {

    // Constantes de juego
    private static final String PIEDRA = "PIEDRA";
    private static final String PAPEL = "PAPEL";
    private static final String TIJERAS = "TIJERAS";
    private static final String SALIR = "SALIR";
    private static final String SI = "SI";
    private static final String NO = "NO";
    private static final String JUGAR_OTRA_VEZ = "JUGAR_OTRA_VEZ";
    private static final String FIN_JUEGO = "FIN_JUEGO";

    /** El socket de conexión con el cliente */
    private final Socket socket;
    /** Puntuación acumulada del jugador en la partida actual */
    private int puntosJugador;
    /** Puntuación acumulada del servidor (bot) en la partida actual */
    private int puntosBot;
    /** Opciones disponibles para jugar */
    private final String[] opciones = { PIEDRA, PAPEL, TIJERAS };
    /** Contador de rondas jugadas en la partida actual */
    private int rondasJugadas;
    /** Generador de números aleatorios para la jugada del bot */
    private final Random random = new Random();

    /**
     * Constructor para inicializar el manejador con el socket del cliente.
     * 
     * @param socket El socket de la conexión aceptada.
     */
    public HandlerPPT(Socket socket) {
        this.socket = socket;
        this.puntosJugador = 0;
        this.puntosBot = 0;
    }

    /**
     * Ciclo de vida del hilo. Gestiona la bienvenida y el bucle para jugar
     * múltiples partidas.
     */
    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("BIENVENIDO A PIEDRA, PAPEL O TIJERAS");

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

    /**
     * Gestiona una partida completa. La partida termina cuando alguien llega a 2
     * puntos.
     * 
     * @param in  Stream de entrada desde el cliente.
     * @param out Stream de salida hacia el cliente.
     * @throws IOException Si ocurre un error de comunicación.
     */
    private void jugarPartida(BufferedReader in, PrintWriter out) throws IOException {
        puntosJugador = 0;
        puntosBot = 0;
        rondasJugadas = 0;

        while (puntosJugador < 2 && puntosBot < 2) {
            pausa(500);
            out.println("\n--- RONDA " + (rondasJugadas + 1) + " ---");
            pausa(500);
            out.println("Ingresa tu jugada (" + PIEDRA + ", " + PAPEL + ", " + TIJERAS + ") o " + SALIR
                    + " para terminar:");
            out.print(">");

            String jugadaCliente = in.readLine();
            if (jugadaCliente == null || jugadaCliente.equalsIgnoreCase(SALIR)) {
                out.println("Juego terminado.");
                return;
            }

            if (!esJugadaValida(jugadaCliente)) {
                out.println("ERROR: Jugada no válida. Inténtalo de nuevo.");
                continue;
            }

            pausa(400);
            String jugadaServidor = generarJugadaServidor();
            out.println("El servidor eligió: " + jugadaServidor);

            pausa(600);
            out.println(determinarGanador(jugadaCliente.toUpperCase(), jugadaServidor));

            pausa(500);
            rondasJugadas++;
            out.println("MARCADOR: Jugador " + puntosJugador + " - " + puntosBot + " Servidor");
        }

        anunciarGanadorPartida(out);
    }

    /**
     * Anuncia el resultado final de la partida al cliente.
     * 
     * @param out Stream de salida hacia el cliente.
     */
    private void anunciarGanadorPartida(PrintWriter out) {
        pausa(800);
        out.println("\n--- FIN DE LA PARTIDA ---");
        pausa(500);
        if (puntosJugador > puntosBot) {
            out.println("¡FELICIDADES! Has ganado la partida.");
        } else if (puntosBot > puntosJugador) {
            out.println("El servidor ha ganado la partida. Mejor suerte la próxima vez.");
        } else {
            out.println("¡La partida ha terminado en empate!");
        }
    }

    /**
     * Introduce una pausa en la ejecución para mejorar la legibilidad del flujo en
     * el cliente.
     * 
     * @param ms Milisegundos de espera.
     */
    private void pausa(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Genera una jugada aleatoria para el servidor.
     * 
     * @return Una de las opciones: PIEDRA, PAPEL o TIJERAS.
     */
    private String generarJugadaServidor() {
        return opciones[random.nextInt(opciones.length)];
    }

    /**
     * Valida si el texto introducido por el cliente es una jugada válida.
     * 
     * @param jugada El texto a validar.
     * @return true si es válido, false en caso contrario.
     */
    boolean esJugadaValida(String jugada) {
        for (String opcion : opciones) {
            if (opcion.equalsIgnoreCase(jugada)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compara las jugadas del jugador y del bot y actualiza el marcador.
     * 
     * @param jugador  La jugada del cliente.
     * @param servidor La jugada del bot.
     * @return Un mensaje indicando el resultado de la ronda.
     */
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
