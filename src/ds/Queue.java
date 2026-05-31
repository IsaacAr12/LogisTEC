package ds;

/**
 * Cola FIFO (First-In, First-Out) implementada por el equipo sobre {@link DLinkedList}.
 *
 * <p>Es la estructura que usa el recorrido BFS: los vértices se encolan en el orden en que
 * se descubren y se desencolan en ese mismo orden, lo que produce el recorrido por niveles.</p>
 *
 * <p>Todas las operaciones principales son O(1).</p>
 *
 * @param <T> tipo de los elementos.
 */
public class Queue<T> {

    private final DLinkedList<T> items = new DLinkedList<>();

    /**
     * Agrega un elemento al final de la cola. O(1).
     * @param value elemento a encolar.
     */
    public void enqueue(T value) {
        items.addLast(value);
    }

    /**
     * Retira y retorna el elemento al frente de la cola. O(1).
     * @return el elemento más antiguo.
     * @throws IllegalStateException si la cola está vacía.
     */
    public T dequeue() {
        return items.removeFirst();
    }

    /**
     * Observa el elemento al frente sin retirarlo. O(1).
     * @return el elemento más antiguo.
     * @throws IllegalStateException si la cola está vacía.
     */
    public T peek() {
        return items.getFirst();
    }

    /** @return {@code true} si la cola no tiene elementos. */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /** @return cantidad de elementos en la cola. */
    public int size() {
        return items.size();
    }

    @Override
    public String toString() {
        return "Queue(front->back)=" + items;
    }
}
