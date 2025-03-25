package org.labs.homework1.behaviours;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import org.labs.homework1.agents.DeliveryAgent;

import java.util.*;

import static jade.lang.acl.ACLMessage.*;

public class PurchaseProductsBehaviour extends AchieveREInitiator {
    private final DeliveryAgent deliveryAgent;
    static final ObjectMapper mapper = new JsonMapper();

    public PurchaseProductsBehaviour(DeliveryAgent a, FulfilOrderBehaviour b) {
        super(a, purchaseProducts(a, b));
        deliveryAgent = a;
    }

    private static ACLMessage purchaseProducts(DeliveryAgent deliveryAgent, FulfilOrderBehaviour b) {
        System.out.printf("[%s] Received the order. Querying the markets for prices %n", deliveryAgent.getLocalName());
        final ACLMessage msgCFP = (ACLMessage) b.getDataStore().get(b.CFP_KEY);
        try {
            //noinspection unchecked
            deliveryAgent.setOrder((List<String>) mapper.readValue(msgCFP.getContent(), List.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        final ACLMessage msg = new ACLMessage(REQUEST);
        msg.setContent("Show me your wares.");
        deliveryAgent.getMarkets().forEach(msg::addReceiver);
        return msg;
    }

    @Override
    protected void handleRefuse(ACLMessage refuse) {
        System.out.printf("[%s] Received refusal from %s %n", deliveryAgent.getLocalName(), refuse.getSender());
    }

//    @Override
//    protected void handleInform(ACLMessage inform) {
//        System.out.printf("[%s] Received confirmation from %s: %s %n", deliveryAgent.getLocalName(), inform.getSender(), inform.getContent());
//    }

    @Override
    protected void handleAllResponses(Vector responses) {
        Random random = new Random();
        int choice = random.nextInt(responses.size());
        int i = 0;
        for (Object obj : responses) {
            ACLMessage response = (ACLMessage) obj;
            if (i == choice) {
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
                break;
            }
            i++;
        }
        Double finalCost = deliveryAgent.getMarketPrice() + deliveryAgent.getDeliveryFee();
        final ACLMessage msgCFP = (ACLMessage) this.getDataStore().get(((FulfilOrderBehaviour) parent).CFP_KEY);
        final ACLMessage clientResponse = msgCFP.createReply();
        clientResponse.setContent(finalCost.toString());
        clientResponse.setPerformative(PROPOSE);
        this.getDataStore().put(((FulfilOrderBehaviour) parent).REPLY_KEY, clientResponse);
    }
}
