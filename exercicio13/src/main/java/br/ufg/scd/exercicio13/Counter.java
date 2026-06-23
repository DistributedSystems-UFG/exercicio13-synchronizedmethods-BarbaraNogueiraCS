package br.ufg.scd.exercicio13;

/**
 * Contrato comum para as duas versões do contador.
 *
 * A atividade pede duas versões da classe Counter. Neste projeto, usamos uma
 * interface chamada Counter para permitir testar as duas implementações com o
 * mesmo código de experimento.
 */
public interface Counter {
    void increment();

    void decrement();

    int value();
}
