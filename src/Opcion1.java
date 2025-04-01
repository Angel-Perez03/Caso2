import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import Filter.Imagen;
import Filter.FiltroSobel;

public class Opcion1 {
   
    public static void ejecutar(Scanner sc) {
        try {
            // Solicitar parámetros de entrada
            System.out.print("Ingrese tamaño de página (en bytes): ");
            int tamPagina = sc.nextInt();
            sc.nextLine(); // Limpiar buffer

            System.out.print("Ingrese nombre del archivo BMP: ");
            String nombreImagen = sc.nextLine();

            // Cargar imagen y obtener dimensiones
            Imagen img = new Imagen(nombreImagen);
            int NF = img.getAlto();
            int NC = img.getAncho();

            // Crear imagen de salida con las mismas dimensiones
            Imagen imgOut = new Imagen(NF, NC);

            // Aplicar el filtro Sobel
            FiltroSobel fs = new FiltroSobel(img, imgOut);
            fs.applySobel();

            // Generar la lista de referencias
            List<String> referencias = fs.generarReferencias(img.getMatriz(), tamPagina);

            // Calcular métricas
            int bytesImagen = NF * NC * 3;
            int bytesFiltro = 9 * Integer.BYTES; // 9 enteros (3x3) de 4 bytes cada uno
            int bytesRespuesta = NF * NC * 3;
            int totalBytes = bytesImagen + (2 * bytesFiltro) + bytesRespuesta;
            int NP = (int) Math.ceil((double) totalBytes / tamPagina);
            int NR = referencias.size();

            // Escribir archivo de referencias con cabecera
            try (PrintWriter salida = new PrintWriter(new FileWriter("referencias.txt"))) {
                salida.println("TP=" + tamPagina);
                salida.println("NF=" + NF);
                salida.println("NC=" + NC);
                salida.println("NR=" + NR);
                salida.println("NP=" + NP);
                for (String ref : referencias) {
                    salida.println(ref);
                }
            }
            System.out.println("Archivo de referencias generado correctamente.");

        } catch (IOException e) {
            System.err.println("Error al manejar archivos: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
        // No se cierra el Scanner aquí
    }
}
