package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.jtimer.CommandLine.CommandLine;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommandLineTest {

	static CommandLine commandLine;

	@Before
	static void setup() {
		commandLine = new CommandLine();
	}

	@DisplayName("Help argument")
	@Test
	void helpArg() throws Throwable {
		commandLine.main(new String[] {"-help"});
	}
}
