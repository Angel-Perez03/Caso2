package MemoriaSistema;

import java.util.HashMap;
import java.util.Map;

/**
 * Se encarga de llevar la tabla de paginas del proceso.
 * Para cada pagina, guarda: si esta presente, referenciada, modificada, y el marco.
 */
public class ManejadorPaginas {

    private Map<Integer, PaginaEntry> tabla;

    public ManejadorPaginas() {
        tabla = new HashMap<>();
    }

    // Retorna true si la pagina existe en la tabla y esta presente
    public boolean isPageInMemory(int pageNumber) {
        PaginaEntry entry = tabla.get(pageNumber);
        return (entry != null && entry.isPresent());
    }

    // Agrega/actualiza un registro de pagina en la tabla
    public void addPage(int pageNumber, int frameNumber, boolean referenced, boolean modified) {
        PaginaEntry entry = new PaginaEntry(pageNumber, frameNumber, true, referenced, modified);
        tabla.put(pageNumber, entry);
    }

    // Quita la pagina completamente de la tabla
    public void removePage(int pageNumber) {
        tabla.remove(pageNumber);
    }

    // Setea el bit de referencia
    public void setReferenced(int pageNumber, boolean referenced) {
        PaginaEntry entry = tabla.get(pageNumber);
        if (entry != null) {
            entry.setReferenced(referenced);
        }
    }

    // Setea el bit de modificado
    public void setModified(int pageNumber, boolean modified) {
        PaginaEntry entry = tabla.get(pageNumber);
        if (entry != null) {
            entry.setModified(modified);
        }
    }

    // Retorna el mapa completo (para NRU)
    public Map<Integer, PaginaEntry> getEntries() {
        return tabla;
    }

    // Retorna la entrada de pagina dada
    public PaginaEntry getEntry(int pageNumber) {
        return tabla.get(pageNumber);
    }

    // Setea a false el bit de referencia de todas las paginas en la tabla
    public void resetAllReferenced() {
        for (PaginaEntry entry : tabla.values()) {
            entry.setReferenced(false);
        }
    }

    // Clase interna para representar la entrada de la tabla
    public static class PaginaEntry {
        private int pageNumber;
        private int frameNumber;
        private boolean present;
        private boolean referenced;
        private boolean modified;

        public PaginaEntry(int pageNumber, int frameNumber,
                           boolean present, boolean referenced, boolean modified) {
            this.pageNumber = pageNumber;
            this.frameNumber = frameNumber;
            this.present = present;
            this.referenced = referenced;
            this.modified = modified;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public int getFrameNumber() {
            return frameNumber;
        }

        public boolean isPresent() {
            return present;
        }

        public boolean isReferenced() {
            return referenced;
        }

        public boolean isModified() {
            return modified;
        }

        public void setReferenced(boolean referenced) {
            this.referenced = referenced;
        }

        public void setModified(boolean modified) {
            this.modified = modified;
        }
    }
}
