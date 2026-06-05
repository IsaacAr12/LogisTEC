package algorithms;

import graph.Graph;
import ds.MinHeap;

/**
 * Algoritmo de Prim para construir el Arbol de Expansion Minima (MST) de un
 * grafo no dirigido y ponderado conexo.
 *
 * <h2>Idea central</h2>
 * <p>Hace crecer el arbol vertice por vertice. Arranca en el vertice 0.
 * Mantiene para cada vertice fuera del arbol la arista de menor peso que lo
 * conecta con algun vertice ya dentro del arbol ({@code key[v]}). En cada paso
 * extrae el vertice de menor {@code key} (con el {@link MinHeap}) y lo agrega
 * al MST, actualizando las claves de sus vecinos.</p>
 *
 * <h2>Complejidad</h2>
 * <ul>
 *   <li>Tiempo: O((V + A) log V) con heap binario.</li>
 *   <li>Espacio: O(V) para los arreglos auxiliares.</li>
 * </ul>
 *
 * <h2>Uso en LogisTEC</h2>
 * <p>Construye el MST de la ciudad. Se compara empiricamente con Kruskal sobre
 * las mismas instancias (tiempos y costo total deben coincidir).</p>
 *
 * <h2>Restricciones del proyecto</h2>
 * <p>No se usa ninguna coleccion de {@code java.util}.</p>
 *
 */
public class Prim {

    // Constante
    private static final int INF = Integer.MAX_VALUE / 2;

    // Resultado

    /**
     * Para cada vertice v (excepto la raiz), {@code parent[v]} es su padre en el MST.
     * {@code parent[root] == -1}.
     */
    private final int[] parent;

    /**
     * Para cada vertice v (excepto la raiz), {@code edgeWeight[v]} es el peso de la
     * arista que lo conecta a su padre en el MST.
     */
    private final int[] edgeWeight;

    /** Costo total del MST (suma de pesos de las n-1 aristas). */
    private final int totalCost;

    /** Numero de vertices. */
    private final int n;

    /** Tiempo de ejecucion en nanosegundos (para comparativa empirica con Kruskal). */
    private final long elapsedNanos;

    // Constructor — ejecuta el algoritmo

    /**
     * Ejecuta Prim arrancando desde el vertice 0.
     *
     * @param g grafo no dirigido ponderado conexo (implementado por P2)
     * @throws IllegalArgumentException si el grafo tiene 0 vertices
     */
    public Prim(Graph g) {
        n = g.vertexCount();
        if (n == 0) throw new IllegalArgumentException("Grafo vacio");

        parent     = new int[n];
        edgeWeight = new int[n];
        int[] key  = new int[n];       // key[v] = peso minimo de arista hacia el arbol
        boolean[] inMST = new boolean[n];

        for (int i = 0; i < n; i++) {
            key[i]        = INF;
            parent[i]     = -1;
            edgeWeight[i] = 0;
        }
        key[0] = 0;

        MinHeap pq = new MinHeap(n);
        for (int i = 0; i < n; i++) pq.insertOrDecrease(i, key[i]);

        long start = System.nanoTime();

        while (!pq.isEmpty()) {
            int u = pq.extractMin();
            inMST[u] = true;

            // Relajar vecinos
            for (Graph.AdjNode nd = g.adj(u); nd != null; nd = nd.next) {
                int v = nd.to, w = nd.weight;
                if (!inMST[v] && w < key[v]) {
                    key[v]        = w;
                    parent[v]     = u;
                    edgeWeight[v] = w;
                    pq.insertOrDecrease(v, w);
                }
            }
        }

        elapsedNanos = System.nanoTime() - start;

        // Calcular costo total
        int cost = 0;
        for (int v = 1; v < n; v++) {
            if (edgeWeight[v] < INF) cost += edgeWeight[v];
        }
        totalCost = cost;
    }

    // Consultas publicas

    /**
     * Costo total del MST (suma de pesos de todas sus aristas).
     *
     * @return costo total en metros
     */
    public int totalCost() { return totalCost; }

    /**
     * Tiempo de ejecucion del algoritmo (sin contar construccion del grafo).
     *
     * @return nanosegundos transcurridos
     */
    public long elapsedNanos() { return elapsedNanos; }

    /**
     * Arreglo de padres en el MST. {@code parent[v]} es el padre de v;
     * {@code parent[root] == -1}.
     *
     * @return copia defensiva del arreglo
     */
    public int[] parent() { return parent.clone(); }

    /**
     * Arreglo de pesos de las aristas del MST. {@code edgeWeight[v]} es el peso
     * de la arista (parent[v], v).
     *
     * @return copia defensiva del arreglo
     */
    public int[] edgeWeights() { return edgeWeight.clone(); }

    /**
     * Retorna las aristas del MST como una matriz de tres columnas:
     * {@code [u, v, peso]}, con exactamente n-1 filas.
     *
     * <p>Util para que Persona 4 construya el MST inducido de paradas.</p>
     *
     * @return arreglo bidimensional (n-1) × 3
     */
    public int[][] edges() {
        int[][] result = new int[n - 1][3];
        int idx = 0;
        for (int v = 1; v < n; v++) {
            if (parent[v] != -1) {
                result[idx][0] = parent[v];
                result[idx][1] = v;
                result[idx][2] = edgeWeight[v];
                idx++;
            }
        }
        return result;
    }

    /**
     * Representacion textual del MST (para depuracion).
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MST (Prim) — Costo total: ").append(totalCost).append("\n");
        for (int v = 1; v < n; v++) {
            if (parent[v] != -1) {
                sb.append("  ").append(parent[v]).append(" - ")
                        .append(v).append("  peso=").append(edgeWeight[v]).append("\n");
            }
        }
        return sb.toString();
    }
}