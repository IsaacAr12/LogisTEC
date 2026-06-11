package algorithms;

import ds.DLinkedList;
import ds.Queue;
import ds.Stack;
import graph.Edge;
import graph.Graph;
import graph.Vertex;

/**
 * Recorridos clásicos sobre grafos no dirigidos: BFS y DFS.
 *
 * <h2>BFS — Búsqueda en amplitud (Breadth-First Search)</h2>
 * <p>Visita los vértices por niveles: primero los adyacentes al origen, luego los
 * adyacentes a esos, y así sucesivamente. Usa una {@link Queue} (FIFO).
 * En grafos no ponderados da el camino con menor número de aristas.</p>
 *
 * <h2>DFS — Búsqueda en profundidad (Depth-First Search)</h2>
 * <p>Avanza tan profundo como puede antes de retroceder. Se ofrece en versión
 * iterativa (usa {@link Stack}) y en versión recursiva (usa la pila de llamadas).
 * Útil para detectar componentes conexas y para el recorrido preorden del MST
 * en la heurística {@code MstRouting}.</p>
 *
 * <h2>Restricciones</h2>
 * <p>No se usa ninguna colección de {@code java.util}; solo las estructuras
 * del equipo ({@link Queue}, {@link Stack}, {@link DLinkedList}) y arreglos nativos.</p>
 *
 * @author Persona 2
 * @version 1.0
 */
public class Traversals {

    // ================================================================
    // BFS
    // ================================================================

    /**
     * Recorre el grafo en amplitud desde el vértice {@code source}.
     *
     * <p>Retorna la lista de vértices en el orden en que fueron visitados.
     * Solo alcanza los vértices en la misma componente conexa que {@code source}.</p>
     *
     * <p>Complejidad: O(|V| + |E|).</p>
     *
     * @param graph  grafo sobre el que se realiza el recorrido.
     * @param source vértice de inicio.
     * @return lista de vértices en orden BFS.
     * @throws IllegalArgumentException si {@code source} es {@code null}.
     */
    public static DLinkedList<Vertex> bfs(Graph graph, Vertex source) {
        if (source == null) throw new IllegalArgumentException("El vértice de inicio no puede ser nulo");

        int n = graph.vertexCount();
        boolean[] visited = new boolean[n];
        DLinkedList<Vertex> result = new DLinkedList<>();
        Queue<Vertex> queue = new Queue<>();

        visited[source.getIndex()] = true;
        queue.enqueue(source);

        while (!queue.isEmpty()) {
            Vertex current = queue.dequeue();
            result.addLast(current);

            for (Edge edge : graph.getNeighbors(current)) {
                Vertex neighbor = edge.getOther(current);
                if (!visited[neighbor.getIndex()]) {
                    visited[neighbor.getIndex()] = true;
                    queue.enqueue(neighbor);
                }
            }
        }

        return result;
    }

    /**
     * Versión de BFS que acepta el id del vértice de inicio.
     *
     * @param graph    grafo sobre el que se realiza el recorrido.
     * @param sourceId id del vértice de inicio.
     * @return lista de vértices en orden BFS, o lista vacía si el id no existe.
     */
    public static DLinkedList<Vertex> bfs(Graph graph, String sourceId) {
        Vertex source = graph.getVertex(sourceId);
        if (source == null) return new DLinkedList<>();
        return bfs(graph, source);
    }

    // ================================================================
    // DFS iterativo
    // ================================================================

    /**
     * Recorre el grafo en profundidad desde el vértice {@code source} (versión iterativa).
     *
     * <p>Usa una {@link Stack} explícita. El orden de visita puede diferir del DFS
     * recursivo cuando un vértice tiene múltiples vecinos, pero garantiza visitar
     * toda la componente conexa de {@code source}.</p>
     *
     * <p>Complejidad: O(|V| + |E|).</p>
     *
     * @param graph  grafo sobre el que se realiza el recorrido.
     * @param source vértice de inicio.
     * @return lista de vértices en orden DFS iterativo.
     * @throws IllegalArgumentException si {@code source} es {@code null}.
     */
    public static DLinkedList<Vertex> dfsIterative(Graph graph, Vertex source) {
        if (source == null) throw new IllegalArgumentException("El vértice de inicio no puede ser nulo");

        int n = graph.vertexCount();
        boolean[] visited = new boolean[n];
        DLinkedList<Vertex> result = new DLinkedList<>();
        Stack<Vertex> stack = new Stack<>();

        stack.push(source);

        while (!stack.isEmpty()) {
            Vertex current = stack.pop();
            if (visited[current.getIndex()]) continue;

            visited[current.getIndex()] = true;
            result.addLast(current);

            for (Edge edge : graph.getNeighbors(current)) {
                Vertex neighbor = edge.getOther(current);
                if (!visited[neighbor.getIndex()]) {
                    stack.push(neighbor);
                }
            }
        }

        return result;
    }

    /**
     * Versión de DFS iterativo que acepta el id del vértice de inicio.
     *
     * @param graph    grafo sobre el que se realiza el recorrido.
     * @param sourceId id del vértice de inicio.
     * @return lista de vértices en orden DFS, o lista vacía si el id no existe.
     */
    public static DLinkedList<Vertex> dfsIterative(Graph graph, String sourceId) {
        Vertex source = graph.getVertex(sourceId);
        if (source == null) return new DLinkedList<>();
        return dfsIterative(graph, source);
    }

    // ================================================================
    // DFS recursivo
    // ================================================================

    /**
     * Recorre el grafo en profundidad desde el vértice {@code source} (versión recursiva).
     *
     * <p>Produce un recorrido preorden: el vértice se agrega a la lista <em>antes</em>
     * de visitar sus vecinos. Este orden es el que usa la heurística {@code MstRouting}
     * para construir la ruta del camión.</p>
     *
     * <p>Complejidad: O(|V| + |E|).</p>
     *
     * @param graph  grafo sobre el que se realiza el recorrido.
     * @param source vértice de inicio.
     * @return lista de vértices en preorden DFS.
     * @throws IllegalArgumentException si {@code source} es {@code null}.
     */
    public static DLinkedList<Vertex> dfsRecursive(Graph graph, Vertex source) {
        if (source == null) throw new IllegalArgumentException("El vértice de inicio no puede ser nulo");

        int n = graph.vertexCount();
        boolean[] visited = new boolean[n];
        DLinkedList<Vertex> result = new DLinkedList<>();

        dfsHelper(graph, source, visited, result);
        return result;
    }

    /**
     * Versión de DFS recursivo que acepta el id del vértice de inicio.
     *
     * @param graph    grafo sobre el que se realiza el recorrido.
     * @param sourceId id del vértice de inicio.
     * @return lista de vértices en preorden DFS, o lista vacía si el id no existe.
     */
    public static DLinkedList<Vertex> dfsRecursive(Graph graph, String sourceId) {
        Vertex source = graph.getVertex(sourceId);
        if (source == null) return new DLinkedList<>();
        return dfsRecursive(graph, source);
    }

    // ================================================================
    // Componentes conexas
    // ================================================================

    /**
     * Detecta cuántas componentes conexas tiene el grafo.
     *
     * <p>Recorre el grafo con BFS repetido: cuando quedan vértices sin visitar
     * tras un BFS, significa que están en una componente diferente. Cada BFS
     * adicional representa una nueva componente.</p>
     *
     * <p>Complejidad: O(|V| + |E|).</p>
     *
     * @param graph grafo a analizar.
     * @return número de componentes conexas (&ge; 1 si el grafo tiene vértices).
     */
    public static int connectedComponents(Graph graph) {
        int n = graph.vertexCount();
        if (n == 0) return 0;

        boolean[] visited = new boolean[n];
        int components = 0;

        for (Vertex v : graph.getVertices()) {
            if (!visited[v.getIndex()]) {
                components++;
                // BFS manual para marcar toda la componente
                Queue<Vertex> queue = new Queue<>();
                queue.enqueue(v);
                visited[v.getIndex()] = true;
                while (!queue.isEmpty()) {
                    Vertex current = queue.dequeue();
                    for (Edge edge : graph.getNeighbors(current)) {
                        Vertex neighbor = edge.getOther(current);
                        if (!visited[neighbor.getIndex()]) {
                            visited[neighbor.getIndex()] = true;
                            queue.enqueue(neighbor);
                        }
                    }
                }
            }
        }
        return components;
    }

    /**
     * Indica si el grafo es completamente conexo.
     *
     * @param graph grafo a analizar.
     * @return {@code true} si todos los vértices pertenecen a la misma componente.
     */
    public static boolean isConnected(Graph graph) {
        return graph.vertexCount() == 0 || connectedComponents(graph) == 1;
    }

    /**
     * Indica si el vértice {@code target} es alcanzable desde {@code source}
     * usando BFS. O(|V| + |E|).
     *
     * @param graph  grafo a analizar.
     * @param source vértice de partida.
     * @param target vértice destino.
     * @return {@code true} si existe un camino de {@code source} a {@code target}.
     */
    public static boolean isReachable(Graph graph, Vertex source, Vertex target) {
        if (source.equals(target)) return true;
        DLinkedList<Vertex> visited = bfs(graph, source);
        for (Vertex v : visited) {
            if (v.equals(target)) return true;
        }
        return false;
    }

    // ================================================================
    // Privados
    // ================================================================

    /**
     * Auxiliar recursivo del DFS preorden.
     *
     * @param graph   grafo.
     * @param current vértice actual.
     * @param visited arreglo de visitados.
     * @param result  lista donde se acumulan los vértices en preorden.
     */
    private static void dfsHelper(Graph graph, Vertex current,
                                   boolean[] visited, DLinkedList<Vertex> result) {
        visited[current.getIndex()] = true;
        result.addLast(current);   // preorden: agregar antes de los hijos

        for (Edge edge : graph.getNeighbors(current)) {
            Vertex neighbor = edge.getOther(current);
            if (!visited[neighbor.getIndex()]) {
                dfsHelper(graph, neighbor, visited, result);
            }
        }
    }
}