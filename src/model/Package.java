package model;

/**
 * Representa un paquete que debe entregarse en un vértice destino del grafo.
 */
public class Package {

    private final String id;
    private final String destinoId;
    private final int peso;
    private final int prioridad;

    private boolean rechazado;
    private String motivoRechazo;

    public Package(String id, String destinoId, int peso, int prioridad) {
        this.id = id;
        this.destinoId = destinoId;
        this.peso = peso;
        this.prioridad = prioridad;
        this.rechazado = false;
        this.motivoRechazo = "";
    }

    public String getId() {
        return id;
    }

    public String getDestinoId() {
        return destinoId;
    }

    public int getPeso() {
        return peso;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public boolean isRechazado() {
        return rechazado;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void rechazar(String motivo) {
        this.rechazado = true;
        this.motivoRechazo = motivo;
    }

    @Override
    public String toString() {
        if (rechazado) {
            return "Package{id='" + id + "', destino='" + destinoId
                    + "', peso=" + peso
                    + ", prioridad=" + prioridad
                    + ", rechazado=true, motivo='" + motivoRechazo + "'}";
        }

        return "Package{id='" + id + "', destino='" + destinoId
                + "', peso=" + peso
                + ", prioridad=" + prioridad
                + ", rechazado=false}";
    }
}