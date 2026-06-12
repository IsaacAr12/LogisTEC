import ds.Queue;
import ds.Stack;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de {@link Queue} (FIFO) y {@link Stack} (LIFO). */
public class QueueStackTest {

    @Test
    void colaRespetaFifo() {
        Queue<String> q = new Queue<>();
        assertTrue(q.isEmpty());
        q.enqueue("a");
        q.enqueue("b");
        q.enqueue("c");
        assertEquals(3, q.size());
        assertEquals("a", q.peek());
        assertEquals("a", q.dequeue());
        assertEquals("b", q.dequeue());
        assertEquals("c", q.dequeue());
        assertTrue(q.isEmpty());
    }

    @Test
    void colaVaciaLanzaExcepcion() {
        Queue<Integer> q = new Queue<>();
        assertThrows(IllegalStateException.class, q::dequeue);
        assertThrows(IllegalStateException.class, q::peek);
    }

    @Test
    void pilaRespetaLifo() {
        Stack<Integer> s = new Stack<>();
        assertTrue(s.isEmpty());
        s.push(1);
        s.push(2);
        s.push(3);
        assertEquals(3, s.size());
        assertEquals(3, s.peek());
        assertEquals(3, s.pop());
        assertEquals(2, s.pop());
        assertEquals(1, s.pop());
        assertTrue(s.isEmpty());
    }

    @Test
    void pilaVaciaLanzaExcepcion() {
        Stack<Integer> s = new Stack<>();
        assertThrows(IllegalStateException.class, s::pop);
        assertThrows(IllegalStateException.class, s::peek);
    }
}
