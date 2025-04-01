package MemoriaSistema.Hilos;

import MemoriaSistema.Kernel;

public class Monitor extends Thread {

    private Kernel kernel;
    private volatile boolean running = true;

    public Monitor(Kernel kernel) {
        this.kernel = kernel;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1); // Se activa cada ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // Llamamos al Kernel para que resetee los bits de referencia
            kernel.resetReferencedBits();
        }
    }

    public void stopRunning() {
        running = false;
    }
}
