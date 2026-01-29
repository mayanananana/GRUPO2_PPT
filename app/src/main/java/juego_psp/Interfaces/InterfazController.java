package juego_psp.Interfaces;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import juego_psp.PPT.ClientePPT;
import juego_psp.PPT.ServidorPPT;

/**
 * Controlador de la interfaz de usuario JavaFX para el juego Piedra, Papel o
 * Tijera.
 * Gestiona la interacción del usuario, la actualización de elementos visuales
 * (imágenes y etiquetas)
 * y la comunicación con el cliente de red.
 */
public class InterfazController {

    // Variables de la interfaz FXML
    /** Etiqueta superior del título principal */
    @FXML
    private Label titulo;
    /**
     * Etiqueta de subtítulo que muestra el estado general (ej. "¡La partida ha
     * comenzado!")
     */
    @FXML
    private Label subtitulo;
    /** Etiqueta (oculta) para el resultado final */
    @FXML
    private Label resultado;
    /** Botón para elegir Piedra */
    @FXML
    private Button botonPiedra;
    /** Botón para elegir Papel */
    @FXML
    private Button botonPapel;
    /** Botón para elegir Tijera */
    @FXML
    private Button botonTijera;
    /** Botón para iniciar el servidor y conectar el cliente */
    @FXML
    private Button botonIniciar;
    /** Área de texto (oculta) que registraba los logs del juego */
    @FXML
    private TextArea areaDeJuego;
    /** Contenedor de botones para confirmar si se quiere jugar otra partida */
    @FXML
    private HBox cajaBotonesConfirmacion;
    /** Botón para confirmar "Sí" al repetir partida */
    @FXML
    private Button botonSi;
    /** Botón para confirmar "No" al repetir partida */
    @FXML
    private Button botonNo;

    // Elementos visuales del juego
    /** Etiqueta que muestra la jugada del jugador (❓ o imagen) */
    @FXML
    private Label labelJugadaJugador;
    /** Etiqueta que muestra la jugada del servidor (❓ o imagen) */
    @FXML
    private Label labelJugadaServidor;
    /** Etiqueta que muestra el resultado de cada ronda individual */
    @FXML
    private Label labelResultadoRonda;
    /** Etiqueta que muestra el tanteador acumulado de la partida */
    @FXML
    private Label labelMarcador;

    // Imágenes del juego
    /** Recurso de imagen para la Piedra */
    private Image imgPiedra;
    /** Recurso de imagen para el Papel */
    private Image imgPapel;
    /** Recurso de imagen para las Tijeras */
    private Image imgTijeras;

    // Variables para la lógica del juego
    /** Instancia del servidor que corre en local */
    private ServidorPPT servidor;
    /** Instancia del cliente que se comunica con el servidor local */
    private ClientePPT cliente;
    /** Puerto TCP por defecto */
    private static final int PUERTO = 9999;
    /** Host por defecto */
    private static final String HOST = "localhost";

    /**
     * Método de inicialización de JavaFX.
     * Configura el estado inicial de los botones y carga las imágenes desde los
     * recursos.
     */
    @FXML
    public void initialize() {
        // Estado inicial de los botones, solo 'Iniciar' está activo.
        botonPiedra.setDisable(true);
        botonPapel.setDisable(true);
        botonTijera.setDisable(true);

        // Cargar las imágenes desde resources/juego_psp/
        try {
            imgPiedra = new Image(getClass().getResourceAsStream("/juego_psp/piedra.png"));
            imgPapel = new Image(getClass().getResourceAsStream("/juego_psp/papel.png"));
            imgTijeras = new Image(getClass().getResourceAsStream("/juego_psp/tijeras.png"));

            // Añadir imágenes a los botones para un look más profesional
            botonPiedra.setGraphic(createIconView(imgPiedra));
            botonPiedra.setText("Piedra");
            botonPapel.setGraphic(createIconView(imgPapel));
            botonPapel.setText("Papel");
            botonTijera.setGraphic(createIconView(imgTijeras));
            botonTijera.setText("Tijeras");

        } catch (Exception e) {
            System.err.println("Error al cargar las imágenes: " + e.getMessage());
        }
    }

    /**
     * Crea un ImageView configurado para ser usado como icono en un botón.
     * 
     * @param img La imagen a redimensionar.
     * @return Un ImageView de 30x30 píxeles.
     */
    private ImageView createIconView(Image img) {
        ImageView view = new ImageView(img);
        view.setFitHeight(30);
        view.setFitWidth(30);
        view.setPreserveRatio(true);
        return view;
    }

    /**
     * Acción del botón "Iniciar Partida".
     * Arranca el servidor local y conecta el cliente.
     */
    @FXML
    private void iniciarJuego() { // INICIA EL SERVIDOR Y EL CLIENTE
        System.out.println("--- INICIANDO JUEGO ---");

        servidor = new ServidorPPT(PUERTO);
        Thread hiloServidor = new Thread(servidor);
        hiloServidor.setDaemon(true);
        hiloServidor.start();
        System.out.println("SERVIDOR: Iniciado en el puerto " + PUERTO);

        // Instancia el cliente pasándole la dirección, el puerto y una función
        // (callback)
        // para que el cliente sepa qué hacer cuando reciba mensajes del servidor.
        cliente = new ClientePPT(HOST, PUERTO, this::mostrarMensajeEnLog);

        // Intenta establecer la conexión física por red (Socket) con el servidor
        // y arranca un hilo en segundo plano para escuchar respuestas sin bloquear la
        // interfaz.
        cliente.conectar();
        System.out.println("CLIENTE: Conectado a " + HOST + ":" + PUERTO);

        botonIniciar.setDisable(true);
        botonPiedra.setDisable(false);
        botonPapel.setDisable(false);
        botonTijera.setDisable(false);
        subtitulo.setText("¡La partida ha comenzado");
        labelResultadoRonda.setText("¡Esperando tu jugada");
        resetVisuals();
    }

    /**
     * Restablece las etiquetas de jugada al estado con el signo de interrogación.
     */
    private void resetVisuals() {
        labelJugadaJugador.setGraphic(null);
        labelJugadaJugador.setText("❓");
        labelJugadaServidor.setGraphic(null);
        labelJugadaServidor.setText("❓");
    }

    /**
     * Procesa los mensajes recibidos desde el servidor.
     * Utiliza Platform.runLater para actualizar los elementos de JavaFX de forma
     * segura desde otro hilo.
     * 
     * @param mensaje El texto plano enviado por el servidor.
     */
    private void mostrarMensajeEnLog(String mensaje) {
        // Todo sale por la terminal del IDE ahora
        System.out.println("SERVIDOR: " + mensaje);

        Platform.runLater(() -> {
            // Actualizar la interfaz de manera visual
            if (mensaje.contains("El servidor eligió:")) {
                String eleccion = mensaje.split(":")[1].trim().toUpperCase();
                setImagenLabel(labelJugadaServidor, eleccion);
            } else if (mensaje.startsWith("RONDA:")) {
                // Mostrar resultado de la ronda y pausar brevemente la UI para que se vea
                labelResultadoRonda.setText(mensaje);
                botonPiedra.setDisable(true);
                botonPapel.setDisable(true);
                botonTijera.setDisable(true);
                PauseTransition pausa = new PauseTransition(Duration.millis(1200));
                pausa.setOnFinished(ev -> {
                    botonPiedra.setDisable(false);
                    botonPapel.setDisable(false);
                    botonTijera.setDisable(false);
                });
                pausa.play();
            } else if (mensaje.startsWith("MARCADOR:")) {
                labelMarcador.setText(mensaje);
            } else if (mensaje.contains("¿Quieres jugar otra partida?")) {
                solicitarConfirmacion();
            } else if (mensaje.contains("FIN_JUEGO")) {
                finalizarPartida();
            } else if (mensaje.startsWith("--- RONDA")) {
                // Mostrar cabecera de ronda; no reiniciar visuales aquí para
                // mantener las imágenes y el resultado anterior hasta que el
                // usuario realice la siguiente acción (pulsar su jugada).
                labelResultadoRonda.setText(mensaje);
            } else if (mensaje.contains("¡FELICIDADES!") || mensaje.contains("ha ganado la partida")) {
                labelResultadoRonda.setText(mensaje);
            }
        });
    }

    /**
     * Cambia el contenido de una etiqueta (Label) para mostrar una imagen PNG en
     * lugar de texto.
     * 
     * @param label  La etiqueta a actualizar.
     * @param jugada La jugada correspondiente (PIEDRA, PAPEL o TIJERAS).
     */
    private void setImagenLabel(Label label, String jugada) {
        Image img = null;
        switch (jugada) {
            case "PIEDRA":
                img = imgPiedra;
                break;
            case "PAPEL":
                img = imgPapel;
                break;
            case "TIJERAS":
                img = imgTijeras;
                break;
        }

        if (img != null) {
            label.setText(""); // Quitamos el ❓
            ImageView view = new ImageView(img);
            view.setFitHeight(100);
            view.setFitWidth(100);
            view.setPreserveRatio(true);
            label.setGraphic(view);
        } else {
            label.setGraphic(null);
            label.setText("❓");
        }
    }

    /**
     * Muestra los botones de "Sí/No" cuando el servidor pregunta si se desea
     * repetir la partida.
     */
    private void solicitarConfirmacion() {
        botonPiedra.setDisable(true);
        botonPapel.setDisable(true);
        botonTijera.setDisable(true);
        cajaBotonesConfirmacion.setVisible(true);
    }

    /**
     * Acción del botón "Sí". Envía la respuesta al servidor para iniciar una nueva
     * partida.
     */
    @FXML
    private void responderSi() {
        cliente.enviarMensaje("SI");
        System.out.println("TÚ: Has seleccionado SI para jugar de nuevo.");
        cajaBotonesConfirmacion.setVisible(false);
        botonPiedra.setDisable(false);
        botonPapel.setDisable(false);
        botonTijera.setDisable(false);
        resetVisuals();
    }

    /**
     * Acción del botón "No". Envía la respuesta al servidor para cerrar la
     * conexión.
     */
    @FXML
    private void responderNo() {
        cliente.enviarMensaje("NO");
        System.out.println("TÚ: Has seleccionado NO para terminar.");
        cajaBotonesConfirmacion.setVisible(false);
    }

    /**
     * Cambia la interfaz al estado final tras concluir el juego completamente.
     */
    private void finalizarPartida() {
        subtitulo.setText("Partida finalizada. Gracias por jugar.");
        botonPiedra.setDisable(true);
        botonPapel.setDisable(true);
        botonTijera.setDisable(true);
        botonIniciar.setDisable(false);
    }

    /** Acción para elegir Piedra. */
    @FXML
    private void elegirPiedra() {
        enviarJugada("PIEDRA");
    }

    /** Acción para elegir Papel. */
    @FXML
    private void elegirPapel() {
        enviarJugada("PAPEL");
    }

    /** Acción para elegir Tijera. */
    @FXML
    private void elegirTijera() {
        enviarJugada("TIJERAS");
    }

    /**
     * Envía la jugada seleccionada al servidor y actualiza la visualización del
     * jugador.
     * 
     * @param jugada El texto de la jugada realizada.
     */
    private void enviarJugada(String jugada) {
        if (cliente != null && !botonPiedra.isDisabled()) {
            cliente.enviarMensaje(jugada);
            System.out.println("TÚ: Has jugado " + jugada);
            setImagenLabel(labelJugadaJugador, jugada);
            labelJugadaServidor.setGraphic(null);
            labelJugadaServidor.setText("⌛"); // Indicamos que el servidor está "pensando"
        }
    }

    /**
     * Cierra todas las conexiones de red (cliente y servidor) al cerrar la
     * aplicación.
     */
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
