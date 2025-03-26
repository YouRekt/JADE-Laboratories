package org.labs.homework1.behaviours.delivery.marketHandling;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.labs.homework1.agents.DeliveryAgent;

import static jade.lang.acl.ACLMessage.REFUSE;

public class IncompleteOrderBehaviour extends OneShotBehaviour {
    private final DeliveryAgent deliveryAgent;
    private final ACLMessage refuse;

    public IncompleteOrderBehaviour(DeliveryAgent a, ACLMessage msg) {
        deliveryAgent = a;
        refuse = msg;
    }

    @Override
    public void action() {
        System.out.printf("[%s] %s didn't send products %n", deliveryAgent.getLocalName(), refuse.getSender().getLocalName());
        ACLMessage clientRefuse = deliveryAgent.getClientResponse().createReply();
        clientRefuse.setPerformative(REFUSE);
        myAgent.send(clientRefuse);
    }
}
