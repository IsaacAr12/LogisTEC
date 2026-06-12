package planner;

import model.Package;

/**
 * Resultado completo de la asignación de paquetes a camiones.
 */
public class AssignmentResult {

    private final TruckAssignment[] assignments;
    private final Package[] rejectedPackages;
    private int rejectedCount;

    public AssignmentResult(TruckAssignment[] assignments, int maxRejected) {
        this.assignments = assignments;
        this.rejectedPackages = new Package[maxRejected];
        this.rejectedCount = 0;
    }

    public TruckAssignment[] getAssignments() {
        return assignments;
    }

    public int getRejectedCount() {
        return rejectedCount;
    }

    public void addRejectedPackage(Package pkg) {
        rejectedPackages[rejectedCount] = pkg;
        rejectedCount++;
    }

    public Package[] getRejectedPackagesCopy() {
        Package[] copy = new Package[rejectedCount];

        for (int i = 0; i < rejectedCount; i++) {
            copy[i] = rejectedPackages[i];
        }

        return copy;
    }

    public void printSummary() {
        System.out.println("\n--- Asignación de paquetes a camiones ---");

        for (int i = 0; i < assignments.length; i++) {
            System.out.print(assignments[i]);
        }

        System.out.println("--- Paquetes rechazados por capacidad ---");

        if (rejectedCount == 0) {
            System.out.println("No hay paquetes rechazados por capacidad.");
            return;
        }

        for (int i = 0; i < rejectedCount; i++) {
            System.out.println("   - " + rejectedPackages[i]);
        }
    }
}