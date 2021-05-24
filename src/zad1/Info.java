package zad1;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class gathers the info sended by clients and admin to server, and helps server to broadcast messages from admin to proper clients
 */
public class Info {

    @Getter
    @Setter
    private String adminChosenCategory;
    @Getter
    @Setter
    private String adminMessage;
    @Getter
    private Map<Integer, Set<String>> set = new HashMap();
    @Getter
    private List<String> actualCategories = new ArrayList<>(Arrays.asList("politics", "celebrities", "sport", "economy"));

    public void linkPortWithCategory(int port, String category) {

        if (category == null) {
            set.put(port, new HashSet<>());
        } else {
            if (!checkIfAlreadySubscribed(port, category)) {
                set.get(port).add(category);
            }
        }
    }

    public void deleteCategoryForPort(int port, String category) {
        set.get(port).remove(category);

    }

    private boolean checkIfAlreadySubscribed(int port, String category) {
        return set.get(port).stream().anyMatch(s -> s.equals(category));
    }

    public void addTopic(String category) {
        actualCategories.add(category);
        System.out.println("category:" + category + " added  to list ");
    }

    public void deleteTopic(String category) {
        if (actualCategories.stream().anyMatch(s -> s.equals(category))) {
            actualCategories.remove(category);
            System.out.println("category:" + category + " deleted from list ");
        } else {
            System.out.println("category is not on list");
        }
    }

    private void validateIfPortHasActualCategories(int port) {
        for (Iterator<String> it = set.get(port).iterator(); it.hasNext(); ) {
            String missing = it.next();

            if (!actualCategories.stream().anyMatch(s -> s.equals(missing))) {
                set.get(port).remove(missing);
            }
        }
    }
    public List<String> showTopicsClientIsSubscribedTo(int port){
        validateIfPortHasActualCategories(port);
        return set.get(port).stream().collect(Collectors.toList());
    }

}
