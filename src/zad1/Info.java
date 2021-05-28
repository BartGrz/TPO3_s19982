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

    /**
     * when client is connected, its info about port and topics is subsribed is stored in map
     * @param port
     * @param category
     */
    public void linkPortWithCategory(int port, String category) {

        if (category == null) {
            set.put(port, new HashSet<>());
        } else {
            if (!checkIfAlreadySubscribed(port, category)) {
                set.get(port).add(category);
            }
        }
    }

    /**
     * deleting topic from set for chosen port (client)
     * @param port
     * @param category
     */
    public void deleteCategoryForPort(int port, String category) {
        set.get(port).remove(category);
    }

    /**
     * method checks if port(client) trying to subsribed to topic it is already subscribed to
     * @param port
     * @param category
     * @return true if topic is on client subsribed topics list
     */
    private boolean checkIfAlreadySubscribed(int port, String category) {
        return set.get(port).stream().anyMatch(s -> s.equals(category));
    }

    /**
     * adding topic to available topics list
     * @param category
     */
    public void addTopic(String category) {
        actualCategories.add(category);
    }
    /**
     * adding topic to available topics list
     * @param category
     */
    public void deleteTopic(String category) {
        if (actualCategories.stream().anyMatch(s -> s.equals(category))) {
            actualCategories.remove(category);
            System.out.println("category " + category + " removed");
        } else {

        }
    }

    /**
     * Checking if port(client) has updated list of possible topics
     * @param port
     */
    private void validateIfPortHasActualCategories(int port) {
        for (Iterator<String> it = set.get(port).iterator(); it.hasNext(); ) {
            String missing = it.next();

            if (!actualCategories.stream().anyMatch(s -> s.equals(missing))) {
                set.get(port).remove(missing);
            }
        }
    }

    /**
     * showing list of all topics choosen port(client) is subscribed to
     * @usedBy Client class only
     * @param port
     * @return
     */
    public List<String> showTopicsClientIsSubscribedTo(int port){
        validateIfPortHasActualCategories(port);
        return set.get(port).stream().collect(Collectors.toList());
    }
}
