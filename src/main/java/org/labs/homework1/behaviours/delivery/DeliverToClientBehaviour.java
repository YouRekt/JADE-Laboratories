package org.labs.homework1.behaviours.delivery;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.labs.homework1.agents.DeliveryAgent;

import static jade.lang.acl.ACLMessage.INFORM;

public class DeliverToClientBehaviour extends OneShotBehaviour {
    private final DeliveryAgent deliveryAgent;
    private final ACLMessage marketInform;

    public DeliverToClientBehaviour(DeliveryAgent a, ACLMessage msg) {
        deliveryAgent = a;
        marketInform = msg;
    }

    @Override
    public void action() {
        System.out.printf("[%s] Bought products from %s and delivered to the client %n", deliveryAgent.getLocalName(), marketInform.getSender().getLocalName());
        ACLMessage inform = deliveryAgent.getClientResponse().createReply();
        inform.setPerformative(INFORM);
        myAgent.send(inform);
    }
}
