package planner;

/**
 * Guarda el resultado de una ruta calculada para un camión.
 */
public class RouteResult {

    private final String heuristicName;
    private final int[] route;
    private final int totalDistance;

    public RouteResult(String heuristicName, int[] route, int totalDistance) {
        this.heuristicName = heuristicName;
        this.route = route;
        this.totalDistance = totalDistance;
    }

    public String getHeuristicName() {
        return heuristicName;
    }

    public int[] getRoute() {
        int[] copy = new int[route.length];

        for (int i = 0; i < route.length; i++) {
            copy[i] = route[i];
        }

        return copy;
    }

    public int getTotalDistance() {
        return totalDistance;
    }
}