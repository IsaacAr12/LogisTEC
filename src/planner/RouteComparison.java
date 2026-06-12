package planner;

/**
 * Compara las rutas generadas por Nearest Neighbor y MST-based.
 */
public class RouteComparison {

    private final RouteResult nearestNeighborRoute;
    private final RouteResult mstBasedRoute;
    private final RouteResult bestRoute;
    private final double improvementPercentage;

    public RouteComparison(RouteResult nearestNeighborRoute, RouteResult mstBasedRoute) {
        this.nearestNeighborRoute = nearestNeighborRoute;
        this.mstBasedRoute = mstBasedRoute;

        if (mstBasedRoute.getTotalDistance() <= nearestNeighborRoute.getTotalDistance()) {
            this.bestRoute = mstBasedRoute;
        } else {
            this.bestRoute = nearestNeighborRoute;
        }

        if (nearestNeighborRoute.getTotalDistance() == 0) {
            this.improvementPercentage = 0.0;
        } else {
            this.improvementPercentage =
                    ((nearestNeighborRoute.getTotalDistance() - mstBasedRoute.getTotalDistance()) * 100.0)
                            / nearestNeighborRoute.getTotalDistance();
        }
    }

    public RouteResult getNearestNeighborRoute() {
        return nearestNeighborRoute;
    }

    public RouteResult getMstBasedRoute() {
        return mstBasedRoute;
    }

    public RouteResult getBestRoute() {
        return bestRoute;
    }

    public double getImprovementPercentage() {
        return improvementPercentage;
    }
}