package juego_psp.interfaces;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class InterfazUsuario extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar el archivo FXML desde resources/juego_psp/
        Parent root = FXMLLoader.load(getClass().getResource("/juego_psp/InterfazUsuario.fxml"));

        // Crear la escena
        Scene scene = new Scene(root, 800, 600);

        // Configurar el escenario (ventana)
        primaryStage.setTitle("Juego Piedra, Papel, Tijera");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
