package MemoriaSistema;

import MemoriaSistema.Hilos.Procesador;
import MemoriaSistema.Hilos.Monitor;
import java.util.Scanner;

/**
 * Núcleo del sistema. Mantiene contadores (hits, misses, totalReferences).
 * Administra la creación de MemoriaPrincipal, ManejadorPaginas y NRU.
 */
public class Kernel {

    // Contadores globales
    private int totalReferences = 0;
    private int hits = 0;
    private int misses = 0;

    // Componentes
    private MemoriaPrincipal memoriaPrincipal;
    private MemoriaVirtual memoriaVirtual;
    private ManejadorPaginas manejadorPaginas;
    private NRU nru;

    public Kernel(int numFrames) {
        System.out.println("[INFO] Kernel creado con " + numFrames + " marcos.");
        this.memoriaPrincipal = new MemoriaPrincipal(numFrames);
        this.memoriaVirtual = new MemoriaVirtual();
        this.manejadorPaginas = new ManejadorPaginas();
        this.nru = new NRU(memoriaPrincipal, manejadorPaginas, memoriaVirtual);
    }

    // Procesa cada referencia (pageNumber + acción)
    public synchronized void processReference(int pageNumber, char action) {
        totalReferences++;
        if (manejadorPaginas.isPageInMemory(pageNumber)) {
            // HIT
            hits++;
            manejadorPaginas.setReferenced(pageNumber, true);
            if (action == 'W') {
                manejadorPaginas.setModified(pageNumber, true);
            }
        } else {
            // MISS
            misses++;
            nru.loadPage(pageNumber, action);
        }
    }

    // Llamado por el Monitor cada ms para resetear bits de referencia
    public synchronized void resetReferencedBits() {
        manejadorPaginas.resetAllReferenced();
    }

    // Getters
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
     * Método estático para ejecutar la opción 2, con hilos concurrentes.
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese el número de marcos: ");
        int numFrames = sc.nextInt();
        sc.nextLine(); // limpiar buffer
        System.out.print("Ingrese el nombre del archivo de referencias: ");
        String refFile = sc.nextLine();
        sc.close();

        Kernel kernel = new Kernel(numFrames);

        // Hilo procesador
        Procesador procesador = new Procesador(refFile, kernel);
        Thread tProcesador = new Thread(procesador);

        // Hilo monitor
        Monitor monitor = new Monitor(kernel);
        Thread tMonitor = new Thread(monitor);

        // Iniciar ambos hilos
        tProcesador.start();
        tMonitor.start();

        // Esperar a que el procesador termine
        try {
            tProcesador.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Detener el monitor
        monitor.stopRunning();
        try {
            tMonitor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Mostrar resultados
        System.out.println("\n--- Resultados de la simulación ---");
        System.out.println("Total referencias procesadas: " + kernel.getTotalReferences());
        System.out.println("Hits: " + kernel.getHits());
        System.out.println("Fallas de página (misses): " + kernel.getMisses());
    }
}
