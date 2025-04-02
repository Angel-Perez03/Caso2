import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import Filter.Imagen;

public class Opcion1 {

    // Atributos para la generación de referencias
    private int TP;             // Tamaño de página
    private String nombreBMP;   // Nombre del archivo BMP
    private Imagen imagenIn;    // Imagen para obtener alto, ancho
    private int NF;            // Filas de la imagen
    private int NC;            // Columnas de la imagen

    // Direcciones base en memoria virtual
    private long baseImagenIn;
    private long baseSobelX;
    private long baseSobelY;
    private long baseImagenOut;

    // Tamaño de los filtros (3x3 int => 36 bytes cada uno)
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

    public Opcion1(int tp, String nombreArchivoBMP) {
        this.TP = tp;
        this.nombreBMP = nombreArchivoBMP;
        
        // Cargar la imagen
        this.imagenIn = new Imagen(nombreArchivoBMP);
        this.NF = imagenIn.alto;
        this.NC = imagenIn.ancho;

        // Calcular tamaños en bytes
        long tamImagen    = (long)NF * NC * 3; // imagenIn
        long tamSobelX    = 3L * 3 * 4;        // 36 bytes
        long tamSobelY    = 3L * 3 * 4;        // 36 bytes
        long tamImagenOut = tamImagen;         // imagenOut

        // Bases en memoria virtual
        baseImagenIn  = 0;
        baseSobelX    = baseImagenIn  + tamImagen;
        baseSobelY    = baseSobelX    + tamSobelX;
        baseImagenOut = baseSobelY    + tamSobelY;
    }

    /**
     * Genera el archivo de referencias simulando applySobel()
     * (accesos a imagenIn, SOBEL_X, SOBEL_Y, imagenOut).
     */
    public void generarArchivoReferencias(String nombreArchivoSalida) {
        // Calcular total de bytes (imagenIn + filtros + imagenOut)
        long totalBytes = baseImagenOut + (long)NF * NC * 3;
        // Cantidad de páginas virtuales
        long NP = (long)Math.ceil( (double)totalBytes / TP );

        StringBuilder sbRefs = new StringBuilder();
        long countRefs = 0;

        for(int i = 1; i < NF - 1; i++) {
            for(int j = 1; j < NC - 1; j++) {

                for(int ki = -1; ki <= 1; ki++) {
                    for(int kj = -1; kj <= 1; kj++) {
                        
                        // Lectura: imagenIn (3 canales)
                        for(int canal = 0; canal < 3; canal++) {
                            long dirIn = getDirImagenIn(i+ki, j+kj, canal);
                            sbRefs.append("Imagen[")
                                  .append(i+ki).append("][")
                                  .append(j+kj).append("].")
                                  .append(canalName(canal)).append(",")
                                  .append(getPaginaOffset(dirIn)).append(",R\n");
                            countRefs++;
                        }

                        // Lectura: SOBEL_X (3 veces para r,g,b)
                        long dirSx = getDirSobelX(ki+1, kj+1);
                        for(int c=0; c<3; c++){
                            sbRefs.append("SOBEL_X[")
                                  .append(ki+1).append("][")
                                  .append(kj+1).append("],")
                                  .append(getPaginaOffset(dirSx))
                                  .append(",R\n");
                            countRefs++;
                        }

                        // Lectura: SOBEL_Y (3 veces)
                        long dirSy = getDirSobelY(ki+1, kj+1);
                        for(int c=0; c<3; c++){
                            sbRefs.append("SOBEL_Y[")
                                  .append(ki+1).append("][")
                                  .append(kj+1).append("],")
                                  .append(getPaginaOffset(dirSy))
                                  .append(",R\n");
                            countRefs++;
                        }
                    }
                }

                // Escritura: imagenOut (3 canales)
                for(int canal = 0; canal < 3; canal++){
                    long dirOut = getDirImagenOut(i, j, canal);
                    sbRefs.append("Rta[")
                          .append(i).append("][")
                          .append(j).append("].")
                          .append(canalName(canal)).append(",")
                          .append(getPaginaOffset(dirOut)).append(",W\n");
                    countRefs++;
                }
            }
        }

        // Encabezado
        StringBuilder sb = new StringBuilder();
        sb.append("TP=").append(TP).append("\n");
        sb.append("NF=").append(NF).append("\n");
        sb.append("NC=").append(NC).append("\n");
        sb.append("NR=").append(countRefs).append("\n");
        sb.append("NP=").append(NP).append("\n");

        // Añadir las referencias
        sb.append(sbRefs);

        // Guardar en archivo
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(nombreArchivoSalida))) {
            pw.print(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Mensaje final
        System.out.println("Archivo de referencias generado: " + nombreArchivoSalida);
        System.out.println("NR (total referencias) = " + countRefs);
        System.out.println("NP (páginas virtuales) = " + NP);
    }

    // Métodos auxiliares

    private long getDirImagenIn(int i, int j, int canal) {
        long offset = ((long)i * NC + j) * 3 + canal;
        return baseImagenIn + offset;
    }

    private long getDirImagenOut(int i, int j, int canal) {
        long offset = ((long)i * NC + j) * 3 + canal;
        return baseImagenOut + offset;
    }

    private long getDirSobelX(int ki, int kj) {
        long offset = (ki * 3 + kj) * 4;
        return baseSobelX + offset;
    }

    private long getDirSobelY(int ki, int kj) {
        long offset = (ki * 3 + kj) * 4;
        return baseSobelY + offset;
    }

    private String getPaginaOffset(long dir) {
        long page = dir / TP;
        long off  = dir % TP;
        return page + "," + off;
    }

    private String canalName(int c) {
        switch(c) {
            case 0: return "r";
            case 1: return "g";
            case 2: return "b";
            default: return "?";
        }
    }

    //Metodo que invoca la opción 1 desde el menú principal
    public static void ejecutar(Scanner sc) {
        System.out.println("=== Opción 1: Generación de las referencias ===");

        System.out.print("Ingrese el tamaño de página (TP): ");
        int tp = sc.nextInt();
        sc.nextLine(); // Consumir salto

        System.out.print("Ingrese el nombre o ruta del archivo BMP: ");
        String bmpName = sc.nextLine();

        System.out.print("Ingrese el nombre del archivo de salida con referencias: ");
        String archivoRefs = sc.nextLine();

        if (!archivoRefs.toLowerCase().endsWith(".txt")) {
        archivoRefs += ".txt";
        }

        // Crear la instancia y generar
        Opcion1 op = new Opcion1(tp, bmpName);
        op.generarArchivoReferencias(archivoRefs);

        System.out.println("Opción 1 finalizada.");
    }
}
