package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.jtimer.Exceptions.NotProperlyInitializedException;
import org.jtimer.Readability.If;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class ReadabilityTest {

	Random random = new Random();

	@DisplayName("false -> else - false")
	@Test
	void firstTest() {
		assertFalse(new If<Boolean>(false).Then(true).Else(false));
	}

	@DisplayName("true -> then - true")
	@Test
	void secondTest() {
		assertTrue(new If<Boolean>(true).Then(true).Else(false));
	}

	@DisplayName("true -> then - false")
	@Test
	void thirdTest() {
		assertFalse(new If<Boolean>(true).Then(false).Else(true));
	}

	@DisplayName("false -> else - true")
	@Test
	void forthTest() {
		assertTrue(new If<Boolean>(false).Then(false).Else(true));
	}

	@DisplayName("random -> random")
	@RepeatedTest(100)
	void fifthTest() {
		boolean test = random.nextBoolean();
		double first = random.nextDouble();
		double second = random.nextDouble();
		assertEquals(test ? first : second, new If<Double>(test).Then(first).Else(second));
	}

	@DisplayName("If Exception")
	@Test
	void sixthTest() {
		Void v = null;
		assertThrows(NotProperlyInitializedException.class, () -> new If<Void>(true).Else(v));
	}
}
