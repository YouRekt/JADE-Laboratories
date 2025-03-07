package org.labs.laboratory1;

import static java.lang.String.format;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.labs.laboratory1.exceptions.AgentContainerException;
import org.labs.laboratory1.exceptions.JadePlatformInitializationException;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Engine {

	private static final ExecutorService jadeExecutor = Executors.newCachedThreadPool();

	public static void main(String[] args) {
		final Runtime runtime = Runtime.instance();
		final Profile profile = new ProfileImpl();

		try {
			final ContainerController container = jadeExecutor.submit(() -> runtime.createMainContainer(profile)).get();

			runGUI(container);
			runAgent(container, "Agent1", "FirstAgent");
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

	private static void runAgent(final ContainerController mainContainer, final String agentName,
			final String className) {
		try {
			final String path = format("org.labs.laboratory1.agents.%s", className);
			final AgentController agent = mainContainer.createNewAgent(agentName, path, new Object[] {});
			agent.start();
		} catch (final StaleProxyException e) {
			throw new AgentContainerException(agentName, e);
		}
	}
}
