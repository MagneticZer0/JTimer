/*
 * JTimer is a Java library that contains various methods and annotations that allow's one to 
 * time various methods and output it into a graph.
 * Copyright (C) 2019  Harley Merkaj
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://github.com/MagneticZer0/JTimer/blob/master/LICENSE> 
 * or <https://www.gnu.org/licenses/>.
 */
package org.jtimer.Collections;

import java.lang.annotation.Annotation;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
public class AnnotationMap<V> extends AbstractMap<String, List<V>> {

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
		List<V> values = internalMap.get(annotation.annotationType().getCanonicalName());
		if (values == null) {
			internalMap.put(annotation.annotationType().getCanonicalName(), new ArrayList<V>());
		}
		internalMap.get(annotation.annotationType().getCanonicalName()).add(value);
	}

	/**
	 * Returns a list of objects that had the given
	 * {@link java.lang.annotation.Annotation annotation} that you specified.
	 * 
	 * @param annotation The {@link java.lang.annotation.Annotation annotation}
	 *                   class to get the objects for
	 * @return Returns a list of objects that had this
	 *         {@link java.lang.annotation.Annotation annotation} as a key. If no
	 *         values exist for a given key then the collection will return a empty
	 *         list.
	 */
	public List<V> get(Class<? extends Annotation> annotation) {
		return internalMap.getOrDefault(annotation.getCanonicalName(), Collections.emptyList());
	}

	/**
	 * Returns the entry set of the map so that it can be iterated through
	 * 
	 * @return A set which contains all entries.
	 */
	@Override
	public Set<Entry<String, List<V>>> entrySet() {
		return internalMap.entrySet();
	}

	/**
	 * Sees if an object is equal to this AnnotationMap
	 * 
	 * @return A boolean that is true if they're equal.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof AnnotationMap)) {
			return false;
		} else {
			return internalMap.equals(((AnnotationMap<?>) o).internalMap);
		}
	}
}
