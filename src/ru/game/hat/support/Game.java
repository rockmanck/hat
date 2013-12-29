package ru.game.hat.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import static ru.game.hat.util.CollectionUtils.*;
import android.util.SparseArray;

public final class Game {

	private final int gameSize;
	private final List<Word> words;
	private final Random random = new Random();
	private final int playersCount;
	private final SparseArray<String> players;
	private Map<Integer, PlayerResult> points = new HashMap<Integer, PlayerResult>();
	
	private int count = 0;
	private int activePlayer = 0;
	private Word currentWord;
	private int previousWord = -1;
	private boolean finished = false;
	
	Game(List<Word> words, SparseArray<String> players) {
		this.gameSize = words.size();
		this.words = words;
		this.playersCount = players.size();
		this.players = players;
		for (int i = 0; i < players.size(); i += 1) {
			final int key = players.keyAt(i);
			points.put(key, new PlayerResult(key, players.get(key)));
		}
	}

	public boolean hasWord() {
		return count < gameSize;
	}

	public Word nextWord() {
		int randomIndex = random.nextInt(words.size());
		if (randomIndex == previousWord) {
			randomIndex = randomIndex + 1 < words.size() ? randomIndex + 1 : 0;
		}
		previousWord = randomIndex;
		
		final Word word = words.get(randomIndex);
		currentWord = word;
		count += 1;
		return word;
	}

	public void wordGuessed() {
		addPoint(activePlayer);
		words.remove(currentWord);
		previousWord = -1;
	}

	public String nextPlayer() {
		activePlayer = activePlayer + 1 > playersCount ? 1 : activePlayer + 1;
		return players.get(activePlayer);
	}

	public boolean finished() {
		return finished ;
	}
	
	public void finish() {
		finished = true;
	}
	
	private void addPoint(int activePlayer) {
		final PlayerResult p = points.get(activePlayer);
		p.setPoints(p.getPoints() + 1);
	}

	/**
	 * Returns string representation of game results. i.e. Player1=4.0~Player2=6.3~... and so on.
	 * @return
	 */
	public String getResults() {
		final List<PlayerResult> values = new ArrayList<PlayerResult>(points.values());
		Collections.sort(values);
		return join(transform(values, new Function<PlayerResult, String>() {
			@Override
			public String apply(PlayerResult input) {
				return input.getName() + "=" + input.getPoints();
			}
		}), "~");
	}

}