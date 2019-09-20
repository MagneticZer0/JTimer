package org.jtimer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class CommandLine {

	public static void main(String[] args) throws FileNotFoundException {
		List<String> arguments = Arrays.asList(args);
		int cIndex = arguments.indexOf("-c");
		if (cIndex != -1) {
			if (args.length > cIndex+1) {
				String name = getArgument(arguments, "-n", "compiledCode");
				String repetitions =  getArgument(arguments, "-r", "100");
				String codeToCompile = "import org.jtimer.*;\n"
									 + "\n"
									 + "public class CompiledClass {\n"
									 + "\n"
									 + "	long counter = 0;\n"
									 + "\n"
									 + "	public static void main(String[] args) throws Throwable {\n"
									 + "		Runner.time(\"CompiledClass.class\");\n"
									 + "	}\n"
									 + "\n"
									 + "	@DisplayName(\"" + name + "\")\n"
									 + "	@Time(repeat = " + repetitions + ")\n"
									 + "	public void compiledCode() {\n"
									 + "		" + args[cIndex+1] + "\n"
									 + "	}\n"
									 + "}";
				System.out.println("Running the supplied code...");
				runCode(codeToCompile);
			} else {
				System.err.println("You must supply code to the -c argument!");
			}
		}
	}
	
	public static String getArgument(List<String> args, String argument, String defaultValue) {
		int argIndex = args.indexOf(argument);
		if (argIndex != -1 && args.size() > argIndex+1) {
			return args.get(argIndex+1);
		}
		return defaultValue;
	}

	public static void runCode(String code) throws FileNotFoundException {
		int random = (int) Math.round(Math.random()*Integer.MAX_VALUE);
		File uncompiled = new File(System.getProperty("java.io.tmpdir") + "compiledCode" + random + ".java");
		uncompiled.deleteOnExit();
		PrintWriter writer = new PrintWriter(uncompiled);
		writer.print(code);
		writer.close();
	}
}
