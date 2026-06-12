package graph;

/**
 * Tipos de vértices que puede tener el grafo de LogísTEC.
 *
 * DEPOT:
 * Representa el depósito principal desde donde salen y regresan los camiones.
 *
 * DELIVERY:
 * Representa un punto de entrega de paquetes.
 *
 * INTERSECTION:
 * Representa una intersección normal de la ciudad.
 */
public enum VertexType {

    DEPOT,
    DELIVERY,
    INTERSECTION;

    /**
     * Convierte un texto leído desde el JSON al tipo de vértice correspondiente.
     *
     * Acepta nombres en inglés y en español para evitar errores con el archivo JSON.
     *
     * @param s texto leído desde el JSON.
     * @return tipo de vértice correspondiente.
     */
    public static VertexType fromString(String s) {
        if (s == null) {
            return INTERSECTION;
        }

        switch (s.trim().toUpperCase()) {
            case "DEPOT":
            case "DEPOSITO":
            case "DEPÓSITO":
                return DEPOT;

            case "DELIVERY":
            case "ENTREGA":
                return DELIVERY;

            case "INTERSECCION":
            case "INTERSECCIÓN":
            case "INTERSECTION":
                return INTERSECTION;

            default:
                throw new IllegalArgumentException("Tipo de vértice desconocido: " + s);
        }
    }
}