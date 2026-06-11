package graph;

/**
 * Tipos posibles de vértice dentro del grafo de la ciudad LogísTEC.
 *
 * <ul>
 *   <li>{@link #DEPOT}        – Depósito central; punto de inicio y fin de todos los camiones.</li>
 *   <li>{@link #INTERSECTION} – Intersección vial sin paquete asignado.</li>
 *   <li>{@link #DELIVERY}     – Dirección de entrega con al menos un paquete pendiente.</li>
 * </ul>
 *
 * <p>El {@link io.JsonLoader} expone el tipo como {@code String} (p. ej. {@code "DEPOT"});
 * el método {@link #fromString(String)} hace la conversión al construir el grafo.</p>
 *
 * @author Persona 2
 * @version 1.0
 */
public enum VertexType {

    /** Depósito central de LogísTEC. Existe exactamente uno en el grafo. */
    DEPOT,

    /** Intersección vial genérica sin paquetes. */
    INTERSECTION,

    /** Dirección de entrega de paquete(s). */
    DELIVERY;

    /**
     * Convierte el {@code String} que viene del JSON al enum correspondiente.
     * Los valores aceptados son {@code "DEPOT"}, {@code "INTERSECCION"},
     * {@code "INTERSECTION"} y {@code "DELIVERY"} (insensibles a mayúsculas).
     *
     * @param s cadena leída del JSON.
     * @return el {@code VertexType} correspondiente.
     * @throws IllegalArgumentException si {@code s} no corresponde a ningún tipo conocido.
     */
    public static VertexType fromString(String s) {
        if (s == null) return INTERSECTION;
        switch (s.trim().toUpperCase()) {
            case "DEPOT":        return DEPOT;
            case "DELIVERY":     return DELIVERY;
            case "INTERSECCION":
            case "INTERSECTION": return INTERSECTION;
            default: throw new IllegalArgumentException("Tipo de vértice desconocido: " + s);
        }
    }
}