import algorithms.Warshall;
import ds.DLinkedList;
import graph.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link Warshall} (cierre transitivo).
 *
 * <p>Se verifican los resultados a mano antes de correr los tests,
 * siguiendo el tip de depuración del enunciado.</p>
 *
 * <b>Grafo conexo base:</b>
 * <pre>
 *   DEPOT(A) --100-- B --200-- C
 *                              |
 *                             250
 *                              |
 *                              D
 * </pre>
 * Matriz de cierre transitivo esperada (todos alcanzan a todos):
 * <pre>
 *     A  B  C  D
 *   A[1, 1, 1, 1]
 *   B[1, 1, 1, 1]
 *   C[1, 1, 1, 1]
 *   D[1, 1, 1, 1]
 * </pre>
 *
 * <b>Grafo desconexo:</b>
 * <pre>
 *   DEPOT(A) --50-- B      C --75-- D
 * </pre>
 * C y D son inalcanzables desde A.
 *
 * @author Persona 2
 */
public class WarshallTest {

    private Graph connectedGraph;
    private Graph disconnectedGraph;

    private Vertex cA, cB, cC, cD;   // grafo conexo
    private Vertex dA, dB, dC, dD;   // grafo desconexo

    @BeforeEach
    void setUp() {
        // ---- Grafo conexo ----
        connectedGraph = new Graph();
        cA = new Vertex("A", VertexType.DEPOT,        0,   0, 0);
        cB = new Vertex("B", VertexType.INTERSECTION, 100, 0, 0);
        cC = new Vertex("C", VertexType.DELIVERY,     200, 0, 0);
        cD = new Vertex("D", VertexType.DELIVERY,     200, 100, 0);

        connectedGraph.addVertex(cA);
        connectedGraph.addVertex(cB);
        connectedGraph.addVertex(cC);
        connectedGraph.addVertex(cD);

        connectedGraph.addEdge(new Edge(cA, cB, 100));
        connectedGraph.addEdge(new Edge(cB, cC, 200));
        connectedGraph.addEdge(new Edge(cC, cD, 250));

        // ---- Grafo desconexo ----
        disconnectedGraph = new Graph();
        dA = new Vertex("A", VertexType.DEPOT,        0,   0, 0);
        dB = new Vertex("B", VertexType.DELIVERY,     100, 0, 0);
        dC = new Vertex("C", VertexType.DELIVERY,     300, 0, 0);
        dD = new Vertex("D", VertexType.DELIVERY,     400, 0, 0);

        disconnectedGraph.addVertex(dA);
        disconnectedGraph.addVertex(dB);
        disconnectedGraph.addVertex(dC);
        disconnectedGraph.addVertex(dD);

        disconnectedGraph.addEdge(new Edge(dA, dB, 50));
        disconnectedGraph.addEdge(new Edge(dC, dD, 75));
        // A-B están conectados; C-D están conectados; pero {A,B} y {C,D} están separados
    }

    // ================================================================
    // Propiedades generales de la matriz
    // ================================================================

    @Test
    void testMatrixSize() {
        boolean[][] reach = Warshall.compute(connectedGraph);
        assertEquals(4, reach.length);
        assertEquals(4, reach[0].length);
    }

    @Test
    void testDiagonalAlwaysTrue() {
        boolean[][] reach = Warshall.compute(connectedGraph);
        for (int i = 0; i < reach.length; i++) {
            assertTrue(reach[i][i], "La diagonal debe ser true en [" + i + "][" + i + "]");
        }
    }

    @Test
    void testMatrixSymmetric() {
        // Grafo no dirigido → la matriz de cierre transitivo también es simétrica
        boolean[][] reach = Warshall.compute(connectedGraph);
        int n = reach.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                assertEquals(reach[i][j], reach[j][i],
                    "Matriz no simétrica en [" + i + "][" + j + "]");
            }
        }
    }

    // ================================================================
    // Grafo completamente conexo
    // ================================================================

    @Test
    void testAllReachableInConnectedGraph() {
        boolean[][] reach = Warshall.compute(connectedGraph);
        int n = reach.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                assertTrue(reach[i][j],
                    "En grafo conexo todos deben alcanzarse: [" + i + "][" + j + "] es false");
            }
        }
    }

    @Test
    void testDepotReachesAllInConnectedGraph() {
        boolean[][] reach = Warshall.compute(connectedGraph);
        int depotIdx = cA.getIndex();
        assertTrue(reach[depotIdx][cB.getIndex()], "Depósito debe alcanzar B");
        assertTrue(reach[depotIdx][cC.getIndex()], "Depósito debe alcanzar C");
        assertTrue(reach[depotIdx][cD.getIndex()], "Depósito debe alcanzar D");
    }

    // ================================================================
    // Grafo desconexo
    // ================================================================

    @Test
    void testDepotCannotReachDisconnectedVertices() {
        boolean[][] reach = Warshall.compute(disconnectedGraph);
        int depotIdx = dA.getIndex();
        // A puede llegar a B
        assertTrue(reach[depotIdx][dB.getIndex()], "Depósito debe alcanzar B");
        // A NO puede llegar a C ni D
        assertFalse(reach[depotIdx][dC.getIndex()], "Depósito NO debe alcanzar C");
        assertFalse(reach[depotIdx][dD.getIndex()], "Depósito NO debe alcanzar D");
    }

    @Test
    void testWithinComponentReachable() {
        boolean[][] reach = Warshall.compute(disconnectedGraph);
        // C y D están en la misma componente
        assertTrue(reach[dC.getIndex()][dD.getIndex()]);
        assertTrue(reach[dD.getIndex()][dC.getIndex()]);
    }

    @Test
    void testAcrossComponentsNotReachable() {
        boolean[][] reach = Warshall.compute(disconnectedGraph);
        // A no alcanza C, D; y C no alcanza A, B
        assertFalse(reach[dA.getIndex()][dC.getIndex()]);
        assertFalse(reach[dC.getIndex()][dA.getIndex()]);
        assertFalse(reach[dC.getIndex()][dB.getIndex()]);
    }

    // ================================================================
    // Método isReachable
    // ================================================================

    @Test
    void testIsReachableConnected() {
        boolean[][] reach = Warshall.compute(connectedGraph);
        assertTrue(Warshall.isReachable(reach, cA, cD));
    }

    @Test
    void testIsReachableDisconnected() {
        boolean[][] reach = Warshall.compute(disconnectedGraph);
        assertFalse(Warshall.isReachable(reach, dA, dC));
    }

    @Test
    void testIsReachableSelf() {
        boolean[][] reach = Warshall.compute(connectedGraph);
        assertTrue(Warshall.isReachable(reach, cA, cA));
    }

    // ================================================================
    // unreachableFromDepot
    // ================================================================

    @Test
    void testUnreachableFromDepotEmptyInConnectedGraph() {
        boolean[][] reach = Warshall.compute(connectedGraph);
        DLinkedList<String> destinos = new DLinkedList<>();
        destinos.add("B");
        destinos.add("C");
        destinos.add("D");

        DLinkedList<String> unreachable = Warshall.unreachableFromDepot(connectedGraph, reach, destinos);
        assertEquals(0, unreachable.size(), "No debe haber destinos inalcanzables en grafo conexo");
    }

    @Test
    void testUnreachableFromDepotDetectsCAndD() {
        boolean[][] reach = Warshall.compute(disconnectedGraph);
        DLinkedList<String> destinos = new DLinkedList<>();
        destinos.add("B");
        destinos.add("C");
        destinos.add("D");

        DLinkedList<String> unreachable = Warshall.unreachableFromDepot(disconnectedGraph, reach, destinos);
        // C y D son inalcanzables
        assertEquals(2, unreachable.size());
        assertTrue(containsId(unreachable, "C"), "C debe estar en inalcanzables");
        assertTrue(containsId(unreachable, "D"), "D debe estar en inalcanzables");
        assertFalse(containsId(unreachable, "B"), "B sí es alcanzable");
    }

    @Test
    void testUnreachableFromDepotNonExistentVertex() {
        boolean[][] reach = Warshall.compute(disconnectedGraph);
        DLinkedList<String> destinos = new DLinkedList<>();
        destinos.add("Z"); // No existe en el grafo

        DLinkedList<String> unreachable = Warshall.unreachableFromDepot(disconnectedGraph, reach, destinos);
        // Vértice inexistente debe reportarse como inalcanzable
        assertEquals(1, unreachable.size());
        assertTrue(containsId(unreachable, "Z"));
    }

    // ================================================================
    // Grafo de un solo vértice
    // ================================================================

    @Test
    void testSingleVertexGraph() {
        Graph single = new Graph();
        Vertex v = new Vertex("X", VertexType.DEPOT, 0, 0, 0);
        single.addVertex(v);

        boolean[][] reach = Warshall.compute(single);
        assertEquals(1, reach.length);
        assertTrue(reach[0][0]);
    }

    // ================================================================
    // Helper
    // ================================================================

    private boolean containsId(DLinkedList<String> list, String id) {
        for (String s : list) {
            if (s.equals(id)) return true;
        }
        return false;
    }
}