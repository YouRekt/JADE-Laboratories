package org.labs.homework1.behaviours.delivery.clientHandling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.labs.homework1.agents.DeliveryAgent;
import org.labs.homework1.behaviours.delivery.marketHandling.IncompleteOrderBehaviour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.ACLMessage.REFUSE;

public class SelectMarketsBehaviour extends OneShotBehaviour {
    private static final ObjectMapper mapper = new JsonMapper();
    private final DeliveryAgent deliveryAgent;
    private final ACLMessage propose;

    public SelectMarketsBehaviour(DeliveryAgent a, ACLMessage msg) {
        deliveryAgent = a;
        propose = msg;
    }

    @Override
    public void action() {
        if (!deliveryAgent.getMarkets().contains(propose.getSender())) {
            System.out.printf("[%s] Invalid market proposal, market not found! %n", deliveryAgent.getLocalName());
            return;
        }
        System.out.printf("[%s] Processing response from %s %n", deliveryAgent.getLocalName(), propose.getSender().getLocalName());
        deliveryAgent.getMarketProposals().put(propose.getSender(), propose);
        // If proposals from all markets are received, process them.
        if (deliveryAgent.getMarketProposals().size() == deliveryAgent.getMarkets().size()) {
            Map<AID, Map<String, Double>> marketPrices = new HashMap<>();
            deliveryAgent.getMarketProposals().forEach((market, msg) -> {
                try {
                    //noinspection unchecked
                    Map<String, Double> marketPrice = (Map<String, Double>) mapper.readValue(msg.getContent(), Map.class);
                    marketPrices.put(market, marketPrice);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
            List<String> remainingOrder = new ArrayList<>(deliveryAgent.getOrder());
            Double totalCost = 0.0;
//            Map<AID, List<String>> fromWhichMarketProducts = new HashMap<>();

            while (!remainingOrder.isEmpty()) {
                AID bestMarket = null;
                int maxCovered = 0;
                Double minPrice = Double.MAX_VALUE;
                List<String> selectedProducts = new ArrayList<>();

                for (Map.Entry<AID, Map<String, Double>> entry : marketPrices.entrySet()) {
                    Map<String, Double> products = entry.getValue();
                    List<String> availableProducts = new ArrayList<>();
                    Double price = 0.0;
                    for (String item : remainingOrder) {
                        if (products.containsKey(item)) {
                            availableProducts.add(item);
                            price += products.get(item);
                        }
                    }
                    int count = availableProducts.size();
                    if (count > maxCovered) {
                        maxCovered = count;
                        minPrice = price;
                        bestMarket = entry.getKey();
                        selectedProducts = availableProducts;
                    } else if (count == maxCovered && count > 0) {
                        if (price < minPrice) {
                            bestMarket = entry.getKey();
                            selectedProducts = availableProducts;
                            minPrice = price;
                        }
                    }
                }

                if (maxCovered == 0 || selectedProducts.isEmpty() || bestMarket == null) {
                    System.out.printf("[%s] Unable to fulfill order for items: %s%n", deliveryAgent.getLocalName(), remainingOrder);
                    ACLMessage clientRefuse = deliveryAgent.getClientCFP().createReply();
                    clientRefuse.setPerformative(REFUSE);
                    myAgent.send(clientRefuse);
                    return;
                }

                remainingOrder.removeAll(selectedProducts);
                totalCost += minPrice;
                deliveryAgent.setMarketPrice(minPrice);
                System.out.printf("[%s] Selected market %s for products: %s = %.2f %n", deliveryAgent.getLocalName(), bestMarket.getLocalName(), selectedProducts, minPrice);
//                fromWhichMarketProducts.put(bestMarket, selectedProducts);
                deliveryAgent.getSelectedMarketProducts().put(bestMarket, selectedProducts);
            }

            totalCost += deliveryAgent.getDeliveryFee();
            System.out.printf("[%s] Total cost: %.2f %n", deliveryAgent.getLocalName(), totalCost);

            ACLMessage propose = deliveryAgent.getClientCFP().createReply();
            propose.setPerformative(PROPOSE);
            propose.setContent(totalCost.toString());
            myAgent.send(propose);
        }
    }
}
