package com.manojkhannakm.machinelearning.geneticalgorithm;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Manoj Khanna
 */

public abstract class GeneticAlgorithm<T> {

    private static final int CHROMOSOME_RATE = 100;
    private static final Random RANDOM = new Random();

    private final T targetValue;

    private ArrayList<Chromosome<T>> chromosomeList = new ArrayList<>(CHROMOSOME_RATE);
    private int generation = 0, population = 0;
    private Chromosome<T> chromosome;

    public GeneticAlgorithm(T targetValue) {
        this.targetValue = targetValue;
    }

    protected abstract Chromosome<T> chromosome();

    private Chromosome<T> select() {
        float totalFitness = 0.0f;

        for (Chromosome<T> chromosome : chromosomeList) {
            totalFitness += chromosome.getFitness();
        }

        float slice = totalFitness * RANDOM.nextFloat();

        totalFitness = 0.0f;

        for (int i = chromosomeList.size() - 1; i >= 0; i--) {
            totalFitness += chromosomeList.get(i).getFitness();

            if (totalFitness >= slice) {
                return chromosomeList.remove(i);
            }
        }

        return chromosomeList.remove(chromosomeList.size() - 1);
    }

    public void run() {
        for (int i = 0; i < CHROMOSOME_RATE; i++) {
            Chromosome<T> chromosome = chromosome();
            chromosome.calculate(targetValue);

            chromosomeList.add(chromosome);
        }

        population += CHROMOSOME_RATE;

        while (chromosome == null) {
            generation++;

            ArrayList<Chromosome<T>> newChromosomeList = new ArrayList<>(CHROMOSOME_RATE);

            while (!chromosomeList.isEmpty()) {
                Chromosome<T> chromosome1 = select(),
                        chromosome2 = select();

                chromosome1.crossover(chromosome2);

                chromosome1.mutate();
                chromosome2.mutate();

                chromosome1.calculate(targetValue);
                chromosome2.calculate(targetValue);

                population++;

                if (targetValue.equals(chromosome1.getValue())) {
                    chromosome = chromosome1;
                    return;
                }

                population++;

                if (targetValue.equals(chromosome2.getValue())) {
                    chromosome = chromosome2;
                    return;
                }

                newChromosomeList.add(chromosome1);
                newChromosomeList.add(chromosome2);
            }

            chromosomeList.addAll(newChromosomeList);
        }
    }

    public int getGeneration() {
        return generation;
    }

    public int getPopulation() {
        return population;
    }

    public Chromosome<T> getChromosome() {
        return chromosome;
    }

}
