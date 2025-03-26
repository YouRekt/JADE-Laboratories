package org.labs.homework1.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import lombok.Getter;
import lombok.Setter;
import org.labs.exceptions.InvalidServiceSpecification;
import org.labs.homework1.behaviours.delivery.MessageHandlerBehaviour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DeliveryAgent extends Agent {
    private Double deliveryFee;
    private List<AID> markets = new ArrayList<>();
    private List<String> order;
    private Double marketPrice = Double.MIN_VALUE;
    private Double deliveryPrice = Double.MIN_VALUE;
    private Map<AID, ACLMessage> marketProposals = new HashMap<>();
    private Map<AID, List<String>> selectedMarketProducts = new HashMap<>();
    private ACLMessage clientCFP = null;
    private ACLMessage clientResponse = null;

    @Override
    protected void setup() {
        final Object[] args = getArguments();

        deliveryFee = (Double) args[0];

        registerDeliveryService();
        addBehaviour(new MessageHandlerBehaviour(this));

        System.out.printf("[%s] My delivery fee is %.2f %n", getLocalName(), deliveryFee);
    }

    private void registerDeliveryService() {
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("DeliveryService");
        serviceDescription.setName("Delivery");
        serviceDescription.setOwnership(getLocalName());

        try {
            final DFAgentDescription agentServices = new DFAgentDescription();
            agentServices.addServices(serviceDescription);
            DFService.register(this, agentServices);
        } catch (final FIPAException e) {
            throw new InvalidServiceSpecification(e);
        }
    }
}
