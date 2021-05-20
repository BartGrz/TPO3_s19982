package dupadupa;

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

    public void linkPortWithKey(int port, String category) {

        if (category == null) {
            set.put(port, new HashSet<String>());
        } else {
            set.get(port).add(category);
        }
    }
}
