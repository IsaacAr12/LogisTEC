package model;

import ds.DLinkedList;
import graph.Graph;
import graph.Vertex;
import io.LogisticsConfig;

/**
 * Caso completo del problema LogísTEC: el grafo de la ciudad junto con los paquetes
 * a repartir y la flota de camiones.
 *
 * <p>Es el objeto de dominio que consume el resto del sistema (validación, planificador,
 * reporte, UI). Se construye a partir del {@link LogisticsConfig} que produce el
 * {@code io.JsonLoader}, centralizando aquí el mapeo "configuración → dominio".</p>
 *
 * @author Persona 4
 */
public class ProblemCase {

    private final Graph grafo;
    private final DLinkedList<Package> paquetes;
    private final DLinkedList<Truck> camiones;
    private final Vertex depot;

    private ProblemCase(Graph grafo, DLinkedList<Package> paquetes,
                        DLinkedList<Truck> camiones, Vertex depot) {
        this.grafo = grafo;
        this.paquetes = paquetes;
        this.camiones = camiones;
        this.depot = depot;
    }

    /**
     * Construye el caso de dominio a partir de la configuración cargada del JSON.
     *
     * <p>El grafo se arma con {@link Graph#from(LogisticsConfig)} (Persona 2); los
     * paquetes y camiones se mapean desde los DTO de {@link LogisticsConfig}.</p>
     *
     * @param config configuración parseada y validada por el {@code JsonLoader}.
     * @return el caso del problema listo para procesar.
     */
    public static ProblemCase from(LogisticsConfig config) {
        Graph g = Graph.from(config);

        DLinkedList<Package> pkgs = new DLinkedList<>();
        for (LogisticsConfig.PackageInfo pi : config.packages()) {
            pkgs.addLast(new Package(pi.id(), pi.destino(), pi.peso(), pi.prioridad()));
        }

        DLinkedList<Truck> trucks = new DLinkedList<>();
        for (LogisticsConfig.TruckInfo ti : config.trucks()) {
            trucks.addLast(new Truck(ti.id(), ti.capacidad()));
        }

        return new ProblemCase(g, pkgs, trucks, g.getDepot());
    }

    /** @return grafo de la ciudad. */
    public Graph getGrafo() { return grafo; }

    /** @return paquetes a repartir. */
    public DLinkedList<Package> getPaquetes() { return paquetes; }

    /** @return flota de camiones. */
    public DLinkedList<Truck> getCamiones() { return camiones; }

    /** @return vértice depósito (inicio/fin de todas las rutas). */
    public Vertex getDepot() { return depot; }

    @Override
    public String toString() {
        return "ProblemCase{vertices=" + grafo.vertexCount()
                + ", aristas=" + grafo.edgeCount()
                + ", paquetes=" + paquetes.size()
                + ", camiones=" + camiones.size()
                + ", depot=" + (depot != null ? depot.getId() : "none") + "}";
    }
}
