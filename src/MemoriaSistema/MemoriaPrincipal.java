package MemoriaSistema;

import java.util.*;

/**
 * Simula la memoria fisica. Maneja la asignación y liberacion de marcos.
 */
public class MemoriaPrincipal {

    private int capacity;             // Número maximo de marcos disponibles
    private Queue<Integer> freeFrames;    // Lista/cola de marcos libres
    private Map<Integer, Integer> pageToFrame; // Asocia pageNumber -> frameNumber

    public MemoriaPrincipal(int capacity) {
        this.capacity = capacity;
        this.freeFrames = new LinkedList<>();
        this.pageToFrame = new HashMap<>();
        
        // Inicializa la lista de marcos libres
        for (int i = 0; i < capacity; i++) {
            freeFrames.add(i);
        }
    }

    public boolean hasFreeFrame() {
        return !freeFrames.isEmpty();
    }

    /**
     * Asigna un marco libre a la página dada y retorna el frameNumber asignado.
     * Lanza excepción si no hay marcos libres (no debería pasar si se verifica antes).
     */
    public int allocateFrame(int pageNumber) {
        if (!hasFreeFrame()) {
            throw new IllegalStateException("No hay marcos disponibles, revise la lógica de reemplazo.");
        }
        int frame = freeFrames.poll(); // Toma un marco libre
        pageToFrame.put(pageNumber, frame); 
        return frame;
    }

    /**
     * Libera el marco ocupado por la pagina especificada.
     * Si la página no esta en pageToFrame, no hace nada.
     */
    public void deallocateFrame(int pageNumber) {
        if (pageToFrame.containsKey(pageNumber)) {
            int frame = pageToFrame.remove(pageNumber);
            freeFrames.add(frame);
        }
    }

    /**
     * Retorna true si la pagina dada esta cargada en la memoria principal.
     */
    public boolean isPageInMemory(int pageNumber) {
        return pageToFrame.containsKey(pageNumber);
    }

    public int getCapacity() {
        return capacity;
    }

    /**
     * Retorna el frame donde se encuentra la pagina, o -1 si no esta en memoria.
     */
    public int getFrameNumber(int pageNumber) {
        return pageToFrame.getOrDefault(pageNumber, -1);
    }
}
