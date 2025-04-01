package MemoriaSistema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Algoritmo de reemplazo NRU (No Recently Used).
 * No realiza el reset de bits por tiempo:
 * Se limita a clasificar y escoger víctima cuando no hay marcos libres.
 */
public class NRU {

    private MemoriaPrincipal memoriaPrincipal;
    private ManejadorPaginas manejadorPaginas;
    private MemoriaVirtual memoriaVirtual;

    public NRU(MemoriaPrincipal memoriaPrincipal,
               ManejadorPaginas manejadorPaginas,
               MemoriaVirtual memoriaVirtual) {
        this.memoriaPrincipal = memoriaPrincipal;
        this.manejadorPaginas = manejadorPaginas;
        this.memoriaVirtual = memoriaVirtual;
    }

    /**
     * Carga la página en memoria principal:
     * - Si hay marco libre, la asigna directo.
     * - Si NO hay marco libre, elige víctima según las 4 clases NRU.
     */
    public void loadPage(int pageNumber, char action) {
        // 1) Verificamos si hay un marco libre
        if (memoriaPrincipal.hasFreeFrame()) {
            int frameNumber = memoriaPrincipal.allocateFrame(pageNumber);
            manejadorPaginas.addPage(pageNumber, frameNumber, true, (action == 'W'));
        } else {
            // 2) Clasificar páginas según (Referenced, Modified)
            // Clase 0: R=0, M=0
            // Clase 1: R=0, M=1
            // Clase 2: R=1, M=0
            // Clase 3: R=1, M=1
            List<Integer>[] clases = new List[4];
            for (int i = 0; i < 4; i++) {
                clases[i] = new ArrayList<>();
            }

            for (Map.Entry<Integer, ManejadorPaginas.PaginaEntry> entry : manejadorPaginas.getEntries().entrySet()) {
                boolean r = entry.getValue().isReferenced();
                boolean m = entry.getValue().isModified();
                int clase = (r ? 2 : 0) + (m ? 1 : 0);
                clases[clase].add(entry.getKey());
            }

            // 3) Buscar la primera clase no vacía
            int victimPage = -1;
            for (int i = 0; i < 4; i++) {
                if (!clases[i].isEmpty()) {
                    victimPage = clases[i].get(0); // la primera víctima
                    break;
                }
            }

            if (victimPage == -1) {
                throw new IllegalStateException("No se encontró víctima. Revisa la lógica NRU.");
            }

            // 4) Si la víctima está modificada, la escribimos al swap
            ManejadorPaginas.PaginaEntry victEntry = manejadorPaginas.getEntry(victimPage);
            if (victEntry.isModified()) {
                memoriaVirtual.writePage(victimPage);
            }

            // 5) Liberamos el marco de la víctima y quitamos su entrada
            memoriaPrincipal.deallocateFrame(victimPage);
            manejadorPaginas.removePage(victimPage);

            // 6) Cargamos la nueva página
            int newFrame = memoriaPrincipal.allocateFrame(pageNumber);
            manejadorPaginas.addPage(pageNumber, newFrame, true, (action == 'W'));
        }
    }
}
