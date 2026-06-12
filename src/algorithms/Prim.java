package algorithms;

import ds.MinHeap;
import graph.Edge;
import graph.Graph;
import graph.Vertex;

/**
 * Algoritmo de Prim para construir el Árbol de Expansión Mínima.
 *
 * Usa el grafo real del proyecto:
 * - Graph
 * - Vertex
 * - Edge
 * - getNeighbors(Vertex)
 *
 * No usa java.util.PriorityQueue.
 */
public class Prim {

    private static final int INF = Integer.MAX_VALUE / 2;

    private final int[] parent;
    private final int[] edgeWeight;
    private final int totalCost;
    private final int n;
    private final long elapsedNanos;

    public Prim(Graph g) {
        n = g.vertexCount();

        if (n == 0) {
            throw new IllegalArgumentException("Grafo vacío");
        }

        parent = new int[n];
        edgeWeight = new int[n];

        int[] key = new int[n];
        boolean[] inMST = new boolean[n];

        Vertex[] vertices = new Vertex[n];
        int index = 0;

        for (Vertex v : g.getVertices()) {
            vertices[index] = v;
            index++;
        }

        for (int i = 0; i < n; i++) {
            key[i] = INF;
            parent[i] = -1;
            edgeWeight[i] = 0;
            inMST[i] = false;
        }

        key[0] = 0;

        MinHeap pq = new MinHeap(n);

        for (int i = 0; i < n; i++) {
            pq.insertOrDecrease(i, key[i]);
        }

        long start = System.nanoTime();

        while (!pq.isEmpty()) {
            int u = pq.extractMin();
            inMST[u] = true;

            Vertex current = vertices[u];

            for (Edge edge : g.getNeighbors(current)) {
                Vertex other = edge.getOther(current);

                int v = other.getIndex();
                int w = edge.getDistance();

                if (!inMST[v] && w < key[v]) {
                    key[v] = w;
                    parent[v] = u;
                    edgeWeight[v] = w;
                    pq.insertOrDecrease(v, w);
                }
            }
        }

        elapsedNanos = System.nanoTime() - start;

        int cost = 0;

        for (int v = 1; v < n; v++) {
            if (parent[v] != -1) {
                cost += edgeWeight[v];
            }
        }

        totalCost = cost;
    }

    public int totalCost() {
        return totalCost;
    }

    public long elapsedNanos() {
        return elapsedNanos;
    }

    public int[] parent() {
        return parent.clone();
    }

    public int[] edgeWeights() {
        return edgeWeight.clone();
    }

    public int[][] edges() {
        int count = 0;

        for (int v = 1; v < n; v++) {
            if (parent[v] != -1) {
                count++;
            }
        }

        int[][] result = new int[count][3];
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("MST (Prim) — Costo total: ")
          .append(totalCost)
          .append("\n");

        for (int v = 1; v < n; v++) {
            if (parent[v] != -1) {
                sb.append("  ")
                  .append(parent[v])
                  .append(" - ")
                  .append(v)
                  .append("  peso=")
                  .append(edgeWeight[v])
                  .append("\n");
            }
        }

        return sb.toString();
    }
}