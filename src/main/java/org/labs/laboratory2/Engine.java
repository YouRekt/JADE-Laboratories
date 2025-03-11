package org.labs.laboratory2;

import static java.lang.String.format;
import static org.labs.laboratory2.domain.Genre.CRIMINAL;
import static org.labs.laboratory2.domain.Region.EU;
import static org.labs.laboratory2.domain.Region.US;

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
			runAgent(container, "TypicalViewer", "ViewerAgent", new Object[] { CRIMINAL, EU });
			runAgent(container, "Hulu", "StreamingPlatformAgent", new Object[] { US, true });
			runAgent(container, "Peacock", "StreamingPlatformAgent", new Object[] { US, true });
			runAgent(container, "Netflix", "StreamingPlatformAgent", new Object[] { EU, true });
			runAgent(container, "HBO", "StreamingPlatformAgent", new Object[] { EU, false });
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
			final String className, final Object[] args) {
		try {
			final String path = format("org.labs.laboratory2.agents.%s", className);
			final AgentController agent = mainContainer.createNewAgent(agentName, path, args);
			agent.start();
		} catch (final StaleProxyException e) {
			throw new AgentContainerException(agentName, e);
		}
	}
}
