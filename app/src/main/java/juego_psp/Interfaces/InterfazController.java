package juego_psp.Interfaces;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class InterfazController {

    @FXML
    private Label titulo;

    @FXML
    private Label subtitulo;

    @FXML
    private Label resultado;

    @FXML
    private Button botonPiedra;

    @FXML
    private Button botonPapel;

    @FXML
    private Button botonTijera;

    @FXML
    public void initialize() {
        // inicializacion al cargar el fxml
    }

    @FXML
    private void elegirPiedra() {
        resultado.setText("Elegiste: Piedra 🪨");
    }

    @FXML
    private void elegirPapel() {
        resultado.setText("Elegiste: Papel 📄");
    }

    @FXML
    private void elegirTijera() {
        resultado.setText("Elegiste: Tijera ✂");
    }
}
