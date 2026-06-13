import algorithms.*;
import graph.Graph;
import graph.Vertex;
import graph.VertexType;
import graph.Edge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests JUnit 5 para Dijkstra, FloydWarshall, Prim y Kruskal.
 *
 * ─── GRAFOS DE PRUEBA ────────────────────────────────────────────────────────
 *
 * G1 — 5 vértices, resuelto a mano
 * ┌─────────────────────────────────────────────────────────┐
 *   A(0) ──4── B(1)
 *    │           │
 *    1           2
 *    │           │
 *   C(2) ──5── D(3) ──1── E(4)
 *
 *  Aristas: A-B=4, A-C=1, B-D=2, C-D=5, D-E=1
 *
 *  Dijkstra desde A(0):
 *    init:  [0, INF, INF, INF, INF]
 *    pop A: relajar B→4, C→1
 *           [0, 4, 1, INF, INF]
 *    pop C: relajar D→1+5=6
 *           [0, 4, 1, 6, INF]
 *    pop B: relajar D→4+2=6 (no mejora)
 *    pop D: relajar E→6+1=7
 *           [0, 4, 1, 6, 7]
 *    pop E: sin vecinos sin visitar
 *    RESULTADO: dist = [0, 4, 1, 6, 7]
 *
 *  Floyd-Warshall (pares seleccionados calculados a mano):
 *    D[B][C] = min(B-A-C=4+1=5, B-D-C=2+5=7) = 5
 *    D[B][E] = B-D-E = 2+1 = 3
 *    D[C][E] = C-D-E = 5+1 = 6
 *    D[A][E] = A-C-D-E = 1+5+1 = 7  (o A-B-D-E=4+2+1=7)
 *
 *  MST (Kruskal — aristas en orden de peso):
 *    A-C(1) → OK  {A-C}
 *    D-E(1) → OK  {A-C, D-E}
 *    B-D(2) → OK  {A-C, D-E, B-D}
 *    A-B(4) → OK  {A-C, D-E, B-D, A-B}  ← 4 aristas = n-1
 *    C-D(5) → forma ciclo, descartado
 *    Costo MST = 1+1+2+4 = 8
 *
 * G2 — 6 vértices (para segunda instancia de comparativa)
 *   0──7──1──5──2
 *   │  \      /
 *   8   9   15
 *   │    \ /
 *   3──6──4──11──5
 *    \          /
 *     ────9────
 *
 *  Aristas: 0-1=7, 0-3=8, 0-4=9, 1-2=5, 2-4=15, 3-4=6, 3-5=9, 4-5=11
 *
 *  MST (Kruskal):
 *    1-2(5): OK
 *    3-4(6): OK
 *    0-1(7): OK
 *    0-3(8): OK  (une {0,1,2} con {3,4})
 *    3-5(9): OK  ← 5 aristas para 6 vértices
 *    Costo = 5+6+7+8+9 = 35
 *
 * G3 — grafo desconexo (dos componentes sin aristas entre ellas)
 *   Componente 1: A(0)-B(1)-C(2)
 *   Componente 2: D(3)-E(4)
 * └─────────────────────────────────────────────────────────┘
 *
 * @author Persona 3
 * @version 1.0
 */
@DisplayName("Algoritmos de Caminos Mínimos y MST")
class AlgorithmTest {

    // =========================================================================
    // Grafos compartidos — construidos en @BeforeEach
    // =========================================================================

    private Graph g1;   // 5 vértices, calculado a mano
    private Graph g2;   // 6 vértices, para segunda instancia
    private Graph g3;   // desconexo

    @BeforeEach
    void buildGraphs() {

        // ── G1: A=0, B=1, C=2, D=3, E=4 ─────────────────────────────────
        g1 = new Graph(5);
        Vertex a = new Vertex("A", VertexType.DEPOT,        0, 0, 0);
        Vertex b = new Vertex("B", VertexType.INTERSECTION, 0, 0, 0);
        Vertex c = new Vertex("C", VertexType.INTERSECTION, 0, 0, 0);
        Vertex d = new Vertex("D", VertexType.INTERSECTION, 0, 0, 0);
        Vertex e = new Vertex("E", VertexType.DELIVERY,     0, 0, 0);
        g1.addVertex(a); g1.addVertex(b); g1.addVertex(c);
        g1.addVertex(d); g1.addVertex(e);
        g1.addEdge(new Edge(a, b, 4));
        g1.addEdge(new Edge(a, c, 1));
        g1.addEdge(new Edge(b, d, 2));
        g1.addEdge(new Edge(c, d, 5));
        g1.addEdge(new Edge(d, e, 1));

        // ── G2: vértices 0..5 ─────────────────────────────────────────────
        g2 = new Graph(6);
        Vertex[] v2 = new Vertex[6];
        for (int i = 0; i < 6; i++) {
            VertexType t = (i == 0) ? VertexType.DEPOT : VertexType.INTERSECTION;
            v2[i] = new Vertex(String.valueOf(i), t, 0, 0, 0);
            g2.addVertex(v2[i]);
        }
        g2.addEdge(new Edge(v2[0], v2[1],  7));
        g2.addEdge(new Edge(v2[0], v2[3],  8));
        g2.addEdge(new Edge(v2[0], v2[4],  9));
        g2.addEdge(new Edge(v2[1], v2[2],  5));
        g2.addEdge(new Edge(v2[2], v2[4], 15));
        g2.addEdge(new Edge(v2[3], v2[4],  6));
        g2.addEdge(new Edge(v2[3], v2[5],  9));
        g2.addEdge(new Edge(v2[4], v2[5], 11));

        // ── G3: desconexo ─────────────────────────────────────────────────
        g3 = new Graph(5);
        Vertex ga = new Vertex("A", VertexType.DEPOT,        0, 0, 0);
        Vertex gb = new Vertex("B", VertexType.INTERSECTION, 0, 0, 0);
        Vertex gc = new Vertex("C", VertexType.INTERSECTION, 0, 0, 0);
        Vertex gd = new Vertex("D", VertexType.INTERSECTION, 0, 0, 0);
        Vertex ge = new Vertex("E", VertexType.DELIVERY,     0, 0, 0);
        g3.addVertex(ga); g3.addVertex(gb); g3.addVertex(gc);
        g3.addVertex(gd); g3.addVertex(ge);
        g3.addEdge(new Edge(ga, gb, 3));
        g3.addEdge(new Edge(gb, gc, 4));
        g3.addEdge(new Edge(gd, ge, 2));
        // sin aristas entre {A,B,C} y {D,E}
    }

    // =========================================================================
    // DIJKSTRA
    // =========================================================================

    @Nested
    @DisplayName("Dijkstra")
    class DijkstraTests {

        @Test
        @DisplayName("Distancias desde A(0) en G1 — calculadas a mano")
        void distancesFromAInG1() {
            Dijkstra dijk = new Dijkstra(g1, 0);
            assertEquals(0, dijk.distTo(0), "dist A→A");
            assertEquals(4, dijk.distTo(1), "dist A→B");
            assertEquals(1, dijk.distTo(2), "dist A→C");
            assertEquals(6, dijk.distTo(3), "dist A→D");
            assertEquals(7, dijk.distTo(4), "dist A→E");
        }

        @Test
        @DisplayName("Distancias desde E(4) en G1 — recorrido inverso")
        void distancesFromEInG1() {
            Dijkstra dijk = new Dijkstra(g1, 4);
            // E-D=1, E-D-B=3, E-D-C=6, E-D-B-A=7 ó E-D-C-A=7
            assertEquals(0, dijk.distTo(4), "dist E→E");
            assertEquals(1, dijk.distTo(3), "dist E→D");
            assertEquals(3, dijk.distTo(1), "dist E→B = E-D-B = 1+2=3");
            assertEquals(6, dijk.distTo(2), "dist E→C = E-D-C = 1+5=6");
            assertEquals(7, dijk.distTo(0), "dist E→A = E-D-B-A = 1+2+4=7");
        }

        @Test
        @DisplayName("Vértices inalcanzables en G3 — grafo desconexo")
        void unreachableVerticesInG3() {
            Dijkstra dijk = new Dijkstra(g3, 0); // desde A
            assertFalse(dijk.hasPathTo(3), "D no es alcanzable desde A");
            assertFalse(dijk.hasPathTo(4), "E no es alcanzable desde A");
            assertTrue(dijk.hasPathTo(1),  "B sí es alcanzable");
            assertTrue(dijk.hasPathTo(2),  "C sí es alcanzable");
            assertEquals(Dijkstra.INF, dijk.distTo(3), "dist A→D = INF");
            assertEquals(Dijkstra.INF, dijk.distTo(4), "dist A→E = INF");
        }

        @Test
        @DisplayName("pathTo retorna null para vértices inalcanzables")
        void pathToNullIfUnreachable() {
            Dijkstra dijk = new Dijkstra(g3, 0);
            assertNull(dijk.pathTo(3), "No debe existir camino A→D en G3");
            assertNull(dijk.pathTo(4), "No debe existir camino A→E en G3");
        }

        @Test
        @DisplayName("pathTo reconstruye un camino con distancia correcta en G1")
        void pathReconstructionG1() {
            Dijkstra dijk = new Dijkstra(g1, 0); // A→E
            int[] path = dijk.pathTo(4);

            assertNotNull(path, "Debe existir camino A→E");
            assertEquals(0, path[0],            "El camino debe comenzar en A(0)");
            assertEquals(4, path[path.length-1],"El camino debe terminar en E(4)");

            // Verificar que la suma de pesos del camino coincide con dist[4]
            int total = sumPathWeight(g1, path);
            assertEquals(7, total, "La distancia del camino reconstruido debe ser 7");
        }

        @Test
        @DisplayName("pathTo sobre el mismo origen retorna arreglo de un elemento")
        void pathToSelf() {
            Dijkstra dijk = new Dijkstra(g1, 2);
            int[] path = dijk.pathTo(2);
            assertNotNull(path);
            assertEquals(1, path.length);
            assertEquals(2, path[0]);
        }

        @Test
        @DisplayName("Origen inválido lanza excepción")
        void invalidSourceThrows() {
            assertThrows(IllegalArgumentException.class, () -> new Dijkstra(g1, -1));
            assertThrows(IllegalArgumentException.class, () -> new Dijkstra(g1, 99));
        }

        @Test
        @DisplayName("allDistances retorna copia — modificarla no afecta el objeto")
        void allDistancesIsCopy() {
            Dijkstra dijk = new Dijkstra(g1, 0);
            int[] copy = dijk.allDistances();
            copy[0] = 9999;
            assertEquals(0, dijk.distTo(0), "El original no debe haberse modificado");
        }
    }

    // =========================================================================
    // FLOYD-WARSHALL
    // =========================================================================

    @Nested
    @DisplayName("Floyd-Warshall")
    class FloydWarshallTests {

        @Test
        @DisplayName("Diagonal = 0 en G1")
        void diagonalIsZero() {
            FloydWarshall fw = new FloydWarshall(g1);
            for (int i = 0; i < 5; i++) {
                assertEquals(0, fw.dist(i, i), "dist(" + i + "," + i + ") debe ser 0");
            }
        }

        @Test
        @DisplayName("Todos los pares en G1 — calculados a mano")
        void allPairsG1() {
            FloydWarshall fw = new FloydWarshall(g1);
            assertEquals(4, fw.dist(0,1), "A→B = 4");
            assertEquals(1, fw.dist(0,2), "A→C = 1");
            assertEquals(6, fw.dist(0,3), "A→D = 6");
            assertEquals(7, fw.dist(0,4), "A→E = 7");
            assertEquals(5, fw.dist(1,2), "B→C = B-A-C = 4+1=5");
            assertEquals(2, fw.dist(1,3), "B→D = 2");
            assertEquals(3, fw.dist(1,4), "B→E = B-D-E = 2+1=3");
            assertEquals(5, fw.dist(2,3), "C→D = 5");
            assertEquals(6, fw.dist(2,4), "C→E = C-D-E = 5+1=6");
            assertEquals(1, fw.dist(3,4), "D→E = 1");
        }

        @Test
        @DisplayName("Simetría — el grafo es no dirigido")
        void symmetryG1() {
            FloydWarshall fw = new FloydWarshall(g1);
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    assertEquals(fw.dist(i,j), fw.dist(j,i),
                        "dist(" + i + "," + j + ") debe ser igual a dist(" + j + "," + i + ")");
                }
            }
        }

        @Test
        @DisplayName("Dijkstra y Floyd-Warshall coinciden para todos los orígenes en G1")
        void dijkstraMatchesFWAllOriginsG1() {
            FloydWarshall fw = new FloydWarshall(g1);
            for (int src = 0; src < 5; src++) {
                Dijkstra dijk = new Dijkstra(g1, src);
                for (int dst = 0; dst < 5; dst++) {
                    assertEquals(dijk.distTo(dst), fw.dist(src, dst),
                        "Dijkstra y FW difieren en dist(" + src + "," + dst + ")");
                }
            }
        }

        @Test
        @DisplayName("Dijkstra y Floyd-Warshall coinciden para todos los orígenes en G2")
        void dijkstraMatchesFWAllOriginsG2() {
            FloydWarshall fw = new FloydWarshall(g2);
            for (int src = 0; src < 6; src++) {
                Dijkstra dijk = new Dijkstra(g2, src);
                for (int dst = 0; dst < 6; dst++) {
                    assertEquals(dijk.distTo(dst), fw.dist(src, dst),
                        "Dijkstra y FW difieren en G2 dist(" + src + "," + dst + ")");
                }
            }
        }

        @Test
        @DisplayName("Pares inalcanzables tienen dist INF en G3")
        void disconnectedG3() {
            FloydWarshall fw = new FloydWarshall(g3);
            assertFalse(fw.hasPath(0, 3), "A→D inalcanzable");
            assertFalse(fw.hasPath(0, 4), "A→E inalcanzable");
            assertFalse(fw.hasPath(2, 4), "C→E inalcanzable");
            assertEquals(FloydWarshall.INF, fw.dist(0, 3));
            assertTrue(fw.hasPath(0, 1));
            assertTrue(fw.hasPath(3, 4));
        }

        @Test
        @DisplayName("path retorna null para pares inalcanzables")
        void pathNullIfUnreachable() {
            FloydWarshall fw = new FloydWarshall(g3);
            assertNull(fw.path(0, 3), "No debe haber camino A→D en G3");
        }

        @Test
        @DisplayName("path reconstruye un camino con distancia correcta en G1")
        void pathReconstructionG1() {
            FloydWarshall fw = new FloydWarshall(g1);
            int[] path = fw.path(0, 4); // A→E
            assertNotNull(path);
            assertEquals(0, path[0]);
            assertEquals(4, path[path.length-1]);
            assertEquals(7, sumPathWeight(g1, path),
                "Distancia del camino reconstruido A→E debe ser 7");
        }

        @Test
        @DisplayName("getDistMatrix retorna copia — modificarla no afecta el objeto")
        void getDistMatrixIsCopy() {
            FloydWarshall fw = new FloydWarshall(g1);
            int[][] mat = fw.getDistMatrix();
            mat[0][1] = 9999;
            assertEquals(4, fw.dist(0, 1), "El original no debe haberse modificado");
        }

        @Test
        @DisplayName("subMatrix retorna las distancias correctas del subconjunto")
        void subMatrix() {
            FloydWarshall fw = new FloydWarshall(g1);
            int[] indices = {0, 3, 4}; // A, D, E
            int[][] sub   = fw.subMatrix(indices);
            assertEquals(3, sub.length);
            assertEquals(fw.dist(0,3), sub[0][1], "sub[A][D]");
            assertEquals(fw.dist(0,4), sub[0][2], "sub[A][E]");
            assertEquals(fw.dist(3,4), sub[1][2], "sub[D][E]");
            assertEquals(0,            sub[0][0], "diagonal = 0");
        }

        @Test
        @DisplayName("Índice inválido lanza IndexOutOfBoundsException")
        void invalidIndexThrows() {
            FloydWarshall fw = new FloydWarshall(g1);
            assertThrows(IndexOutOfBoundsException.class, () -> fw.dist(-1, 0));
            assertThrows(IndexOutOfBoundsException.class, () -> fw.dist(0, 99));
        }
    }

    // =========================================================================
    // PRIM
    // =========================================================================

    @Nested
    @DisplayName("Prim")
    class PrimTests {

        @Test
        @DisplayName("Costo del MST en G1 = 8 (calculado a mano)")
        void mstCostG1() {
            Prim prim = new Prim(g1);
            // Aristas: A-C(1) + D-E(1) + B-D(2) + A-B(4) = 8
            assertEquals(8, prim.totalCost(), "Costo MST G1 = 8");
        }

        @Test
        @DisplayName("Costo del MST en G2 = 35 (calculado a mano)")
        void mstCostG2() {
            Prim prim = new Prim(g2);
            // 1-2(5)+3-4(6)+0-1(7)+0-3(8)+3-5(9) = 35
            assertEquals(35, prim.totalCost(), "Costo MST G2 = 35");
        }

        @Test
        @DisplayName("El MST tiene exactamente n-1 aristas")
        void mstHasNMinusOneEdges() {
            Prim prim = new Prim(g1);
            int[][] edges = prim.edges();
            assertEquals(g1.vertexCount() - 1, edges.length,
                "El MST debe tener n-1 aristas");
        }

        @Test
        @DisplayName("Todas las aristas del MST existen en el grafo original")
        void mstEdgesExistInGraph() {
            Prim prim = new Prim(g1);
            for (int[] edge : prim.edges()) {
                int w = findEdgeWeight(g1, edge[0], edge[1]);
                assertTrue(w > 0,
                    "Arista (" + edge[0] + "," + edge[1] + ") debe existir en G1");
                assertEquals(edge[2], w,
                    "El peso de la arista del MST debe coincidir con el grafo");
            }
        }

        @Test
        @DisplayName("La suma de pesos de edges() coincide con totalCost()")
        void edgesSumMatchesTotalCost() {
            Prim prim = new Prim(g1);
            int sum = 0;
            for (int[] edge : prim.edges()) sum += edge[2];
            assertEquals(prim.totalCost(), sum,
                "La suma de los pesos de edges() debe coincidir con totalCost()");
        }

        @Test
        @DisplayName("elapsedNanos retorna un valor positivo")
        void elapsedNanosIsPositive() {
            Prim prim = new Prim(g1);
            assertTrue(prim.elapsedNanos() >= 0,
                "elapsedNanos debe ser no negativo");
        }

        @Test
        @DisplayName("Grafo vacío lanza excepción")
        void emptyGraphThrows() {
            Graph empty = new Graph(1);
            assertThrows(IllegalArgumentException.class, () -> new Prim(empty));
        }
    }

    // =========================================================================
    // KRUSKAL
    // =========================================================================

    @Nested
    @DisplayName("Kruskal")
    class KruskalTests {

        @Test
        @DisplayName("Costo del MST en G1 = 8 (calculado a mano)")
        void mstCostG1() {
            Kruskal kruskal = new Kruskal(g1);
            assertEquals(8, kruskal.totalCost(), "Costo MST G1 = 8");
        }

        @Test
        @DisplayName("Costo del MST en G2 = 35 (calculado a mano)")
        void mstCostG2() {
            Kruskal kruskal = new Kruskal(g2);
            assertEquals(35, kruskal.totalCost(), "Costo MST G2 = 35");
        }

        @Test
        @DisplayName("El MST tiene exactamente n-1 aristas")
        void mstHasNMinusOneEdges() {
            Kruskal kruskal = new Kruskal(g1);
            assertEquals(g1.vertexCount() - 1, kruskal.edges().length);
        }

        @Test
        @DisplayName("Las aristas del MST están en orden no decreciente de peso")
        void edgesInWeightOrder() {
            Kruskal kruskal = new Kruskal(g1);
            int[][] edges = kruskal.edges();
            for (int i = 1; i < edges.length; i++) {
                assertTrue(edges[i-1][2] <= edges[i][2],
                    "Las aristas deben estar en orden no decreciente de peso");
            }
        }

        @Test
        @DisplayName("Todas las aristas del MST existen en el grafo original")
        void mstEdgesExistInGraph() {
            Kruskal kruskal = new Kruskal(g1);
            for (int[] edge : kruskal.edges()) {
                int w = findEdgeWeight(g1, edge[0], edge[1]);
                assertTrue(w > 0,
                    "Arista (" + edge[0] + "," + edge[1] + ") debe existir en G1");
            }
        }

        @Test
        @DisplayName("La suma de pesos de edges() coincide con totalCost()")
        void edgesSumMatchesTotalCost() {
            Kruskal kruskal = new Kruskal(g1);
            int sum = 0;
            for (int[] edge : kruskal.edges()) sum += edge[2];
            assertEquals(kruskal.totalCost(), sum);
        }

        @Test
        @DisplayName("elapsedNanos retorna un valor no negativo")
        void elapsedNanosIsPositive() {
            Kruskal kruskal = new Kruskal(g1);
            assertTrue(kruskal.elapsedNanos() >= 0);
        }

        @Test
        @DisplayName("Grafo vacío lanza excepción")
        void emptyGraphThrows() {
            Graph empty = new Graph(1);
            assertThrows(IllegalArgumentException.class, () -> new Kruskal(empty));
        }
    }

    // =========================================================================
    // COMPARATIVA EMPÍRICA — PRIM VS KRUSKAL
    // =========================================================================

    @Nested
    @DisplayName("Comparativa empírica Prim vs Kruskal")
    class PrimVsKruskalComparison {

        // ── Instancia 1: G1 (5 vértices, 5 aristas) ──────────────────────

        @Test
        @DisplayName("Instancia 1 — G1: Prim y Kruskal producen el mismo costo")
        void sameCostG1() {
            Prim    prim    = new Prim(g1);
            Kruskal kruskal = new Kruskal(g1);
            assertEquals(prim.totalCost(), kruskal.totalCost(),
                "Prim y Kruskal deben producir el mismo costo total en G1");
        }

        @Test
        @DisplayName("Instancia 1 — G1: tabla de tiempos empíricos")
        void timingTableG1() {
            Prim    prim    = new Prim(g1);
            Kruskal kruskal = new Kruskal(g1);
            printTimingTable("G1", g1.vertexCount(), g1.edgeCount(), prim, kruskal);
            // No hay assertion de tiempo — el resultado varía por hardware
            assertTrue(true);
        }

        // ── Instancia 2: G2 (6 vértices, 8 aristas) ──────────────────────

        @Test
        @DisplayName("Instancia 2 — G2: Prim y Kruskal producen el mismo costo")
        void sameCostG2() {
            Prim    prim    = new Prim(g2);
            Kruskal kruskal = new Kruskal(g2);
            assertEquals(prim.totalCost(), kruskal.totalCost(),
                "Prim y Kruskal deben producir el mismo costo total en G2");
        }

        @Test
        @DisplayName("Instancia 2 — G2: tabla de tiempos empíricos")
        void timingTableG2() {
            Prim    prim    = new Prim(g2);
            Kruskal kruskal = new Kruskal(g2);
            printTimingTable("G2", g2.vertexCount(), g2.edgeCount(), prim, kruskal);
            assertTrue(true);
        }

        // ── Instancia 3: grafo denso 10 vértices ─────────────────────────

        @Test
        @DisplayName("Instancia 3 — G-denso 10v: Prim y Kruskal producen el mismo costo")
        void sameCostDense() {
            Graph g = buildDense10();
            Prim    prim    = new Prim(g);
            Kruskal kruskal = new Kruskal(g);
            assertEquals(prim.totalCost(), kruskal.totalCost(),
                "Prim y Kruskal deben coincidir en el grafo denso de 10 vértices");
        }

        @Test
        @DisplayName("Instancia 3 — G-denso 10v: tabla de tiempos empíricos")
        void timingTableDense() {
            Graph g     = buildDense10();
            Prim    prim    = new Prim(g);
            Kruskal kruskal = new Kruskal(g);
            printTimingTable("G-denso-10v", g.vertexCount(), g.edgeCount(), prim, kruskal);
            assertTrue(true);
        }

        // ── Grafo denso de 10 vértices ────────────────────────────────────

        /**
         * Construye un grafo con 10 vértices y 20 aristas para la tercera instancia
         * de la comparativa. Los pesos son distintos para evitar empates en el MST.
         *
         * MST esperado — aristas en orden (Kruskal elige las más baratas sin ciclos):
         *   0-1(2), 1-3(3), 3-5(4), 5-7(5), 7-9(6), 9-8(7), 8-6(8), 6-4(9), 4-2(10)
         *   = 9 aristas para 10 vértices, costo = 2+3+4+5+6+7+8+9+10 = 54
         */
        private Graph buildDense10() {
            Graph g = new Graph(10);
            Vertex[] v = new Vertex[10];
            for (int i = 0; i < 10; i++) {
                VertexType t = (i == 0) ? VertexType.DEPOT : VertexType.INTERSECTION;
                v[i] = new Vertex(String.valueOf(i), t, 0, 0, 0);
                g.addVertex(v[i]);
            }
            // Cadena base (MST)
            g.addEdge(new Edge(v[0], v[1],  2));
            g.addEdge(new Edge(v[1], v[3],  3));
            g.addEdge(new Edge(v[3], v[5],  4));
            g.addEdge(new Edge(v[5], v[7],  5));
            g.addEdge(new Edge(v[7], v[9],  6));
            g.addEdge(new Edge(v[9], v[8],  7));
            g.addEdge(new Edge(v[8], v[6],  8));
            g.addEdge(new Edge(v[6], v[4],  9));
            g.addEdge(new Edge(v[4], v[2], 10));
            // Aristas extra (más pesadas que las del MST para no alterar el resultado)
            g.addEdge(new Edge(v[0], v[2], 20));
            g.addEdge(new Edge(v[1], v[5], 18));
            g.addEdge(new Edge(v[2], v[6], 22));
            g.addEdge(new Edge(v[3], v[7], 15));
            g.addEdge(new Edge(v[4], v[8], 25));
            g.addEdge(new Edge(v[0], v[9], 30));
            g.addEdge(new Edge(v[2], v[4], 12));
            g.addEdge(new Edge(v[1], v[7], 17));
            g.addEdge(new Edge(v[5], v[9], 14));
            g.addEdge(new Edge(v[3], v[9], 19));
            g.addEdge(new Edge(v[6], v[0], 28));
            return g;
        }

        /** Imprime la tabla de comparación empírica (visible en la salida de JUnit). */
        private void printTimingTable(String label, int v, int e, Prim prim, Kruskal kruskal) {
            System.out.println();
            System.out.println("══════════════════════════════════════════════════════");
            System.out.printf ("  Comparativa empírica Prim vs Kruskal — %s%n", label);
            System.out.println("══════════════════════════════════════════════════════");
            System.out.printf ("  Vértices : %d%n", v);
            System.out.printf ("  Aristas  : %d%n", e);
            System.out.println("──────────────────────────────────────────────────────");
            System.out.printf ("  %-10s  Costo=%6d  Tiempo=%10d ns%n",
                "Prim",    prim.totalCost(),    prim.elapsedNanos());
            System.out.printf ("  %-10s  Costo=%6d  Tiempo=%10d ns%n",
                "Kruskal", kruskal.totalCost(), kruskal.elapsedNanos());
            System.out.println("──────────────────────────────────────────────────────");
            long tP = prim.elapsedNanos(), tK = kruskal.elapsedNanos();
            if (tK > 0) {
                System.out.printf("  Ratio Prim/Kruskal = %.2f%n", (double) tP / tK);
            }
            System.out.println("══════════════════════════════════════════════════════");
        }
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /**
     * Suma los pesos de las aristas de un camino dado como arreglo de índices.
     * Usado para verificar que un camino reconstruido tiene la distancia correcta.
     */
    private static int sumPathWeight(Graph g, int[] path) {
        int total = 0;
        for (int i = 0; i < path.length - 1; i++) {
            total += findEdgeWeight(g, path[i], path[i + 1]);
        }
        return total;
    }

    /**
     * Busca el peso de la arista (u, v) en el grafo usando la lista de adyacencia.
     * Retorna -1 si la arista no existe.
     */
    private static int findEdgeWeight(Graph g, int uIdx, int vIdx) {
        Vertex u = g.getVertexByIndex(uIdx);
        if (u == null) return -1;
        for (Edge e : g.getNeighbors(u)) {
            if (e.getOther(u).getIndex() == vIdx) return e.getDistance();
        }
        return -1;
    }
}