package Filter;
/**
 * Clase con la lógica de aplicar el Filtro Sobel a una imagen.
 */
public class FiltroSobel {
    
    // Imagen de entrada y salida
    Imagen imagenIn;
    Imagen imagenOut;

    // Sobel kernels
    static final int[][] SOBEL_X = {
        {-1, 0, 1},
        {-2, 0, 2},
        {-1, 0, 1}
    };

    static final int[][] SOBEL_Y = {
        {-1, -2, -1},
        { 0,  0,  0},
        { 1,  2,  1}
    };


    public FiltroSobel(Imagen imagenEntrada, Imagen imagenSalida) {
        this.imagenIn = imagenEntrada;
        this.imagenOut = imagenSalida;
    }

    /**
     * Aplica el filtro de Sobel a la imagen de entrada, almacenando
     * el resultado en la imagen de salida.
     */
    public void applySobel() {
        for (int i = 1; i < imagenIn.alto - 1; i++) {
            for (int j = 1; j < imagenIn.ancho - 1; j++) {
                
                int gradXRed   = 0, gradXGreen   = 0, gradXBlue   = 0;
                int gradYRed   = 0, gradYGreen   = 0, gradYBlue   = 0;

                // Recorrer vecindario 3x3
                for (int ki = -1; ki <= 1; ki++) {
                    for (int kj = -1; kj <= 1; kj++) {
                        
                        int red   = imagenIn.imagen[i + ki][j + kj][2] & 0xFF; // Red canal
                        int green = imagenIn.imagen[i + ki][j + kj][1] & 0xFF; // Green
                        int blue  = imagenIn.imagen[i + ki][j + kj][0] & 0xFF; // Blue

                        // SOBEL_X
                        gradXRed   += red   * SOBEL_X[ki + 1][kj + 1];
                        gradXGreen += green * SOBEL_X[ki + 1][kj + 1];
                        gradXBlue  += blue  * SOBEL_X[ki + 1][kj + 1];

                        // SOBEL_Y
                        gradYRed   += red   * SOBEL_Y[ki + 1][kj + 1];
                        gradYGreen += green * SOBEL_Y[ki + 1][kj + 1];
                        gradYBlue  += blue  * SOBEL_Y[ki + 1][kj + 1];
                    }
                }

                // Magnitud
                int valRed   = (int) Math.sqrt(gradXRed   * (long)gradXRed   + gradYRed   * (long)gradYRed  );
                int valGreen = (int) Math.sqrt(gradXGreen * (long)gradXGreen + gradYGreen * (long)gradYGreen);
                int valBlue  = (int) Math.sqrt(gradXBlue  * (long)gradXBlue  + gradYBlue  * (long)gradYBlue );

                // Clamping [0..255]
                valRed   = Math.min(Math.max(valRed,   0), 255);
                valGreen = Math.min(Math.max(valGreen, 0), 255);
                valBlue  = Math.min(Math.max(valBlue,  0), 255);

                // Escribir en la imagen de salida
                imagenOut.imagen[i][j][2] = (byte) valRed;
                imagenOut.imagen[i][j][1] = (byte) valGreen;
                imagenOut.imagen[i][j][0] = (byte) valBlue;
            }
        }
    }
}
