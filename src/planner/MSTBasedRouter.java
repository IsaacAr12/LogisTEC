package planner;

import algorithms.FloydWarshall;
import graph.Graph;
import graph.Vertex;

/**
 * Calcula una ruta usando la heurística basada en MST.
 *
 * Pasos:
 * 1. Toma el depósito y las paradas del camión.
 * 2. Crea un grafo completo "implícito" usando las distancias mínimas de Floyd-Warshall.
 * 3. Construye un MST sobre esos puntos.
 * 4. Hace DFS preorden sobre el MST.
 * 5. Devuelve la ruta: depósito -> paradas en preorden -> depósito.
 */
public class MSTBasedRouter {

    private static final int INF = Integer.MAX_VALUE / 2;

    public RouteResult buildRoute(Graph graph, FloydWarshall fw, String depotId, String[] destinationIds) {
        Vertex depot = graph.getVertex(depotId);

        if (depot == null) {
            throw new IllegalArgumentException("No existe el depósito: " + depotId);
        }

        int depotIndex = depot.getIndex();

        int stopCount = countUniqueDestinations(destinationIds);
        int[] stops = buildUniqueStops(graph, destinationIds, stopCount);

        if (stopCount == 0) {
            return new RouteResult("MST-based", new int[]{depotIndex, depotIndex}, 0);
        }

        /*
         * points[0] = depósito
         * points[1..n] = paradas
         */
        int[] points = new int[stopCount + 1];
        points[0] = depotIndex;

        for (int i = 0; i < stopCount; i++) {
            points[i + 1] = stops[i];
        }

        int pointCount = points.length;

        int[][] mstAdj = buildMSTAdjacency(fw, points, pointCount);

        boolean[] visited = new boolean[pointCount];
        int[] preorder = new int[pointCount];
        int[] preorderCount = new int[]{0};

        dfsPreorder(0, mstAdj, visited, preorder, preorderCount);

        int[] route = new int[pointCount + 1];

        for (int i = 0; i < pointCount; i++) {
            route[i] = points[preorder[i]];
        }

        route[route.length - 1] = depotIndex;

        int totalDistance = calculateRouteDistance(fw, route);

        return new RouteResult("MST-based", route, totalDistance);
    }

    private int[][] buildMSTAdjacency(FloydWarshall fw, int[] points, int pointCount) {
        boolean[] inMST = new boolean[pointCount];
        int[] key = new int[pointCount];
        int[] parent = new int[pointCount];

        for (int i = 0; i < pointCount; i++) {
            inMST[i] = false;
            key[i] = INF;
            parent[i] = -1;
        }

        key[0] = 0;

        for (int step = 0; step < pointCount; step++) {
            int u = minKeyVertex(key, inMST, pointCount);

            if (u == -1) {
                throw new IllegalStateException("No se pudo construir el MST-based: puntos desconectados.");
            }

            inMST[u] = true;

            for (int v = 0; v < pointCount; v++) {
                if (!inMST[v] && u != v) {
                    int distance = fw.dist(points[u], points[v]);

                    if (distance < key[v]) {
                        key[v] = distance;
                        parent[v] = u;
                    }
                }
            }
        }

        int[][] mstAdj = new int[pointCount][pointCount];

        for (int i = 0; i < pointCount; i++) {
            for (int j = 0; j < pointCount; j++) {
                mstAdj[i][j] = 0;
            }
        }

        for (int v = 1; v < pointCount; v++) {
            int u = parent[v];

            if (u != -1) {
                mstAdj[u][v] = 1;
                mstAdj[v][u] = 1;
            }
        }

        return mstAdj;
    }

    private int minKeyVertex(int[] key, boolean[] inMST, int pointCount) {
        int bestIndex = -1;
        int bestValue = INF;

        for (int i = 0; i < pointCount; i++) {
            if (!inMST[i] && key[i] < bestValue) {
                bestValue = key[i];
                bestIndex = i;
            }
        }

        return bestIndex;
    }

    private void dfsPreorder(int current, int[][] mstAdj, boolean[] visited, int[] preorder, int[] preorderCount) {
        visited[current] = true;

        preorder[preorderCount[0]] = current;
        preorderCount[0]++;

        for (int next = 0; next < mstAdj.length; next++) {
            if (mstAdj[current][next] == 1 && !visited[next]) {
                dfsPreorder(next, mstAdj, visited, preorder, preorderCount);
            }
        }
    }

    private int calculateRouteDistance(FloydWarshall fw, int[] route) {
        int total = 0;

        for (int i = 0; i < route.length - 1; i++) {
            total += fw.dist(route[i], route[i + 1]);
        }

        return total;
    }

    private int countUniqueDestinations(String[] destinationIds) {
        int count = 0;

        for (int i = 0; i < destinationIds.length; i++) {
            if (!appearsBefore(destinationIds, i)) {
                count++;
            }
        }

        return count;
    }

    private int[] buildUniqueStops(Graph graph, String[] destinationIds, int stopCount) {
        int[] stops = new int[stopCount];
        int index = 0;

        for (int i = 0; i < destinationIds.length; i++) {
            if (!appearsBefore(destinationIds, i)) {
                Vertex v = graph.getVertex(destinationIds[i]);

                if (v == null) {
                    throw new IllegalArgumentException("Destino no existe en el grafo: " + destinationIds[i]);
                }

                stops[index] = v.getIndex();
                index++;
            }
        }

        return stops;
    }

    private boolean appearsBefore(String[] values, int position) {
        for (int i = 0; i < position; i++) {
            if (values[i].equals(values[position])) {
                return true;
            }
        }

        return false;
    }
}