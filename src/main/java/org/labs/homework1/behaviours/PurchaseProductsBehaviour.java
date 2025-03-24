package org.labs.homework1.behaviours;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import org.labs.homework1.agents.DeliveryAgent;

import java.util.*;

import static jade.lang.acl.ACLMessage.*;

public class PurchaseProductsBehaviour extends ContractNetInitiator {
    private final DeliveryAgent deliveryAgent;
    static final ObjectMapper mapper = new JsonMapper();

    public PurchaseProductsBehaviour(DeliveryAgent a) {
        super(a, purchaseProducts(a));
        deliveryAgent = a;
    }

    private static ACLMessage purchaseProducts(DeliveryAgent deliveryAgent) {
        System.out.printf("[%s] Querying the markets for prices %n", deliveryAgent.getLocalName());
        final ACLMessage msg = new ACLMessage(CFP);
        msg.setContent("Show me your wares.");
        deliveryAgent.getMarkets().forEach(msg::addReceiver);
        return msg;
    }

    @Override
    protected void handlePropose(ACLMessage propose, Vector acceptances) {
        try {
            System.out.printf("[%s] Received product prices from %s: %s %n", deliveryAgent.getLocalName(), propose.getSender(), mapper.readValue(propose.getContent(), Map.class).toString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void handleRefuse(ACLMessage refuse) {
        System.out.printf("[%s] Received refusal from %s %n", deliveryAgent.getLocalName(), refuse.getSender());
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        System.out.printf("[%s] Received confirmation from %s: %s %n", deliveryAgent.getLocalName(), inform.getSender(), inform.getContent());
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        Random random = new Random();
        int choice = random.nextInt(responses.size());
        int i = 0;
        for (Object obj : responses) {
            ACLMessage response = (ACLMessage) obj;
            ACLMessage reply = response.createReply();
            if (i == choice) {
                reply.setPerformative(ACCEPT_PROPOSAL);
                try {
                    reply.setContent(mapper.writeValueAsString(deliveryAgent.getOrder()));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                System.out.printf("[%s] Accepting proposal from %s %n", deliveryAgent.getLocalName(), response.getSender());
                try {
                    //noinspection unchecked
                    Map<String, Double> products = (Map<String, Double>) mapper.readValue(response.getContent(), Map.class);
                    Double price = deliveryAgent.getOrder().stream().map(products::get).reduce(0.0, Double::sum);
                    deliveryAgent.setMarketPrice(price);
                    deliveryAgent.setPriceReceived(true);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            } else {
                reply.setPerformative(REJECT_PROPOSAL);
            }
            acceptances.add(reply);
            i++;
        }
    }
}
