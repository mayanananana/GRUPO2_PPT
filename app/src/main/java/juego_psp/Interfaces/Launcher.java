package juego_psp.interfaces;

/**
 * Clase lanzadora para ejecutar la aplicación JavaFX.
 * Se utiliza para evitar problemas de dependencias de módulos al ejecutar desde
 * el IDE.
 * Esta clase es el punto de entrada principal que inicia la interfaz de usuario
 * del juego.
 */
public class Launcher {
    /**
     * Punto de entrada principal para el inicio manual de la aplicación JavaFX.
     * Este método invoca el método `main` de la clase `InterfazUsuario` para
     * arrancar la interfaz gráfica.
     * 
     * @param args Argumentos de la línea de comandos pasados a la aplicación.
     */
    public static void main(String[] args) {
        InterfazUsuario.main(args);
    }
}
