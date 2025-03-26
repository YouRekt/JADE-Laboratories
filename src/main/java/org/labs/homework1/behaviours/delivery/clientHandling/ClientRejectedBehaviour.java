package org.labs.homework1.behaviours.delivery.clientHandling;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.labs.homework1.agents.DeliveryAgent;

import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;

public class ClientRejectedBehaviour extends OneShotBehaviour {
    private final DeliveryAgent deliveryAgent;

    public ClientRejectedBehaviour(DeliveryAgent a, ACLMessage msg) {
        deliveryAgent = a;
        deliveryAgent.setClientResponse(msg);
    }

    @Override
    public void action() {
        System.out.printf("[%s] %s rejected proposal %n", deliveryAgent.getLocalName(), deliveryAgent.getClientResponse().getSender().getLocalName());
        deliveryAgent.getMarketProposals().forEach((k, v) -> {
            ACLMessage rejectProposal = v.createReply();
            rejectProposal.setPerformative(REJECT_PROPOSAL);
            myAgent.send(rejectProposal);
        });

    }
}
