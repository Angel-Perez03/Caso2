package Filter;

import java.util.ArrayList;
import java.util.List;

public class FiltroSobel {
    private Imagen imagenIn;
    private Imagen imagenOut;

    // Kernels Sobel para detección de bordes
    private static final int[][] SOBEL_X = {
        {-1, 0, 1},
        {-2, 0, 2},
        {-1, 0, 1}
    };

    private static final int[][] SOBEL_Y = {
        {-1, -2, -1},
        { 0,  0,  0},
        { 1,  2,  1}
    };

    /**
     * Constructor que recibe la imagen de entrada y la de salida.
     */
    public FiltroSobel(Imagen imagenEntrada, Imagen imagenSalida) {
        this.imagenIn = imagenEntrada;
        this.imagenOut = imagenSalida;
    }

    /**
     * Aplica el filtro Sobel a la imagen de entrada y guarda el resultado en imagenOut.
     */
    public void applySobel() {
        int alto = imagenIn.getAlto();
        int ancho = imagenIn.getAncho();
        byte[][][] matrizIn = imagenIn.getMatriz();
        byte[][][] matrizOut = imagenOut.getMatriz();

        // Se procesan solo los píxeles con vecindario completo: desde 1 hasta alto-2 y 1 hasta ancho-2
        for (int i = 1; i < alto - 1; i++) {
            for (int j = 1; j < ancho - 1; j++) {
                int gradXRed = 0, gradXGreen = 0, gradXBlue = 0;
                int gradYRed = 0, gradYGreen = 0, gradYBlue = 0;

                // Aplicar máscaras Sobel X y Y para el vecindario 3x3
                for (int ki = -1; ki <= 1; ki++) {
                    for (int kj = -1; kj <= 1; kj++) {
                        // Convertir a entero sin signo usando & 0xFF
                        int red   = matrizIn[i + ki][j + kj][0] & 0xFF;
                        int green = matrizIn[i + ki][j + kj][1] & 0xFF;
                        int blue  = matrizIn[i + ki][j + kj][2] & 0xFF;

                        gradXRed   += red   * SOBEL_X[ki + 1][kj + 1];
                        gradXGreen += green * SOBEL_X[ki + 1][kj + 1];
                        gradXBlue  += blue  * SOBEL_X[ki + 1][kj + 1];

                        gradYRed   += red   * SOBEL_Y[ki + 1][kj + 1];
                        gradYGreen += green * SOBEL_Y[ki + 1][kj + 1];
                        gradYBlue  += blue  * SOBEL_Y[ki + 1][kj + 1];
                    }
                }

                int red   = Math.min(Math.max((int) Math.sqrt(gradXRed * gradXRed + gradYRed * gradYRed), 0), 255);
                int green = Math.min(Math.max((int) Math.sqrt(gradXGreen * gradXGreen + gradYGreen * gradYGreen), 0), 255);
                int blue  = Math.min(Math.max((int) Math.sqrt(gradXBlue * gradXBlue + gradYBlue * gradYBlue), 0), 255);

                // Almacenar resultado en la imagen de salida
                matrizOut[i][j][0] = (byte) red;
                matrizOut[i][j][1] = (byte) green;
                matrizOut[i][j][2] = (byte) blue;
            }
        }
    }

    /**
     * Genera la lista de referencias que simularían los accesos a memoria durante
     * la ejecución del método applySobel. Se generan referencias para:
     *   a) La lectura del vecindario 3x3 de la imagen (9 referencias).
     *   b) La lectura de los filtros SOBEL_X y SOBEL_Y (cada uno: 3x3 enteros de 4 bytes → 36 referencias).
     *   c) La escritura de la respuesta (3 referencias por píxel).
     * 
     * Solo se procesan los píxeles internos (de 1 a NF-2 y 1 a NC-2).
     *
     * @param matriz La matriz de la imagen.
     * @param tamPagina Tamaño de página en bytes.
     * @return Lista de cadenas con las referencias en formato: id, numPagina, offset, acción.
     */
    public List<String> generarReferencias(byte[][][] matriz, int tamPagina) {
        List<String> referencias = new ArrayList<>();
        int NF = matriz.length;
        int NC = matriz[0].length;
        int direccionActual = 0;

        // Procesar solo píxeles internos: desde 1 hasta NF-2 y 1 hasta NC-2
        for (int i = 1; i < NF - 1; i++) {
            for (int j = 1; j < NC - 1; j++) {
                // a) Lectura de la imagen: vecindario 3x3 (9 referencias)
                for (int ki = -1; ki <= 1; ki++) {
                    for (int kj = -1; kj <= 1; kj++) {
                        String ref = "Imagen[" + (i + ki) + "][" + (j + kj) + "].r";
                        int numPagina = direccionActual / tamPagina;
                        int offset = direccionActual % tamPagina;
                        referencias.add(ref + "," + numPagina + "," + offset + ",R");
                        direccionActual++;
                    }
                }
                // b) Lectura del filtro SOBEL_X: 3x3 enteros, 4 bytes cada uno → 36 referencias
                for (int a = 0; a < 3; a++) {
                    for (int b = 0; b < 3; b++) {
                        for (int byteIdx = 0; byteIdx < 4; byteIdx++) {
                            String ref = "SOBEL_X[" + a + "][" + b + "].byte" + byteIdx;
                            int numPagina = direccionActual / tamPagina;
                            int offset = direccionActual % tamPagina;
                            referencias.add(ref + "," + numPagina + "," + offset + ",R");
                            direccionActual++;
                        }
                    }
                }
                // c) Lectura del filtro SOBEL_Y: de igual forma, 36 referencias
                for (int a = 0; a < 3; a++) {
                    for (int b = 0; b < 3; b++) {
                        for (int byteIdx = 0; byteIdx < 4; byteIdx++) {
                            String ref = "SOBEL_Y[" + a + "][" + b + "].byte" + byteIdx;
                            int numPagina = direccionActual / tamPagina;
                            int offset = direccionActual % tamPagina;
                            referencias.add(ref + "," + numPagina + "," + offset + ",R");
                            direccionActual++;
                        }
                    }
                }
                // d) Escritura de la respuesta: 3 referencias (r, g, b)
                String[] canales = {"r", "g", "b"};
                for (int c = 0; c < 3; c++) {
                    String ref = "Rta[" + i + "][" + j + "]." + canales[c];
                    int numPagina = direccionActual / tamPagina;
                    int offset = direccionActual % tamPagina;
                    referencias.add(ref + "," + numPagina + "," + offset + ",W");
                    direccionActual++;
                }
            }
        }
        return referencias;
    }
}
