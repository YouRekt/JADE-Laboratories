package org.labs.homework1.agents;

import jade.core.AID;
import jade.core.Agent;
import lombok.Getter;
import lombok.Setter;
import org.labs.homework1.behaviours.client.SearchDeliverersBehaviour;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ClientAgent extends Agent {
    private List<String> order;
    private List<AID> deliveryPlatforms = new ArrayList<>();


    @Override
    protected void setup() {
        final Object[] args = getArguments();

        //noinspection unchecked
        order = (List<String>) args[0];

        /*
 Uncomment if running sniffer :)
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
*/
        addBehaviour(new SearchDeliverersBehaviour(this));

        System.out.printf("[%s] I want to order grocery delivery! I want to order: %s %n", getLocalName(), order);
    }
}
