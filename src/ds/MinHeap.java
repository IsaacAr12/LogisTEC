package ds;

/**
 * Cola de prioridad de mínimos indexada, implementada por el equipo como un
 * <b>heap binario</b> sobre arreglos nativos.
 *
 * <p>Está diseñada para Dijkstra y Prim: en esos algoritmos los elementos son los
 * <b>índices de los vértices</b> (enteros en el rango {@code [0, maxN)}) y la prioridad
 * es la distancia tentativa. Trabajar con índices enteros permite ofrecer
 * {@link #decreaseKey(int, double)} en O(log n) sin necesidad de un {@code HashMap}
 * (prohibido por el enunciado): se mantiene un arreglo inverso {@code posicionEnHeap}
 * que ubica en O(1) la posición de cada índice dentro del heap.</p>
 *
 * <p>Si algún algoritmo necesita encolar objetos arbitrarios, basta con asignarles un
 * índice entero estable (por ejemplo, el índice del vértice que devuelve el grafo) y
 * usar ese entero como elemento.</p>
 *
 * <p>Complejidades: {@code insert}, {@code delMin}, {@code decreaseKey} en O(log n);
 * {@code contains}, {@code minIndex}, {@code keyOf} en O(1).</p>
 */
public class MinHeap {

    /** Capacidad máxima: los índices válidos son {@code 0 .. maxN-1}. */
    private final int maxN;

    /** Cantidad de elementos actualmente en el heap. */
    private int n;

    /** Heap binario 1-indexado: {@code heap[pos]} = índice del elemento en esa posición. */
    private final int[] heap;

    /** Inverso de {@code heap}: {@code posicionEnHeap[i]} = posición del índice {@code i}, o -1 si ausente. */
    private final int[] posicionEnHeap;

    /** Prioridad asociada a cada índice: {@code keys[i]} (válida solo si el índice está presente). */
    private final double[] keys;

    /**
     * Crea una cola de prioridad para índices en el rango {@code [0, maxN)}.
     * @param maxN cantidad máxima de elementos distintos (típicamente |V|).
     * @throws IllegalArgumentException si {@code maxN} no es positivo.
     */
    public MinHeap(int maxN) {
        if (maxN <= 0) throw new IllegalArgumentException("maxN debe ser positivo: " + maxN);
        this.maxN = maxN;
        this.n = 0;
        this.heap = new int[maxN + 1];
        this.posicionEnHeap = new int[maxN];
        this.keys = new double[maxN];
        for (int i = 0; i < maxN; i++) posicionEnHeap[i] = -1;
    }

    /** @return {@code true} si el heap no tiene elementos. */
    public boolean isEmpty() { return n == 0; }

    /** @return cantidad de elementos en el heap. */
    public int size() { return n; }

    /** @return capacidad máxima (índices válidos {@code 0..maxN-1}). */
    public int capacity() { return maxN; }

    /**
     * @param i índice consultado.
     * @return {@code true} si el índice está actualmente en el heap. O(1).
     */
    public boolean contains(int i) {
        validateIndex(i);
        return posicionEnHeap[i] != -1;
    }

    /**
     * Inserta un índice con una prioridad. O(log n).
     * @param i índice a insertar.
     * @param key prioridad asociada.
     * @throws IllegalArgumentException si el índice ya está presente.
     */
    public void insert(int i, double key) {
        validateIndex(i);
        if (contains(i)) throw new IllegalArgumentException("El índice ya está en el heap: " + i);
        n++;
        posicionEnHeap[i] = n;
        heap[n] = i;
        keys[i] = key;
        swim(n);
    }

    /**
     * @return el índice con menor prioridad, sin retirarlo. O(1).
     * @throws IllegalStateException si el heap está vacío.
     */
    public int minIndex() {
        if (n == 0) throw new IllegalStateException("El heap está vacío");
        return heap[1];
    }

    /**
     * @return la menor prioridad presente. O(1).
     * @throws IllegalStateException si el heap está vacío.
     */
    public double minKey() {
        if (n == 0) throw new IllegalStateException("El heap está vacío");
        return keys[heap[1]];
    }

    /**
     * Retira y retorna el índice de menor prioridad. O(log n).
     * @return el índice extraído.
     * @throws IllegalStateException si el heap está vacío.
     */
    public int delMin() {
        if (n == 0) throw new IllegalStateException("El heap está vacío");
        int min = heap[1];
        exch(1, n--);
        sink(1);
        posicionEnHeap[min] = -1;   // ya no está
        heap[n + 1] = -1;           // limpieza defensiva
        return min;
    }

    /**
     * @param i índice consultado.
     * @return la prioridad actual del índice. O(1).
     * @throws IllegalArgumentException si el índice no está en el heap.
     */
    public double keyOf(int i) {
        validateIndex(i);
        if (!contains(i)) throw new IllegalArgumentException("El índice no está en el heap: " + i);
        return keys[i];
    }

    /**
     * Disminuye la prioridad de un índice ya presente (operación clave para Dijkstra/Prim). O(log n).
     * @param i índice a actualizar.
     * @param key nueva prioridad, que debe ser estrictamente menor a la actual.
     * @throws IllegalArgumentException si el índice no está presente o la nueva clave no es menor.
     */
    public void decreaseKey(int i, double key) {
        validateIndex(i);
        if (!contains(i)) throw new IllegalArgumentException("El índice no está en el heap: " + i);
        if (key >= keys[i]) {
            throw new IllegalArgumentException(
                "decreaseKey requiere una clave estrictamente menor (actual=" + keys[i] + ", nueva=" + key + ")");
        }
        keys[i] = key;
        swim(posicionEnHeap[i]);
    }

    /**
     * Conveniencia para Dijkstra/Prim: inserta el índice si no existe, o le hace
     * {@code decreaseKey} si la nueva clave mejora la actual. Si la nueva clave no
     * mejora, no hace nada.
     * @param i índice a relajar.
     * @param key nueva prioridad candidata.
     */
    public void insertOrDecrease(int i, double key) {
        validateIndex(i);
        if (!contains(i)) insert(i, key);
        else if (key < keys[i]) decreaseKey(i, key);
    }

    // ---- mecánica del heap ----

    private void swim(int k) {
        while (k > 1 && greater(k / 2, k)) {
            exch(k / 2, k);
            k = k / 2;
        }
    }

    private void sink(int k) {
        while (2 * k <= n) {
            int j = 2 * k;
            if (j < n && greater(j, j + 1)) j++;     // hijo menor
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }

    private boolean greater(int a, int b) {
        return keys[heap[a]] > keys[heap[b]];
    }

    private void exch(int a, int b) {
        int tmp = heap[a];
        heap[a] = heap[b];
        heap[b] = tmp;
        posicionEnHeap[heap[a]] = a;
        posicionEnHeap[heap[b]] = b;
    }

    private void validateIndex(int i) {
        if (i < 0 || i >= maxN) {
            throw new IndexOutOfBoundsException("Índice " + i + " fuera de [0," + maxN + ")");
        }
    }
}
