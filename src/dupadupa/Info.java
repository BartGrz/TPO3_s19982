package dupadupa;

import lombok.Getter;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class Info {
    @Getter
    private Map<SelectionKey, String> subscriptions = new HashMap<>();
    @Getter
    private Map<SelectionKey, String> clients = new HashMap<>();

    public void addToMap(SelectionKey key, String subsc ) {
        subscriptions.put(key, subsc);
        System.out.println("Added to subsc " + subsc);

    }

    public void gatherConnectionInfo(SelectionKey key, String client ){
        clients.put(key,client);
        System.out.println("Added to clients " + clients);
    }

    public String getKeyValueClients(SelectionKey key) {

        if (validateServerClientsList(key)) {
            return clients.get(key);
        }else {
           return "";
        }
    }
    public String getKeyValueSubs(SelectionKey key) {

        if (validateServerSubscribentsList(key)) {
            return subscriptions.get(key);
        }else {
            return "";
        }
    }

    private boolean validateServerClientsList(SelectionKey key) {

       return clients.keySet().stream().anyMatch(k->k.equals(key));

    }
    private boolean validateServerSubscribentsList(SelectionKey key) {

        return subscriptions.keySet().stream().anyMatch(k->k.equals(key));

    }

}
