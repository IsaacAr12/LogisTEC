package graph;

/**
 * Vértice del grafo de la ciudad LogísTEC.
 *
 * <p>Representa una intersección, el depósito o una dirección de entrega.
 * Cada vértice tiene un identificador único (String), un tipo ({@link VertexType})
 * y coordenadas (x, y) para la visualización gráfica.</p>
 *
 * <p>El índice ({@code index}) es asignado por el {@link Graph} al insertar el vértice
 * y se usa para acceder eficientemente a la matriz de adyacencia en algoritmos como
 * Floyd-Warshall y Warshall.</p>
 *
 * @author Persona 2
 * @version 1.0
 */
public class Vertex {

    /** Identificador único del vértice (p. ej. {@code "A"}, {@code "DEPOT_1"}). */
    private final String id;

    /** Tipo semántico del vértice dentro del problema logístico. */
    private final VertexType type;

    /** Coordenada X para la visualización gráfica (en píxeles). */
    private final int x;

    /** Coordenada Y para la visualización gráfica (en píxeles). */
    private final int y;

    /**
     * Índice numérico asignado por el {@link Graph} (base 0).
     * Permite mapear el vértice a filas/columnas de matrices.
     */
    private int index;

    /**
     * Crea un vértice con todos sus atributos.
     *
     * @param id    identificador único; no debe ser {@code null} ni vacío.
     * @param type  tipo semántico del vértice.
     * @param x     coordenada X para la UI.
     * @param y     coordenada Y para la UI.
     * @param index índice numérico asignado por el grafo (≥ 0).
     */
    public Vertex(String id, VertexType type, int x, int y, int index) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id del vértice no puede ser nulo o vacío");
        }
        this.id    = id;
        this.type  = (type == null) ? VertexType.INTERSECTION : type;
        this.x     = x;
        this.y     = y;
        this.index = index;
    }

    // ---------------------------------------------------------------- getters

    /** @return identificador único del vértice. */
    public String getId() { return id; }

    /** @return tipo semántico del vértice. */
    public VertexType getType() { return type; }

    /** @return coordenada X para la visualización. */
    public int getX() { return x; }

    /** @return coordenada Y para la visualización. */
    public int getY() { return y; }

    /**
     * Retorna el índice numérico del vértice dentro del grafo.
     * Es asignado al momento de agregar el vértice con {@link Graph#addVertex(Vertex)}.
     *
     * @return índice ≥ 0.
     */
    public int getIndex() { return index; }

    /**
     * Actualiza el índice del vértice. Solo debe ser llamado por {@link Graph}.
     *
     * @param index nuevo índice ≥ 0.
     */
    void setIndex(int index) { this.index = index; }

    /** @return {@code true} si este vértice es el depósito central. */
    public boolean isDepot() { return type == VertexType.DEPOT; }

    // ---------------------------------------------------------------- Object

    /**
     * Dos vértices son iguales si y solo si tienen el mismo {@code id}.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vertex)) return false;
        return id.equals(((Vertex) obj).id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() {
        return "Vertex{id='" + id + "', type=" + type + ", index=" + index + "}";
    }
}