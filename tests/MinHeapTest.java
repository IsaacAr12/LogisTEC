import ds.MinHeap;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de {@link MinHeap} (cola de prioridad indexada / heap binario). */
public class MinHeapTest {

    @Test
    void extraeEnOrdenDePrioridadCreciente() {
        MinHeap h = new MinHeap(5);
        h.insert(0, 5.0);
        h.insert(1, 3.0);
        h.insert(2, 9.0);
        h.insert(3, 1.0);
        h.insert(4, 7.0);
        assertEquals(3, h.delMin());
        assertEquals(1, h.delMin());
        assertEquals(0, h.delMin());
        assertEquals(4, h.delMin());
        assertEquals(2, h.delMin());
        assertTrue(h.isEmpty());
    }

    @Test
    void containsYSizeSonConsistentes() {
        MinHeap h = new MinHeap(4);
        assertTrue(h.isEmpty());
        h.insert(2, 1.0);
        assertTrue(h.contains(2));
        assertFalse(h.contains(0));
        assertEquals(1, h.size());
    }

    @Test
    void decreaseKeyReordenaElHeap() {
        MinHeap h = new MinHeap(3);
        h.insert(0, 5.0);
        h.insert(1, 4.0);
        h.insert(2, 9.0);
        assertEquals(1, h.minIndex());
        h.decreaseKey(2, 0.5);
        assertEquals(2, h.minIndex());
        assertEquals(0.5, h.minKey());
    }

    @Test
    void decreaseKeyConClaveMayorOIgualLanzaExcepcion() {
        MinHeap h = new MinHeap(2);
        h.insert(0, 3.0);
        assertThrows(IllegalArgumentException.class, () -> h.decreaseKey(0, 3.0));
        assertThrows(IllegalArgumentException.class, () -> h.decreaseKey(0, 4.0));
    }

    @Test
    void insertarIndiceRepetidoLanzaExcepcion() {
        MinHeap h = new MinHeap(2);
        h.insert(1, 2.0);
        assertThrows(IllegalArgumentException.class, () -> h.insert(1, 1.0));
    }

    @Test
    void insertOrDecreaseInsertaYLuegoRelaja() {
        MinHeap h = new MinHeap(2);
        h.insertOrDecrease(0, 4.0);
        assertEquals(4.0, h.keyOf(0));
        h.insertOrDecrease(0, 2.0);
        assertEquals(2.0, h.keyOf(0));
        h.insertOrDecrease(0, 9.0);
        assertEquals(2.0, h.keyOf(0));
    }

    @Test
    void delMinEnHeapVacioLanzaExcepcion() {
        MinHeap h = new MinHeap(1);
        assertThrows(IllegalStateException.class, h::delMin);
        assertThrows(IllegalStateException.class, h::minIndex);
    }

    @Test
    void indiceFueraDeRangoLanzaExcepcion() {
        MinHeap h = new MinHeap(3);
        assertThrows(IndexOutOfBoundsException.class, () -> h.insert(3, 1.0));
        assertThrows(IndexOutOfBoundsException.class, () -> h.contains(-1));
    }
}
