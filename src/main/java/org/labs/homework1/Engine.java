package org.labs.homework1;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.labs.exceptions.AgentContainerException;
import org.labs.exceptions.JadePlatformInitializationException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.String.format;

public class Engine {
    private static final ExecutorService jadeExecutor = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        final Runtime runtime = Runtime.instance();
        final Profile profile = new ProfileImpl();

        try {
            final ContainerController container = jadeExecutor.submit(() -> runtime.createMainContainer(profile)).get();

            runGUI(container);

//            runClients(container);
//            runDeliverers(container);
//            runMarkets(container);
            runTest(container);
//            runNikczemnyTestSzefa(container);
//            runTestNoStoreContainsOrder(container);

        } catch (final InterruptedException | ExecutionException e) {
            throw new JadePlatformInitializationException(e);
        }
    }

    private static void runGUI(final ContainerController mainContainer) {
        try {
            final AgentController guiAgent = mainContainer.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
            guiAgent.start();
        } catch (final StaleProxyException e) {
            throw new AgentContainerException("GUIAgent", e);
        }
    }

    private static void runAgent(final ContainerController mainContainer, final String agentName, final String className, final Object[] args) {
        try {
            final String path = format("org.labs.homework1.agents.%s", className);
            final AgentController agent = mainContainer.createNewAgent(agentName, path, args);
            agent.start();
        } catch (final StaleProxyException e) {
            throw new AgentContainerException(agentName, e);
        }
    }

    private static void runClients(final ContainerController mainContainer) {
        runAgent(mainContainer, "Client", "ClientAgent", new Object[]{List.of("milk", "coffee", "rice")});
    }

    private static void runDeliverers(final ContainerController mainContainer) {
        runAgent(mainContainer, "Bolt Food", "DeliveryAgent", new Object[]{9.99});
        runAgent(mainContainer, "Uber Eats", "DeliveryAgent", new Object[]{5.99});
        runAgent(mainContainer, "Wolt", "DeliveryAgent", new Object[]{7.99});
        runAgent(mainContainer, "Glovo", "DeliveryAgent", new Object[]{21.37});
    }

    private static void runMarkets(final ContainerController mainContainer) {
        runAgent(mainContainer, "Biedronka", "MarketAgent", new Object[]{Map.of("milk", 5.00, "coffee", 25.00, "rice", 3.00)});
        runAgent(mainContainer, "Lidl", "MarketAgent", new Object[]{Map.of("milk", 4.00, "coffee", 28.00, "rice", 3.00)});
        runAgent(mainContainer, "Aldi", "MarketAgent", new Object[]{Map.of("milk", 6.00, "coffee", 22.50, "rice", 2.50)});
        runAgent(mainContainer, "Auchan", "MarketAgent", new Object[]{Map.of("milk", 5.00, "coffee", 23.00, "rice", 6.90)});
    }

    private static void runTest(final ContainerController mainContainer) {
        runAgent(mainContainer, "DeliveryBolt", "DeliveryAgent", new Object[]{5.00});
        runAgent(mainContainer, "DeliveryUber", "DeliveryAgent", new Object[]{9.99});
        runAgent(mainContainer, "DeliveryWolt", "DeliveryAgent", new Object[]{15.50});
        runAgent(mainContainer, "Client1", "ClientAgent", new Object[]{List.of("milk", "coffee", "rice")});
        runAgent(mainContainer, "MarketBiedronka", "MarketAgent", new Object[]{Map.of("milk", 5.00, "rice", 3.40)});
        runAgent(mainContainer, "MarketOsiedlowy", "MarketAgent", new Object[]{Map.of("rice", 1.00)});
        runAgent(mainContainer, "MarketŻabka", "MarketAgent", new Object[]{Map.of("coffee", 7.50, "milk", 6.39)});
    }

    private static void runTestNoStoreContainsOrder(final ContainerController mainContainer) {
        runAgent(mainContainer, "Client", "ClientAgent", new Object[]{List.of("gold")});

        runAgent(mainContainer, "Bolt Food", "DeliveryAgent", new Object[]{9.99});
        runAgent(mainContainer, "Uber Eats", "DeliveryAgent", new Object[]{5.99});

        runAgent(mainContainer, "PoorStore1", "MarketAgent", new Object[]{Map.of("ash", 1.00, "dust", 0.01)});
        runAgent(mainContainer, "PoorStore2", "MarketAgent", new Object[]{Map.of("ash", 1.00, "dust", 0.01)});
        runAgent(mainContainer, "PoorStore3", "MarketAgent", new Object[]{Map.of("ash", 1.00, "dust", 0.01)});

    }

    private static void runNikczemnyTestSzefa(final ContainerController mainContainer) {
        // Ruthless delivery agents fighting for dominance
        runAgent(mainContainer, "Pyszne", "DeliveryAgent", new Object[]{3.99});
        runAgent(mainContainer, "Glovo", "DeliveryAgent", new Object[]{8.49});
        runAgent(mainContainer, "DHL", "DeliveryAgent", new Object[]{13.37});
        runAgent(mainContainer, "UberEats", "DeliveryAgent", new Object[]{9.95});
        runAgent(mainContainer, "BoltFood", "DeliveryAgent", new Object[]{6.66}); // The devil's price

        // The ultimate client with extravagant needs
        runAgent(mainContainer, "Bougie Client", "ClientAgent", new Object[]{List.of("golden apple", "caviar", "wagyu steak", "truffle", "champagne")});

        // Merciless markets with ridiculous pricing strategies
        runAgent(mainContainer, "Lidl", "MarketAgent", new Object[]{Map.of("golden apple", 99.99, "caviar", 250.00, "wagyu steak", 499.95)});
        runAgent(mainContainer, "Carrefour", "MarketAgent", new Object[]{Map.of("truffle", 150.50, "champagne", 200.00, "wagyu steak", 450.00)});
        runAgent(mainContainer, "Auchan", "MarketAgent", new Object[]{Map.of("golden apple", 120.00, "caviar", 275.00, "truffle", 160.99)});
        runAgent(mainContainer, "Biedronka", "MarketAgent", new Object[]{Map.of("champagne", 175.49, "wagyu steak", 520.00, "caviar", 300.00)});
        runAgent(mainContainer, "Żabka", "MarketAgent", new Object[]{Map.of("golden apple", 110.10, "champagne", 190.90, "truffle", 175.75)});
        runAgent(mainContainer, "AmazonFresh", "MarketAgent", new Object[]{Map.of("golden apple", 105.99, "caviar", 290.00, "wagyu steak", 510.25, "champagne", 225.00)});
    }
}
