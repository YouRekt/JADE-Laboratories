package org.labs.homework1.behaviours.delivery.clientHandling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.labs.homework1.agents.DeliveryAgent;

import static jade.lang.acl.ACLMessage.ACCEPT_PROPOSAL;
import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;

public class ClientAcceptedBehaviour extends OneShotBehaviour {
    static final ObjectMapper mapper = new JsonMapper();
    private final DeliveryAgent deliveryAgent;

    public ClientAcceptedBehaviour(DeliveryAgent a, ACLMessage msg) {
        deliveryAgent = a;
        deliveryAgent.setClientResponse(msg);
    }

    @Override
    public void action() {
        System.out.printf("[%s] %s accepted proposal %n", deliveryAgent.getLocalName(), deliveryAgent.getClientResponse().getSender().getLocalName());
        deliveryAgent.getMarketProposals().forEach((k, v) -> {
            if (deliveryAgent.getSelectedMarketProducts().containsKey(k)) {
                ACLMessage acceptProposal = v.createReply();
                acceptProposal.setPerformative(ACCEPT_PROPOSAL);
                try {
                    acceptProposal.setContent(mapper.writeValueAsString(deliveryAgent.getSelectedMarketProducts().get(k)));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                myAgent.send(acceptProposal);
            } else {
                ACLMessage rejectProposal = v.createReply();
                rejectProposal.setPerformative(REJECT_PROPOSAL);
                myAgent.send(rejectProposal);
            }
        });
    }
}
