import static org.junit.jupiter.api.Assertions.*;

import org.jtimer.CommandLine.CommandLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommandLineTest {

	@DisplayName("Help argument")
	@Test
	void helpArg() throws Throwable {
		CommandLine.main(new String[] {"-help"});
		assertTrue(true);
	}
}
