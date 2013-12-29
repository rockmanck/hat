package ru.game.hat.util;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionUtils {

	public static <T,K> Collection<K> transform(Collection<T> collection, Function<? super T, K> func) {
		final Collection<K> result = new ArrayList<K>();
		for (T elem : collection) {
			result.add(func.apply(elem));
		}
		return result;
	}
	
	public static interface Function<A,B> {
		B apply(A input);
	}
	
	public static String join(Collection<String> collection, String separator) {
		final StringBuilder sb = new StringBuilder();
		boolean sep = false;
		for (String val : collection) {
			if (sep) {
				sb.append(separator);
			}
			sb.append(val);
			sep = true;
		}
		return sb.toString();
	}
}
