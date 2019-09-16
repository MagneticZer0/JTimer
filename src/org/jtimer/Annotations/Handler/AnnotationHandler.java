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
package org.jtimer.Annotations.Handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.HashMap;

/**
 * An object that is able to handle all meta annotations of a
 * {@link java.lang.reflect.AnnotatedElement AnnotatedElement} object. If
 * annotations are repeated, then only the topmost annotation present will be
 * the one returned, if it's needed.
 * 
 * @author MagneticZero
 *
 */
public class AnnotationHandler {

	/**
	 * A mapping for annotation classes to the actual annotations.
	 */
	private HashMap<Class<? extends Annotation>, Annotation> annotations = new HashMap<>();
	/**
	 * A mapping for annotation classes to the level that they're on. Since the
	 * topmost annotaitons should be the ones used in meta annotations.
	 */
	private HashMap<Class<? extends Annotation>, Integer> levelMap = new HashMap<>();

	/**
	 * Creates an annotation handler for a Java
	 * {@link java.lang.reflect.AnnotatedElement AnnotatedElement} object.
	 * 
	 * @param object The object to get the annotations of.
	 */
	public AnnotationHandler(AnnotatedElement object) {
		for (Annotation annotation : object.getAnnotations()) {
			process(annotation, 0);
		}
	}

	/**
	 * Processes annotations recursively. Once the object annotations are handeled,
	 * then the annotations of the annotations will be processed.
	 * 
	 * @param annotation The annotation to process
	 * @param level      The level of recursions we're in
	 */
	private void process(Annotation annotation, int level) {
		if (annotations.get(annotation.annotationType()) == null || levelMap.getOrDefault(annotation.annotationType(), -1) > level) {
			levelMap.put(annotation.annotationType(), level);
			annotations.put(annotation.annotationType(), annotation);
			for (Annotation metaAnnotation : annotation.annotationType().getAnnotations()) {
				process(metaAnnotation, level + 1);
			}
		}
	}

	/**
	 * Returns all annotataions present for the object.
	 * 
	 * @return All annotations present
	 */
	public Collection<Annotation> getAnnotations() {
		return annotations.values();
	}

	/**
	 * Tells if a annotation is present within the object.
	 * 
	 * @param annotationType The annotation type to check for
	 * @return Whether or not the annotation is preset
	 */
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
		return annotations.containsKey(annotationType);
	}

	/**
	 * Returns an annotation, if present, otherwise it will return null.
	 * 
	 * @param <T>            The annotation type
	 * @param annotationType The class for the annotation you want
	 * @return The annotation object you want.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
		if (isAnnotationPresent(annotationType)) {
			return (T) annotations.get(annotationType);
		} else {
			return null;
		}
	}
}
