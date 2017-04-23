package com.manojkhannakm.machinelearning.geneticalgorithm;

import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

/**
 * @author Manoj Khanna
 */

public class Example {

    public static void main(String[] args) {
        GeneticAlgorithm<Integer> geneticAlgorithm = new GeneticAlgorithm<Integer>(100) {

            @Override
            protected Chromosome<Integer> chromosome() {
                return new ExpressionChromosome();
            }

        };
        geneticAlgorithm.run();

        System.out.println("Generation: " + geneticAlgorithm.getGeneration());
        System.out.println("Population: " + geneticAlgorithm.getPopulation());
        System.out.println("Chromosome: " + geneticAlgorithm.getChromosome());
    }

    private static class ExpressionChromosome extends Chromosome<Integer> {

        private static final char[] CHARS = new char[]{
                '0', '1', '2', '3', '4',
                '5', '6', '7', '8', '9',
                '+', '-', '*', '/'
        };
        private static final int CHAR_COUNT = 9;
        private static final int BIT_COUNT = 4;
        private static final Random RANDOM = new Random();

        @Override
        protected boolean[] encode() {
            boolean[] bits = new boolean[CHAR_COUNT * BIT_COUNT];

            for (int i = 0; i < CHAR_COUNT; i++) {
                System.arraycopy(toBits(RANDOM.nextInt(CHARS.length)), 0, bits, i * BIT_COUNT, BIT_COUNT);
            }

            return bits;
        }

        @Override
        protected Integer decode() {
            String string = format(toString(getBits()));

            if (string == null) {
                return null;
            }

            Stack<Integer> operandStack = new Stack<>();
            Stack<String> operatorStack = new Stack<>();
            boolean f = false;

            for (String s : string.split(" ")) {
                if (!f) {
                    operandStack.push(Integer.parseInt(s));
                } else {
                    if (!operatorStack.isEmpty()) {
                        String t = operatorStack.peek();

                        if ((s.equals("+") || s.equals("-"))
                                && (t.equals("*") || t.equals("/"))) {
                            while (!operatorStack.isEmpty()) {
                                Integer operand = evaluate(operandStack.pop(), operatorStack.pop(), operandStack.pop());

                                if (operand == null) {
                                    return null;
                                }

                                operandStack.push(operand);
                            }
                        }
                    }

                    operatorStack.push(s);
                }

                f = !f;
            }

            while (!operatorStack.isEmpty()) {
                Integer operand = evaluate(operandStack.pop(), operatorStack.pop(), operandStack.pop());

                if (operand == null) {
                    return null;
                }

                operandStack.push(operand);
            }

            return operandStack.pop();
        }

        @Override
        protected float fitness(Integer targetValue) {
            Integer value = getValue();

            if (value != null && !value.equals(targetValue)) {
                return 1.0f / (targetValue - value);
            }

            return 0.0f;
        }

        @Override
        public String toString() {
            boolean[] bits = getBits();
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < CHAR_COUNT; i++) {
                if (i > 0) {
                    stringBuilder.append(" ");
                }

                for (int j = 0; j < BIT_COUNT; j++) {
                    stringBuilder.append(bits[i * BIT_COUNT + j] ? 1 : 0);
                }
            }

            String string = toString(bits);
            stringBuilder.append(" -> ")
                    .append(string);

            stringBuilder.append(" -> ")
                    .append(format(string));

            return stringBuilder.toString();
        }

        private boolean[] toBits(int n) {
            boolean[] bits = new boolean[BIT_COUNT];

            for (int i = 0, p = 1 << BIT_COUNT - 1; i < BIT_COUNT; i++, p /= 2) {
                if (n >= p) {
                    bits[i] = true;
                    n -= p;
                }
            }

            return bits;
        }

        private int toInt(boolean[] bits) {
            int n = 0;

            for (int i = 0, p = 1 << BIT_COUNT - 1; i < BIT_COUNT; i++, p /= 2) {
                if (bits[i]) {
                    n += p;
                }
            }

            return n;
        }

        private String toString(boolean[] bits) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < CHAR_COUNT; i++) {
                int index = toInt(Arrays.copyOfRange(bits, i * BIT_COUNT, i * BIT_COUNT + BIT_COUNT));

                stringBuilder.append(index < CHARS.length ? CHARS[index] : ' ');
            }

            return stringBuilder.toString();
        }

        private String format(String string) {
            StringBuilder stringBuilder = new StringBuilder();
            char c = string.charAt(0);
            int t = c < '0' || c > '9' ? 1 : 2;

            for (int i = 0; i < CHAR_COUNT; i++) {
                c = string.charAt(i);

                if (c == ' ') {
                    return null;
                }

                if (t == 1) {
                    if (c == '*' || c == '/') {
                        return null;
                    }

                    stringBuilder.append(c);

                    t = 2;
                } else if (t == 2) {
                    if (c < '0' || c > '9') {
                        return null;
                    }

                    char d = i + 1 < CHAR_COUNT ? string.charAt(i + 1) : '\0';

                    if (c == '0') {
                        char b = stringBuilder.length() > 0 ? stringBuilder.charAt(stringBuilder.length() - 1) : '\0';

                        if (b >= '0' && b <= '9'
                                || d < '0' || d > '9') {
                            stringBuilder.append(c);
                        }
                    } else {
                        stringBuilder.append(c);
                    }

                    if (d < '0' || d > '9') {
                        t = 3;
                    }
                } else {
                    stringBuilder.append(" ")
                            .append(c)
                            .append(" ");

                    char d = i + 1 < CHAR_COUNT ? string.charAt(i + 1) : '\0';

                    if (d < '0' || d > '9') {
                        t = 1;
                    } else {
                        t = 2;
                    }
                }
            }

            return t == 3 ? stringBuilder.toString() : null;
        }

        private Integer evaluate(Integer b, String o, Integer a) {
            if (o.equals("+")) {
                return a + b;
            } else if (o.equals("-")) {
                return a - b;
            } else if (o.equals("*")) {
                return a * b;
            } else if (o.equals("/") && b != 0) {
                return a / b;
            }

            return null;
        }

    }

}
