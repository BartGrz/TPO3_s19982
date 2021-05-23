package zad1;

import lombok.Getter;
import lombok.Setter;
import java.util.*;

public class Info {

    @Getter
    @Setter
    private String adminChosenCategory;
    @Getter
    @Setter
    private String adminMessage;
    @Getter
    private Map<Integer, Set<String>> set = new HashMap();

    public void linkPortWithCategory(int port, String category) {

        if (category == null) {
            set.put(port, new HashSet<>());
        } else {
            set.get(port).add(category);
        }
    }
    public void deleteCategoryForPort(int port, String category) {

        set.get(port).remove(category);
        System.out.println("deleted " + category + " from port " + port);

    }
}
