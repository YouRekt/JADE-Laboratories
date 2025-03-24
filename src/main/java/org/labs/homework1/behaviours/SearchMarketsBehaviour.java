package org.labs.homework1.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.labs.exceptions.InvalidServiceSpecification;
import org.labs.homework1.agents.DeliveryAgent;

import java.util.Arrays;
import java.util.List;

public class SearchMarketsBehaviour extends WakerBehaviour {
    private final DeliveryAgent deliveryAgent;

    public SearchMarketsBehaviour(DeliveryAgent a) {
        super(a, 1000);
        deliveryAgent = a;
    }

    @Override
    protected void onWake() {
        System.out.printf("[%s] I'm searching for available markets... %n", deliveryAgent.getLocalName());

        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("Market");

        try {
            final DFAgentDescription agentServices = new DFAgentDescription();
            agentServices.addServices(serviceDescription);
            final DFAgentDescription[] availableServices = DFService.search(deliveryAgent, agentServices);

            final List<AID> markets = Arrays.stream(availableServices)
                    .map(DFAgentDescription::getName)
                    .toList();

            markets.forEach(
                    platform -> System.out.printf("[%s] Found market: %s %n", deliveryAgent.getLocalName(), platform));
            deliveryAgent.getMarkets().addAll(markets);
        } catch (FIPAException e) {
            throw new InvalidServiceSpecification(e);
        }
        finally {
            myAgent.addBehaviour(new PurchaseProductsBehaviour(deliveryAgent));
        }
    }
}
