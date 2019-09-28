import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.*;
import java.lang.reflect.Method;

import org.jtimer.Annotations.Handler.AnnotationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AnnotationHandlerTest {

	@DisplayName("Recursive annotations")
	@Test
	void recursion() {
		AnnotationHandler handler = new AnnotationHandler(RecursiveAnnotationTest.class);
		assertAll("Handler could not handle recursive annotations", () -> assertEquals(5, handler.getAnnotations().size(), "Annotations length is incorrect"), () -> assertTrue(handler.isAnnotationPresent(Recursive1.class), "Recursive1 is not present"), () -> assertTrue(handler.isAnnotationPresent(Recursive1.class), "Recursive1 is not present"));
	}

	@DisplayName("Meta annotations")
	@Test
	void annotation() {
		AnnotationHandler handler = new AnnotationHandler(AnnClass.class);
		assertAll("Annotation handler couldn't get all annotations", () -> assertTrue(handler.isAnnotationPresent(FunName.class), "Couldn't find FunName"), () -> assertTrue(handler.isAnnotationPresent(Ann.class), "Couldn't find Ann"), () -> assertEquals("Pickle", handler.getAnnotation(FunName.class).value(), "Couldn't get FunName.value()"));
	}

	@DisplayName("Annotation levels")
	@Test
	void level() throws Throwable {
		Method test1 = AnnClass.class.getDeclaredMethod("Test1");
		Method test2 = AnnClass.class.getDeclaredMethod("Test2");
		AnnotationHandler handler1 = new AnnotationHandler(test1);
		AnnotationHandler handler2 = new AnnotationHandler(test2);
		assertAll("Annotation handler levels didn't work correctly", () -> assertTrue(handler1.isAnnotationPresent(levelTest1.class), "Level 1 test annotation isn't present"), () -> assertEquals("Cucumber", handler1.getAnnotation(FunName.class).value()), () -> assertTrue(handler2.isAnnotationPresent(levelTest2.class), "Level 2 test annotation isn't present"), () -> assertEquals("Zucchini", handler2.getAnnotation(FunName.class).value()));
	}

}

@Recursive2
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Recursive1 {

}

@Recursive1
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Recursive2 {

}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface FunName {
	String value() default "";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@FunName("Pickle")
@interface Ann {

}

@Ann
@FunName("Cucumber")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface levelTest1 {

}

@FunName("Zucchini")
@Ann
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface levelTest2 {

}

@Recursive1
class RecursiveAnnotationTest {

}

@Ann
class AnnClass {

	@levelTest1
	void Test1() {

	}

	@levelTest2
	void Test2() {

	}
}