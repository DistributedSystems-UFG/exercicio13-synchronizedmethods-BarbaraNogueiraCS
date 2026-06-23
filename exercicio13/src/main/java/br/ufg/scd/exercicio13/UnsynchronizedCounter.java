package br.ufg.scd.exercicio13;

/**
 * Versão sem sincronização.
 *
 * Esta classe é intencionalmente NÃO thread-safe, pois a atividade pede que
 * os efeitos da ausência de sincronização sejam observados na prática.
 */
public class UnsynchronizedCounter implements Counter {
    private int c = 0;

    @Override
    public void increment() {
        c++;
    }

    @Override
    public void decrement() {
        c--;
    }

    @Override
    public int value() {
        return c;
    }
}
