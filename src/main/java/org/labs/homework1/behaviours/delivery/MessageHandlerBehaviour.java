package org.labs.homework1.behaviours.delivery;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.labs.homework1.agents.DeliveryAgent;
import org.labs.homework1.behaviours.delivery.clientHandling.ClientAcceptedBehaviour;
import org.labs.homework1.behaviours.delivery.clientHandling.ClientRejectedBehaviour;
import org.labs.homework1.behaviours.delivery.clientHandling.SearchMarketsBehaviour;
import org.labs.homework1.behaviours.delivery.clientHandling.SelectMarketsBehaviour;
import org.labs.homework1.behaviours.delivery.marketHandling.DeliverToClientBehaviour;
import org.labs.homework1.behaviours.delivery.marketHandling.DeliveryIssueBehaviour;
import org.labs.homework1.behaviours.delivery.marketHandling.IncompleteOrderBehaviour;

import static jade.lang.acl.ACLMessage.*;
import static java.util.Objects.nonNull;

public class MessageHandlerBehaviour extends CyclicBehaviour {
    private final DeliveryAgent deliveryAgent;

    public MessageHandlerBehaviour(DeliveryAgent a) {
        deliveryAgent = a;
    }

    @Override
    public void action() {
        final ACLMessage msg = myAgent.receive();

        if (nonNull(msg)) {
            switch (msg.getPerformative()) {
                case CFP -> handleCFP(msg);
                case ACCEPT_PROPOSAL -> handleAcceptProposal(msg);
                case REJECT_PROPOSAL -> handleRejectProposal(msg);
                case PROPOSE -> handlePropose(msg);
                case REFUSE -> handleRefuse(msg);
                case FAILURE -> handleFailure(msg);
                case INFORM -> handleInform(msg);
                default -> {
                }
            }
        } else {
            block();
        }
    }

    private void handleCFP(ACLMessage msg) {
        deliveryAgent.setClientCFP(msg);
        myAgent.addBehaviour(new SearchMarketsBehaviour(deliveryAgent));
    }

    private void handleAcceptProposal(ACLMessage msg) {
        myAgent.addBehaviour(new ClientAcceptedBehaviour(deliveryAgent, msg));
    }

    private void handleRejectProposal(ACLMessage msg) {
        myAgent.addBehaviour(new ClientRejectedBehaviour(deliveryAgent, msg));
    }

    private void handleRefuse(ACLMessage msg) {
        myAgent.addBehaviour(new IncompleteOrderBehaviour(deliveryAgent, msg));
    }

    private void handlePropose(ACLMessage msg) {
        myAgent.addBehaviour(new SelectMarketsBehaviour(deliveryAgent, msg));
    }

    private void handleInform(ACLMessage msg) {
        myAgent.addBehaviour(new DeliverToClientBehaviour(deliveryAgent, msg));
    }

    private void handleFailure(ACLMessage msg) {
        myAgent.addBehaviour(new DeliveryIssueBehaviour(deliveryAgent, msg));
    }
}
