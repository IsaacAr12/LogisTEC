package planner;

import model.Package;
import model.Truck;

/**
 * Asigna paquetes a camiones usando la heurística solicitada:
 *
 * 1. Ordenar paquetes por prioridad ascendente.
 * 2. Si tienen la misma prioridad, ordenar por peso descendente.
 * 3. Asignar cada paquete al camión con menor capacidad libre suficiente.
 *
 * Nota:
 * Aunque el enunciado dice "camión con mayor capacidad libre" y lo llama best-fit,
 * realmente best-fit normalmente significa escoger el camión donde quede menos espacio sobrante.
 * Aquí usamos la interpretación de best-fit: menor espacio libre que pueda alojar el paquete.
 */
public class PackageAssigner {

    public AssignmentResult assign(Package[] packages, Truck[] trucks) {
        Package[] sortedPackages = copyPackages(packages);
        sortPackages(sortedPackages);

        TruckAssignment[] assignments = new TruckAssignment[trucks.length];

        for (int i = 0; i < trucks.length; i++) {
            assignments[i] = new TruckAssignment(trucks[i], packages.length);
        }

        AssignmentResult result = new AssignmentResult(assignments, packages.length);

        for (int i = 0; i < sortedPackages.length; i++) {
            Package pkg = sortedPackages[i];

            if (pkg.isRechazado()) {
                result.addRejectedPackage(pkg);
                continue;
            }

            int bestTruckIndex = findBestTruck(pkg, assignments);

            if (bestTruckIndex == -1) {
                pkg.rechazar("No cabe en ningún camión por capacidad.");
                result.addRejectedPackage(pkg);
            } else {
                assignments[bestTruckIndex].addPackage(pkg);
            }
        }

        return result;
    }

    private Package[] copyPackages(Package[] packages) {
        Package[] copy = new Package[packages.length];

        for (int i = 0; i < packages.length; i++) {
            copy[i] = packages[i];
        }

        return copy;
    }

    private void sortPackages(Package[] packages) {
        for (int i = 1; i < packages.length; i++) {
            Package key = packages[i];
            int j = i - 1;

            while (j >= 0 && comparePackages(packages[j], key) > 0) {
                packages[j + 1] = packages[j];
                j--;
            }

            packages[j + 1] = key;
        }
    }

    private int comparePackages(Package a, Package b) {
        if (a.getPrioridad() != b.getPrioridad()) {
            return a.getPrioridad() - b.getPrioridad();
        }

        return b.getPeso() - a.getPeso();
    }

    private int findBestTruck(Package pkg, TruckAssignment[] assignments) {
        int bestIndex = -1;
        int bestRemainingAfterAssign = Integer.MAX_VALUE;

        for (int i = 0; i < assignments.length; i++) {
            if (assignments[i].canFit(pkg)) {
                int remainingAfterAssign = assignments[i].getFreeCapacity() - pkg.getPeso();

                if (remainingAfterAssign < bestRemainingAfterAssign) {
                    bestRemainingAfterAssign = remainingAfterAssign;
                    bestIndex = i;
                }
            }
        }

        return bestIndex;
    }
}