package ds;

/**
 * Conjunto disjunto (Union-Find) implementado por el equipo sobre arreglos nativos,
 * con las optimizaciones de <b>compresión de caminos</b> y <b>unión por rango</b>.
 *
 * <p>Es la estructura de soporte natural de Kruskal: permite saber en tiempo casi
 * constante si dos vértices ya están en la misma componente (lo que crearía un ciclo)
 * y unir componentes cuando se acepta una arista.</p>
 *
 * <p>Los elementos son enteros en el rango {@code [0, n)}. Con ambas optimizaciones,
 * {@code find} y {@code union} son prácticamente O(1) amortizado (más precisamente
 * O(α(n)), inverso de Ackermann).</p>
 */
public class UnionFind {

    /** parent[x] = padre de x en el bosque; si parent[x]==x, x es la raíz de su conjunto. */
    private final int[] parent;

    /** Cota superior de la altura del árbol con raíz x (heurística de unión por rango). */
    private final int[] rank;

    /** Cantidad de conjuntos disjuntos actuales. */
    private int count;

    /**
     * Crea {@code n} conjuntos unitarios: {0}, {1}, ..., {n-1}.
     * @param n cantidad de elementos.
     * @throws IllegalArgumentException si {@code n} es negativo.
     */
    public UnionFind(int n) {
        if (n < 0) throw new IllegalArgumentException("n no puede ser negativo: " + n);
        this.parent = new int[n];
        this.rank = new int[n];
        this.count = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    /**
     * Encuentra el representante (raíz) del conjunto de {@code x}, aplicando
     * compresión de caminos para acelerar futuras consultas.
     * @param x elemento a consultar.
     * @return el identificador del conjunto al que pertenece {@code x}.
     * @throws IndexOutOfBoundsException si {@code x} está fuera de rango.
     */
    public int find(int x) {
        validate(x);
        int root = x;
        while (root != parent[root]) {
            root = parent[root];
        }
        // compresión de caminos: cuelga todo el camino directamente de la raíz
        while (x != root) {
            int next = parent[x];
            parent[x] = root;
            x = next;
        }
        return root;
    }

    /**
     * Une los conjuntos que contienen a {@code x} y a {@code y}.
     * @param x primer elemento.
     * @param y segundo elemento.
     * @return {@code true} si estaban en conjuntos distintos y se unieron;
     *         {@code false} si ya estaban en el mismo conjunto (Kruskal descarta la arista).
     */
    public boolean union(int x, int y) {
        int rx = find(x);
        int ry = find(y);
        if (rx == ry) return false;          // ya conectados: agregar la arista crearía un ciclo
        // unión por rango: el árbol más bajo cuelga del más alto
        if (rank[rx] < rank[ry]) {
            parent[rx] = ry;
        } else if (rank[rx] > rank[ry]) {
            parent[ry] = rx;
        } else {
            parent[ry] = rx;
            rank[rx]++;
        }
        count--;
        return true;
    }

    /**
     * @param x primer elemento.
     * @param y segundo elemento.
     * @return {@code true} si ambos están en el mismo conjunto.
     */
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    /** @return cantidad de conjuntos disjuntos actuales (componentes). */
    public int count() {
        return count;
    }

    private void validate(int x) {
        if (x < 0 || x >= parent.length) {
            throw new IndexOutOfBoundsException("Elemento " + x + " fuera de [0," + parent.length + ")");
        }
    }
}
