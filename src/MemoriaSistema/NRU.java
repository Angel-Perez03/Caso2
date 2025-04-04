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
        //Verificamos si hay un marco libre
        if (memoriaPrincipal.hasFreeFrame()) {
            int frameNumber = memoriaPrincipal.allocateFrame(pageNumber);
            manejadorPaginas.addPage(pageNumber, frameNumber, true, (action == 'W'));
        } else {
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

            //Buscar la primera clase no vacía
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

            //Si la víctima está modificada, la escribimos al swap
            ManejadorPaginas.PaginaEntry victEntry = manejadorPaginas.getEntry(victimPage);
            if (victEntry.isModified()) {
                memoriaVirtual.writePage(victimPage);
            }

            //Liberamos el marco de la víctima y quitamos su entrada
            memoriaPrincipal.deallocateFrame(victimPage);
            manejadorPaginas.removePage(victimPage);

            //Cargamos la nueva página
            int newFrame = memoriaPrincipal.allocateFrame(pageNumber);
            manejadorPaginas.addPage(pageNumber, newFrame, true, (action == 'W'));
        }
    }
}
