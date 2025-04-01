package MemoriaSistema.Hilos;

import MemoriaSistema.Kernel;

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
                Thread.sleep(1); // dormir más tiempo para no consumir CPU en vano
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stopRunning() {
        running = false;
    }
}