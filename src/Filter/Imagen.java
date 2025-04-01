package Filter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Imagen {
    private byte[] header = new byte[54];
    private byte[][][] imagen; // Matriz de píxeles [filas][columnas][RGB]
    private int alto, ancho;   // en píxeles
    private int padding;       // bytes de relleno por fila

    /**
     * Constructor para leer una imagen BMP de 24 bits.
     * @param nombre Nombre del archivo BMP.
     */
    public Imagen(String nombre) {
        try {
            FileInputStream fis = new FileInputStream(nombre);
            fis.read(header);

            // Extraer ancho y alto de la cabecera BMP (Little Endian)
            ancho = ((header[21] & 0xFF) << 24) | ((header[20] & 0xFF) << 16) |
                    ((header[19] & 0xFF) << 8)  | (header[18] & 0xFF);
            alto = ((header[25] & 0xFF) << 24) | ((header[24] & 0xFF) << 16) |
                   ((header[23] & 0xFF) << 8)  | (header[22] & 0xFF);

            System.out.println("Ancho: " + ancho + " px, Alto: " + alto + " px");

            // Inicializar la matriz de imagen
            imagen = new byte[alto][ancho][3];
            int rowSizeSinPadding = ancho * 3;
            padding = (4 - (rowSizeSinPadding % 4)) % 4;

            // Leer los píxeles (almacenados en BGR en el archivo BMP)
            byte[] pixel = new byte[3];
            for (int i = 0; i < alto; i++) {
                for (int j = 0; j < ancho; j++) {
                    fis.read(pixel);
                    // Se almacena en el mismo orden (B, G, R)
                    imagen[i][j][0] = pixel[0];
                    imagen[i][j][1] = pixel[1];
                    imagen[i][j][2] = pixel[2];
                }
                fis.skip(padding);
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor para crear una imagen vacía.
     */
    public Imagen(int alto, int ancho) {
        this.alto = alto;
        this.ancho = ancho;
        this.imagen = new byte[alto][ancho][3];
    }

    /**
     * Guarda la imagen en formato BMP.
     * @param output Nombre del archivo de salida.
     */
    public void escribirImagen(String output) {
        try (FileOutputStream fos = new FileOutputStream(output)) {
            fos.write(header);
            byte[] pixel = new byte[3];
            byte pad = 0;

            for (int i = 0; i < alto; i++) {
                for (int j = 0; j < ancho; j++) {
                    pixel[0] = imagen[i][j][0];
                    pixel[1] = imagen[i][j][1];
                    pixel[2] = imagen[i][j][2];
                    fos.write(pixel);
                }
                for (int k = 0; k < padding; k++) {
                    fos.write(pad);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Métodos getters
    public int getAlto() { return alto; }
    public int getAncho() { return ancho; }
    public byte[][][] getMatriz() { return imagen; }

    // Método setter para la matriz (útil para inicializar la imagen de salida)
    public void setMatriz(byte[][][] nuevaImagen) {
        this.imagen = nuevaImagen;
    }
}
