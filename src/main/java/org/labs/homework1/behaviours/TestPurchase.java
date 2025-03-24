package org.labs.homework1.behaviours;

import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import org.labs.homework1.agents.DeliveryAgent;

public class TestPurchase extends WakerBehaviour {
    private final DeliveryAgent deliveryAgent;
    public TestPurchase(DeliveryAgent a) {
        super(a, 6000);
        deliveryAgent = a;
    }

    @Override
    protected void onWake() {
        myAgent.addBehaviour(new PurchaseProductsBehaviour(deliveryAgent));
    }
}
