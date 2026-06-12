import io.JsonLoader;
import io.LogisticsConfig;
import io.ConfigValidationException;

import graph.Graph;
import graph.Vertex;

import ds.DLinkedList;

import algorithms.Traversals;
import algorithms.Warshall;
import algorithms.Dijkstra;
import algorithms.FloydWarshall;
import algorithms.Prim;
import algorithms.Kruskal;

import model.ProblemCase;
import model.Package;
import model.Truck;

import planner.AssignmentResult;
import planner.PackageAssigner;
import planner.TruckAssignment;
import planner.NearestNeighborRouter;
import planner.MSTBasedRouter;
import planner.RouteResult;
import planner.RouteComparison;

import ui.LogisTecFrame;

import java.io.IOException;

/**
 * Punto de entrada y orquestación del sistema LogísTEC.
 *
 * Ejecuta:
 * - Carga del JSON.
 * - Validación de conectividad con Warshall.
 * - Camino mínimo con Dijkstra.
 * - Matriz de distancias con Floyd-Warshall.
 * - MST con Prim y Kruskal.
 * - Asignación de paquetes a camiones.
 * - Ruteo con Nearest Neighbor.
 * - Ruteo con MST-based.
 * - Comparación de heurísticas.
 * - Visualización gráfica básica con Swing.
 */
public class Main {

    public static void main(String[] args) {
        String path = (args.length > 0) ? args[0] : "data/caso_prueba.json";

        System.out.println("=== LogísTEC (LGTC) — caso: " + path + " ===\n");

        ProblemCase caso;

        try {
            LogisticsConfig config = new JsonLoader().load(path);
            caso = ProblemCase.from(config);
        } catch (IOException e) {
            System.err.println("No se pudo leer el archivo: " + e.getMessage());
            return;
        } catch (ConfigValidationException e) {
            System.err.println("Configuración inválida: " + e.getMessage());
            return;
        }

        Graph g = caso.getGrafo();
        Vertex depot = caso.getDepot();

        System.out.println(caso);
        System.out.println("Componentes conexas: " + Traversals.connectedComponents(g)
                + (Traversals.isConnected(g) ? " (grafo conexo)" : " (grafo NO conexo)"));
        System.out.println();

        // ---- 1. Validación: alcanzabilidad desde el depósito con Warshall ----
        boolean[][] reach = Warshall.compute(g);

        DLinkedList<String> destinos = new DLinkedList<>();

        for (Package p : caso.getPaquetes()) {
            destinos.addLast(p.getDestinoId());
        }

        DLinkedList<String> inalcanzables = Warshall.unreachableFromDepot(g, reach, destinos);

        int rechazados = 0;

        for (Package p : caso.getPaquetes()) {
            if (inalcanzables.contains(p.getDestinoId())) {
                p.rechazar("destino inalcanzable desde el depósito");
                rechazados++;
            }
        }

        System.out.println("--- Validación (Warshall) ---");
        System.out.println("Paquetes con destino inalcanzable: " + rechazados);

        for (Package p : caso.getPaquetes()) {
            if (p.isRechazado()) {
                System.out.println("   RECHAZADO " + p.getId() + " -> " + p.getDestinoId());
            }
        }

        System.out.println();

        // ---- 2. Camino más corto depósito -> primer destino entregable con Dijkstra ----
        Package objetivo = null;

        for (Package p : caso.getPaquetes()) {
            if (!p.isRechazado()) {
                objetivo = p;
                break;
            }
        }

        FloydWarshall fw = new FloydWarshall(g);

        if (objetivo != null) {
            int src = depot.getIndex();
            int dst = g.getVertex(objetivo.getDestinoId()).getIndex();

            Dijkstra dij = new Dijkstra(g, src);

            System.out.println("--- Dijkstra: " + depot.getId() + " -> " + objetivo.getDestinoId() + " ---");

            if (dij.hasPathTo(dst)) {
                System.out.println("Distancia mínima: " + dij.distTo(dst) + " m");
                System.out.print("Camino: ");

                int[] ruta = dij.pathTo(dst);

                for (int i = 0; i < ruta.length; i++) {
                    System.out.print(g.getVertexByIndex(ruta[i]).getId());

                    if (i < ruta.length - 1) {
                        System.out.print(" -> ");
                    }
                }

                System.out.println();
            }

            System.out.println();

            // ---- 3. Matriz de distancias con Floyd-Warshall ----
            System.out.println("--- Floyd-Warshall ---");
            System.out.println("Distancia " + depot.getId() + " -> " + objetivo.getDestinoId()
                    + " (matriz D): " + fw.dist(src, dst) + " m");
            System.out.println("Coincide con Dijkstra: " + (fw.dist(src, dst) == dij.distTo(dst)));
            System.out.println();
        }

        // ---- 4. MST con Prim y Kruskal ----
        System.out.println("--- MST: Prim vs Kruskal ---");

        Prim prim = new Prim(g);
        Kruskal kru = new Kruskal(g);

        System.out.println("Costo Prim:    " + prim.totalCost() + " m   (" + prim.elapsedNanos() / 1000 + " µs)");
        System.out.println("Costo Kruskal: " + kru.totalCost() + " m   (" + kru.elapsedNanos() / 1000 + " µs)");

        if (Traversals.isConnected(g)) {
            boolean igual = prim.totalCost() == kru.totalCost();
            System.out.println("¿Mismo costo total?  " + (igual ? "SÍ ✔" : "NO ✘ (revisar)"));
        } else {
            System.out.println("(grafo no conexo: Prim cubre la componente del vértice 0; "
                    + "Kruskal cubre el bosque de expansión, por lo que los costos pueden diferir)");
        }

        System.out.println();

        // ---- 5. Resumen de la flota ----
        System.out.println("--- Flota ---");

        for (Truck t : caso.getCamiones()) {
            System.out.println("   " + t);
        }

        System.out.println();

        // ---- 6. Asignación de paquetes a camiones con best-fit ----
        Package[] paquetes = caso.getPaquetes().toArray(new Package[caso.getPaquetes().size()]);
        Truck[] camiones = caso.getCamiones().toArray(new Truck[caso.getCamiones().size()]);

        PackageAssigner assigner = new PackageAssigner();
        AssignmentResult assignmentResult = assigner.assign(paquetes, camiones);

        assignmentResult.printSummary();

        // ---- 7. Ruteo con Nearest Neighbor y MST-based ----
        System.out.println("\n--- Rutas por camión: Nearest Neighbor vs MST-based ---");

        NearestNeighborRouter nnRouter = new NearestNeighborRouter();
        MSTBasedRouter mstRouter = new MSTBasedRouter();

        TruckAssignment[] assignments = assignmentResult.getAssignments();

        RouteResult[] bestRoutes = new RouteResult[assignments.length];
        String[] truckIdsForRoutes = new String[assignments.length];

        for (int i = 0; i < assignments.length; i++) {
            TruckAssignment assignment = assignments[i];

            System.out.println("\nCamión " + assignment.getTruck().getId());

            if (assignment.getPackageCount() == 0) {
                System.out.println("   Sin paquetes asignados.");
                continue;
            }

            String[] destinationIds = assignment.getDestinationIds();

            RouteResult nnRoute = nnRouter.buildRoute(g, fw, depot.getId(), destinationIds);
            RouteResult mstRoute = mstRouter.buildRoute(g, fw, depot.getId(), destinationIds);

            RouteComparison comparison = new RouteComparison(nnRoute, mstRoute);

            bestRoutes[i] = comparison.getBestRoute();
            truckIdsForRoutes[i] = assignment.getTruck().getId();

            System.out.println("   Ruta Nearest Neighbor:");
            printRoute(g, nnRoute);

            System.out.println("   Ruta MST-based:");
            printRoute(g, mstRoute);

            System.out.println("   Mejor ruta elegida: " + comparison.getBestRoute().getHeuristicName());
            System.out.println("   Distancia mejor ruta: " + comparison.getBestRoute().getTotalDistance() + " m");

            System.out.println("   Carga total: " + assignment.getUsedCapacity()
                    + "/" + assignment.getTruck().getCapacidad() + " kg");

            System.out.println("   Ocupación: " + String.format("%.2f", assignment.getOccupationPercentage()) + "%");

            System.out.println("   Ahorro MST sobre NN: "
                    + String.format("%.2f", comparison.getImprovementPercentage()) + "%");
        }

        // ---- 8. Interfaz gráfica básica ----
        LogisTecFrame.showWindow(g, bestRoutes, truckIdsForRoutes);
    }

    /**
     * Imprime una ruta en consola usando los IDs reales de los vértices.
     *
     * @param g grafo del caso.
     * @param route ruta calculada.
     */
    private static void printRoute(Graph g, RouteResult route) {
        System.out.print("      ");

        int[] routeVertices = route.getRoute();

        for (int i = 0; i < routeVertices.length; i++) {
            System.out.print(g.getVertexByIndex(routeVertices[i]).getId());

            if (i < routeVertices.length - 1) {
                System.out.print(" -> ");
            }
        }

        System.out.println();
        System.out.println("      Distancia: " + route.getTotalDistance() + " m");
    }
}