package org.labs.laboratory2.agents;

import org.labs.laboratory2.domain.Region;

import jade.core.Agent;

public class StreamingPlatformAgent extends Agent {

	private Region region;
	private boolean supportsLiveEvents;

	@Override
	protected void setup() {
		System.out.printf("[%s] I'm ready to register my services! %n", getLocalName());

		final Object[] args = getArguments();
		region = (Region) args[0];
		supportsLiveEvents = (boolean) args[1];

		//TODO: Register streaming service
	}
}
