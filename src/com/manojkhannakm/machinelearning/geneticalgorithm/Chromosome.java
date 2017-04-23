package com.manojkhannakm.machinelearning.geneticalgorithm;

import java.util.Random;

/**
 * @author Manoj Khanna
 */

public abstract class Chromosome<T> {

    private static final float CROSSOVER_RATE = 0.7f;
    private static final float MUTATE_RATE = 0.001f;
    private static final Random RANDOM = new Random();

    private final boolean[] bits = encode();

    private T value;
    private float fitness;

    protected abstract boolean[] encode();

    protected abstract T decode();

    protected abstract float fitness(T targetValue);

    void crossover(Chromosome chromosome) {
        if (RANDOM.nextFloat() < CROSSOVER_RATE) {
            for (int i = RANDOM.nextInt(bits.length); i < bits.length; i++) {
                boolean bit = bits[i];
                bits[i] = chromosome.bits[i];
                chromosome.bits[i] = bit;
            }
        }
    }

    void mutate() {
        for (int i = 0; i < bits.length; i++) {
            if (RANDOM.nextFloat() < MUTATE_RATE) {
                bits[i] = !bits[i];
            }
        }
    }

    void calculate(T targetValue) {
        value = decode();
        fitness = fitness(targetValue);
    }

    public boolean[] getBits() {
        return bits;
    }

    public T getValue() {
        return value;
    }

    public float getFitness() {
        return fitness;
    }

}
