package org.jtimer.Collections;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A version of a Multi-Hashmap I created in order to make the code more
 * efficient. Internally, this is just a way of mapping a java annotation to a
 * list of objects, in my case Methods.
 * 
 * @author MagneticZero
 *
 * @param <V> The type of the value of the map
 */
public class MultiMap<V> {

	/**
	 * The internal data structure of the {@link org.jtimer.Collections.MultiMap}
	 */
	private HashMap<String, List<V>> internalMap;

	/**
	 * Creates a {@link org.jtimer.Collections.MultiMap} by instantiating the inner
	 * data structure.
	 */
	public MultiMap() {
		internalMap = new HashMap<>();
	}

	/**
	 * Puts a value into the map given a array of annotations
	 * 
	 * @param annotations The array of annotations, typically the object's
	 *                    annotations.
	 * @param value       The value to put into the map
	 */
	public void put(Collection<Annotation> annotations, V value) {
		for (Annotation annotation : annotations) {
			put(annotation, value);
		}
	}

	/**
	 * Puts a value into the map given a single annotatoin
	 * 
	 * @param annotation The annotation that is essentially the key
	 * @param value      The value to put into the map
	 */
	public void put(Annotation annotation, V value) {
		if (internalMap.get(annotation.annotationType().getCanonicalName()) == null) {
			List<V> list = new ArrayList<>(100);
			list.add(value);
			internalMap.put(annotation.annotationType().getCanonicalName(), list);
		} else {
			internalMap.get(annotation.annotationType().getCanonicalName()).add(value);
		}
	}

	/**
	 * Returns a list of objects that had the given annotation that you specified.
	 * 
	 * @param annotation The annotation to get the objects for
	 * @return Returns a list of objects that had this annotation as a key.
	 */
	public List<V> get(Class<? extends Annotation> annotation) {
		if (internalMap.get(annotation.getCanonicalName()) == null) {
			return Collections.emptyList();
		} else {
			return internalMap.get(annotation.getCanonicalName());
		}
	}
}
