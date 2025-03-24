package org.labs.homework1.agents;

import jade.core.AID;
import jade.core.Agent;
import lombok.Getter;
import lombok.Setter;
import org.labs.homework1.behaviours.SearchDeliverersBehaviour;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ClientAgent extends Agent {

    @Setter
    private List<String> order;
    @Setter
    private List<AID> deliveryPlatforms;


    @Override
    protected void setup() {
        final Object[] args = getArguments();

        order = (List<String>) args[0];
        deliveryPlatforms = new ArrayList<>();

        addBehaviour(new SearchDeliverersBehaviour(this));

        System.out.printf("[%s] I want to order grocery delivery! I want to order: %s %n", getLocalName(), order);
    }
}
