package myTest;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

public class AskMe {

    @Getter
    @Setter
    private Set<Integer> clients_ports = new HashSet<>();


}
