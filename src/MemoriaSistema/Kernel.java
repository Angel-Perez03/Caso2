package MemoriaSistema;


/**
 * Mantiene contadores (hits, misses, totalReferences) y
 * administra la creación de MemoriaPrincipal, ManejadorPaginas y NRU.
 */
public class Kernel {

    // Contadores globales
    private int totalReferences = 0;
    private int hits = 0;
    private int misses = 0;

    // Componentes del sistema
    private MemoriaPrincipal memoriaPrincipal;
    private MemoriaVirtual memoriaVirtual;
    private ManejadorPaginas manejadorPaginas;
    private NRU nru;

    /**
     * Crea el Kernel con el número de marcos especificado.
     * Número de marcos de la memoria principal.
     */
    public Kernel(int numFrames) {
        System.out.println("[INFO] Kernel creado con " + numFrames + " marcos.");
        this.memoriaPrincipal = new MemoriaPrincipal(numFrames);
        this.memoriaVirtual = new MemoriaVirtual();
        this.manejadorPaginas = new ManejadorPaginas();
        this.nru = new NRU(memoriaPrincipal, manejadorPaginas, memoriaVirtual);
    }

    /**
     * Procesa cada referencia (número de página y acción). Si la página ya está en
     * memoria se considera HIT y se actualiza el bit R (y M en caso de escritura).
     * Si no, se produce un MISS y se carga la página mediante NRU.
     */
    public synchronized void processReference(int pageNumber, char action) {
        totalReferences++;
        if (manejadorPaginas.isPageInMemory(pageNumber)) {
            // HIT: la página ya está en memoria
            hits++;
            manejadorPaginas.setReferenced(pageNumber, true);
            if (action == 'W') {
                manejadorPaginas.setModified(pageNumber, true);
            }
        } else {
            // MISS: la página no está en memoria y se debe cargar
            misses++;
            nru.loadPage(pageNumber, action);
        }
    }

    /**
     * Resetea el bit de referencia de todas las páginas, para ser llamado por el
     * Monitor cada 1 ms.
     */
    public synchronized void resetReferencedBits() {
        manejadorPaginas.resetAllReferenced();
    }

    // Getters para los contadores
    public synchronized int getTotalReferences() {
        return totalReferences;
    }

    public synchronized int getHits() {
        return hits;
    }

    public synchronized int getMisses() {
        return misses;
    }
    
    /**
     * Retorna el porcentaje de error calculado como la proporción de misses sobre totalReferences multiplicado por 100.
     */
    public synchronized double getPorcentajeError() {
        return totalReferences > 0 ? ((double) misses / totalReferences) * 100 : 0;
    }
}
