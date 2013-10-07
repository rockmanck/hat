package ru.game.hat.support;

import java.io.Serializable;
import java.util.List;

public class GameResults implements Serializable {
	private static final long serialVersionUID = 510089365244968059L;
	
	private final List<PlayerResult> results;

	public List<PlayerResult> getResults() {
		return results;
	}
	
	public GameResults(List<PlayerResult> results) {
		this.results = results;
	}
}
