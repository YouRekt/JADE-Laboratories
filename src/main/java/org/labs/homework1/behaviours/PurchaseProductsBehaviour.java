package org.labs.homework1.behaviours;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import org.labs.exceptions.InvalidMessageContentException;
import org.labs.homework1.agents.DeliveryAgent;

import java.io.IOException;
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
        System.out.println("Received confirmation from " + inform.getSender() + ": " + inform.getContent());
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        // Assume DeliveryAgent provides the client's order as a list of item names.
        // For example: ["milk", "coffee", "rice"]
        List<String> remainingItems = new ArrayList<>(deliveryAgent.getOrder());

        // Helper class to store each proposal's data.
        class ProposalData {
            ACLMessage msg;
            Set<String> offeredItems;
            double totalCost;
        }

        List<ProposalData> proposals = new ArrayList<>();

        // Process each response (we only consider PROPOSE responses).
        for (Object obj : responses) {
            ACLMessage response = (ACLMessage) obj;
            if (response.getPerformative() == PROPOSE) {
                try {
                    // Parse the JSON content to get a map of product -> price.
                    //noinspection unchecked
                    Map<String, Double> marketOffer = (Map<String, Double>) mapper.readValue(response.getContent(), Map.class);
                    Set<String> offered = new HashSet<>();
                    double cost = 0.0;
                    // For every item remaining in the client's order,
                    // check if the market can supply it.
                    for (String item : remainingItems) {
                        if (marketOffer.containsKey(item)) {
                            offered.add(item);
                            cost += marketOffer.get(item);
                        }
                    }
                    ProposalData pd = new ProposalData();
                    pd.msg = response;
                    pd.offeredItems = offered;
                    pd.totalCost = cost;
                    proposals.add(pd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Set to store proposals we will accept.
        Set<ACLMessage> acceptedMessages = new HashSet<>();

        // Iterate until we've covered all order items or no proposal can help.
        while (!remainingItems.isEmpty()) {
            ProposalData best = null;
            // Find the proposal that offers the largest number of items (and lowest cost on tie)
            for (ProposalData pd : proposals) {
                // Only consider proposals that cover at least one still-missing item.
                Set<String> intersection = new HashSet<>(pd.offeredItems);
                intersection.retainAll(remainingItems);
                if (!intersection.isEmpty()) {
                    if (best == null) {
                        best = pd;
                    } else {
                        int bestCount = new HashSet<>(best.offeredItems).retainAll(remainingItems) ? best.offeredItems.size() : 0;
                        int pdCount = intersection.size();
                        if (pdCount > bestCount) {
                            best = pd;
                        } else if (pdCount == bestCount && pd.totalCost < best.totalCost) {
                            best = pd;
                        }
                    }
                }
            }
            if (best == null) {
                // No proposal covers any of the remaining items.
                break;
            }
            acceptedMessages.add(best.msg);
            // Remove the items provided by this market from the remaining order.
            remainingItems.removeAll(best.offeredItems);
            // Remove the selected proposal from further consideration.
            proposals.remove(best);
        }

        // Busy waiting for user confirmation of the best proposal
        long waitStart = System.currentTimeMillis();
        long waitTimeout = 10000; // wait 10 seconds for user input
        while (!deliveryAgent.isUserConfirmed() && System.currentTimeMillis() - waitStart < waitTimeout) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
        // If the user did not confirm, then clear accepted proposals (i.e. reject all)
        if (!deliveryAgent.isUserConfirmed()) {
            acceptedMessages.clear();
            System.out.printf("[%s] User did not confirm. Rejecting all proposals.%n", deliveryAgent.getLocalName());
        } else {
            System.out.printf("[%s] User confirmed best proposals.%n", deliveryAgent.getLocalName());
        }

        // Create replies for every response.
        for (Object obj : responses) {
            ACLMessage response = (ACLMessage) obj;
            ACLMessage reply = response.createReply();
            if (acceptedMessages.contains(response)) {
                reply.setPerformative(ACCEPT_PROPOSAL);
            } else {
                reply.setPerformative(REJECT_PROPOSAL);
            }
            acceptances.add(reply);
        }

        if (!remainingItems.isEmpty()) {
            System.out.printf("[%s] Cannot pick up items: %s%n", deliveryAgent.getLocalName(), remainingItems);
        } else {
            System.out.printf("[%s] Successfully selected proposals for all items.%n", deliveryAgent.getLocalName());
        }
    }
}
