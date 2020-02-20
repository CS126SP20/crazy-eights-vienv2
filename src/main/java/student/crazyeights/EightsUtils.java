package student.crazyeights;

import java.util.*;

public final class EightsUtils {

    private static final String[] botNames = {"Liam", "Emma", "Noah", "Olivia", "William", "Ava",
            "James", "Isabella", "Oliver", "Sophia", "Benjamin", "Charlotte", "Elijah", "Mia",
            "Lucas", "Amelia", "Mason", "Harper", "Logan", "Evelyn"};

    private EightsUtils() {
    }

    /***************************************************************************************
     *    Title: Finding the Highest Value in a Java Map
     *    Author: baeldung
     *    Date: September 24, 2019
     *    Availability: https://www.baeldung.com/java-find-map-max
     *
     ***************************************************************************************/

    public static <K, V extends Comparable<V>> V maxValueInMap(Map<K, V> map) {
        Map.Entry<K, V> maxEntry = Collections.max(map.entrySet(), Comparator.comparing(Map.Entry::getValue));
        return maxEntry.getValue();
    }

    public static String generateBotNames(Random random) {
        List<String> namesToChooseFrom = Arrays.asList(botNames);

        /*
        List<String> namesChosen = new ArrayList<>();
        String randomName;
        for (int i = 0; i < noOfBots; i++) {
            randomName = namesToChooseFrom.get(random.nextInt(namesToChooseFrom.size()));
            namesToChooseFrom.remove(randomName);
            namesChosen.add(randomName);
        }
        return namesChosen;
        */

        return namesToChooseFrom.get(random.nextInt(namesToChooseFrom.size()));
    }

}
