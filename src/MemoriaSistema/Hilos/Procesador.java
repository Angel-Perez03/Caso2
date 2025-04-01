package MemoriaSistema.Hilos;

import MemoriaSistema.Kernel;
import MemoriaSistema.Almacenamiento;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Hilo que lee el archivo de referencias y procesa cada referencia.
 * Cada 10000 referencias, duerme 1ms (simulando que corre cada ms).
 */
public class Procesador implements Runnable {

    private String filename;
    private Kernel kernel;
    // Bloque de 10000 referencias
    private static final int BLOCK_SIZE = 1000;

    public Procesador(String filename, Kernel kernel) {
        this.filename = filename;
        this.kernel = kernel;
    }

    @Override
    public void run() {
        try (BufferedReader br = Almacenamiento.openFile(filename)) {
            // Saltar cabecera TP, NF, NC, NR, NP
            for (int i = 0; i < 5; i++) {
                br.readLine();
            }

            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) {
                    continue;
                }
                int pageNumber = Integer.parseInt(parts[1].trim());
                char action = parts[3].trim().charAt(0);

                // Procesa referencia
                kernel.processReference(pageNumber, action);
                count++;

                // Espera 1ms cada 10000 referencias
                if (count % BLOCK_SIZE == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
