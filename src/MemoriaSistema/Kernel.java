package MemoriaSistema;

import MemoriaSistema.Hilos.Procesador;
import MemoriaSistema.Hilos.Monitor;
import java.util.Scanner;

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
    
    public Kernel(int numFrames) {
        this.memoriaPrincipal = new MemoriaPrincipal(numFrames);
        this.memoriaVirtual = new MemoriaVirtual();
        this.manejadorPaginas = new ManejadorPaginas();
        this.nru = new NRU(memoriaPrincipal, manejadorPaginas, memoriaVirtual);
    }
    
    // Método sincronizado para procesar cada referencia (número de página y acción)
    public synchronized void processReference(int pageNumber, char action) {
        totalReferences++;
        if (manejadorPaginas.isPageInMemory(pageNumber)) {
            // Hit: la página ya se encuentra en la memoria principal
            hits++;
            manejadorPaginas.setReferenced(pageNumber, true);
            if (action == 'W') {
                manejadorPaginas.setModified(pageNumber, true);
            }
        } else {
            // Miss: la página no está en la memoria principal
            misses++;
            nru.loadPage(pageNumber, action);
        }
    }
    
    // Método para resetear las banderas de referencia (llamado por Monitor)
    public synchronized void resetReferencedBits() {
        manejadorPaginas.resetAllReferenced();
    }
    
    // Getters para las métricas
    public synchronized int getTotalReferences() {
        return totalReferences;
    }
    public synchronized int getHits() {
        return hits;
    }
    public synchronized int getMisses() {
        return misses;
    }
    
    // Método main: configura la simulación, inicia los hilos y muestra los resultados
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese el número de marcos: ");
        int numFrames = sc.nextInt();
        sc.nextLine(); // Limpiar buffer
        System.out.print("Ingrese el nombre del archivo de referencias: ");
        String refFile = sc.nextLine();
        sc.close();
        
        Kernel kernel = new Kernel(numFrames);
        
        // Crear hilos Procesador y Monitor
        Procesador procesador = new Procesador(refFile, kernel);
        Monitor monitor = new Monitor(kernel);
        
        Thread tProcesador = new Thread(procesador);
        Thread tMonitor = new Thread(monitor);
        
        tMonitor.start();
        tProcesador.start();
        
        try {
            tProcesador.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        monitor.stopRunning();
        try {
            tMonitor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Mostrar resultados finales
        System.out.println("Total referencias procesadas: " + kernel.getTotalReferences());
        System.out.println("Hits: " + kernel.getHits());
        System.out.println("Fallas de página (misses): " + kernel.getMisses());
    }
}
