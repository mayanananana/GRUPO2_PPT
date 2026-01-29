package juego_psp.PPT;

import static org.junit.jupiter.api.Assertions.*; // Importamos las afirmaciones (assertions) para verificar resultados
import org.junit.jupiter.api.BeforeEach; // Para ejecutar código antes de cada test
import org.junit.jupiter.api.Test; // Para marcar métodos como pruebas unitarias
import java.net.Socket;

/**
 * Pruebas unitarias para la lógica del juego en HandlerPPT.
 */
public class HandlerPPTTest {

    // Instancia del manejador que vamos a probar
    private HandlerPPT handler;

    // Esta anotación indica que el método se ejecutará antes de cada test
    // individual (@Test)
    // Sirve para preparar un entorno limpio (resetear variables, crear objetos,
    // etc.)
    @BeforeEach
    void setUp() {
        // Inicializamos el handler. Pasamos 'null' como Socket porque para probar la
        // lógica
        // de "esJugadaValida" y "determinarGanador" no necesitamos una conexión real de
        // red.
        // Esto permite que los tests sean rápidos y no dependan de que un servidor esté
        // encendido.
        handler = new HandlerPPT(null);
    }

    /**
     * Prueba que la validación de jugadas funcione correctamente.
     */
    @Test
    void testEsJugadaValida() {
        // assertTrue verifica que la condición dentro del paréntesis sea VERDADERA.
        // Si es falsa, el test fallará.
        assertTrue(handler.esJugadaValida("PIEDRA")); // Caso estándar en mayúsculas
        assertTrue(handler.esJugadaValida("PAPEL")); // Caso estándar en mayúsculas
        assertTrue(handler.esJugadaValida("TIJERAS")); // Caso estándar en mayúsculas

        // Probamos que el sistema no sea sensible a mayúsculas/minúsculas.
        // "piedra" debería ser aceptado igual que "PIEDRA".
        assertTrue(handler.esJugadaValida("piedra"));

        // assertFalse verifica que la condición sea FALSA.
        // "TORTILLA" no es una opción válida en este juego, por lo que debe dar false.
        assertFalse(handler.esJugadaValida("TORTILLA"));

        // Probamos casos límite: cadenas vacías o valores nulos para asegurar que el
        // código no explote.
        assertFalse(handler.esJugadaValida(""));
        assertFalse(handler.esJugadaValida(null));
    }

    /**
     * Prueba los resultados de determinación del ganador para diversas
     * combinaciones.
     */
    @Test
    void testDeterminarGanador() {
        // assertEquals(esperado, actual) compara si dos valores son idénticos.
        // Si el jugador y el servidor eligen lo mismo, el método debe devolver "RONDA:
        // EMPATE".
        assertEquals("RONDA: EMPATE", handler.determinarGanador("PIEDRA", "PIEDRA"));
        assertEquals("RONDA: EMPATE", handler.determinarGanador("PAPEL", "PAPEL"));

        // Casos de victoria del jugador:
        // Usamos .contains("GANAS") porque el mensaje devuelto por el método suele ser
        // largo (ej: "RONDA: GANAS (Piedra vence a Tijeras)").
        // Solo verificamos que la palabra clave "GANAS" esté presente.
        assertTrue(handler.determinarGanador("PIEDRA", "TIJERAS").contains("GANAS"));
        assertTrue(handler.determinarGanador("PAPEL", "PIEDRA").contains("GANAS"));
        assertTrue(handler.determinarGanador("TIJERAS", "PAPEL").contains("GANAS"));

        // Casos de derrota del jugador (victoria del bot):
        // Verificamos que el mensaje contenga "PIERDES".
        assertTrue(handler.determinarGanador("PIEDRA", "PAPEL").contains("PIERDES"));
        assertTrue(handler.determinarGanador("TIJERAS", "PIEDRA").contains("PIERDES"));
        assertTrue(handler.determinarGanador("PAPEL", "TIJERAS").contains("PIERDES"));
    }
}
