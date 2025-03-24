package org.labs.homework1.behaviours;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import org.labs.homework1.agents.DeliveryAgent;

import java.util.List;
import java.util.Random;

import static jade.lang.acl.ACLMessage.*;

public class FulfilOrderBehaviour extends ContractNetResponder {

    private final DeliveryAgent deliveryAgent;
    static final ObjectMapper mapper = new JsonMapper();

    public FulfilOrderBehaviour(DeliveryAgent a) {
        super(a, MessageTemplate.MatchPerformative(CFP));
        deliveryAgent = a;
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
        // Extract order details from the CFP message.
        List<String> orderDetails = null;
        try {
            //noinspection unchecked
            orderDetails = (List<String>) mapper.readValue(cfp.getContent(), List.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(deliveryAgent.getLocalName() + " received CFP for order: " + orderDetails.toString());
        deliveryAgent.setOrder(orderDetails);

        // Compute final cost (for example, adding a service fee).
//        Random random = new Random();
//        double estimatedGroceryCost = random.nextDouble();  // Example estimated cost
//        double finalCost = estimatedGroceryCost + deliveryAgent.getDeliveryFee();
        deliveryAgent.addBehaviour(new PurchaseProductsBehaviour(deliveryAgent));
        // Busy waiting for market price to be computed (with timeout to avoid indefinite blocking)
        long startTime = System.currentTimeMillis();
        long timeout = 15000; // wait up to 15 seconds
        while (deliveryAgent.getMarketPrice() == null) {
            if (System.currentTimeMillis() - startTime > timeout) {
                throw new FailureException("Market price not received within timeout");
            }
            try {
                Thread.sleep(100); // sleep briefly to avoid a tight loop
            } catch (InterruptedException ie) {
                // Restore interrupted status and break if needed.
                Thread.currentThread().interrupt();
                break;
            }
        }
        double finalCost = deliveryAgent.getMarketPrice() + deliveryAgent.getDeliveryFee();

        // Create a proposal with the calculated cost.
        ACLMessage propose = cfp.createReply();
        propose.setPerformative(PROPOSE);
        propose.setContent(String.valueOf(finalCost));
        System.out.println(deliveryAgent.getLocalName() + " proposes a cost: " + finalCost);
        return propose;
    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
        System.out.println(deliveryAgent.getLocalName() + " received ACCEPT_PROPOSAL from " + accept.getSender());
        // Process the accepted proposal (e.g., initiate the delivery process)
        ACLMessage inform = accept.createReply();
        inform.setPerformative(INFORM);
        inform.setContent("Delivery confirmed. Order will be processed.");
        return inform;
    }

    @Override
    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        System.out.println(deliveryAgent.getLocalName() + " received REJECT_PROPOSAL from " + reject.getSender());
        // Optionally perform cleanup here.
    }
}
