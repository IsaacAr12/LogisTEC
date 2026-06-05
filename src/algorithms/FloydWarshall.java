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
 * <p>Interpretacion: la distancia minima de i a j usando solo los vertices
 * intermedios {0, 1, …, k} se obtiene comparando "ir directo" con "pasar
 * por k". Se implementa con dos matrices (estado actual y anterior) para
 * ahorrar memoria, o con actualizacion in-place dado que el resultado es
 * correcto en grafos sin ciclos negativos.</p>
 *
 * <h2>Complejidad</h2>
 * <ul>
 *   <li>Tiempo: O(V³)</li>
 *   <li>Espacio: O(V²) para las matrices {@code D} y {@code next}.</li>
 * </ul>
 *
 * <h2>Uso en LogisTEC</h2>
 * <p>Genera la <strong>matriz D</strong> de distancias minimas entre el deposito
 * y todos los puntos de entrega. Esta matriz es el insumo principal del
 * planificador de rutas.</p>
 *
 * <h2>Restricciones del proyecto</h2>
 * <p>No se usa ninguna coleccion de {@code java.util}. all sobre arreglos nativos.</p>
 *
 */
public class FloydWarshall {


    // Constante

    /** Valor centinela para distancia infinita (par de vertices no conectados). */
    public static final int INF = Integer.MAX_VALUE / 2;


    // Resultado


    /** Matriz de distancias minimas. {@code dist[i][j]} es la distancia minima de i a j. */
    private final int[][] dist;

    /**
     * Matriz de reconstruccion de caminos.
     * {@code next[i][j]} es el primer paso desde i hacia j en el camino minimo.
     * Si {@code next[i][j] == -1}, no hay camino de i a j.
     */
    private final int[][] next;

    /** Numero de vertices. */
    private final int n;


    // Constructor , ejecuta el algoritmo


    /**
     * Ejecuta Floyd-Warshall sobre el grafo {@code g}.
     * Despues de la construccion, use {@link #dist(int, int)},
     * {@link #hasPath(int, int)} y {@link #path(int, int)} para consultar.
     *
     * @param g grafo no dirigido ponderado
     */
    public FloydWarshall(Graph g) {
        n    = g.vertexCount();
        dist = new int[n][n];
        next = new int[n][n];

        // ========= Inicializacion ===========
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = INF;
                next[i][j] = -1;
            }
            dist[i][i] = 0;
        }

        // Llenar con las aristas del grafo
        for (int u = 0; u < n; u++) {
            for (Graph.AdjNode nd = g.adj(u); nd != null; nd = nd.next) {
                int v = nd.to, w = nd.weight;
                if (w < dist[u][v]) {          // por si hay aristas paralelas
                    dist[u][v] = w;
                    dist[v][u] = w;
                    next[u][v] = v;
                    next[v][u] = u;
                }
            }
        }

        // ========   Algoritmo principal =========
        // Para cada vertice intermedio k:
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                // Optimizacion: si i no llega a k, ninguna mejora es posible
                if (dist[i][k] == INF) continue;
                for (int j = 0; j < n; j++) {
                    if (dist[k][j] == INF) continue;
                    long through = (long) dist[i][k] + dist[k][j];
                    if (through < dist[i][j]) {
                        dist[i][j] = (int) through;
                        next[i][j] = next[i][k];  // el camino i→j pasa por k
                    }
                }
            }
        }
    }


    // Consultas publicas


    /**
     * Distancia minima entre los vertices {@code i} y {@code j}.
     *
     * @return distancia en metros, o {@link #INF} si no son alcanzables entre si
     */
    public int dist(int i, int j) {
        checkIndex(i); checkIndex(j);
        return dist[i][j];
    }

    /**
     * Indica si existe al menos un camino entre {@code i} y {@code j}.
     */
    public boolean hasPath(int i, int j) {
        checkIndex(i); checkIndex(j);
        return dist[i][j] < INF;
    }

    /**
     * Reconstruye el camino minimo de {@code src} a {@code dst} como arreglo
     * de indices de vertices (ambos extremos incluidos).
     *
     * @param src indice del vertice origen
     * @param dst indice del vertice destino
     * @return arreglo de indices, o {@code null} si no hay camino
     */
    public int[] path(int src, int dst) {
        checkIndex(src); checkIndex(dst);
        if (!hasPath(src, dst)) return null;
        if (src == dst) return new int[]{src};

        // Contar nodos del camino recorriendo next[][]
        int len = 1;
        int cur = src;
        while (cur != dst) {
            cur = next[cur][dst];
            len++;
            if (len > n + 1) return null; // salvaguarda ante inconsistencia
        }

        // Construir el arreglo
        int[] result = new int[len];
        cur = src;
        for (int i = 0; i < len; i++) {
            result[i] = cur;
            if (cur != dst) cur = next[cur][dst];
        }
        return result;
    }

    /**
     * Retorna una <strong>copia</strong> de la matriz de distancias completa.
     * {@code dist[i][j]} = distancia minima de i a j ({@link #INF} si inalcanzable).
     *
     * <p>Esta es la <strong>matriz D</strong> </p>
     *
     * @return matriz n×n de enteros (copia defensiva)
     */
    public int[][] getDistMatrix() {
        int[][] copy = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(dist[i], 0, copy[i], 0, n);
        }
        return copy;
    }

    /**
     * Retorna la submatriz de distancias entre un subconjunto de vertices.
     * Util para el planificador: construye la matriz de distancias entre
     * {deposito} ∪ {paradas asignadas a un camion}.
     *
     * @param indices arreglo de indices de vertices de interes
     * @return matriz {@code indices.length × indices.length}
     */
    public int[][] subMatrix(int[] indices) {
        int m = indices.length;
        int[][] sub = new int[m][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                sub[i][j] = dist[indices[i]][indices[j]];
            }
        }
        return sub;
    }


    private void checkIndex(int v) {
        if (v < 0 || v >= n)
            throw new IndexOutOfBoundsException("Vertice " + v + " fuera de rango [0," + (n - 1) + "]");
    }
}