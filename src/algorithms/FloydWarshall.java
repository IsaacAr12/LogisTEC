package algorithms;

import graph.Graph;

/**
 * Algoritmo de Floyd-Warshall para calcular la distancia minima entre
 * <em>todos</em> los pares de vertices de un grafo ponderado.
 *
 * <h2>Recurrencia</h2>
 * <pre>
 *   D[k][i][j] = min( D[k-1][i][j],  D[k-1][i][k] + D[k-1][k][j] )
 * </pre>
 *
 * <h2>Complejidad</h2>
 * <ul>
 *   <li>Tiempo: O(V³)</li>
 *   <li>Espacio: O(V²) para las matrices {@code dist} y {@code next}.</li>
 * </ul>
 *
 * <h2>Uso en LogisTEC</h2>
 * <p>Genera la <strong>matriz D</strong> de distancias minimas, insumo del
 * planificador de rutas.</p>
 *
 * <h2>Restricciones del proyecto</h2>
 * <p>No se usa ninguna coleccion de {@code java.util}; todo sobre arreglos nativos.</p>
 */
public class FloydWarshall {

    /** Valor centinela para distancia infinita (coincide con {@code Graph.INF}). */
    public static final int INF = Integer.MAX_VALUE / 2;

    /** {@code dist[i][j]} = distancia minima de i a j. */
    private final int[][] dist;

    /** {@code next[i][j]} = primer paso de i hacia j; -1 si no hay camino. */
    private final int[][] next;

    /** Numero de vertices. */
    private final int n;

    /**
     * Ejecuta Floyd-Warshall sobre el grafo {@code g}.
     * @param g grafo no dirigido ponderado
     */
    public FloydWarshall(Graph g) {
        n    = g.vertexCount();
        dist = new int[n][n];
        next = new int[n][n];

        // Inicializacion a partir de la matriz de adyacencia del grafo:
        // 0 en la diagonal, peso de la arista directa, o INF si no hay arista.
        int[][] adj = g.toAdjacencyMatrix();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = adj[i][j];
                next[i][j] = (i != j && adj[i][j] < INF) ? j : -1;
            }
        }

        // Algoritmo principal: para cada vertice intermedio k.
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (dist[i][k] == INF) continue;
                for (int j = 0; j < n; j++) {
                    if (dist[k][j] == INF) continue;
                    long through = (long) dist[i][k] + dist[k][j];
                    if (through < dist[i][j]) {
                        dist[i][j] = (int) through;
                        next[i][j] = next[i][k];
                    }
                }
            }
        }
    }

    /**
     * Distancia minima entre los vertices {@code i} y {@code j}.
     * @return distancia en metros, o {@link #INF} si no son alcanzables entre si
     */
    public int dist(int i, int j) { checkIndex(i); checkIndex(j); return dist[i][j]; }

    /** Indica si existe al menos un camino entre {@code i} y {@code j}. */
    public boolean hasPath(int i, int j) { checkIndex(i); checkIndex(j); return dist[i][j] < INF; }

    /**
     * Reconstruye el camino minimo de {@code src} a {@code dst} (ambos incluidos).
     * @return arreglo de indices, o {@code null} si no hay camino
     */
    public int[] path(int src, int dst) {
        checkIndex(src); checkIndex(dst);
        if (!hasPath(src, dst)) return null;
        if (src == dst) return new int[]{src};
        int len = 1, cur = src;
        while (cur != dst) {
            cur = next[cur][dst];
            len++;
            if (cur == -1 || len > n + 1) return null;
        }
        int[] result = new int[len];
        cur = src;
        for (int i = 0; i < len; i++) {
            result[i] = cur;
            if (cur != dst) cur = next[cur][dst];
        }
        return result;
    }

    /**
     * Copia de la matriz de distancias completa (la <strong>matriz D</strong>).
     * @return matriz n×n (copia defensiva)
     */
    public int[][] getDistMatrix() {
        int[][] copy = new int[n][n];
        for (int i = 0; i < n; i++) System.arraycopy(dist[i], 0, copy[i], 0, n);
        return copy;
    }

    /**
     * Submatriz de distancias entre un subconjunto de vertices.
     * Util para el planificador: {deposito} ∪ {paradas de un camion}.
     * @param indices indices de vertices de interes
     * @return matriz {@code indices.length × indices.length}
     */
    public int[][] subMatrix(int[] indices) {
        int m = indices.length;
        int[][] sub = new int[m][m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < m; j++)
                sub[i][j] = dist[indices[i]][indices[j]];
        return sub;
    }

    private void checkIndex(int v) {
        if (v < 0 || v >= n)
            throw new IndexOutOfBoundsException("Vertice " + v + " fuera de rango [0," + (n - 1) + "]");
    }
}
