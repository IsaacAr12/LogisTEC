package algorithms;

import ds.UnionFind;
import graph.Edge;
import graph.Graph;

/**
 * Algoritmo de Kruskal para construir el Árbol de Expansión Mínima.
 *
 * Usa:
 * - Graph.getEdges()
 * - Edge.getU()
 * - Edge.getV()
 * - Edge.getDistance()
 * - UnionFind propio del proyecto
 *
 * No usa java.util.Arrays ni colecciones de java.util.
 */
public class Kruskal {

    private final int[][] mstEdges;
    private final int totalCost;
    private final int n;
    private final long elapsedNanos;

    public Kruskal(Graph g) {
        n = g.vertexCount();

        if (n == 0) {
            throw new IllegalArgumentException("Grafo vacío");
        }

        int edgeCount = g.edgeCount();

        int[][] edges = new int[edgeCount][3];

        int idx = 0;

        for (Edge edge : g.getEdges()) {
            edges[idx][0] = edge.getU().getIndex();
            edges[idx][1] = edge.getV().getIndex();
            edges[idx][2] = edge.getDistance();
            idx++;
        }

        long start = System.nanoTime();

        mergeSort(edges, 0, edgeCount - 1);

        UnionFind uf = new UnionFind(n);

        int[][] tempMstEdges = new int[n - 1][3];
        int mstSize = 0;
        int cost = 0;

        for (int i = 0; i < edgeCount && mstSize < n - 1; i++) {
            int u = edges[i][0];
            int v = edges[i][1];
            int w = edges[i][2];

            if (uf.union(u, v)) {
                tempMstEdges[mstSize][0] = u;
                tempMstEdges[mstSize][1] = v;
                tempMstEdges[mstSize][2] = w;

                mstSize++;
                cost += w;
            }
        }

        elapsedNanos = System.nanoTime() - start;
        totalCost = cost;

        mstEdges = new int[mstSize][3];

        for (int i = 0; i < mstSize; i++) {
            mstEdges[i][0] = tempMstEdges[i][0];
            mstEdges[i][1] = tempMstEdges[i][1];
            mstEdges[i][2] = tempMstEdges[i][2];
        }
    }

    public int totalCost() {
        return totalCost;
    }

    public long elapsedNanos() {
        return elapsedNanos;
    }

    public int[][] edges() {
        int[][] copy = new int[mstEdges.length][3];

        for (int i = 0; i < mstEdges.length; i++) {
            copy[i][0] = mstEdges[i][0];
            copy[i][1] = mstEdges[i][1];
            copy[i][2] = mstEdges[i][2];
        }

        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("MST (Kruskal) — Costo total: ")
          .append(totalCost)
          .append("\n");

        for (int i = 0; i < mstEdges.length; i++) {
            sb.append("  ")
              .append(mstEdges[i][0])
              .append(" - ")
              .append(mstEdges[i][1])
              .append("  peso=")
              .append(mstEdges[i][2])
              .append("\n");
        }

        return sb.toString();
    }

    private static void mergeSort(int[][] edges, int lo, int hi) {
        if (lo >= hi) {
            return;
        }

        int mid = lo + (hi - lo) / 2;

        mergeSort(edges, lo, mid);
        mergeSort(edges, mid + 1, hi);
        merge(edges, lo, mid, hi);
    }

    private static void merge(int[][] edges, int lo, int mid, int hi) {
        int leftLen = mid - lo + 1;
        int rightLen = hi - mid;

        int[][] left = new int[leftLen][3];
        int[][] right = new int[rightLen][3];

        for (int i = 0; i < leftLen; i++) {
            left[i][0] = edges[lo + i][0];
            left[i][1] = edges[lo + i][1];
            left[i][2] = edges[lo + i][2];
        }

        for (int j = 0; j < rightLen; j++) {
            right[j][0] = edges[mid + 1 + j][0];
            right[j][1] = edges[mid + 1 + j][1];
            right[j][2] = edges[mid + 1 + j][2];
        }

        int i = 0;
        int j = 0;
        int k = lo;

        while (i < leftLen && j < rightLen) {
            if (left[i][2] <= right[j][2]) {
                edges[k][0] = left[i][0];
                edges[k][1] = left[i][1];
                edges[k][2] = left[i][2];
                i++;
            } else {
                edges[k][0] = right[j][0];
                edges[k][1] = right[j][1];
                edges[k][2] = right[j][2];
                j++;
            }

            k++;
        }

        while (i < leftLen) {
            edges[k][0] = left[i][0];
            edges[k][1] = left[i][1];
            edges[k][2] = left[i][2];
            i++;
            k++;
        }

        while (j < rightLen) {
            edges[k][0] = right[j][0];
            edges[k][1] = right[j][1];
            edges[k][2] = right[j][2];
            j++;
            k++;
        }
    }
}