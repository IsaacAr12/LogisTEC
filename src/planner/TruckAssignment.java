package planner;

import model.Package;
import model.Truck;

/**
 * Guarda los paquetes asignados a un camión específico.
 */
public class TruckAssignment {

    private final Truck truck;
    private final Package[] packages;
    private int packageCount;
    private int usedCapacity;

    public TruckAssignment(Truck truck, int maxPackages) {
        this.truck = truck;
        this.packages = new Package[maxPackages];
        this.packageCount = 0;
        this.usedCapacity = 0;
    }

    public Truck getTruck() {
        return truck;
    }

    public int getPackageCount() {
        return packageCount;
    }

    public int getUsedCapacity() {
        return usedCapacity;
    }

    public int getFreeCapacity() {
        return truck.getCapacidad() - usedCapacity;
    }

    public double getOccupationPercentage() {
        if (truck.getCapacidad() == 0) {
            return 0.0;
        }

        return (usedCapacity * 100.0) / truck.getCapacidad();
    }

    public boolean canFit(Package pkg) {
        return pkg.getPeso() <= getFreeCapacity();
    }

    public void addPackage(Package pkg) {
        if (!canFit(pkg)) {
            throw new IllegalArgumentException("El paquete no cabe en el camión.");
        }

        packages[packageCount] = pkg;
        packageCount++;
        usedCapacity += pkg.getPeso();
    }

    public Package[] getPackagesCopy() {
        Package[] copy = new Package[packageCount];

        for (int i = 0; i < packageCount; i++) {
            copy[i] = packages[i];
        }

        return copy;
    }

    public String[] getDestinationIds() {
        String[] destinations = new String[packageCount];

        for (int i = 0; i < packageCount; i++) {
            destinations[i] = packages[i].getDestinoId();
        }

        return destinations;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(truck.getId())
          .append(" | carga ")
          .append(usedCapacity)
          .append("/")
          .append(truck.getCapacidad())
          .append(" kg | ocupación ")
          .append(String.format("%.2f", getOccupationPercentage()))
          .append("%\n");

        for (int i = 0; i < packageCount; i++) {
            sb.append("   - ")
              .append(packages[i].getId())
              .append(" -> ")
              .append(packages[i].getDestinoId())
              .append(" | peso=")
              .append(packages[i].getPeso())
              .append(" | prioridad=")
              .append(packages[i].getPrioridad())
              .append("\n");
        }

        return sb.toString();
    }
}