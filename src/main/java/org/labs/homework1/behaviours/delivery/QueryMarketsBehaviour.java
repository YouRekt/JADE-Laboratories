package org.labs.homework1.behaviours.delivery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.labs.homework1.agents.DeliveryAgent;

import java.util.List;

public class QueryMarketsBehaviour extends OneShotBehaviour {
    private static final ObjectMapper mapper = new JsonMapper();
    private final DeliveryAgent deliveryAgent;

    public QueryMarketsBehaviour(DeliveryAgent a) {
        deliveryAgent = a;
    }

    @Override
    public void action() {
        purchaseProducts();
    }

    private void purchaseProducts() {
        System.out.printf("[%s] Querying the markets for prices %n", deliveryAgent.getLocalName());
        try {
            //noinspection unchecked
            deliveryAgent.setOrder((List<String>) mapper.readValue(deliveryAgent.getClientCFP().getContent(), List.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        final ACLMessage marketCFP = new ACLMessage(ACLMessage.CFP);
        marketCFP.setContent("Show me your wares.");
        marketCFP.setConversationId(deliveryAgent.getLocalName());
        deliveryAgent.getMarkets().forEach(marketCFP::addReceiver);
        myAgent.send(marketCFP);
    }
}
