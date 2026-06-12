import ds.UnionFind;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias de {@link UnionFind} (conjunto disjunto). */
public class UnionFindTest {

    @Test
    void inicialmenteCadaElementoEsSuPropioConjunto() {
        UnionFind uf = new UnionFind(5);
        assertEquals(5, uf.count());
        assertFalse(uf.connected(0, 1));
    }

    @Test
    void unionConectaElementos() {
        UnionFind uf = new UnionFind(5);
        assertTrue(uf.union(0, 1));
        assertTrue(uf.connected(0, 1));
        assertEquals(4, uf.count());
    }

    @Test
    void unionTransitivaFuncionaConCadenas() {
        UnionFind uf = new UnionFind(5);
        uf.union(0, 1);
        uf.union(1, 2);
        uf.union(2, 3);
        assertTrue(uf.connected(0, 3));
        assertFalse(uf.connected(0, 4));
        assertEquals(2, uf.count());
    }

    @Test
    void unionDeElementosYaConectadosRetornaFalse() {
        UnionFind uf = new UnionFind(3);
        assertTrue(uf.union(0, 1));
        assertFalse(uf.union(1, 0));
        assertEquals(2, uf.count());
    }

    @Test
    void elementoFueraDeRangoLanzaExcepcion() {
        UnionFind uf = new UnionFind(3);
        assertThrows(IndexOutOfBoundsException.class, () -> uf.find(3));
        assertThrows(IndexOutOfBoundsException.class, () -> uf.union(0, 9));
    }

    @Test
    void escenarioTipoKruskal() {
        UnionFind uf = new UnionFind(6);
        int aristasAceptadas = 0;
        int[][] aristasEnOrden = {{0, 1}, {1, 2}, {0, 2}, {3, 4}, {2, 3}, {4, 5}, {1, 5}};
        for (int[] e : aristasEnOrden) {
            if (uf.union(e[0], e[1])) aristasAceptadas++;
        }
        assertEquals(5, aristasAceptadas);
        assertEquals(1, uf.count());
    }
}
