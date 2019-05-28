package org.jtimer.Collections;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A version of a Multi-Hashmap I created in order to make the code more
 * efficient. Internally, this is just a way of mapping an
 * {@link java.lang.annotation.Annotation Annotation} to a list of objects, in
 * my case Methods.
 * 
 * @author MagneticZero
 *
 * @param <V> The type of the value of the map
 */
public class AnnotationMap<V> {

	/**
	 * The internal data structure of the
	 * {@link org.jtimer.Collections.AnnotationMap AnnotationMap}
	 */
	private HashMap<String, List<V>> internalMap;

	/**
	 * Creates a {@link org.jtimer.Collections.AnnotationMap AnnotationMap} by
	 * instantiating the inner data structure.
	 */
	public AnnotationMap() {
		internalMap = new HashMap<>();
	}

	/**
	 * Puts a value into the map given a collection of
	 * {@link java.lang.annotation.Annotation annotations}
	 * 
	 * @param annotations The array of {@link java.lang.annotation.Annotation
	 *                    annotations}, typically the object's
	 *                    {@link java.lang.annotation.Annotation annotations}.
	 * @param value       The value to put into the map
	 */
	public void put(Collection<Annotation> annotations, V value) {
		for (Annotation annotation : annotations) {
			put(annotation, value);
		}
	}

	/**
	 * Puts a value into the map given a single
	 * {@link java.lang.annotation.Annotation annotation}.
	 * 
	 * @param annotation The {@link java.lang.annotation.Annotation annotation} that
	 *                   is essentially the key
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
	 * Returns a list of objects that had the given
	 * {@link java.lang.annotation.Annotation annotation} that you specified.
	 * 
	 * @param annotation The {@link java.lang.annotation.Annotation annotation}
	 *                   class to get the objects for
	 * @return Returns a list of objects that had this
	 *         {@link java.lang.annotation.Annotation annotation} as a key
	 */
	public List<V> get(Class<? extends Annotation> annotation) {
		if (internalMap.get(annotation.getCanonicalName()) == null) {
			return Collections.emptyList();
		} else {
			return internalMap.get(annotation.getCanonicalName());
		}
	}
}
