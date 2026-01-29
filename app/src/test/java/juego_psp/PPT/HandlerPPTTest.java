package juego_psp.PPT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HandlerPPTTest {

    private HandlerPPT handler;

    @BeforeEach
    void setUp() {
        // Se pasa null porque los métodos a probar no usan el socket.
        handler = new HandlerPPT(null);
    }

    @Test
    @DisplayName("Prueba de jugadas válidas")
    void testEsJugadaValida() {
        assertTrue(handler.esJugadaValida("PIEDRA"), "PIEDRA debería ser válida");
        assertTrue(handler.esJugadaValida("PAPEL"), "PAPEL debería ser válida");
        assertTrue(handler.esJugadaValida("TIJERAS"), "TIJERAS debería ser válida");
    }

    @Test
    @DisplayName("Prueba de jugadas inválidas")
    void testEsJugadaInvalida() {
        assertFalse(handler.esJugadaValida("LAGARTO"), "LAGARTO debería ser inválida");
        assertFalse(handler.esJugadaValida("SPOCK"), "SPOCK debería ser inválida");
        assertFalse(handler.esJugadaValida(null), "null debería ser inválida");
        assertFalse(handler.esJugadaValida(""), "Cadena vacía debería ser inválida");
    }

    @Test
    @DisplayName("Prueba de empates")
    void testDeterminarGanador_Empates() {
        assertEquals("RONDA: EMPATE", handler.determinarGanador("PIEDRA", "PIEDRA"));
        assertEquals("RONDA: EMPATE", handler.determinarGanador("PAPEL", "PAPEL"));
        assertEquals("RONDA: EMPATE", handler.determinarGanador("TIJERAS", "TIJERAS"));
    }

    @Test
    @DisplayName("Prueba de victorias del jugador")
    void testDeterminarGanador_GanaJugador() {
        assertEquals("RONDA: GANAS (Piedra vence a Tijeras)", handler.determinarGanador("PIEDRA", "TIJERAS"));
        assertEquals("RONDA: GANAS (Papel vence a Piedra)", handler.determinarGanador("PAPEL", "PIEDRA"));
        assertEquals("RONDA: GANAS (Tijeras vence a Papel)", handler.determinarGanador("TIJERAS", "PAPEL"));
    }

    @Test
    @DisplayName("Prueba de victorias del servidor")
    void testDeterminarGanador_GanaServidor() {
        assertEquals("RONDA: PIERDES (Papel vence a Piedra)", handler.determinarGanador("PIEDRA", "PAPEL"));
        assertEquals("RONDA: PIERDES (Tijeras vence a Papel)", handler.determinarGanador("PAPEL", "TIJERAS"));
        assertEquals("RONDA: PIERDES (Piedra vence a Tijeras)", handler.determinarGanador("TIJERAS", "PIEDRA"));
    }
}
