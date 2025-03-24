package org.labs.homework1.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceDescriptor;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import lombok.Getter;
import lombok.Setter;
import org.labs.exceptions.InvalidServiceSpecification;
import org.labs.homework1.behaviours.FulfilOrderBehaviour;
import org.labs.homework1.behaviours.SearchMarketsBehaviour;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DeliveryAgent extends Agent {
    @Setter
    private Double deliveryFee;
    @Setter
    private List<AID> markets;
    @Setter
    private List<String> order;
    @Setter
    private Double marketPrice;
    @Setter
    private volatile boolean userConfirmed;


    @Override
    protected void setup() {
        final Object[] args = getArguments();

        deliveryFee = (Double) args[0];
        markets = new ArrayList<>();
        marketPrice = null;
        userConfirmed = false;

        registerDeliveryService();
        addBehaviour(new SearchMarketsBehaviour(this));
        addBehaviour(new FulfilOrderBehaviour(this));

        System.out.printf("[%s] I'm ready to register my services! My delivery fee is %.2f %n", getLocalName(), deliveryFee);
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
