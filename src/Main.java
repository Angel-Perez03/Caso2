import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean salir = false;
        
        while (!salir) {
            System.out.println("Menú Principal");
            System.out.println("1 - Generación de las referencias (Opción 1)");
            System.out.println("2 - Simulación de paginación y cálculo de métricas (Opción 2)");
            System.out.println("S - Salir");
            System.out.print("Seleccione una opción: ");
            String opcion = sc.nextLine().trim();
            
            switch(opcion.toUpperCase()) {
                case "1":
                    // Llamar a Opcion1
                    Opcion1.ejecutar(sc);
                    break;
                    
                case "2":
                    // llamar a Opcion2
                    Opcion2.ejecutar(sc);
                    break;
                    
                case "S":
                    salir = true;
                    System.out.println("Saliendo...");
                    break;
                    
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
                    break;
            }
        }
        sc.close();
    }
}
