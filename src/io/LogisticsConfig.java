package io;

import ds.DLinkedList;

/**
 * Contenedor inmutable con el caso ya parseado y validado a partir del JSON.
 *
 * <p>Es la <b>frontera entre el módulo de E/S (Persona 1) y el resto del equipo</b>: el
 * {@link JsonLoader} produce un {@code LogisticsConfig} y Persona 2 (grafo) y Persona 4
 * (modelo/planner) lo consumen para construir sus objetos de dominio
 * ({@code Graph}, {@code Package}, {@code Truck}, {@code ProblemCase}).</p>
 *
 * <p>Deliberadamente <b>no</b> depende de las clases de dominio de otros paquetes para que
 * el módulo de E/S compile y se pruebe de forma independiente. Por eso el tipo de vértice
 * se expone como {@code String} (p. ej. {@code "DEPOT"}); Persona 2 lo mapea a su enum
 * {@code VertexType}.</p>
 *
 * <p>Todas las colecciones se exponen con la {@link DLinkedList} del equipo, no con
 * colecciones de {@code java.util}.</p>
 */
public final class LogisticsConfig {

    /** Datos de un vértice del grafo tal como vienen del JSON. */
    public record VertexInfo(String id, String tipo, int x, int y) { }

    /** Datos de una arista no dirigida: extremos y distancia en metros. */
    public record EdgeInfo(String u, String v, int distancia) { }

    /** Datos de un paquete: destino, peso (kg) y prioridad ∈ {1,2,3}. */
    public record PackageInfo(String id, String destino, int peso, int prioridad) { }

    /** Datos de un camión: identificador y capacidad máxima (kg). */
    public record TruckInfo(String id, int capacidad) { }

    private final String depotId;
    private final DLinkedList<VertexInfo> vertices;
    private final DLinkedList<EdgeInfo> edges;
    private final DLinkedList<PackageInfo> packages;
    private final DLinkedList<TruckInfo> trucks;

    LogisticsConfig(String depotId,
                    DLinkedList<VertexInfo> vertices,
                    DLinkedList<EdgeInfo> edges,
                    DLinkedList<PackageInfo> packages,
                    DLinkedList<TruckInfo> trucks) {
        this.depotId = depotId;
        this.vertices = vertices;
        this.edges = edges;
        this.packages = packages;
        this.trucks = trucks;
    }

    /** @return el identificador del vértice marcado como depósito (tipo {@code DEPOT}). */
    public String depotId() { return depotId; }

    /** @return lista de vértices del grafo. */
    public DLinkedList<VertexInfo> vertices() { return vertices; }

    /** @return lista de aristas no dirigidas con su distancia. */
    public DLinkedList<EdgeInfo> edges() { return edges; }

    /** @return lista de paquetes a repartir. */
    public DLinkedList<PackageInfo> packages() { return packages; }

    /** @return lista de camiones de la flota. */
    public DLinkedList<TruckInfo> trucks() { return trucks; }

    @Override
    public String toString() {
        return "LogisticsConfig{depot=" + depotId
                + ", vertices=" + vertices.size()
                + ", aristas=" + edges.size()
                + ", paquetes=" + packages.size()
                + ", camiones=" + trucks.size() + "}";
    }
}
