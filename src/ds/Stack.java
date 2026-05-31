package ds;

/**
 * Pila LIFO (Last-In, First-Out) implementada por el equipo sobre {@link DLinkedList}.
 *
 * <p>La usa el recorrido DFS iterativo (alternativa a la pila de llamadas de la recursión):
 * el último vértice apilado es el primero en procesarse, produciendo el avance en profundidad.</p>
 *
 * <p>Todas las operaciones principales son O(1).</p>
 *
 * @param <T> tipo de los elementos.
 */
public class Stack<T> {

    private final DLinkedList<T> items = new DLinkedList<>();

    /**
     * Apila un elemento. O(1).
     * @param value elemento a colocar en el tope.
     */
    public void push(T value) {
        items.addFirst(value);
    }

    /**
     * Desapila y retorna el elemento del tope. O(1).
     * @return el último elemento agregado.
     * @throws IllegalStateException si la pila está vacía.
     */
    public T pop() {
        return items.removeFirst();
    }

    /**
     * Observa el elemento del tope sin retirarlo. O(1).
     * @return el último elemento agregado.
     * @throws IllegalStateException si la pila está vacía.
     */
    public T peek() {
        return items.getFirst();
    }

    /** @return {@code true} si la pila no tiene elementos. */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /** @return cantidad de elementos en la pila. */
    public int size() {
        return items.size();
    }

    @Override
    public String toString() {
        return "Stack(top->bottom)=" + items;
    }
}
