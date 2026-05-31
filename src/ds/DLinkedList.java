package ds;

import java.util.Iterator;

/**
 * Lista doblemente enlazada genérica implementada por el equipo.
 *
 * <p>Es la estructura base del proyecto: sobre ella se construyen {@link Queue} y
 * {@link Stack}, y la usan las listas de adyacencia del grafo y las listas de aristas.
 * No utiliza ninguna colección de {@code java.util}; la única dependencia de ese paquete
 * es la interfaz {@link java.util.Iterator}, que es solo el protocolo de iteración del
 * lenguaje (no una colección prefabricada) y permite recorrer la lista con {@code for-each}.</p>
 *
 * <p>Complejidades: {@code addFirst}/{@code addLast}/{@code removeFirst}/{@code removeLast}
 * en O(1); {@code get}/{@code remove(int)}/{@code indexOf}/{@code contains} en O(n).</p>
 *
 * @param <T> tipo de los elementos almacenados.
 */
public class DLinkedList<T> implements Iterable<T> {

    /** Nodo interno de la lista doblemente enlazada. */
    private static final class Node<T> {
        T value;
        Node<T> prev;
        Node<T> next;
        Node(T value) { this.value = value; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    /** Crea una lista vacía. */
    public DLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /** @return cantidad de elementos en la lista. */
    public int size() { return size; }

    /** @return {@code true} si la lista no tiene elementos. */
    public boolean isEmpty() { return size == 0; }

    /**
     * Inserta un elemento al final de la lista. O(1).
     * @param value elemento a agregar (se permite {@code null}).
     */
    public void addLast(T value) {
        Node<T> node = new Node<>(value);
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        size++;
    }

    /** Alias de {@link #addLast(Object)}, equivalente a "append". O(1). */
    public void add(T value) { addLast(value); }

    /**
     * Inserta un elemento al inicio de la lista. O(1).
     * @param value elemento a agregar.
     */
    public void addFirst(T value) {
        Node<T> node = new Node<>(value);
        if (head == null) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        size++;
    }

    /**
     * @return el primer elemento.
     * @throws IllegalStateException si la lista está vacía.
     */
    public T getFirst() {
        if (head == null) throw new IllegalStateException("La lista está vacía");
        return head.value;
    }

    /**
     * @return el último elemento.
     * @throws IllegalStateException si la lista está vacía.
     */
    public T getLast() {
        if (tail == null) throw new IllegalStateException("La lista está vacía");
        return tail.value;
    }

    /**
     * Elimina y retorna el primer elemento. O(1).
     * @return el elemento removido.
     * @throws IllegalStateException si la lista está vacía.
     */
    public T removeFirst() {
        if (head == null) throw new IllegalStateException("La lista está vacía");
        T value = head.value;
        head = head.next;
        if (head == null) tail = null;
        else head.prev = null;
        size--;
        return value;
    }

    /**
     * Elimina y retorna el último elemento. O(1).
     * @return el elemento removido.
     * @throws IllegalStateException si la lista está vacía.
     */
    public T removeLast() {
        if (tail == null) throw new IllegalStateException("La lista está vacía");
        T value = tail.value;
        tail = tail.prev;
        if (tail == null) head = null;
        else tail.next = null;
        size--;
        return value;
    }

    /**
     * Retorna el elemento en la posición dada. O(n).
     * @param index índice base 0.
     * @return el elemento en {@code index}.
     * @throws IndexOutOfBoundsException si el índice es inválido.
     */
    public T get(int index) {
        return nodeAt(index).value;
    }

    /**
     * Reemplaza el elemento en la posición dada. O(n).
     * @param index índice base 0.
     * @param value nuevo valor.
     * @throws IndexOutOfBoundsException si el índice es inválido.
     */
    public void set(int index, T value) {
        nodeAt(index).value = value;
    }

    /**
     * Elimina el elemento en la posición dada. O(n).
     * @param index índice base 0.
     * @return el elemento removido.
     * @throws IndexOutOfBoundsException si el índice es inválido.
     */
    public T remove(int index) {
        Node<T> node = nodeAt(index);
        unlink(node);
        return node.value;
    }

    /**
     * Elimina la primera aparición del valor indicado. O(n).
     * @param value valor a eliminar.
     * @return {@code true} si se eliminó algún elemento.
     */
    public boolean remove(T value) {
        for (Node<T> n = head; n != null; n = n.next) {
            if (equalsValue(n.value, value)) {
                unlink(n);
                return true;
            }
        }
        return false;
    }

    /**
     * @param value valor buscado.
     * @return {@code true} si el valor está en la lista. O(n).
     */
    public boolean contains(T value) {
        return indexOf(value) >= 0;
    }

    /**
     * @param value valor buscado.
     * @return el índice de la primera aparición, o -1 si no existe. O(n).
     */
    public int indexOf(T value) {
        int i = 0;
        for (Node<T> n = head; n != null; n = n.next, i++) {
            if (equalsValue(n.value, value)) return i;
        }
        return -1;
    }

    /** Vacía la lista. O(1). */
    public void clear() {
        head = tail = null;
        size = 0;
    }

    /**
     * Copia los elementos a un arreglo nuevo en orden.
     * @param array arreglo destino del tipo deseado; si no alcanza se ignora y se usa su tipo.
     * @return arreglo con los elementos en orden.
     */
    @SuppressWarnings("unchecked")
    public T[] toArray(T[] array) {
        T[] result = array.length >= size
                ? array
                : (T[]) java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), size);
        int i = 0;
        for (Node<T> n = head; n != null; n = n.next) result[i++] = n.value;
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Node<T> current = head;
            @Override public boolean hasNext() { return current != null; }
            @Override public T next() {
                if (current == null) throw new IllegalStateException("No hay más elementos");
                T value = current.value;
                current = current.next;
                return value;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Node<T> n = head; n != null; n = n.next) {
            if (!first) sb.append(", ");
            sb.append(n.value);
            first = false;
        }
        return sb.append(']').toString();
    }

    // ---- helpers privados ----

    private Node<T> nodeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice " + index + " fuera de [0," + size + ")");
        }
        Node<T> n;
        if (index < size / 2) {            // recorre desde la cabeza
            n = head;
            for (int i = 0; i < index; i++) n = n.next;
        } else {                           // recorre desde la cola
            n = tail;
            for (int i = size - 1; i > index; i--) n = n.prev;
        }
        return n;
    }

    private void unlink(Node<T> node) {
        Node<T> p = node.prev;
        Node<T> q = node.next;
        if (p == null) head = q; else p.next = q;
        if (q == null) tail = p; else q.prev = p;
        size--;
    }

    private boolean equalsValue(T a, T b) {
        return (a == null) ? (b == null) : a.equals(b);
    }
}
