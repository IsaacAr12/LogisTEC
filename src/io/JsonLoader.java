package io;

import ds.DLinkedList;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Carga y valida el caso del problema desde un archivo de configuración JSON.
 *
 * <p>El parseo del texto JSON se delega a <b>Gson</b> (única librería de terceros permitida
 * por la sección 5 del enunciado, ya que el parsing no es objetivo del curso). Gson rellena
 * unos DTO internos con arreglos nativos {@code T[]}; luego este loader valida la coherencia
 * del modelo y construye un {@link LogisticsConfig} usando las estructuras del equipo.</p>
 *
 * <p>Validaciones realizadas (todas lanzan {@link ConfigValidationException} con un mensaje
 * claro para el usuario):</p>
 * <ul>
 *   <li>Estructura básica presente (ciudad, vértices, aristas, camiones).</li>
 *   <li>Identificadores de vértice únicos y no vacíos.</li>
 *   <li>Existe exactamente un vértice de tipo {@code DEPOT}.</li>
 *   <li>Cada arista referencia vértices existentes, sin lazos, con distancia &gt; 0.</li>
 *   <li>Cada paquete referencia un destino existente, peso &gt; 0 y prioridad ∈ {1,2,3}.</li>
 *   <li>Identificadores de paquete y de camión únicos; capacidad de camión &gt; 0.</li>
 * </ul>
 *
 * <p>Nota: detectar destinos <i>inalcanzables</i> desde el depósito NO es tarea de este loader;
 * eso lo resuelve Warshall (Persona 2) sobre el grafo ya construido. Aquí solo se valida que
 * los identificadores referenciados existan.</p>
 */
public class JsonLoader {

    private final Gson gson = new Gson();

    /**
     * Carga el caso desde la ruta de un archivo JSON.
     * @param path ruta al archivo.
     * @return la configuración parseada y validada.
     * @throws IOException si el archivo no se puede leer.
     * @throws ConfigValidationException si el JSON es inválido o incumple el modelo.
     */
    public LogisticsConfig load(Path path) throws IOException, ConfigValidationException {
        String json = Files.readString(path);
        return loadFromString(json);
    }

    /**
     * Carga el caso desde la ruta (String) de un archivo JSON.
     * @param path ruta al archivo.
     * @return la configuración parseada y validada.
     * @throws IOException si el archivo no se puede leer.
     * @throws ConfigValidationException si el JSON es inválido o incumple el modelo.
     */
    public LogisticsConfig load(String path) throws IOException, ConfigValidationException {
        return load(Path.of(path));
    }

    /**
     * Carga el caso directamente desde una cadena con el contenido JSON
     * (útil para pruebas unitarias sin tocar el disco).
     * @param json contenido JSON.
     * @return la configuración parseada y validada.
     * @throws ConfigValidationException si el JSON es inválido o incumple el modelo.
     */
    public LogisticsConfig loadFromString(String json) throws ConfigValidationException {
        RawConfig raw;
        try {
            raw = gson.fromJson(json, RawConfig.class);
        } catch (JsonSyntaxException e) {
            throw new ConfigValidationException("El JSON está mal formado: " + e.getMessage(), e);
        }
        if (raw == null) {
            throw new ConfigValidationException("El JSON está vacío o es nulo");
        }
        return build(raw);
    }

    // ----------------------------------------------------------------
    // Construcción + validación
    // ----------------------------------------------------------------

    private LogisticsConfig build(RawConfig raw) throws ConfigValidationException {
        if (raw.ciudad == null) throw new ConfigValidationException("Falta el objeto 'ciudad'");
        if (raw.ciudad.vertices == null || raw.ciudad.vertices.length == 0) {
            throw new ConfigValidationException("La ciudad no tiene vértices");
        }
        if (raw.ciudad.aristas == null) {
            throw new ConfigValidationException("Falta la lista 'aristas' (puede ser vacía pero debe existir)");
        }
        if (raw.camiones == null || raw.camiones.length == 0) {
            throw new ConfigValidationException("No hay camiones en la flota");
        }

        DLinkedList<LogisticsConfig.VertexInfo> vertices = new DLinkedList<>();
        String depotId = null;
        int depotCount = 0;

        for (RawVertex rv : raw.ciudad.vertices) {
            if (rv == null || rv.id == null || rv.id.isBlank()) {
                throw new ConfigValidationException("Hay un vértice sin 'id'");
            }
            if (containsVertexId(vertices, rv.id)) {
                throw new ConfigValidationException("Identificador de vértice duplicado: " + rv.id);
            }
            String tipo = (rv.tipo == null) ? "INTERSECCION" : rv.tipo.trim().toUpperCase();
            if (tipo.equals("DEPOT")) {
                depotCount++;
                depotId = rv.id;
            }
            vertices.add(new LogisticsConfig.VertexInfo(rv.id, tipo, rv.x, rv.y));
        }

        if (depotCount == 0) {
            throw new ConfigValidationException("No hay ningún vértice de tipo DEPOT");
        }
        if (depotCount > 1) {
            throw new ConfigValidationException("Debe existir un único depósito, se encontraron " + depotCount);
        }

        DLinkedList<LogisticsConfig.EdgeInfo> edges = new DLinkedList<>();
        for (RawEdge re : raw.ciudad.aristas) {
            if (re == null || re.u == null || re.v == null) {
                throw new ConfigValidationException("Hay una arista sin extremos 'u'/'v'");
            }
            if (!containsVertexId(vertices, re.u)) {
                throw new ConfigValidationException("La arista referencia un vértice inexistente: " + re.u);
            }
            if (!containsVertexId(vertices, re.v)) {
                throw new ConfigValidationException("La arista referencia un vértice inexistente: " + re.v);
            }
            if (re.u.equals(re.v)) {
                throw new ConfigValidationException("Arista inválida (lazo) en el vértice: " + re.u);
            }
            if (re.distancia <= 0) {
                throw new ConfigValidationException(
                    "La arista " + re.u + "-" + re.v + " debe tener distancia positiva, tiene " + re.distancia);
            }
            edges.add(new LogisticsConfig.EdgeInfo(re.u, re.v, re.distancia));
        }

        DLinkedList<LogisticsConfig.PackageInfo> packages = new DLinkedList<>();
        if (raw.paquetes != null) {
            for (RawPackage rp : raw.paquetes) {
                if (rp == null || rp.id == null || rp.id.isBlank()) {
                    throw new ConfigValidationException("Hay un paquete sin 'id'");
                }
                if (containsPackageId(packages, rp.id)) {
                    throw new ConfigValidationException("Identificador de paquete duplicado: " + rp.id);
                }
                if (rp.destino == null || !containsVertexId(vertices, rp.destino)) {
                    throw new ConfigValidationException(
                        "El paquete " + rp.id + " tiene un destino inexistente: " + rp.destino);
                }
                if (rp.peso <= 0) {
                    throw new ConfigValidationException(
                        "El paquete " + rp.id + " debe tener peso positivo, tiene " + rp.peso);
                }
                if (rp.prioridad < 1 || rp.prioridad > 3) {
                    throw new ConfigValidationException(
                        "El paquete " + rp.id + " tiene prioridad fuera de {1,2,3}: " + rp.prioridad);
                }
                packages.add(new LogisticsConfig.PackageInfo(rp.id, rp.destino, rp.peso, rp.prioridad));
            }
        }

        DLinkedList<LogisticsConfig.TruckInfo> trucks = new DLinkedList<>();
        for (RawTruck rt : raw.camiones) {
            if (rt == null || rt.id == null || rt.id.isBlank()) {
                throw new ConfigValidationException("Hay un camión sin 'id'");
            }
            if (containsTruckId(trucks, rt.id)) {
                throw new ConfigValidationException("Identificador de camión duplicado: " + rt.id);
            }
            if (rt.capacidad <= 0) {
                throw new ConfigValidationException(
                    "El camión " + rt.id + " debe tener capacidad positiva, tiene " + rt.capacidad);
            }
            trucks.add(new LogisticsConfig.TruckInfo(rt.id, rt.capacidad));
        }

        return new LogisticsConfig(depotId, vertices, edges, packages, trucks);
    }

    // ---- chequeos de membresía/duplicado (O(n), suficiente para los tamaños del proyecto) ----

    private boolean containsVertexId(DLinkedList<LogisticsConfig.VertexInfo> list, String id) {
        for (LogisticsConfig.VertexInfo v : list) {
            if (v.id().equals(id)) return true;
        }
        return false;
    }

    private boolean containsPackageId(DLinkedList<LogisticsConfig.PackageInfo> list, String id) {
        for (LogisticsConfig.PackageInfo p : list) {
            if (p.id().equals(id)) return true;
        }
        return false;
    }

    private boolean containsTruckId(DLinkedList<LogisticsConfig.TruckInfo> list, String id) {
        for (LogisticsConfig.TruckInfo t : list) {
            if (t.id().equals(id)) return true;
        }
        return false;
    }

    // ----------------------------------------------------------------
    // DTO internos: solo los usa Gson para volcar el JSON. Usan arreglos
    // nativos T[] (permitidos por el enunciado) y no salen de esta clase.
    // ----------------------------------------------------------------

    private static final class RawConfig {
        RawCity ciudad;
        RawPackage[] paquetes;
        RawTruck[] camiones;
    }

    private static final class RawCity {
        RawVertex[] vertices;
        RawEdge[] aristas;
    }

    private static final class RawVertex {
        String id;
        String tipo;
        int x;
        int y;
    }

    private static final class RawEdge {
        String u;
        String v;
        int distancia;
    }

    private static final class RawPackage {
        String id;
        String destino;
        int peso;
        int prioridad;
    }

    private static final class RawTruck {
        @SerializedName("id") String id;
        @SerializedName("capacidad") int capacidad;
    }
}
