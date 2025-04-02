package Filter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Clase para manejar lectura/escritura de imagen BMP (24 bits).
 */
public class Imagen {
    
    // Cabecera de 54 bytes de un archivo BMP
    byte[] header = new byte[54];

    // Matriz de la imagen: [alto][ancho][3]
    // en donde cada pixel tiene 3 canales (B, G, R) en este orden
    // pero los indices 0,1,2 aquí serán (blue, green, red).
    public byte[][][] imagen;

    // Alto y ancho en pixeles
    public int alto;
    public int ancho;
    
    // Padding para cada fila (un BMP de 24 bits
    // rellena cada fila hasta múltiplo de 4 bytes)
    int padding;
    
    /**
     * Constructor que lee desde un archivo BMP y construye la matriz 'imagen'.
     * Nombre del archivo BMP (24 bits de profundidad).
     */
    public Imagen(String nombre) {
        try {
            FileInputStream fis = new FileInputStream(nombre);
            // Lee la cabecera de 54 bytes
            fis.read(header);

            // Extraer ancho y alto (little endian)
            ancho = ((header[21] & 0xFF) << 24) |
                    ((header[20] & 0xFF) << 16) |
                    ((header[19] & 0xFF) << 8) |
                     (header[18] & 0xFF);

            alto =  ((header[25] & 0xFF) << 24) |
                    ((header[24] & 0xFF) << 16) |
                    ((header[23] & 0xFF) << 8) |
                     (header[22] & 0xFF);

            System.out.println("Ancho: " + ancho + " px, Alto: " + alto + " px");

            // Matriz: alto x ancho x 3 (B, G, R)
            imagen = new byte[alto][ancho][3];

            int rowSizeSinPadding = ancho * 3;  
            // Se calcula el padding a múltiplo de 4
            padding = (4 - (rowSizeSinPadding % 4)) % 4;

            byte[] pixel = new byte[3];
            // Leer los pixeles fila por fila
            for (int i = 0; i < alto; i++) {
                for (int j = 0; j < ancho; j++) {
                    // Cada píxel trae 3 bytes (B, G, R)
                    fis.read(pixel);
                    imagen[i][j][0] = pixel[0]; // Blue
                    imagen[i][j][1] = pixel[1]; // Green
                    imagen[i][j][2] = pixel[2]; // Red
                }
                // Saltar padding
                fis.skip(padding);
            }

            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Imagen(int alto, int ancho) {
        this.alto = alto;
        this.ancho = ancho;
        imagen = new byte[alto][ancho][3];
        
    }

    public void escribirImagen(String output) {
        try {
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(header);

            byte[] pixel = new byte[3];
            for (int i = 0; i < alto; i++) {
                for (int j = 0; j < ancho; j++) {
                    pixel[0] = imagen[i][j][0]; // Blue
                    pixel[1] = imagen[i][j][1]; // Green
                    pixel[2] = imagen[i][j][2]; // Red
                    fos.write(pixel);
                }
                // Escribir el padding
                for(int k = 0; k < padding; k++) {
                    fos.write((byte)0);
                }
            }

            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
