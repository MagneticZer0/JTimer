package org.jtimer.Collections;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MultiMap<V> {
	
	private HashMap<String, List<V>> internalMap;

	public MultiMap() {
		internalMap = new HashMap<>();
	}
	
	public void put(Collection<Annotation> annotations, V value) {
		for(Annotation annotation : annotations) {
			put(annotation, value);
		}
 	}
	
	public void put(Annotation annotation, V value) {
		if (internalMap.get(annotation.annotationType().getCanonicalName()) == null) {
			List<V> list = new ArrayList<>(100);
			list.add(value);
			internalMap.put(annotation.annotationType().getCanonicalName(), list);
		} else {
			internalMap.get(annotation.annotationType().getCanonicalName()).add(value);
		}
 	}
	
	public List<V> get(Class<? extends Annotation> annotation) {
		if (internalMap.get(annotation.getCanonicalName()) == null) {
			return Collections.emptyList();
		} else {
			return internalMap.get(annotation.getCanonicalName());
		}
	}
}
