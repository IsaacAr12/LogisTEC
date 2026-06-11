import algorithms.Traversals;
import ds.DLinkedList;
import graph.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link Traversals} (BFS y DFS).
 *
 * <p>Se usan dos grafos de prueba verificados a mano:</p>
 *
 * <b>Grafo conexo (6 vértices):</b>
 * <pre>
 *   A ---1--- B ---2--- C
 *   |                   |
 *   3                   4
 *   |                   |
 *   D ---5--- E ---6--- F
 * </pre>
 * BFS desde A: A, B, D, C, E, F  (por niveles)
 * DFS desde A: A, B, C, F, E, D  (preorden, puede variar por orden de adyacencia)
 *
 * <b>Grafo desconexo:</b>
 * <pre>
 *   A ---1--- B    C ---2--- D
 * </pre>
 * Componentes: 2
 *
 * @author Persona 2
 */
public class TraversalsTest {

    private Graph connectedGraph;
    private Graph disconnectedGraph;

    private Vertex cA, cB, cC, cD, cE, cF; // grafo conexo
    private Vertex dA, dB, dC, dD;          // grafo desconexo

    @BeforeEach
    void setUp() {
        // ---- Grafo conexo ----
        connectedGraph = new Graph();
        cA = new Vertex("A", VertexType.DEPOT,        0,   0, 0);
        cB = new Vertex("B", VertexType.INTERSECTION, 100, 0, 0);
        cC = new Vertex("C", VertexType.DELIVERY,     200, 0, 0);
        cD = new Vertex("D", VertexType.DELIVERY,     0, 100, 0);
        cE = new Vertex("E", VertexType.INTERSECTION, 100, 100, 0);
        cF = new Vertex("F", VertexType.DELIVERY,     200, 100, 0);

        connectedGraph.addVertex(cA);
        connectedGraph.addVertex(cB);
        connectedGraph.addVertex(cC);
        connectedGraph.addVertex(cD);
        connectedGraph.addVertex(cE);
        connectedGraph.addVertex(cF);

        connectedGraph.addEdge(new Edge(cA, cB, 1));
        connectedGraph.addEdge(new Edge(cB, cC, 2));
        connectedGraph.addEdge(new Edge(cA, cD, 3));
        connectedGraph.addEdge(new Edge(cC, cF, 4));
        connectedGraph.addEdge(new Edge(cD, cE, 5));
        connectedGraph.addEdge(new Edge(cE, cF, 6));

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

        disconnectedGraph.addEdge(new Edge(dA, dB, 1));
        disconnectedGraph.addEdge(new Edge(dC, dD, 2));
        // A-B y C-D son componentes separadas
    }

    // ================================================================
    // BFS
    // ================================================================

    @Test
    void testBfsVisitsAllConnectedVertices() {
        DLinkedList<Vertex> result = Traversals.bfs(connectedGraph, cA);
        assertEquals(6, result.size(), "BFS debe visitar los 6 vértices del grafo conexo");
    }

    @Test
    void testBfsFirstVertexIsSource() {
        DLinkedList<Vertex> result = Traversals.bfs(connectedGraph, cA);
        assertEquals(cA, result.getFirst());
    }

    @Test
    void testBfsLevelOrder() {
        // Nivel 0: A | Nivel 1: B, D | Nivel 2: C, E | Nivel 3: F
        DLinkedList<Vertex> result = Traversals.bfs(connectedGraph, cA);

        // A debe aparecer antes que B y D
        int idxA = indexOf(result, cA);
        int idxB = indexOf(result, cB);
        int idxD = indexOf(result, cD);
        assertTrue(idxA < idxB, "A debe visitarse antes que B");
        assertTrue(idxA < idxD, "A debe visitarse antes que D");

        // B y D deben aparecer antes que C, E, F
        int idxC = indexOf(result, cC);
        int idxE = indexOf(result, cE);
        int idxF = indexOf(result, cF);
        assertTrue(idxB < idxC, "B debe visitarse antes que C");
        assertTrue(idxD < idxE, "D debe visitarse antes que E");
        assertTrue(idxC < idxF || idxE < idxF, "F debe visitarse al final");
    }

    @Test
    void testBfsDisconnectedOnlyVisitsComponent() {
        DLinkedList<Vertex> result = Traversals.bfs(disconnectedGraph, dA);
        // Solo debe alcanzar A y B (la componente de A)
        assertEquals(2, result.size());
        assertTrue(contains(result, dA));
        assertTrue(contains(result, dB));
        assertFalse(contains(result, dC));
        assertFalse(contains(result, dD));
    }

    @Test
    void testBfsById() {
        DLinkedList<Vertex> result = Traversals.bfs(connectedGraph, "A");
        assertEquals(6, result.size());
    }

    @Test
    void testBfsByIdNotFound() {
        DLinkedList<Vertex> result = Traversals.bfs(connectedGraph, "Z");
        assertEquals(0, result.size());
    }

    @Test
    void testBfsNullSourceThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> Traversals.bfs(connectedGraph, (Vertex) null));
    }

    @Test
    void testBfsNoVertexVisitedTwice() {
        DLinkedList<Vertex> result = Traversals.bfs(connectedGraph, cA);
        // Verificar que no hay duplicados
        for (Vertex v : result) {
            int count = 0;
            for (Vertex u : result) {
                if (v.equals(u)) count++;
            }
            assertEquals(1, count, "El vértice " + v.getId() + " aparece más de una vez en BFS");
        }
    }

    // ================================================================
    // DFS iterativo
    // ================================================================

    @Test
    void testDfsIterativeVisitsAllConnectedVertices() {
        DLinkedList<Vertex> result = Traversals.dfsIterative(connectedGraph, cA);
        assertEquals(6, result.size());
    }

    @Test
    void testDfsIterativeFirstVertexIsSource() {
        DLinkedList<Vertex> result = Traversals.dfsIterative(connectedGraph, cA);
        assertEquals(cA, result.getFirst());
    }

    @Test
    void testDfsIterativeDisconnectedOnlyVisitsComponent() {
        DLinkedList<Vertex> result = Traversals.dfsIterative(disconnectedGraph, dA);
        assertEquals(2, result.size());
        assertTrue(contains(result, dA));
        assertTrue(contains(result, dB));
    }

    @Test
    void testDfsIterativeNoVertexVisitedTwice() {
        DLinkedList<Vertex> result = Traversals.dfsIterative(connectedGraph, cA);
        for (Vertex v : result) {
            int count = 0;
            for (Vertex u : result) {
                if (v.equals(u)) count++;
            }
            assertEquals(1, count, "DFS iterativo repite el vértice " + v.getId());
        }
    }

    @Test
    void testDfsIterativeById() {
        DLinkedList<Vertex> result = Traversals.dfsIterative(connectedGraph, "A");
        assertEquals(6, result.size());
    }

    // ================================================================
    // DFS recursivo
    // ================================================================

    @Test
    void testDfsRecursiveVisitsAllConnectedVertices() {
        DLinkedList<Vertex> result = Traversals.dfsRecursive(connectedGraph, cA);
        assertEquals(6, result.size());
    }

    @Test
    void testDfsRecursiveFirstVertexIsSource() {
        DLinkedList<Vertex> result = Traversals.dfsRecursive(connectedGraph, cA);
        assertEquals(cA, result.getFirst());
    }

    @Test
    void testDfsRecursiveNoVertexVisitedTwice() {
        DLinkedList<Vertex> result = Traversals.dfsRecursive(connectedGraph, cA);
        for (Vertex v : result) {
            int count = 0;
            for (Vertex u : result) {
                if (v.equals(u)) count++;
            }
            assertEquals(1, count, "DFS recursivo repite el vértice " + v.getId());
        }
    }

    @Test
    void testDfsRecursiveDisconnectedOnlyVisitsComponent() {
        DLinkedList<Vertex> result = Traversals.dfsRecursive(disconnectedGraph, dC);
        assertEquals(2, result.size());
        assertTrue(contains(result, dC));
        assertTrue(contains(result, dD));
    }

    @Test
    void testDfsNullSourceThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> Traversals.dfsRecursive(connectedGraph, (Vertex) null));
    }

    // ================================================================
    // Componentes conexas
    // ================================================================

    @Test
    void testConnectedComponentsConnectedGraph() {
        assertEquals(1, Traversals.connectedComponents(connectedGraph));
    }

    @Test
    void testConnectedComponentsDisconnectedGraph() {
        assertEquals(2, Traversals.connectedComponents(disconnectedGraph));
    }

    @Test
    void testIsConnectedConnectedGraph() {
        assertTrue(Traversals.isConnected(connectedGraph));
    }

    @Test
    void testIsConnectedDisconnectedGraph() {
        assertFalse(Traversals.isConnected(disconnectedGraph));
    }

    @Test
    void testIsConnectedEmptyGraph() {
        assertTrue(Traversals.isConnected(new Graph()));
    }

    // ================================================================
    // Alcanzabilidad
    // ================================================================

    @Test
    void testIsReachableInConnectedGraph() {
        assertTrue(Traversals.isReachable(connectedGraph, cA, cF));
    }

    @Test
    void testIsReachableInDisconnectedGraph() {
        assertFalse(Traversals.isReachable(disconnectedGraph, dA, dC));
    }

    @Test
    void testIsReachableSameVertex() {
        assertTrue(Traversals.isReachable(connectedGraph, cA, cA));
    }

    // ================================================================
    // Helpers
    // ================================================================

    private int indexOf(DLinkedList<Vertex> list, Vertex target) {
        int i = 0;
        for (Vertex v : list) {
            if (v.equals(target)) return i;
            i++;
        }
        return -1;
    }

    private boolean contains(DLinkedList<Vertex> list, Vertex target) {
        for (Vertex v : list) {
            if (v.equals(target)) return true;
        }
        return false;
    }
}