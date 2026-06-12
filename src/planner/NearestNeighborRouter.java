package planner;

import algorithms.FloydWarshall;
import graph.Graph;
import graph.Vertex;

/**
 * Calcula una ruta usando la heurística de Vecino Más Cercano.
 *
 * La ruta empieza en el depósito, visita las paradas asignadas
 * al camión y vuelve al depósito.
 */
public class NearestNeighborRouter {

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
            return new RouteResult("Nearest Neighbor", new int[]{depotIndex, depotIndex}, 0);
        }

        boolean[] visited = new boolean[stopCount];

        int[] route = new int[stopCount + 2];
        route[0] = depotIndex;

        int current = depotIndex;
        int totalDistance = 0;

        for (int step = 1; step <= stopCount; step++) {
            int nearestPosition = findNearestStop(fw, current, stops, visited);

            if (nearestPosition == -1) {
                throw new IllegalStateException("No se pudo encontrar una parada alcanzable desde el vértice actual.");
            }

            visited[nearestPosition] = true;

            int next = stops[nearestPosition];

            totalDistance += fw.dist(current, next);
            route[step] = next;
            current = next;
        }

        totalDistance += fw.dist(current, depotIndex);
        route[route.length - 1] = depotIndex;

        return new RouteResult("Nearest Neighbor", route, totalDistance);
    }

    private int findNearestStop(FloydWarshall fw, int current, int[] stops, boolean[] visited) {
        int bestPosition = -1;
        int bestDistance = INF;

        for (int i = 0; i < stops.length; i++) {
            if (!visited[i]) {
                int distance = fw.dist(current, stops[i]);

                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestPosition = i;
                }
            }
        }

        return bestPosition;
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