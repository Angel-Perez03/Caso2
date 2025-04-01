package MemoriaSistema;

import MemoriaSistema.Hilos.Procesador;
import MemoriaSistema.Hilos.Monitor;
import java.util.Scanner;

/**
 * Núcleo del sistema. Mantiene contadores (hits, misses, totalReferences) y
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
     * @param numFrames Número de marcos de la memoria principal.
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
     * 
     * @param pageNumber Número de página referenciada.
     * @param action     Acción: 'R' para lectura o 'W' para escritura.
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
     * Método principal para ejecutar la opción 2 (simulación de paginación y cálculo
     * de métricas) con hilos concurrentes.
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese el número de marcos: ");
        int numFrames = sc.nextInt();
        sc.nextLine(); // Limpiar el buffer
        System.out.print("Ingrese el nombre del archivo de referencias: ");
        String refFile = sc.nextLine();

        Kernel kernel = new Kernel(numFrames);

        // Se instancian y se inician los hilos Procesador y Monitor (ambos extienden Thread)
        Procesador procesador = new Procesador(refFile, kernel);
        Monitor monitor = new Monitor(kernel);

        procesador.start();
        monitor.start();

        // Se espera a que el Procesador termine de procesar todas las referencias
        try {
            procesador.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Se detiene el Monitor y se espera a que finalice
        monitor.stopRunning();
        try {
            monitor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Se muestran los resultados de la simulación
        System.out.println("\n--- Resultados de la simulación ---");
        System.out.println("Total referencias procesadas: " + kernel.getTotalReferences());
        System.out.println("Hits: " + kernel.getHits());
        System.out.println("Fallas de página (misses): " + kernel.getMisses());

        sc.close();
    }
}
