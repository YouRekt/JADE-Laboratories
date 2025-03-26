package org.labs.homework1.behaviours.market;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.labs.homework1.agents.MarketAgent;

import static jade.lang.acl.ACLMessage.*;
import static java.util.Objects.nonNull;

public class MarketBehaviour extends CyclicBehaviour {
    static final ObjectMapper mapper = new JsonMapper();
    private final MarketAgent marketAgent;

    public MarketBehaviour(MarketAgent a) {
        marketAgent = a;
    }

    @Override
    public void action() {
        final ACLMessage msg = myAgent.receive();

        if (nonNull(msg)) {
            switch (msg.getPerformative()) {
                case CFP -> handleCFP(msg);
                case ACCEPT_PROPOSAL -> handleAcceptProposal(msg);
                case REJECT_PROPOSAL -> handleRejectProposal(msg);
                default -> {
                }
            }
        } else {
            block();
        }
    }

    private void handleCFP(ACLMessage cfp) {
        System.out.printf("[%s] Responding to %s with products and their prices %n", marketAgent.getLocalName(), cfp.getSender().getLocalName());
        ACLMessage propose = cfp.createReply();
        propose.setPerformative(PROPOSE);
        try {
            propose.setContent(mapper.writeValueAsString(marketAgent.getProducts()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        myAgent.send(propose);
    }

    private void handleAcceptProposal(ACLMessage accept) {
        System.out.printf("[%s] %s purchased products %n", marketAgent.getLocalName(), accept.getSender().getLocalName());

        ACLMessage inform = accept.createReply();
        inform.setPerformative(INFORM);

        myAgent.send(inform);
    }

    private void handleRejectProposal(ACLMessage reject) {
        System.out.printf("[%s] %s didn't make a purchase %n", marketAgent.getLocalName(), reject.getSender().getLocalName());
    }
}
