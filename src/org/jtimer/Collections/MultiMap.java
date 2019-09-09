package org.jtimer.Collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MultiMap<K, V> {

	private HashMap<K, List<V>> internalMap;

	public MultiMap() {
		internalMap = new HashMap<>();
	}

	public void put(K key, V value) {
		List<V> values = internalMap.get(key);
		if (values == null) {
			internalMap.put(key, new ArrayList<V>());
		}
		internalMap.get(key).add(value);
	}

	public List<V> get(K key) {
		return internalMap.getOrDefault(key, Collections.emptyList());
	}
}
