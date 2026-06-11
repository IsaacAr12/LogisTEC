package graph;

import ds.DLinkedList;
import io.LogisticsConfig;

/**
 * Grafo no dirigido y ponderado que modela la ciudad LogísTEC.
 *
 * <h2>Representación interna</h2>
 * <p>Se usa una <b>lista de adyacencia</b> implementada con {@link DLinkedList}:
 * para cada vértice se mantiene una lista de las aristas ({@link Edge}) incidentes.
 * Adicionalmente se guarda una lista maestra de todos los vértices y otra de
 * todas las aristas (útil para Kruskal y para la visualización).</p>
 *
 * <h2>Complejidades</h2>
 * <ul>
 *   <li>{@code addVertex} — O(1)</li>
 *   <li>{@code addEdge}   — O(1)</li>
 *   <li>{@code getVertex} — O(n)</li>
 *   <li>{@code getNeighbors} — O(1)</li>
 *   <li>{@code toAdjacencyMatrix} — O(n²)</li>
 * </ul>
 *
 * <h2>Restricciones</h2>
 * <p>No se permite {@code java.util.*}; todas las colecciones son {@link DLinkedList}
 * del equipo. Los vértices son referenciados por su {@code id} (String) en la API
 * pública para facilitar el uso desde otros paquetes.</p>
 *
 * @author Persona 2
 * @version 1.0
 */
public class Graph {

    /** Valor que representa la ausencia de arista en la matriz de adyacencia. */
    public static final int INF = Integer.MAX_VALUE / 2;

    /** Lista maestra de todos los vértices del grafo. */
    private final DLinkedList<Vertex> vertices;

    /** Lista maestra de todas las aristas (sin duplicar; cada arista aparece una vez). */
    private final DLinkedList<Edge> edges;

    /**
     * Lista de adyacencia: para cada vértice (accedido por su índice),
     * se almacena la lista de aristas incidentes.
     * Se implementa como arreglo de DLinkedList (arreglo nativo, permitido).
     */
    private DLinkedList<Edge>[] adjList;

    /** Vértice que actúa como depósito (tipo DEPOT). */
    private Vertex depot;

    /** Capacidad actual del arreglo de listas de adyacencia. */
    private int capacity;

    /**
     * Crea un grafo vacío con la capacidad inicial indicada.
     *
     * @param initialCapacity número estimado de vértices (se redimensiona si hace falta).
     */
    @SuppressWarnings("unchecked")
    public Graph(int initialCapacity) {
        this.capacity = Math.max(initialCapacity, 4);
        this.vertices = new DLinkedList<>();
        this.edges    = new DLinkedList<>();
        this.adjList  = new DLinkedList[this.capacity];
    }

    /** Crea un grafo vacío con capacidad inicial de 16. */
    public Graph() { this(16); }

    // ================================================================
    // Construcción desde LogisticsConfig
    // ================================================================

    /**
     * Fábrica estática: construye el grafo a partir de la configuración
     * cargada por {@link io.JsonLoader}.
     *
     * @param config configuración parseada del JSON.
     * @return el grafo construido.
     * @throws IllegalArgumentException si algún id de arista no existe en los vértices.
     */
    public static Graph from(LogisticsConfig config) {
        Graph g = new Graph(config.vertices().size());

        // 1. Agregar vértices
        for (LogisticsConfig.VertexInfo vi : config.vertices()) {
            VertexType type = VertexType.fromString(vi.tipo());
            g.addVertex(new Vertex(vi.id(), type, vi.x(), vi.y(), 0));
        }

        // 2. Agregar aristas
        for (LogisticsConfig.EdgeInfo ei : config.edges()) {
            Vertex u = g.getVertex(ei.u());
            Vertex v = g.getVertex(ei.v());
            if (u == null) throw new IllegalArgumentException("Vértice no encontrado: " + ei.u());
            if (v == null) throw new IllegalArgumentException("Vértice no encontrado: " + ei.v());
            g.addEdge(new Edge(u, v, ei.distancia()));
        }

        return g;
    }

    // ================================================================
    // Mutadores
    // ================================================================

    /**
     * Agrega un vértice al grafo. Si ya existe un vértice con el mismo id,
     * no lo agrega y retorna {@code false}.
     *
     * @param vertex vértice a agregar.
     * @return {@code true} si fue agregado; {@code false} si ya existía.
     */
    public boolean addVertex(Vertex vertex) {
        if (vertex == null) return false;
        if (getVertex(vertex.getId()) != null) return false;   // duplicado

        int index = vertices.size();
        vertex.setIndex(index);
        vertices.addLast(vertex);

        // Redimensionar adjList si es necesario
        if (index >= capacity) {
            resize();
        }
        adjList[index] = new DLinkedList<>();

        if (vertex.isDepot()) depot = vertex;
        return true;
    }

    /**
     * Agrega una arista no dirigida al grafo.
     * La registra en ambas listas de adyacencia (u y v).
     *
     * @param edge arista a agregar; no debe ser {@code null}.
     */
    public void addEdge(Edge edge) {
        if (edge == null) throw new IllegalArgumentException("La arista no puede ser nula");
        adjList[edge.getU().getIndex()].addLast(edge);
        adjList[edge.getV().getIndex()].addLast(edge);
        edges.addLast(edge);
    }

    // ================================================================
    // Consultas
    // ================================================================

    /**
     * Busca un vértice por su id. O(n).
     *
     * @param id identificador del vértice.
     * @return el vértice, o {@code null} si no existe.
     */
    public Vertex getVertex(String id) {
        for (Vertex v : vertices) {
            if (v.getId().equals(id)) return v;
        }
        return null;
    }

    /**
     * Retorna la lista de aristas incidentes al vértice dado. O(1).
     *
     * @param vertex vértice del que se quieren los vecinos.
     * @return lista de aristas (puede estar vacía).
     */
    public DLinkedList<Edge> getNeighbors(Vertex vertex) {
        return adjList[vertex.getIndex()];
    }

    /**
     * Retorna la lista de aristas incidentes al vértice con el id dado. O(n).
     *
     * @param id identificador del vértice.
     * @return lista de aristas, o {@code null} si el vértice no existe.
     */
    public DLinkedList<Edge> getNeighbors(String id) {
        Vertex v = getVertex(id);
        return (v == null) ? null : getNeighbors(v);
    }

    /** @return lista de todos los vértices del grafo. */
    public DLinkedList<Vertex> getVertices() { return vertices; }

    /** @return lista de todas las aristas del grafo (sin duplicar). */
    public DLinkedList<Edge> getEdges() { return edges; }

    /** @return cantidad de vértices. */
    public int vertexCount() { return vertices.size(); }

    /** @return cantidad de aristas. */
    public int edgeCount() { return edges.size(); }

    /**
     * @return el vértice depósito, o {@code null} si no se ha agregado ninguno
     *         con tipo {@link VertexType#DEPOT}.
     */
    public Vertex getDepot() { return depot; }

    /**
     * Indica si el grafo tiene algún vértice.
     * @return {@code true} si está vacío.
     */
    public boolean isEmpty() { return vertices.isEmpty(); }

    // ================================================================
    // Matriz de adyacencia (para Floyd-Warshall y Warshall)
    // ================================================================

    /**
     * Construye y retorna la matriz de adyacencia de pesos.
     *
     * <p>La celda {@code [i][j]} contiene la distancia de la arista directa
     * entre los vértices con índices {@code i} y {@code j}, o {@link #INF}
     * si no existe arista. La diagonal es {@code 0}.</p>
     *
     * <p>Complejidad: O(n + m) donde n = |V| y m = |E|.</p>
     *
     * @return matriz {@code int[n][n]}.
     */
    public int[][] toAdjacencyMatrix() {
        int n = vertices.size();
        int[][] matrix = new int[n][n];

        // Inicializar: diagonal 0, resto INF
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = (i == j) ? 0 : INF;
            }
        }

        // Llenar con las aristas
        for (Edge e : edges) {
            int i = e.getU().getIndex();
            int j = e.getV().getIndex();
            matrix[i][j] = e.getDistance();
            matrix[j][i] = e.getDistance();   // no dirigido
        }

        return matrix;
    }

    /**
     * Construye y retorna la matriz booleana de adyacencia.
     *
     * <p>La celda {@code [i][j]} es {@code true} si existe arista directa entre
     * los vértices con índices {@code i} y {@code j}. Usada como punto de partida
     * por el algoritmo de Warshall.</p>
     *
     * @return matriz {@code boolean[n][n]}.
     */
    public boolean[][] toBooleanMatrix() {
        int n = vertices.size();
        boolean[][] matrix = new boolean[n][n];
        for (int i = 0; i < n; i++) matrix[i][i] = true;
        for (Edge e : edges) {
            int i = e.getU().getIndex();
            int j = e.getV().getIndex();
            matrix[i][j] = true;
            matrix[j][i] = true;
        }
        return matrix;
    }

    /**
     * Convierte un arreglo de índices en un arreglo de vértices.
     * Útil para reconstruir caminos desde Dijkstra/Floyd-Warshall.
     *
     * @param indices arreglo de índices de vértices.
     * @return arreglo de {@link Vertex} en el mismo orden.
     */
    public Vertex[] indicesToVertices(int[] indices) {
        Vertex[] result = new Vertex[indices.length];
        for (int i = 0; i < indices.length; i++) {
            result[i] = getVertexByIndex(indices[i]);
        }
        return result;
    }

    /**
     * Retorna el vértice con el índice dado. O(n).
     *
     * @param index índice del vértice.
     * @return el vértice, o {@code null} si no existe.
     */
    public Vertex getVertexByIndex(int index) {
        for (Vertex v : vertices) {
            if (v.getIndex() == index) return v;
        }
        return null;
    }

    // ================================================================
    // toString
    // ================================================================

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph{vertices=").append(vertices.size())
          .append(", edges=").append(edges.size())
          .append(", depot=").append(depot != null ? depot.getId() : "none")
          .append("}\n");

        for (Vertex v : vertices) {
            sb.append("  ").append(v.getId()).append(": ");
            DLinkedList<Edge> neighbors = getNeighbors(v);
            boolean first = true;
            for (Edge e : neighbors) {
                if (!first) sb.append(", ");
                sb.append(e.getOther(v).getId())
                  .append("(").append(e.getDistance()).append("m)");
                first = false;
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // ================================================================
    // Privados
    // ================================================================

    /** Duplica la capacidad del arreglo de listas de adyacencia. */
    @SuppressWarnings("unchecked")
    private void resize() {
        capacity *= 2;
        DLinkedList<Edge>[] newAdj = new DLinkedList[capacity];
        for (int i = 0; i < adjList.length; i++) {
            newAdj[i] = adjList[i];
        }
        adjList = newAdj;
    }
}
