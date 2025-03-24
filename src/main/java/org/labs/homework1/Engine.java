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

            runClients(container);
            runDeliverers(container);
            runMarkets(container);

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
}
