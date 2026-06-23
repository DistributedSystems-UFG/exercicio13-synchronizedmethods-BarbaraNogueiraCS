package br.ufg.scd.exercicio13;

/**
 * Versão com métodos sincronizados.
 *
 * O modificador synchronized impede que duas threads executem, ao mesmo tempo,
 * métodos sincronizados sobre a mesma instância deste objeto.
 */
public class SynchronizedCounter implements Counter {
    private int c = 0;

    @Override
    public synchronized void increment() {
        c++;
    }

    @Override
    public synchronized void decrement() {
        c--;
    }

    @Override
    public synchronized int value() {
        return c;
    }
}
