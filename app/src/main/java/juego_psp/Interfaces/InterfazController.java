package juego_psp.Interfaces;

import juego_psp.PPT.ClientePPT;
import juego_psp.PPT.ServidorPPT;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

public class InterfazController {

    // Variables de la interfaz FXML
    @FXML private Label titulo;
    @FXML private Label subtitulo;
    @FXML private Label resultado;
    @FXML private Button botonPiedra;
    @FXML private Button botonPapel;
    @FXML private Button botonTijera;
    @FXML private Button botonIniciar;
    @FXML private TextArea areaDeJuego;
    @FXML private HBox cajaBotonesConfirmacion;
    @FXML private Button botonSi;
    @FXML private Button botonNo;

    // Variables para la lógica del juego
    private ServidorPPT servidor;
    private ClientePPT cliente;
    private static final int PUERTO = 9999;
    private static final String HOST = "localhost";

    @FXML
    public void initialize() {
        // Estado inicial de los botones, solo 'Iniciar' está activo.
        botonPiedra.setDisable(true);
        botonPapel.setDisable(true);
        botonTijera.setDisable(true);
    }

    @FXML
    private void iniciarJuego() {
        areaDeJuego.clear();
        areaDeJuego.appendText("Iniciando partida...\n");

        servidor = new ServidorPPT(PUERTO);
        Thread hiloServidor = new Thread(servidor);
        hiloServidor.setDaemon(true);
        hiloServidor.start();
        areaDeJuego.appendText("Servidor iniciado en el puerto " + PUERTO + ".\n");

        cliente = new ClientePPT(HOST, PUERTO, this::mostrarMensajeEnLog);
        cliente.conectar();
        
        areaDeJuego.appendText("Cliente conectado a " + HOST + ":" + PUERTO + ".\n");

        botonIniciar.setDisable(true);
        botonPiedra.setDisable(false);
        botonPapel.setDisable(false);
        botonTijera.setDisable(false);
        subtitulo.setText("¡La partida ha comenzado!");
    }

    private void mostrarMensajeEnLog(String mensaje) {
        Platform.runLater(() -> {
            areaDeJuego.appendText("SERVIDOR: " + mensaje + "\n");
            
            if (mensaje.contains("¿Quieres jugar otra partida?")) {
                solicitarConfirmacion();
            } else if (mensaje.contains("FIN_JUEGO")) {
                finalizarPartida();
            }
        });
    }

    private void solicitarConfirmacion() {
        botonPiedra.setDisable(true);
        botonPapel.setDisable(true);
        botonTijera.setDisable(true);
        cajaBotonesConfirmacion.setVisible(true);
    }

    @FXML
    private void responderSi() {
        cliente.enviarMensaje("SI");
        areaDeJuego.appendText("TÚ: Sí, quiero jugar otra vez.\n");
        cajaBotonesConfirmacion.setVisible(false);
        botonPiedra.setDisable(false);
        botonPapel.setDisable(false);
        botonTijera.setDisable(false);
    }

    @FXML
    private void responderNo() {
        cliente.enviarMensaje("NO");
        areaDeJuego.appendText("TÚ: No, fin de la partida.\n");
        cajaBotonesConfirmacion.setVisible(false);
    }

    private void finalizarPartida() {
        subtitulo.setText("Partida finalizada. Gracias por jugar.");
        botonPiedra.setDisable(true);
        botonPapel.setDisable(true);
        botonTijera.setDisable(true);
        botonIniciar.setDisable(false); // Reactivar el botón de iniciar partida
    }

    @FXML
    private void elegirPiedra() {
        enviarJugada("PIEDRA");
    }

    @FXML
    private void elegirPapel() {
        enviarJugada("PAPEL");
    }

    @FXML
    private void elegirTijera() {
        enviarJugada("TIJERAS");
    }

    private void enviarJugada(String jugada) {
        if (cliente != null && !botonPiedra.isDisabled()) {
            cliente.enviarMensaje(jugada);
            areaDeJuego.appendText("TÚ: Has jugado " + jugada + "\n");
        }
    }

    public void detener() {
        if (cliente != null) {
            cliente.desconectar();
        }
        if (servidor != null) {
            servidor.detener();
        }
        System.out.println("Servicios detenidos.");
    }
}
