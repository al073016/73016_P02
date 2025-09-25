import java.util.Random;

public class MonteCarloSecuencial {

    public static void main(String[] args) {
        long ts = System.nanoTime();
        int totalSamples = 1_000_000;
        Random random = new Random();
        int globalCount = 0;

        // Generar todos los puntos secuencialmente
        for (int i = 0; i < totalSamples; i++) {
            // Generar un punto aleatorio (x, y) en el rango [0, 1)
            double x = random.nextDouble();
            double y = random.nextDouble();
            // Verificar si el punto está dentro del círculo (x² + y² <= 1)
            if (x * x + y * y <= 1.0) {
                globalCount++;
            }
            
            /* Mostrar progreso cada 100,000 iteraciones
            if ((i + 1) % 100000 == 0) {
                System.out.printf("Procesados %d puntos...%n", i + 1);
            } */
        }

        // Calcular la aproximación de pi
        double piApprox = (4.0 * globalCount) / totalSamples;
        
        System.out.printf("%nNúmero total de puntos: %d%n", totalSamples);
        System.out.printf("Puntos dentro del círculo: %d%n", globalCount);
        System.out.printf("Aproximación de pi: %.10f%n", piApprox);
        System.out.printf("Error: %.10f%n", Math.abs(piApprox - 3.1415926535));
        
        // Mostrar porcentaje de puntos dentro del círculo
        double percentage = (globalCount * 100.0) / totalSamples;
        System.out.printf("Porcentaje dentro del círculo: %.2f%%%n", percentage);

        //Tiempo secuencial
        System.out.printf("Tiempo secuencial: %.3f ms%n", (System.nanoTime() - ts) / 1_000_000.0);
    }
}
