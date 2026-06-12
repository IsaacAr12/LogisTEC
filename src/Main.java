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

import java.io.IOException;

/**
 * Punto de entrada y orquestación del sistema LogísTEC.
 *
 * <p>Demuestra el pipeline algorítmico de extremo a extremo sobre el caso indicado:
 * carga del JSON, construcción del dominio, validación de alcanzabilidad (Warshall),
 * camino más corto (Dijkstra), matriz de distancias (Floyd-Warshall) y MST con
 * Prim y Kruskal (con comparación de costo y tiempos).</p>
 *
 * <p>Uso: {@code java Main [ruta-al-json]} (por defecto {@code data/caso_prueba.json}).</p>
 *
 * <p>Nota: la asignación de paquetes a camiones y el ruteo (Nearest Neighbor /
 * MST-based) corresponden al paquete {@code planner} (Persona 4) y se integran aquí
 * una vez disponibles.</p>
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

        // ---- 1. Validación: alcanzabilidad desde el depósito (Warshall) ----
        boolean[][] reach = Warshall.compute(g);
        DLinkedList<String> destinos = new DLinkedList<>();
        for (Package p : caso.getPaquetes()) destinos.addLast(p.getDestinoId());
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
            if (p.isRechazado()) System.out.println("   RECHAZADO " + p.getId() + " -> " + p.getDestinoId());
        }
        System.out.println();

        // ---- 2. Camino más corto depósito -> primer destino entregable (Dijkstra) ----
        Package objetivo = null;
        for (Package p : caso.getPaquetes()) {
            if (!p.isRechazado()) { objetivo = p; break; }
        }
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
                    if (i < ruta.length - 1) System.out.print(" -> ");
                }
                System.out.println();
            }
            System.out.println();

            // ---- 3. Matriz de distancias (Floyd-Warshall) ----
            FloydWarshall fw = new FloydWarshall(g);
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
        System.out.println("Costo Kruskal: " + kru.totalCost()  + " m   (" + kru.elapsedNanos()  / 1000 + " µs)");
        if (Traversals.isConnected(g)) {
            boolean igual = prim.totalCost() == kru.totalCost();
            System.out.println("¿Mismo costo total?  " + (igual ? "SÍ ✔" : "NO ✘ (revisar)"));
        } else {
            System.out.println("(grafo no conexo: Prim cubre la componente del vértice 0; "
                    + "Kruskal cubre el bosque de expansión, por lo que los costos pueden diferir)");
        }
        System.out.println();

        // ---- Resumen de la flota ----
        System.out.println("--- Flota ---");
        for (Truck t : caso.getCamiones()) System.out.println("   " + t);
        System.out.println("\n(Asignación best-fit y ruteo NN/MST-based: pendientes en el paquete planner.)");
    }
}
