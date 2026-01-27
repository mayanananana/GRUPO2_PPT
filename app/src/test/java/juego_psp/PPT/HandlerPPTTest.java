package juego_psp.PPT;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.Socket;

/**
 * Pruebas unitarias para la lógica del juego en HandlerPPT.
 */
public class HandlerPPTTest {

    private HandlerPPT handler;

    @BeforeEach
    void setUp() {
        // El socket puede ser null ya que solo probaremos métodos lógicos que no lo
        // usan
        handler = new HandlerPPT(null);
    }

    /**
     * Prueba que la validación de jugadas funcione correctamente.
     */
    @Test
    void testEsJugadaValida() {
        assertTrue(handler.esJugadaValida("PIEDRA"));
        assertTrue(handler.esJugadaValida("PAPEL"));
        assertTrue(handler.esJugadaValida("TIJERAS"));
        assertTrue(handler.esJugadaValida("piedra")); // Caso insensible
        assertFalse(handler.esJugadaValida("LAGARTO"));
        assertFalse(handler.esJugadaValida(""));
        assertFalse(handler.esJugadaValida(null));
    }

    /**
     * Prueba los resultados de determinación del ganador para diversas
     * combinaciones.
     */
    @Test
    void testDeterminarGanador() {
        // Empates
        assertEquals("RONDA: EMPATE", handler.determinarGanador("PIEDRA", "PIEDRA"));
        assertEquals("RONDA: EMPATE", handler.determinarGanador("PAPEL", "PAPEL"));

        // Victorias del jugador
        assertTrue(handler.determinarGanador("PIEDRA", "TIJERAS").contains("GANAS"));
        assertTrue(handler.determinarGanador("PAPEL", "PIEDRA").contains("GANAS"));
        assertTrue(handler.determinarGanador("TIJERAS", "PAPEL").contains("GANAS"));

        // Victorias del bot
        assertTrue(handler.determinarGanador("PIEDRA", "PAPEL").contains("PIERDES"));
        assertTrue(handler.determinarGanador("TIJERAS", "PIEDRA").contains("PIERDES"));
        assertTrue(handler.determinarGanador("PAPEL", "TIJERAS").contains("PIERDES"));
    }
}
