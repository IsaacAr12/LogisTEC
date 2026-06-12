package algorithms;

import graph.Graph;
import graph.Vertex;
import graph.Edge;
import ds.MinHeap;

/**
 * Algoritmo de Dijkstra para caminos minimos desde un vertice origen en un
 * grafo no dirigido ponderado con pesos no negativos.
 *
 * <h2>Idea central</h2>
 * <p>Mantiene para cada vertice una distancia tentativa {@code dist[v]} desde
 * el origen. Repite: extraer el vertice no resuelto con menor distancia y
 * <em>relajar</em> sus aristas. Si pasar por {@code u} mejora la distancia
 * conocida a un vecino {@code v}, actualiza {@code dist[v]} y {@code prev[v]}.</p>
 *
 * <h2>Complejidad</h2>
 * <ul>
 *   <li>Tiempo: O((V + A) log V) usando el {@link MinHeap} de P1.</li>
 *   <li>Espacio: O(V) para los arreglos {@code dist} y {@code prev}.</li>
 * </ul>
 *
 * <h2>Restricciones del proyecto</h2>
 * <p>No se usa ninguna coleccion de {@code java.util}. La cola de prioridad es
 * el {@link MinHeap} del equipo y la lista de adyacencia es la del {@link Graph}.</p>
 */
public class Dijkstra {

    /** Valor centinela que indica distancia infinita (inalcanzable). */
    public static final int INF = Integer.MAX_VALUE / 2;

    /** Arreglo de distancias minimas desde el origen ({@code dist[v]}). */
    private final int[] dist;

    /**
     * Arreglo de predecesores en el arbol de caminos minimos.
     * {@code prev[v] == -1} si {@code v} es el origen o no es alcanzable.
     */
    private final int[] prev;

    /** Numero de vertices del grafo. */
    private final int n;

    /**
     * Crea un objeto Dijkstra y ejecuta el algoritmo desde {@code source}.
     * Despues de la construccion, use {@link #distTo(int)}, {@link #pathTo(int)}
     * y {@link #hasPathTo(int)} para consultar resultados.
     *
     * @param g      grafo no dirigido ponderado (implementado por P2)
     * @param source indice del vertice origen (0-based)
     * @throws IllegalArgumentException si {@code source} esta fuera de rango
     */
    public Dijkstra(Graph g, int source) {
        n = g.vertexCount();
        if (source < 0 || source >= n)
            throw new IllegalArgumentException("Vertice origen fuera de rango: " + source);

        dist = new int[n];
        prev = new int[n];
        for (int i = 0; i < n; i++) {
            dist[i] = INF;
            prev[i] = -1;
        }
        dist[source] = 0;

        // Mapa indice -> Vertex (O(V), evita busquedas O(V) dentro del bucle).
        Vertex[] byIndex = new Vertex[n];
        for (Vertex vv : g.getVertices()) byIndex[vv.getIndex()] = vv;

        // Cola de prioridad minima (implementada por P1).
        MinHeap pq = new MinHeap(n);
        pq.insertOrDecrease(source, 0);

        while (!pq.isEmpty()) {
            int u = pq.extractMin();
            if (dist[u] == INF) continue;            // u inalcanzable: no relaja

            Vertex vu = byIndex[u];
            for (Edge e : g.getNeighbors(vu)) {       // lista de adyacencia real
                int v = e.getOther(vu).getIndex();
                int w = e.getDistance();
                int newDist = dist[u] + w;
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    prev[v] = u;
                    pq.insertOrDecrease(v, newDist);
                }
            }
        }
    }

    /**
     * Distancia minima desde el origen hasta {@code v}.
     * @param v indice del vertice destino
     * @return distancia en metros, o {@link #INF} si no es alcanzable
     */
    public int distTo(int v) { checkIndex(v); return dist[v]; }

    /**
     * Indica si existe camino desde el origen hasta {@code v}.
     * @param v indice del vertice destino
     * @return {@code true} si es alcanzable
     */
    public boolean hasPathTo(int v) { checkIndex(v); return dist[v] < INF; }

    /**
     * Reconstruye el camino minimo desde el origen hasta {@code v} como un
     * arreglo de indices de vertices (origen y destino incluidos).
     *
     * @param v indice del vertice destino
     * @return arreglo de indices del camino, o {@code null} si no es alcanzable
     */
    public int[] pathTo(int v) {
        checkIndex(v);
        if (!hasPathTo(v)) return null;
        int len = 0;
        for (int cur = v; cur != -1; cur = prev[cur]) len++;
        int[] path = new int[len];
        int idx = len - 1;
        for (int cur = v; cur != -1; cur = prev[cur]) path[idx--] = cur;
        return path;
    }

    /**
     * Retorna una copia del arreglo completo de distancias minimas desde el origen.
     * Util para construir la fila correspondiente en la matriz de Floyd-Warshall.
     * @return arreglo {@code dist[]} (copia defensiva)
     */
    public int[] allDistances() { return dist.clone(); }

    private void checkIndex(int v) {
        if (v < 0 || v >= n)
            throw new IndexOutOfBoundsException("Vertice " + v + " fuera de rango [0," + (n - 1) + "]");
    }
}
