# Exercício 13 - Métodos sincronizados

## Objetivo

Comparar duas versões de um contador compartilhado por múltiplas threads:

1. `UnsynchronizedCounter`: versão sem sincronização.
2. `SynchronizedCounter`: versão com métodos `synchronized`.

O experimento cria threads concorrentes que executam incrementos e decrementos sobre a mesma instância do contador. Como a quantidade de incrementos e decrementos é igual, o valor final esperado é sempre `0`.

## Organização

```text
exercicio13-metodos-sincronizados/
├── README.md
├── .gitignore
├── scripts/
│   └── run.sh
└── src/
    └── main/
        └── java/
            └── br/
                └── ufg/
                    └── scd/
                        └── exercicio13/
                            ├── Counter.java
                            ├── UnsynchronizedCounter.java
                            ├── SynchronizedCounter.java
                            └── CounterExperiment.java
```

## Como executar

Na raiz do projeto, execute:

```bash
./scripts/run.sh
```

Também é possível personalizar a execução:

```bash
./scripts/run.sh --operations 1000000 --pairs 4 --rounds 10
```

## Parâmetros

- `--operations`: quantidade de operações feitas por cada thread.
- `--pairs`: quantidade de pares de threads. Cada par possui uma thread de incremento e uma de decremento.
- `--rounds`: quantidade de rodadas por versão.

## Resultado esperado

Na versão sem sincronização, o valor final pode ser diferente de `0`, indicando condição de corrida e inconsistência dos dados em memória.

Na versão com sincronização, o valor final tende a ser sempre `0`, indicando que os métodos sincronizados protegeram o acesso ao estado compartilhado.
