package org.labs.homework1.behaviours;

import jade.core.AID;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.labs.exceptions.InvalidServiceSpecification;
import org.labs.homework1.agents.ClientAgent;

import java.util.Arrays;
import java.util.List;

public class SearchDeliverersBehaviour extends WakerBehaviour {

    private final ClientAgent clientAgent;

    public SearchDeliverersBehaviour(final ClientAgent a) {
        super(a, 3000);
        clientAgent = a;
    }

    @Override
    protected void onWake() {
        System.out.printf("[%s] I'm searching for available delivery platforms... %n", clientAgent.getLocalName());

        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("DeliveryService");
        serviceDescription.setName("Delivery");

        try {
            final DFAgentDescription agentServices = new DFAgentDescription();
            agentServices.addServices(serviceDescription);
            final DFAgentDescription[] availableServices = DFService.search(clientAgent, agentServices);

            final List<AID> deliveryPlatforms = Arrays.stream(availableServices)
                    .map(DFAgentDescription::getName)
                    .toList();

            deliveryPlatforms.forEach(
                    platform -> System.out.printf("[%s] Found delivery platform: %s %n", clientAgent.getLocalName(), platform));
            clientAgent.getDeliveryPlatforms().addAll(deliveryPlatforms);
        } catch (FIPAException e) {
            throw new InvalidServiceSpecification(e);
        } finally {
            clientAgent.addBehaviour(new MakeOrderBehaviour(clientAgent));
        }
    }
}
