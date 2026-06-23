package br.ufg.scd.exercicio13;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

/**
 * Programa principal do experimento.
 *
 * Ele executa a mesma carga de trabalho em duas versões do contador:
 * 1. UnsynchronizedCounter: sem sincronização.
 * 2. SynchronizedCounter: com métodos synchronized.
 *
 * Em cada rodada, a quantidade de incrementos é igual à quantidade de
 * decrementos. Portanto, o valor final esperado é sempre 0.
 */
public final class CounterExperiment {
    private static final int DEFAULT_OPERATIONS_PER_THREAD = 1_000_000;
    private static final int DEFAULT_PAIRS = 4;
    private static final int DEFAULT_ROUNDS = 10;

    private CounterExperiment() {
        // Classe utilitária: não deve ser instanciada.
    }

    public static void main(String[] args) throws InterruptedException {
        ExperimentConfig config = ExperimentConfig.from(args);

        System.out.println("Exercício 13 - Métodos sincronizados");
        System.out.println("Operações por thread: " + config.operationsPerThread);
        System.out.println("Pares de threads: " + config.pairs);
        System.out.println("Total de threads por rodada: " + (config.pairs * 2));
        System.out.println("Rodadas por versão: " + config.rounds);
        System.out.println("Valor esperado em todas as rodadas: 0");
        System.out.println();

        runExperiment("SEM sincronização", UnsynchronizedCounter::new, config);
        System.out.println();
        runExperiment("COM sincronização", SynchronizedCounter::new, config);
    }

    private static void runExperiment(
            String title,
            Supplier<Counter> counterFactory,
            ExperimentConfig config) throws InterruptedException {

        System.out.println("=== " + title + " ===");
        System.out.printf("%8s | %12s | %10s | %12s%n", "Rodada", "Valor final", "Correto?", "Tempo (ms)");
        System.out.println("-----------------------------------------------------");

        int incorrectResults = 0;

        for (int round = 1; round <= config.rounds; round++) {
            Counter counter = counterFactory.get();
            long start = System.nanoTime();

            runConcurrentOperations(counter, config.operationsPerThread, config.pairs);

            long elapsedMillis = (System.nanoTime() - start) / 1_000_000;
            int finalValue = counter.value();
            boolean correct = finalValue == 0;

            if (!correct) {
                incorrectResults++;
            }

            System.out.printf(
                    "%8d | %12d | %10s | %12d%n",
                    round,
                    finalValue,
                    correct ? "sim" : "não",
                    elapsedMillis);
        }

        System.out.println("-----------------------------------------------------");
        System.out.println("Resultados incorretos: " + incorrectResults + " de " + config.rounds);
    }

    private static void runConcurrentOperations(
            Counter counter,
            int operationsPerThread,
            int pairs) throws InterruptedException {

        CountDownLatch startGate = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < pairs; i++) {
            threads.add(new Thread(
                    new CounterTask(counter, operationsPerThread, Operation.INCREMENT, startGate),
                    "increment-thread-" + (i + 1)));

            threads.add(new Thread(
                    new CounterTask(counter, operationsPerThread, Operation.DECREMENT, startGate),
                    "decrement-thread-" + (i + 1)));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        // Libera todas as threads ao mesmo tempo para aumentar a concorrência.
        startGate.countDown();

        for (Thread thread : threads) {
            thread.join();
        }
    }

    private enum Operation {
        INCREMENT,
        DECREMENT
    }

    private static final class CounterTask implements Runnable {
        private final Counter counter;
        private final int repetitions;
        private final Operation operation;
        private final CountDownLatch startGate;

        private CounterTask(
                Counter counter,
                int repetitions,
                Operation operation,
                CountDownLatch startGate) {
            this.counter = counter;
            this.repetitions = repetitions;
            this.operation = operation;
            this.startGate = startGate;
        }

        @Override
        public void run() {
            try {
                startGate.await();

                for (int i = 0; i < repetitions; i++) {
                    if (operation == Operation.INCREMENT) {
                        counter.increment();
                    } else {
                        counter.decrement();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println(Thread.currentThread().getName() + " foi interrompida.");
            }
        }
    }

    private static final class ExperimentConfig {
        private final int operationsPerThread;
        private final int pairs;
        private final int rounds;

        private ExperimentConfig(int operationsPerThread, int pairs, int rounds) {
            this.operationsPerThread = operationsPerThread;
            this.pairs = pairs;
            this.rounds = rounds;
        }

        private static ExperimentConfig from(String[] args) {
            int operations = DEFAULT_OPERATIONS_PER_THREAD;
            int pairs = DEFAULT_PAIRS;
            int rounds = DEFAULT_ROUNDS;

            for (int i = 0; i < args.length; i++) {
                String arg = args[i];

                if ("--operations".equals(arg) && i + 1 < args.length) {
                    operations = parsePositiveInt(args[++i], "operations");
                } else if ("--pairs".equals(arg) && i + 1 < args.length) {
                    pairs = parsePositiveInt(args[++i], "pairs");
                } else if ("--rounds".equals(arg) && i + 1 < args.length) {
                    rounds = parsePositiveInt(args[++i], "rounds");
                } else if ("--help".equals(arg) || "-h".equals(arg)) {
                    printUsageAndExit();
                } else {
                    throw new IllegalArgumentException("Argumento inválido: " + arg + "\nUse --help para ver as opções.");
                }
            }

            return new ExperimentConfig(operations, pairs, rounds);
        }

        private static int parsePositiveInt(String value, String fieldName) {
            try {
                int parsed = Integer.parseInt(value);
                if (parsed <= 0) {
                    throw new IllegalArgumentException(fieldName + " deve ser maior que zero.");
                }
                return parsed;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(fieldName + " deve ser um número inteiro válido: " + value, e);
            }
        }

        private static void printUsageAndExit() {
            System.out.println("Uso:");
            System.out.println("  java -cp out br.ufg.scd.exercicio13.CounterExperiment [opções]");
            System.out.println();
            System.out.println("Opções:");
            System.out.println("  --operations <n>  Operações por thread. Padrão: " + DEFAULT_OPERATIONS_PER_THREAD);
            System.out.println("  --pairs <n>       Pares de threads. Cada par cria 1 thread de incremento e 1 de decremento. Padrão: " + DEFAULT_PAIRS);
            System.out.println("  --rounds <n>      Rodadas por versão. Padrão: " + DEFAULT_ROUNDS);
            System.out.println("  --help            Mostra esta ajuda.");
            System.exit(0);
        }
    }
}
