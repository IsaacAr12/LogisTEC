import io.JsonLoader;
import io.LogisticsConfig;
import io.ConfigValidationException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de {@link JsonLoader}. Usan {@link JsonLoader#loadFromString(String)}
 * para no depender del sistema de archivos. Requieren Gson en el classpath.
 */
public class JsonLoaderTest {

    private static final String CASO_VALIDO = """
        {
          "ciudad": {
            "vertices": [
              {"id": "A", "tipo": "DEPOT", "x": 100, "y": 100},
              {"id": "B", "tipo": "INTERSECCION", "x": 250, "y": 150},
              {"id": "G", "tipo": "ENTREGA", "x": 300, "y": 400}
            ],
            "aristas": [
              {"u": "A", "v": "B", "distancia": 320},
              {"u": "B", "v": "G", "distancia": 410}
            ]
          },
          "paquetes": [
            {"id": "P01", "destino": "G", "peso": 5, "prioridad": 1}
          ],
          "camiones": [
            {"id": "C01", "capacidad": 50},
            {"id": "C02", "capacidad": 30}
          ]
        }
        """;

    @Test
    void cargaCasoValido() throws Exception {
        LogisticsConfig cfg = new JsonLoader().loadFromString(CASO_VALIDO);
        assertEquals("A", cfg.depotId());
        assertEquals(3, cfg.vertices().size());
        assertEquals(2, cfg.edges().size());
        assertEquals(1, cfg.packages().size());
        assertEquals(2, cfg.trucks().size());
        assertEquals("G", cfg.packages().get(0).destino());
        assertEquals(50, cfg.trucks().get(0).capacidad());
    }

    @Test
    void rechazaJsonMalFormado() {
        JsonLoader loader = new JsonLoader();
        assertThrows(ConfigValidationException.class, () -> loader.loadFromString("{ esto no es json"));
    }

    @Test
    void rechazaAristaConVerticeInexistente() {
        String json = """
            {"ciudad":{"vertices":[{"id":"A","tipo":"DEPOT"}],
             "aristas":[{"u":"A","v":"Z","distancia":10}]},
             "camiones":[{"id":"C01","capacidad":10}]}
            """;
        ConfigValidationException ex = assertThrows(ConfigValidationException.class,
                () -> new JsonLoader().loadFromString(json));
        assertTrue(ex.getMessage().contains("Z"));
    }

    @Test
    void rechazaPrioridadFueraDeRango() {
        String json = """
            {"ciudad":{"vertices":[{"id":"A","tipo":"DEPOT"},{"id":"B","tipo":"ENTREGA"}],
             "aristas":[]},
             "paquetes":[{"id":"P1","destino":"B","peso":3,"prioridad":4}],
             "camiones":[{"id":"C01","capacidad":10}]}
            """;
        assertThrows(ConfigValidationException.class, () -> new JsonLoader().loadFromString(json));
    }

    @Test
    void rechazaSinDeposito() {
        String json = """
            {"ciudad":{"vertices":[{"id":"A","tipo":"INTERSECCION"}],"aristas":[]},
             "camiones":[{"id":"C01","capacidad":10}]}
            """;
        assertThrows(ConfigValidationException.class, () -> new JsonLoader().loadFromString(json));
    }

    @Test
    void rechazaIdsDuplicados() {
        String json = """
            {"ciudad":{"vertices":[{"id":"A","tipo":"DEPOT"},{"id":"A","tipo":"ENTREGA"}],
             "aristas":[]},
             "camiones":[{"id":"C01","capacidad":10}]}
            """;
        assertThrows(ConfigValidationException.class, () -> new JsonLoader().loadFromString(json));
    }

    @Test
    void rechazaDistanciaNoPositiva() {
        String json = """
            {"ciudad":{"vertices":[{"id":"A","tipo":"DEPOT"},{"id":"B","tipo":"ENTREGA"}],
             "aristas":[{"u":"A","v":"B","distancia":0}]},
             "camiones":[{"id":"C01","capacidad":10}]}
            """;
        assertThrows(ConfigValidationException.class, () -> new JsonLoader().loadFromString(json));
    }
}
