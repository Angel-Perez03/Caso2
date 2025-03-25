import java.io.FileInputStream;
import java.io.IOException;

public class Imagen {
    private byte[] header = new byte[54];
    private byte[][][] imagen; // Matriz tridimensional: filas x columnas x 3 canales (RGB)
    private int alto; // NF: número de filas (alto)
    private int ancho; // NC: número de columnas (ancho)
    private int padding;

    public Imagen(String nombreArchivo) {
        try (FileInputStream fis = new FileInputStream(nombreArchivo)) {
            fis.read(header);
            // Extraer ancho y alto desde la cabecera (formato BMP, little endian)
            ancho = ((header[21] & 0xFF) << 24) | ((header[20] & 0xFF) << 16) |
                    ((header[19] & 0xFF) << 8) | (header[18] & 0xFF);
            alto = ((header[25] & 0xFF) << 24) | ((header[24] & 0xFF) << 16) |
                   ((header[23] & 0xFF) << 8) | (header[22] & 0xFF);
            // Inicializar la matriz de la imagen
            imagen = new byte[alto][ancho][3];

            int rowSizeSinPadding = ancho * 3;
            // Calcular el padding, cada fila debe ser múltiplo de 4 bytes
            padding = (4 - (rowSizeSinPadding % 4)) % 4;

            // Leer los píxeles (formato BMP, cada píxel en orden BGR)
            byte[] pixel = new byte[3];
            for (int i = 0; i < alto; i++) {
                for (int j = 0; j < ancho; j++) {
                    fis.read(pixel);
                    imagen[i][j][0] = pixel[0]; // Azul
                    imagen[i][j][1] = pixel[1]; // Verde
                    imagen[i][j][2] = pixel[2]; // Rojo
                }
                fis.skip(padding);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getAlto() {
        return alto;
    }

    public int getAncho() {
        return ancho;
    }

    public byte[][][] getMatriz() {
        return imagen;
    }
}
