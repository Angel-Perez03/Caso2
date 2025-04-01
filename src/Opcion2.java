import MemoriaSistema.Kernel;
import MemoriaSistema.Hilos.Procesador;
import java.util.Scanner;

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

        Kernel kernel = new Kernel(numFrames);
        Procesador procesador = new Procesador(refFile, kernel);
        Thread tProcesador = new Thread(procesador);

        tProcesador.start();
        try {
            tProcesador.join();
        } catch (InterruptedException e) {
            System.out.println("Interrupción en el hilo del Procesador.");
            Thread.currentThread().interrupt();
        }

        System.out.println("\n--- Resultados de la simulación ---");
        System.out.println("Total referencias procesadas: " + kernel.getTotalReferences());
        System.out.println("Hits: " + kernel.getHits());
        System.out.println("Fallas de página (misses): " + kernel.getMisses());
    }
}
