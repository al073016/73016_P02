import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MonteCarloHilos {

    // ----- Clase que representa la tarea de cada hilo -----
    static class PiTask implements Callable<Long> {
        private final long samples;

        PiTask(long samples) {
            this.samples = samples;
        }

        @Override
        public Long call() {
            SplittableRandom random = new SplittableRandom();
            long inside = 0;

            // Generar puntos aleatorios y contar los que caen dentro del círculo
            for (long i = 0; i < samples; i++) {
                double x = random.nextDouble();
                double y = random.nextDouble();
                if (x * x + y * y <= 1.0) {
                    inside++;
                }
            }
            return inside; // Resultado local del hilo
        }
    }

    public static void main(String[] args) throws Exception {
        // Número total de puntos a generar
        long totalSamples = 1_000_000L;
        // Número de hilos a utilizar
        int numThreads = 4;

        // ================= SECUENCIAL =================
        long t0s = System.nanoTime(); // inicio tiempo secuencial
        long insideSeq = 0;
        SplittableRandom rnd = new SplittableRandom();

        for (long i = 0; i < totalSamples; i++) {
            double x = rnd.nextDouble();
            double y = rnd.nextDouble();
            if (x * x + y * y <= 1.0) {
                insideSeq++;
            }
        }

        double piSeq = (4.0 * insideSeq) / totalSamples;
        double Ts = (System.nanoTime() - t0s) / 1_000_000.0; // tiempo secuencial en ms

        // ================= PARALELO =================
        long t0p = System.nanoTime(); // inicio tiempo paralelo
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Long>> futures = new ArrayList<>();

        long samplesPerThread = totalSamples / numThreads;
        for (int i = 0; i < numThreads; i++) {
            futures.add(executor.submit(new PiTask(samplesPerThread)));
        }

        long globalCount = 0;
        for (Future<Long> f : futures) {
            globalCount += f.get();
        }
        executor.shutdown();

        double piPar = (4.0 * globalCount) / totalSamples;
        double Tp = (System.nanoTime() - t0p) / 1_000_000.0; // tiempo paralelo en ms

        // ================= MÉTRICAS =================
        double speedup = Ts / Tp;                 // Speedup
        double eficiencia = speedup / numThreads; // Eficiencia
        double overhead = numThreads * Tp - Ts;   // Overhead en ms

        // ----- Imprimir resultados detallados del cálculo paralelo -----
        System.out.printf("%nNúmero total de puntos: %d%n", totalSamples);
        System.out.printf("Puntos dentro del círculo: %d%n", globalCount);
        System.out.printf("Aproximación de pi: %.10f%n", piPar);
        System.out.printf("Error: %.10f%n", Math.abs(piPar - 3.1415926535));
        System.out.printf("Porcentaje dentro del círculo: %.2f%%%n",
                (globalCount * 100.0) / totalSamples);

        // ================= RESULTADOS =================
        System.out.println("\n=== Resultados Secuencial ===");
        System.out.printf("Pi = %.10f%n", piSeq);
        System.out.printf("Tiempo secuencial (Ts): %.3f ms%n", Ts);

        System.out.println("\n=== Resultados Paralelo ===");
        System.out.printf("Pi = %.10f%n", piPar);
        System.out.printf("Tiempo paralelo (Tp) con %d hilos: %.3f ms%n", numThreads, Tp);

        System.out.println("\n=== Métricas Comparativas ===");
        System.out.printf("Speedup (S = Ts/Tp): %.3f%n", speedup);
        System.out.printf("Eficiencia (E = S/p): %.3f%n", eficiencia);
        System.out.printf("Overhead (To = p*Tp - Ts): %.3f ms%n", overhead);
    }
}
