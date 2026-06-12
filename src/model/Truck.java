package model;

/**
 * Representa un camión de la flota de LogísTEC.
 */
public class Truck {

    private final String id;
    private final int capacidad;

    public Truck(String id, int capacidad) {
        this.id = id;
        this.capacidad = capacidad;
    }

    public String getId() {
        return id;
    }

    public int getCapacidad() {
        return capacidad;
    }

    @Override
    public String toString() {
        return "Truck{id='" + id + "', capacidad=" + capacidad + " kg}";
    }
}