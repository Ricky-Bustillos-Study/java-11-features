package digitalinnovation.one;

import java.util.function.Function;

public class InferenceLambda {

    public static void main(String[] args) {
        Function<Integer, Double> divideBy2 = (var number) -> number / 2.0;

        System.out.println(divideBy2.apply(10));
    }
}
