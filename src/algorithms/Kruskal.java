package algorithms;

import graph.Graph;

/**
 * Algoritmo de Kruskal para construir el Arbol de Expansion Minima (MST) de un
 * grafo no dirigido y ponderado conexo.
 *
 * <h2>Idea central</h2>
 * <p>Procesa las aristas en orden creciente de peso. Para cada arista (u, v):
 * si u y v estan en distintas componentes (detectado con Union-Find), la arista
 * se agrega al MST y se unen las componentes; si estan en la misma componente,
 * agregarla formaria un ciclo y se descarta.
 * El algoritmo termina cuando el MST tiene n-1 aristas.</p>
 *
 * <h2>Complejidad</h2>
 * <ul>
 *   <li>Tiempo: O(A log A) dominado por el ordenamiento inicial de aristas.</li>
 *   <li>Espacio: O(A + V) para el arreglo de aristas y Union-Find.</li>
 * </ul>
 *
 * <h2>Union-Find interno</h2>
 * <p>Implementado aqui mismo (clase privada estatica) con compresion de caminos
 * y union por rango, dando O(α(n)) amortizado por operacion.</p>
 *
 * <h2>Restricciones del proyecto</h2>
 * <p>No se usa ninguna coleccion de {@code java.util}. El ordenamiento es
 * mergesort implementado aqui (no {@code Arrays.sort}).</p>
 *
 */
public class Kruskal {

    // Resultado

    /** Aristas del MST como arreglo [u, v, peso]. Exactamente n-1 aristas. */
    private final int[][] mstEdges;

    /** Costo total del MST. */
    private final int totalCost;

    /** Numero de vertices. */
    private final int n;

    /** Tiempo de ejecucion en nanosegundos (para comparativa empirica con Prim). */
    private final long elapsedNanos;

    // Constructor - ejecuta el algoritmo

    /**
     * Ejecuta Kruskal sobre el grafo {@code g}.
     *
     * @param g grafo no dirigido ponderado conexo
     * @throws IllegalArgumentException si el grafo tiene 0 vertices
     */
    public Kruskal(Graph g) {
        n = g.vertexCount();
        if (n == 0) throw new IllegalArgumentException("Grafo vacio");

        //  1. Recolectar todas las aristas (sin duplicar: u < v)
        // Primero contamos cuantas hay (el grafo no expone edgeCount directamente)
        int edgeCount = 0;
        for (int u = 0; u < n; u++) {
            for (Graph.AdjNode nd = g.adj(u); nd != null; nd = nd.next) {
                if (u < nd.to) edgeCount++;
            }
        }

        int[][] edges = new int[edgeCount][3]; // [u, v, peso]
        int idx = 0;
        for (int u = 0; u < n; u++) {
            for (Graph.AdjNode nd = g.adj(u); nd != null; nd = nd.next) {
                if (u < nd.to) {
                    edges[idx][0] = u;
                    edges[idx][1] = nd.to;
                    edges[idx][2] = nd.weight;
                    idx++;
                }
            }
        }

        //  2. Ordenar aristas por peso (mergesort propio)
        long start = System.nanoTime();

        mergeSort(edges, 0, edgeCount - 1);

        //  3. Procesar aristas con Union-Find
        UnionFind uf   = new UnionFind(n);
        mstEdges       = new int[n - 1][3];
        int mstSize    = 0;
        int cost       = 0;

        for (int i = 0; i < edgeCount && mstSize < n - 1; i++) {
            int u = edges[i][0], v = edges[i][1], w = edges[i][2];
            if (uf.find(u) != uf.find(v)) {
                uf.union(u, v);
                mstEdges[mstSize][0] = u;
                mstEdges[mstSize][1] = v;
                mstEdges[mstSize][2] = w;
                mstSize++;
                cost += w;
            }
        }

        elapsedNanos = System.nanoTime() - start;
        totalCost    = cost;
    }


    // Consultas publicas

    /** @return costo total del MST */
    public int totalCost() { return totalCost; }

    /** @return tiempo de ejecucion en nanosegundos */
    public long elapsedNanos() { return elapsedNanos; }

    /**
     * Retorna las aristas del MST como una matriz [u, v, peso] de n-1 filas.
     * La misma interfaz que {@link Prim#edges()} para facilitar la comparacion.
     *
     * @return copia defensiva de las aristas del MST
     */
    public int[][] edges() {
        int[][] copy = new int[mstEdges.length][3];
        for (int i = 0; i < mstEdges.length; i++) {
            System.arraycopy(mstEdges[i], 0, copy[i], 0, 3);
        }
        return copy;
    }

    /**
     * Representacion textual del MST (para depuracion).
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MST (Kruskal) — Costo total: ").append(totalCost).append("\n");
        for (int[] e : mstEdges) {
            sb.append("  ").append(e[0]).append(" — ").append(e[1])
                    .append("  peso=").append(e[2]).append("\n");
        }
        return sb.toString();
    }

    // Mergesort propio (sin java.util.Arrays)

    /**
     * Ordena {@code edges[lo..hi]} por la columna 2 (peso) usando mergesort.
     * Complejidad O(A log A).
     */
    private static void mergeSort(int[][] edges, int lo, int hi) {
        if (lo >= hi) return;
        int mid = lo + (hi - lo) / 2;
        mergeSort(edges, lo, mid);
        mergeSort(edges, mid + 1, hi);
        merge(edges, lo, mid, hi);
    }

    private static void merge(int[][] edges, int lo, int mid, int hi) {
        int leftLen  = mid - lo + 1;
        int rightLen = hi - mid;

        int[][] left  = new int[leftLen][3];
        int[][] right = new int[rightLen][3];

        for (int i = 0; i < leftLen; i++)
            System.arraycopy(edges[lo + i], 0, left[i], 0, 3);
        for (int j = 0; j < rightLen; j++)
            System.arraycopy(edges[mid + 1 + j], 0, right[j], 0, 3);

        int i = 0, j = 0, k = lo;
        while (i < leftLen && j < rightLen) {
            if (left[i][2] <= right[j][2]) {
                System.arraycopy(left[i++], 0, edges[k++], 0, 3);
            } else {
                System.arraycopy(right[j++], 0, edges[k++], 0, 3);
            }
        }
        while (i < leftLen)  System.arraycopy(left[i++],  0, edges[k++], 0, 3);
        while (j < rightLen) System.arraycopy(right[j++], 0, edges[k++], 0, 3);
    }


    // Union-Find interno (compresion de caminos + union por rango)

    /**
     * Estructura Union-Find para uso exclusivo de Kruskal.
     * No expuesta publicamente - se accede solo dentro de este archivo.
     */
    private static final class UnionFind {
        private final int[] parent;
        private final int[] rank;

        UnionFind(int n) {
            parent = new int[n];
            rank   = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        /**
         * Encuentra el representante del conjunto al que pertenece {@code x}.
         * Aplica compresion de caminos.
         */
        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]); // compresion
            return parent[x];
        }

        /**
         * Une los conjuntos que contienen {@code x} y {@code y}.
         * Aplica union por rango.
         */
        void union(int x, int y) {
            int rx = find(x), ry = find(y);
            if (rx == ry) return;
            if (rank[rx] < rank[ry])      { parent[rx] = ry; }
            else if (rank[rx] > rank[ry]) { parent[ry] = rx; }
            else                          { parent[ry] = rx; rank[rx]++; }
        }
    }
}