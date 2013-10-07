package ru.game.hat.support;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.util.SparseArray;

public class WordSupport {
	private static Words words = null;
	
	/**
	 * Builder for word support
	 * @param wordsCount count words for one game
	 * @param level words complexity
	 * @param lang language of words
	 * @return support for one game
	 */
	public static Game newGame(int wordsCount, Level level, Lang lang, Activity activity, SparseArray<String> players) throws Exception {
		if (words == null || lang != words.getLang()) {
			words = new Words(lang, activity);
		}
		final List<Word> wordList;
		try {
			wordList = words.wordsForGame(wordsCount, level);
		} catch (Exception e) {
			throw new RuntimeException("Failed to get words", e);
		}
		return new Game(wordList, players);
	}

	public static long wordToNumber(String value, Lang lang) {
		try {
			final String word = value.toUpperCase();
			final Map<Character, Integer> positions = lang.positions();
			long acc = 0;
			for (int i = 0; i < word.length(); i++) {
				final int digit = positions.get(word.charAt(i));
				acc = acc * positions.size() + digit;
			}
			return acc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
