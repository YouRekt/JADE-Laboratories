package org.labs.homework1.behaviours.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import org.labs.exceptions.InvalidMessageContentException;
import org.labs.homework1.agents.ClientAgent;

import java.io.IOException;
import java.util.Vector;

import static jade.lang.acl.ACLMessage.*;

public class MakeOrderBehaviour extends ContractNetInitiator {

    static final ObjectMapper mapper = new JsonMapper();
    private final ClientAgent client;

    public MakeOrderBehaviour(final ClientAgent a) {
        super(a, makeOrder(a));
        client = a;
    }

    private static ACLMessage makeOrder(ClientAgent client) {
        try {
            final ACLMessage msg = new ACLMessage(CFP);
            msg.setContent(mapper.writeValueAsString(client.getOrder()));
            client.getDeliveryPlatforms().forEach(msg::addReceiver);
            return msg;
        } catch (final IOException e) {
            throw new InvalidMessageContentException(e);
        }
    }

    @Override
    protected void handlePropose(ACLMessage propose, Vector acceptances) {
        try {
            System.out.printf("[%s] Received proposal from %s with price %.2f %n", client.getLocalName(), propose.getSender().getLocalName(), mapper.readValue(propose.getContent(), Double.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void handleFailure(ACLMessage failure) {
        System.out.printf("[%s] Received failure from %s %n", client.getLocalName(), failure.getSender().getLocalName());
    }

    @Override
    protected void handleRefuse(ACLMessage refuse) {
        System.out.printf("[%s] Received refusal from %s %n", client.getLocalName(), refuse.getSender().getLocalName());
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        System.out.printf("[%s] %s delivered the order %n", client.getLocalName(), inform.getSender().getLocalName());
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        ACLMessage bestProposal = null;
        double bestPrice = Double.MAX_VALUE;

        for (Object obj : responses) {
            ACLMessage response = (ACLMessage) obj;
            if (response.getPerformative() == PROPOSE) {
                double price = Double.parseDouble(response.getContent());
                if (price < bestPrice) {
                    bestPrice = price;
                    bestProposal = response;
                }
            }
        }

        for (Object obj : responses) {
            ACLMessage response = (ACLMessage) obj;
            ACLMessage reply = response.createReply();
            if (response.equals(bestProposal)) {
                reply.setPerformative(ACCEPT_PROPOSAL);
                System.out.printf("[%s] Accepting proposal from %s %n", client.getLocalName(), response.getSender().getLocalName());
            } else {
                reply.setPerformative(REJECT_PROPOSAL);
            }
            //noinspection unchecked
            acceptances.add(reply);
        }
    }
}
