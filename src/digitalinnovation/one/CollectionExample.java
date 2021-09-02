package digitalinnovation.one;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CollectionExample {

    public static void main(String[] args) {
        // List<String> names = Arrays.asList("Ricky", "Rafaela", "Marco", "Diego", "Akira", "Akita");
        Collection<String> names = Set.of("Ricky", "Rafaela", "Marco", "Diego", "Akira", "Akita");
        System.out.println(names);
    }
}
