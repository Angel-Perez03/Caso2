import MemoriaSistema.Kernel;
import MemoriaSistema.Hilos.Procesador;
import java.util.Scanner;
import MemoriaSistema.Hilos.Monitor;
public class Opcion2 {

    public static void ejecutar(Scanner sc) {
        System.out.print("Ingrese el número de marcos: ");
        int numFrames;
        try {
            numFrames = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Número inválido. Operación cancelada.");
            return;
        }

        System.out.print("Ingrese el nombre del archivo de referencias: ");
        String refFile = sc.nextLine().trim();

        // Creamos el Kernel
        Kernel kernel = new Kernel(numFrames);

        // Creamos el procesador e hilo
        Procesador procesador = new Procesador(refFile, kernel);
        Thread tProcesador = new Thread(procesador);

        // Creamos el monitor e hilo
        Monitor monitor = new Monitor(kernel);
        Thread tMonitor = new Thread(monitor);

        // Arrancamos ambos hilos
        tProcesador.start();
        tMonitor.start();

        // Esperamos a que el procesador termine
        try {
            tProcesador.join();
        } catch (InterruptedException e) {
            System.out.println("Interrupción en el hilo del Procesador.");
            Thread.currentThread().interrupt();
        }

        // Detenemos el monitor
        monitor.stopRunning();
        try {
            tMonitor.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Mostramos resultados
        System.out.println("\n--- Resultados de la simulación ---");
        System.out.println("Total referencias procesadas: " + kernel.getTotalReferences());
        System.out.println("Hits: " + kernel.getHits());
        System.out.println("Fallas de página (misses): " + kernel.getMisses());
        double porcentajeError = kernel.getTotalReferences() > 0 ?
            ((double) kernel.getMisses() / kernel.getTotalReferences()) * 100 : 0;
            System.out.printf("Porcentaje de error: %.2f%%\n", porcentajeError);
    }
}
