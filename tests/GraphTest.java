import graph.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link Graph}, {@link Vertex} y {@link Edge}.
 *
 * <p>Se usan grafos pequeños construidos a mano para verificar cada operación.
 * Los resultados se comparan manualmente antes de correr las pruebas (tip del enunciado).</p>
 *
 * Grafo base de pruebas (5 vértices):
 * <pre>
 *   DEPOT(A) --100-- B --200-- C
 *      |                       |
 *     150                     250
 *      |                       |
 *      D ----------300--------- E
 * </pre>
 *
 * @author Persona 2
 */
public class GraphTest {

    private Graph graph;
    private Vertex vA, vB, vC, vD, vE;

    @BeforeEach
    void setUp() {
        graph = new Graph();

        vA = new Vertex("A", VertexType.DEPOT,        0,   0, 0);
        vB = new Vertex("B", VertexType.INTERSECTION, 100, 0, 0);
        vC = new Vertex("C", VertexType.DELIVERY,     200, 0, 0);
        vD = new Vertex("D", VertexType.DELIVERY,     0, 100, 0);
        vE = new Vertex("E", VertexType.DELIVERY,     200, 100, 0);

        graph.addVertex(vA);
        graph.addVertex(vB);
        graph.addVertex(vC);
        graph.addVertex(vD);
        graph.addVertex(vE);

        graph.addEdge(new Edge(vA, vB, 100));
        graph.addEdge(new Edge(vB, vC, 200));
        graph.addEdge(new Edge(vA, vD, 150));
        graph.addEdge(new Edge(vC, vE, 250));
        graph.addEdge(new Edge(vD, vE, 300));
    }

    // ---- Vértices -------------------------------------------------------

    @Test
    void testVertexCount() {
        assertEquals(5, graph.vertexCount());
    }

    @Test
    void testEdgeCount() {
        assertEquals(5, graph.edgeCount());
    }

    @Test
    void testGetVertexById() {
        Vertex found = graph.getVertex("C");
        assertNotNull(found);
        assertEquals("C", found.getId());
        assertEquals(VertexType.DELIVERY, found.getType());
    }

    @Test
    void testGetVertexByIdNotFound() {
        assertNull(graph.getVertex("Z"));
    }

    @Test
    void testDepotDetected() {
        assertNotNull(graph.getDepot());
        assertEquals("A", graph.getDepot().getId());
    }

    @Test
    void testVertexIndicesUnique() {
        // Cada vértice debe tener un índice distinto en [0, n)
        int n = graph.vertexCount();
        boolean[] seen = new boolean[n];
        for (Vertex v : graph.getVertices()) {
            int idx = v.getIndex();
            assertTrue(idx >= 0 && idx < n, "Índice fuera de rango: " + idx);
            assertFalse(seen[idx], "Índice duplicado: " + idx);
            seen[idx] = true;
        }
    }

    @Test
    void testDuplicateVertexNotAdded() {
        Vertex duplicate = new Vertex("A", VertexType.INTERSECTION, 0, 0, 0);
        boolean added = graph.addVertex(duplicate);
        assertFalse(added);
        assertEquals(5, graph.vertexCount());
    }

    // ---- Aristas --------------------------------------------------------

    @Test
    void testNeighborsOfA() {
        // A debe tener 2 vecinos: B (100m) y D (150m)
        var neighbors = graph.getNeighbors(vA);
        assertEquals(2, neighbors.size());
    }

    @Test
    void testNeighborsOfB() {
        // B debe tener 2 vecinos: A y C
        var neighbors = graph.getNeighbors(vB);
        assertEquals(2, neighbors.size());
    }

    @Test
    void testEdgeGetOther() {
        Edge edge = new Edge(vA, vB, 100);
        assertEquals(vB, edge.getOther(vA));
        assertEquals(vA, edge.getOther(vB));
    }

    @Test
    void testEdgeGetOtherInvalidVertex() {
        Edge edge = new Edge(vA, vB, 100);
        assertThrows(IllegalArgumentException.class, () -> edge.getOther(vC));
    }

    @Test
    void testEdgeSelfLoopThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Edge(vA, vA, 100));
    }

    @Test
    void testEdgeNegativeDistanceThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Edge(vA, vB, -5));
    }

    @Test
    void testEdgeZeroDistanceThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Edge(vA, vB, 0));
    }

    // ---- Matriz de adyacencia -------------------------------------------

    @Test
    void testAdjacencyMatrixDiagonalZero() {
        int[][] matrix = graph.toAdjacencyMatrix();
        for (int i = 0; i < matrix.length; i++) {
            assertEquals(0, matrix[i][i]);
        }
    }

    @Test
    void testAdjacencyMatrixSymmetric() {
        int[][] matrix = graph.toAdjacencyMatrix();
        int n = matrix.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                assertEquals(matrix[i][j], matrix[j][i],
                    "Matriz no simétrica en [" + i + "][" + j + "]");
            }
        }
    }

    @Test
    void testAdjacencyMatrixEdgeValue() {
        int[][] matrix = graph.toAdjacencyMatrix();
        int iA = vA.getIndex();
        int iB = vB.getIndex();
        assertEquals(100, matrix[iA][iB]);
        assertEquals(100, matrix[iB][iA]);
    }

    @Test
    void testAdjacencyMatrixNoEdgeIsINF() {
        int[][] matrix = graph.toAdjacencyMatrix();
        int iA = vA.getIndex();
        int iC = vC.getIndex();
        // No hay arista directa A-C
        assertEquals(Graph.INF, matrix[iA][iC]);
    }

    // ---- VertexType -----------------------------------------------------

    @Test
    void testVertexTypeFromString() {
        assertEquals(VertexType.DEPOT,        VertexType.fromString("DEPOT"));
        assertEquals(VertexType.DELIVERY,     VertexType.fromString("DELIVERY"));
        assertEquals(VertexType.INTERSECTION, VertexType.fromString("INTERSECCION"));
        assertEquals(VertexType.INTERSECTION, VertexType.fromString("INTERSECTION"));
    }

    @Test
    void testVertexTypeFromStringCaseInsensitive() {
        assertEquals(VertexType.DEPOT, VertexType.fromString("depot"));
        assertEquals(VertexType.DEPOT, VertexType.fromString("Depot"));
    }

    @Test
    void testVertexTypeFromStringInvalidThrows() {
        assertThrows(IllegalArgumentException.class, () -> VertexType.fromString("UNKNOWN"));
    }

    @Test
    void testVertexTypeFromStringNull() {
        // null debe retornar INTERSECTION por defecto
        assertEquals(VertexType.INTERSECTION, VertexType.fromString(null));
    }

    // ---- Grafo vacío ----------------------------------------------------

    @Test
    void testEmptyGraph() {
        Graph empty = new Graph();
        assertTrue(empty.isEmpty());
        assertEquals(0, empty.vertexCount());
        assertEquals(0, empty.edgeCount());
        assertNull(empty.getDepot());
    }

    // ---- getVertexByIndex -----------------------------------------------

    @Test
    void testGetVertexByIndex() {
        Vertex v = graph.getVertexByIndex(vC.getIndex());
        assertNotNull(v);
        assertEquals("C", v.getId());
    }
}