package org.labs.laboratory2.agents;

import org.labs.laboratory2.domain.Genre;
import org.labs.laboratory2.domain.Region;

import jade.core.Agent;

public class ViewerAgent extends Agent {

	private Genre favouriteGenre;
	private Region region;

	@Override
	protected void setup() {
		System.out.println("[ViewerAgent] I'm ready to watch my favourite shows!");

		final Object[] args = getArguments();
		favouriteGenre = (Genre) args[0];
		region = (Region) args[1];

		//TODO: Find streaming platforms within a given region
	}
}
