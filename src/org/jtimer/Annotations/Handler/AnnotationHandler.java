package org.jtimer.Annotations.Handler;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AnnotationHandler {

	/**
	 * A field containing the java.lang annotations as these annotations tend to
	 * lead to infinite recursion, which I have yet to deal with besides checking.
	 */
	private static final Set<Class<?>> JAVA_LANG_ANNOTATIONS = new HashSet<>(Arrays.asList(new Class[] { Documented.class, Inherited.class, Native.class, Repeatable.class, Retention.class, Target.class }));
	/**
	 * A mapping for annotation classes to the actual annotations
	 */
	private HashMap<Class<? extends Annotation>, Annotation> annotations = new HashMap<>();
	/**
	 * A mapping for annotation classes to the level that they're on. Since the
	 * topmost annotaitons should be the ones used in meta annotations.
	 */
	private HashMap<Class<? extends Annotation>, Integer> levelMap = new HashMap<>();

	/**
	 * Creates an annotation handler for a Java AnnotatedElement Object 
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
		if (annotations.get(annotation.annotationType()) == null || levelMap.getOrDefault(annotation.annotationType(), 1) < level) {
			levelMap.put(annotation.annotationType(), level);
			annotations.put(annotation.annotationType(), annotation);
		}
		for (Annotation metaAnnotation : annotation.annotationType().getAnnotations()) {
			if (!JAVA_LANG_ANNOTATIONS.contains(metaAnnotation.annotationType())) {
				process(metaAnnotation, level - 1);
			}
		}
	}

	/**
	 * Returns all annotataions present for the object
	 * @return All annotations present
	 */
	public Annotation[] getAnnotations() {
		Collection<Annotation> annotations = this.annotations.values();
		return annotations.toArray(new Annotation[annotations.size()]);
	}

	/**
	 * Tells if a annotation is present within the object
	 * 
	 * @param annotationType The annotation type to check for
	 * @return Whether or not the annotation is preset
	 */
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
		return annotations.keySet().contains(annotationType);
	}

	/**
	 * Returns an annotation, if present, otherwise it will return null.
	 * 
	 * @param <T>            The annotation type
	 * @param annotationType The class for the annotation you want
	 * @return The annotation object you want.
	 */
	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
		if (isAnnotationPresent(annotationType)) {
			return (T) annotations.get(annotationType);
		} else {
			return null;
		}
	}
}
