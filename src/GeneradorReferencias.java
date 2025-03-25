import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GeneradorReferencias {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        //Parámetros de entrada
        System.out.print("Ingrese tamaño de página (en bytes): ");
        int tamPagina = sc.nextInt();
        System.out.print("Ingrese nombre del archivo de imagen (BMP): ");
        String nombreImagen = sc.next();
        
        //Obtener dimensiones (NF y NC)
        Imagen imagen = new Imagen(nombreImagen);
        int NF = imagen.getAlto();   // Número de filas
        int NC = imagen.getAncho();  // Número de columnas
        
        //Calcular el espacio en bytes requerido para cada matriz:
        // Imagen y respuesta: 3 bytes por píxel (RGB)
        int bytesImagen = NF * NC * 3;
        int bytesRespuesta = bytesImagen;
        // Filtros SOBEL_X y SOBEL_Y: matriz 3x3, se asume 4 bytes por entero
        int bytesFiltro = 3 * 3 * 4;
        // Total de bytes usados para las 4 matrices
        int totalBytes = bytesImagen + (bytesFiltro * 2) + bytesRespuesta;
        // NP: número de páginas virtuales (redondeo hacia arriba)
        int NP = (int) Math.ceil((double) totalBytes / tamPagina);
        
        // Generar las referencias de memoria
        List<String> referencias = new ArrayList<>();
        int direccionActual = 0;  // Dirección virtual (en bytes)
        
        //Referencias para la matriz "Imagen" (lectura)
        for (int i = 0; i < NF; i++) {
            for (int j = 0; j < NC; j++) {
                // Cada píxel tiene 3 componentes: r, g, b
                referencias.add(registrarReferencia("Imagen[" + i + "][" + j + "].r", 'R', direccionActual, tamPagina));
                direccionActual++;
                referencias.add(registrarReferencia("Imagen[" + i + "][" + j + "].g", 'R', direccionActual, tamPagina));
                direccionActual++;
                referencias.add(registrarReferencia("Imagen[" + i + "][" + j + "].b", 'R', direccionActual, tamPagina));
                direccionActual++;
            }
        }
        
        //Referencias para la matriz "SOBEL_X" (lectura)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                referencias.add(registrarReferencia("SOBEL_X[" + i + "][" + j + "]", 'R', direccionActual, tamPagina));
                direccionActual++;
            }
        }
        
        //Referencias para la matriz "SOBEL_Y" (lectura)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                referencias.add(registrarReferencia("SOBEL_Y[" + i + "][" + j + "]", 'R', direccionActual, tamPagina));
                direccionActual++;
            }
        }
        
        //Referencias para la matriz "Respuesta" (escritura)
        // Se asume que se escribe un byte por canal en cada píxel o, según se requiera, una referencia por píxel
        for (int i = 0; i < NF; i++) {
            for (int j = 0; j < NC; j++) {
                // Aquí se simula que se escribe el resultado de la operación (por ejemplo, un único valor)
                referencias.add(registrarReferencia("Rta[" + i + "][" + j + "]", 'W', direccionActual, tamPagina));
                direccionActual++;
            }
        }
        
        int NR = referencias.size();  // Número total de referencias
        
        //lista de referencias
        try (PrintWriter salida = new PrintWriter(new FileWriter("referencias.txt"))) {
            salida.println("TP=" + tamPagina);
            salida.println("NF=" + NF);
            salida.println("NC=" + NC);
            salida.println("NR=" + NR);
            salida.println("NP=" + NP);
            for (String ref : referencias) {
                salida.println(ref);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("Archivo de referencias generado correctamente.");
    }
    
    
    private static String registrarReferencia(String idCelda, char accion, int direccionActual, int tamPagina) {
        int numPagina = direccionActual / tamPagina;
        int offset = direccionActual % tamPagina;
        return idCelda + "," + numPagina + "," + offset + "," + accion;
    }
}
