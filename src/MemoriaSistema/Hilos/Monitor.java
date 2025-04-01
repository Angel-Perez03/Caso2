package MemoriaSistema.Hilos;

import MemoriaSistema.Kernel;

/**
 * Hilo que corre cada ms y resetea los bits de referencia,
 * simulando la acción periódica del SO (Tanenbaum).
 */
public class Monitor implements Runnable {

    private Kernel kernel;
    private volatile boolean running = true;

    public Monitor(Kernel kernel) {
        this.kernel = kernel;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1); // se activa cada ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // Llamamos al Kernel para que resetee bits de referencia
            kernel.resetReferencedBits();
        }
    }

    public void stopRunning() {
        running = false;
    }
}
