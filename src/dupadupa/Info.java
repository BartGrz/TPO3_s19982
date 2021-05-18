package dupadupa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.channels.SelectionKey;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
public class Info {
    @Getter
    private Map<SelectionKey, String> subscriptions = new HashMap<>();
    @Getter
    private Map<Integer, String> clients = new HashMap<>();
    @Getter
    @Setter
    private Client client;
    private String category;
    @Getter
    @Setter
    private String adminMessage;
    private int port;
    @Getter
    private Map<Integer, Set<String>> set = new HashMap();
    Set<String> categories = new HashSet<>();

    @Getter
    private List<SelectionKey> keysInMemory = new ArrayList<>();

    public void linkPortWithKey(int port, String category) {

        System.out.println(set + " port = " + port);
        if (category == null) {
            set.put(port, new HashSet<String>());
        } else  {
            set.get(port).add(category);

        }
    }

    public Set<String> addToPort(int port, String category) {

        set.get(port).add(category);

        return set.get(port);
    }

}
