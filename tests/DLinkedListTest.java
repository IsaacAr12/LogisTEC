import ds.DLinkedList;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de {@link DLinkedList}. */
public class DLinkedListTest {

    @Test
    void nuevaListaEstaVacia() {
        DLinkedList<Integer> list = new DLinkedList<>();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void addLastYAddFirstMantienenElOrden() {
        DLinkedList<Integer> list = new DLinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.addFirst(0);
        assertEquals(4, list.size());
        assertEquals(0, list.get(0));
        assertEquals(1, list.get(1));
        assertEquals(3, list.get(3));
    }

    @Test
    void getConIndiceInvalidoLanzaExcepcion() {
        DLinkedList<Integer> list = new DLinkedList<>();
        list.add(10);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    }

    @Test
    void removerExtremosEsCorrecto() {
        DLinkedList<String> list = new DLinkedList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        assertEquals("a", list.removeFirst());
        assertEquals("c", list.removeLast());
        assertEquals(1, list.size());
        assertEquals("b", list.getFirst());
    }

    @Test
    void removerPorValorEliminaPrimeraAparicion() {
        DLinkedList<Integer> list = new DLinkedList<>();
        list.add(5);
        list.add(7);
        list.add(5);
        assertTrue(list.remove(Integer.valueOf(5)));
        assertEquals(2, list.size());
        assertEquals(7, list.get(0));
        assertFalse(list.remove(Integer.valueOf(99)));
    }

    @Test
    void indexOfYContains() {
        DLinkedList<String> list = new DLinkedList<>();
        list.add("x");
        list.add("y");
        assertEquals(1, list.indexOf("y"));
        assertEquals(-1, list.indexOf("z"));
        assertTrue(list.contains("x"));
        assertFalse(list.contains("z"));
    }

    @Test
    void iteradorRecorreEnOrden() {
        DLinkedList<Integer> list = new DLinkedList<>();
        for (int i = 1; i <= 4; i++) list.add(i);
        int suma = 0;
        for (int x : list) suma += x;
        assertEquals(10, suma);
    }

    @Test
    void soportaValoresNulos() {
        DLinkedList<String> list = new DLinkedList<>();
        list.add(null);
        list.add("a");
        assertTrue(list.contains(null));
        assertEquals(0, list.indexOf(null));
    }

    @Test
    void removerEnListaVaciaLanzaExcepcion() {
        DLinkedList<Integer> list = new DLinkedList<>();
        assertThrows(IllegalStateException.class, list::removeFirst);
        assertThrows(IllegalStateException.class, list::removeLast);
    }
}
