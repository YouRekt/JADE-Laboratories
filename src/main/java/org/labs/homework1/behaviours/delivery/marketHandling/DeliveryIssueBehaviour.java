package org.labs.homework1.behaviours.delivery.marketHandling;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.labs.homework1.agents.DeliveryAgent;

import static jade.lang.acl.ACLMessage.FAILURE;

public class DeliveryIssueBehaviour extends OneShotBehaviour {
    private final DeliveryAgent deliveryAgent;
    private final ACLMessage failure;

    public DeliveryIssueBehaviour(DeliveryAgent a, ACLMessage msg) {
        deliveryAgent = a;
        failure = msg;
    }

    @Override
    public void action() {
        System.out.printf("[%s] There was an issue while buying products from %s %n", deliveryAgent.getLocalName(), failure.getSender().getLocalName());
        ACLMessage clientFailure = deliveryAgent.getClientResponse().createReply();
        clientFailure.setPerformative(FAILURE);
        myAgent.send(clientFailure);
    }
}
