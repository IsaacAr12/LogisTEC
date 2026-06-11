package algorithms;

import ds.DLinkedList;
import graph.Graph;
import graph.Vertex;

/**
 * Algoritmo de Warshall — Cierre transitivo de un grafo.
 *
 * <h2>Qué calcula</h2>
 * <p>Dada la matriz de adyacencia booleana del grafo, construye la matriz de
 * <b>cierre transitivo</b> {@code P} tal que {@code P[i][j] == true} si y solo si
 * existe algún camino (de cualquier longitud) del vértice {@code i} al vértice {@code j}.</p>
 *
 * <p>Esta es una respuesta de <em>alcanzabilidad</em>, no de distancia: no importa
 * cuánto cueste el camino, solo si existe.</p>
 *
 * <h2>Uso en LogísTEC</h2>
 * <p>Warshall responde: "¿Es alcanzable el destino de este paquete desde el depósito?"
 * Si {@code P[depotIndex][destinoIndex] == false}, el paquete se marca como rechazado
 * y no se asigna a ningún camión.</p>
 *
 * <h2>Recurrencia</h2>
 * <pre>
 *   P⁰[i][j]  =  true si existe arista directa (i,j) o i == j
 *   Pᵏ[i][j]  =  Pᵏ⁻¹[i][j]  OR  (Pᵏ⁻¹[i][k] AND Pᵏ⁻¹[k][j])
 * </pre>
 * <p>Es decir: "puedo ir de i a j si ya podía antes, o si ahora puedo pasando por k".</p>
 *
 * <h2>Complejidad</h2>
 * <ul>
 *   <li>Tiempo: O(|V|³) — tres bucles anidados sobre los vértices.</li>
 *   <li>Espacio: O(|V|²) — la matriz booleana n×n.</li>
 * </ul>
 *
 * @author Persona 2
 * @version 1.0
 */
public class Warshall {

    /**
     * Calcula el cierre transitivo del grafo dado.
     *
     * <p>El índice de cada vértice ({@link Vertex#getIndex()}) determina la fila/columna
     * en la matriz resultante.</p>
     *
     * @param graph grafo no dirigido sobre el que se calcula la alcanzabilidad.
     * @return matriz {@code boolean[n][n]} donde {@code result[i][j] == true}
     *         indica que existe un camino del vértice con índice {@code i}
     *         al vértice con índice {@code j}.
     */
    public static boolean[][] compute(Graph graph) {
        // Inicializar con la matriz de adyacencia booleana del grafo
        boolean[][] reach = graph.toBooleanMatrix();
        int n = reach.length;

        // Algoritmo de Warshall: O(n³)
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    // ¿Puedo ir de i a j pasando por k?
                    if (reach[i][k] && reach[k][j]) {
                        reach[i][j] = true;
                    }
                }
            }
        }

        return reach;
    }

    // ================================================================
    // Consultas sobre la matriz resultante
    // ================================================================

    /**
     * Indica si el vértice {@code target} es alcanzable desde {@code source}
     * según la matriz de cierre transitivo precalculada.
     *
     * @param reach  matriz de cierre transitivo obtenida con {@link #compute(Graph)}.
     * @param source vértice de partida.
     * @param target vértice destino.
     * @return {@code true} si existe un camino de {@code source} a {@code target}.
     */
    public static boolean isReachable(boolean[][] reach, Vertex source, Vertex target) {
        return reach[source.getIndex()][target.getIndex()];
    }

    /**
     * Filtra de una lista de vértices destino aquellos que <em>no</em> son
     * alcanzables desde el depósito.
     *
     * <p>Retorna la lista de ids de los vértices inalcanzables (paquetes que
     * deben ser marcados como rechazados).</p>
     *
     * @param graph    grafo de la ciudad.
     * @param reach    matriz de cierre transitivo precalculada con {@link #compute(Graph)}.
     * @param destinos lista de vértices destino (de los paquetes).
     * @return lista de ids de vértices inalcanzables desde el depósito.
     */
    public static DLinkedList<String> unreachableFromDepot(
            Graph graph,
            boolean[][] reach,
            DLinkedList<String> destinos) {

        DLinkedList<String> unreachable = new DLinkedList<>();
        Vertex depot = graph.getDepot();
        if (depot == null) return unreachable;

        for (String id : destinos) {
            Vertex target = graph.getVertex(id);
            if (target == null || !reach[depot.getIndex()][target.getIndex()]) {
                unreachable.addLast(id);
            }
        }
        return unreachable;
    }

    // ================================================================
    // Utilidades de visualización / reporte
    // ================================================================

    /**
     * Genera una representación textual de la matriz de cierre transitivo,
     * usando los ids de los vértices como encabezados de filas y columnas.
     *
     * <p>Útil para el reporte final y para depuración.</p>
     *
     * @param graph grafo cuyos vértices se usan como encabezados.
     * @param reach matriz de cierre transitivo calculada con {@link #compute(Graph)}.
     * @return String con la tabla formateada.
     */
    public static String matrixToString(Graph graph, boolean[][] reach) {
        int n = reach.length;
        // Obtener ids en orden de índice
        String[] ids = new String[n];
        for (Vertex v : graph.getVertices()) {
            ids[v.getIndex()] = v.getId();
        }

        // Ancho máximo de los ids para alineación
        int maxLen = 2;
        for (String id : ids) {
            if (id != null && id.length() > maxLen) maxLen = id.length();
        }

        StringBuilder sb = new StringBuilder();

        // Encabezado de columnas
        sb.append(pad("", maxLen)).append(" | ");
        for (int j = 0; j < n; j++) {
            sb.append(pad(ids[j] != null ? ids[j] : "?", maxLen)).append(" ");
        }
        sb.append("\n");

        // Línea separadora
        sb.append("-".repeat(maxLen)).append("-+-");
        sb.append("-".repeat((maxLen + 1) * n)).append("\n");

        // Filas
        for (int i = 0; i < n; i++) {
            sb.append(pad(ids[i] != null ? ids[i] : "?", maxLen)).append(" | ");
            for (int j = 0; j < n; j++) {
                sb.append(pad(reach[i][j] ? "1" : "0", maxLen)).append(" ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Imprime en consola la matriz de cierre transitivo con encabezados.
     *
     * @param graph grafo.
     * @param reach matriz de cierre transitivo.
     */
    public static void printMatrix(Graph graph, boolean[][] reach) {
        System.out.println("=== Cierre Transitivo (Warshall) ===");
        System.out.println(matrixToString(graph, reach));
    }

    /**
     * Imprime el reporte de alcanzabilidad desde el depósito:
     * para cada vértice del grafo indica si es alcanzable o no.
     *
     * @param graph grafo.
     * @param reach matriz de cierre transitivo.
     */
    public static void printReachabilityReport(Graph graph, boolean[][] reach) {
        Vertex depot = graph.getDepot();
        if (depot == null) {
            System.out.println("[Warshall] No hay depósito en el grafo.");
            return;
        }
        System.out.println("=== Alcanzabilidad desde depósito '" + depot.getId() + "' ===");
        for (Vertex v : graph.getVertices()) {
            boolean reachable = reach[depot.getIndex()][v.getIndex()];
            System.out.printf("  %-15s -> %s%n",
                v.getId(),
                reachable ? "ALCANZABLE" : "*** INALCANZABLE ***");
        }
        System.out.println();
    }

    // ================================================================
    // Privados
    // ================================================================

    private static String pad(String s, int width) {
        if (s.length() >= width) return s.substring(0, width);
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < width) sb.append(' ');
        return sb.toString();
    }
}