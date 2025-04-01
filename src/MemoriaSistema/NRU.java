package MemoriaSistema;

import java.util.*;

public class NRU {
    private MemoriaPrincipal memoriaPrincipal;
    private ManejadorPaginas manejadorPaginas;
    private MemoriaVirtual memoriaVirtual;
    private long lastResetTime;
    private static final long RESET_INTERVAL_MS = 1; 

    public NRU(MemoriaPrincipal memoriaPrincipal, ManejadorPaginas manejadorPaginas, MemoriaVirtual memoriaVirtual) {
        this.memoriaPrincipal = memoriaPrincipal;
        this.manejadorPaginas = manejadorPaginas;
        this.memoriaVirtual = memoriaVirtual;
        this.lastResetTime = System.currentTimeMillis();
    }

    public void loadPage(int pageNumber, char action) {
        // Reset periodico de bits de referencia cada RESET_INTERVAL_MS
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastResetTime >= RESET_INTERVAL_MS) {
            manejadorPaginas.resetAllReferenced();
            lastResetTime = currentTime;
        }

        if (memoriaPrincipal.hasFreeFrame()) {
            int frameNumber = memoriaPrincipal.allocateFrame(pageNumber);
            manejadorPaginas.addPage(pageNumber, frameNumber, true, action == 'W');
        } else {
            // Clasificación NRU: 4 clases
            List<Integer>[] clases = new List[4];
            for (int i = 0; i < 4; i++) clases[i] = new ArrayList<>();

            for (Map.Entry<Integer, ManejadorPaginas.PaginaEntry> entry : manejadorPaginas.getEntries().entrySet()) {
                boolean r = entry.getValue().isReferenced();
                boolean m = entry.getValue().isModified();

                int clase = (r ? 2 : 0) + (m ? 1 : 0);
                clases[clase].add(entry.getKey());
            }

            // Seleccionar la primera victima de la clase más baja disponible
            int victimPage = -1;
            for (int i = 0; i < 4; i++) {
                if (!clases[i].isEmpty()) {
                    victimPage = clases[i].get(0);
                    break;
                }
            }

            if (victimPage == -1) {
                throw new IllegalStateException("No se encontró página para reemplazo (esto no debería pasar)." );
            }

            ManejadorPaginas.PaginaEntry victimEntry = manejadorPaginas.getEntry(victimPage);
            if (victimEntry.isModified()) {
                memoriaVirtual.writePage(victimPage);
            }

            memoriaPrincipal.deallocateFrame(victimPage);
            manejadorPaginas.removePage(victimPage);

            int frameNumber = memoriaPrincipal.allocateFrame(pageNumber);
            manejadorPaginas.addPage(pageNumber, frameNumber, true, action == 'W');
        }
    }
}
