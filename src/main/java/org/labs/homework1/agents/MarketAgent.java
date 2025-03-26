package org.labs.homework1.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import lombok.Getter;
import lombok.Setter;
import org.labs.exceptions.InvalidServiceSpecification;
import org.labs.homework1.behaviours.market.MarketBehaviour;

import java.util.Map;

@Setter
@Getter
public class MarketAgent extends Agent {
    private Map<String, Double> products;

    @Override
    protected void setup() {
        final Object[] args = getArguments();

        //noinspection unchecked
        products = (Map<String, Double>) args[0];

        registerMarketService();
        addBehaviour(new MarketBehaviour(this));

        System.out.printf("[%s] My products are %s %n", getLocalName(), products.toString());
    }

    private void registerMarketService() {
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("Market");
        serviceDescription.setName(getLocalName());
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
