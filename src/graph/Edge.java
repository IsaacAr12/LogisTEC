package graph;

/**
 * Arista no dirigida y ponderada del grafo de la ciudad LogísTEC.
 *
 * <p>Representa una calle bidireccional entre dos intersecciones.
 * El peso ({@code distance}) es la distancia en metros (entero positivo).</p>
 *
 * <p>Como el grafo es <b>no dirigido</b>, la arista {@code (u, v, d)} y
 * {@code (v, u, d)} son la misma arista; el {@link Graph} se encarga de
 * registrarla en ambas listas de adyacencia.</p>
 *
 * @author Persona 2
 * @version 1.0
 */
public class Edge {

    /** Uno de los dos extremos de la arista. */
    private final Vertex u;

    /** El otro extremo de la arista. */
    private final Vertex v;

    /** Distancia en metros entre {@code u} y {@code v} (siempre &gt; 0). */
    private final int distance;

    /**
     * Crea una arista no dirigida entre {@code u} y {@code v}.
     *
     * @param u        primer extremo; no puede ser {@code null}.
     * @param v        segundo extremo; no puede ser {@code null} ni igual a {@code u}.
     * @param distance distancia en metros; debe ser positiva.
     * @throws IllegalArgumentException si algún parámetro es inválido.
     */
    public Edge(Vertex u, Vertex v, int distance) {
        if (u == null || v == null) {
            throw new IllegalArgumentException("Los extremos de la arista no pueden ser nulos");
        }
        if (u.equals(v)) {
            throw new IllegalArgumentException("No se permiten lazos (arista de " + u.getId() + " a sí mismo)");
        }
        if (distance <= 0) {
            throw new IllegalArgumentException(
                "La distancia debe ser positiva, se recibió " + distance
                + " en la arista " + u.getId() + "-" + v.getId());
        }
        this.u        = u;
        this.v        = v;
        this.distance = distance;
    }

    // ---------------------------------------------------------------- getters

    /** @return primer extremo de la arista. */
    public Vertex getU() { return u; }

    /** @return segundo extremo de la arista. */
    public Vertex getV() { return v; }

    /** @return distancia en metros (siempre &gt; 0). */
    public int getDistance() { return distance; }

    /**
     * Dado uno de los extremos, retorna el otro.
     * Útil para recorrer la lista de adyacencia sin saber cuál extremo es el actual.
     *
     * @param vertex uno de los extremos.
     * @return el extremo opuesto.
     * @throws IllegalArgumentException si {@code vertex} no es ninguno de los dos extremos.
     */
    public Vertex getOther(Vertex vertex) {
        if (u.equals(vertex)) return v;
        if (v.equals(vertex)) return u;
        throw new IllegalArgumentException(
            "El vértice " + vertex.getId() + " no pertenece a esta arista");
    }

    // ---------------------------------------------------------------- Object

    /**
     * Dos aristas son iguales si conectan los mismos dos vértices con la misma distancia.
     * Como el grafo es no dirigido, {@code (u,v,d)} == {@code (v,u,d)}.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Edge)) return false;
        Edge other = (Edge) obj;
        return distance == other.distance
            && ((u.equals(other.u) && v.equals(other.v))
             || (u.equals(other.v) && v.equals(other.u)));
    }

    @Override
    public int hashCode() {
        // Orden independiente: suma de hashcodes
        return u.hashCode() + v.hashCode() + Integer.hashCode(distance);
    }

    @Override
    public String toString() {
        return "Edge{" + u.getId() + " <-> " + v.getId() + ", dist=" + distance + "m}";
    }
}