package org.jtimer.CommandLine;

import java.util.Arrays;
import java.util.List;

import org.jtimer.Runner;
import org.jtimer.Annotations.DisplayName;
import org.jtimer.Annotations.Time;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class CommandLine {

	public static void main(String[] args) throws Throwable {
		List<String> arguments = Arrays.asList(args);
		int cIndex = arguments.indexOf("-c");
		if (cIndex != -1) {
			if (args.length > cIndex+1) {
				String name = getArgument(arguments, "-n", "compiledCode");
				String repetitions =  getArgument(arguments, "-r", "100");
				System.out.println("Running the supplied code...");
				runCode(name, Integer.parseInt(repetitions), args[cIndex+1]);
			} else {
				System.err.println("You must supply code to the -c argument!");
			}
		} else if (arguments.indexOf("-help") != -1) {
			System.out.println("JTimer (" + Runner.version + ") has various arguments:");
			System.out.println("	The -c argument must be used. This argument allows you to supply the code you want to execute");
			System.out.println("		Usage: -c \"{code}\"");
			System.out.println();
			System.out.println("	The -r argument allows you to specify how many times to repeat the execution");
			System.out.println("		Usage: -r {integer} (Default 100)");
			System.out.println();
			System.out.println("	The -n argument allows you to specify that name of the legend item within the chart");
			System.out.println("		Usage: -n \"{name}\" (Default \"compiledCode\")");
		} else {
			System.out.println("Try using the -help command to see the command line arguments!");
		}
	}
	
	public static String getArgument(List<String> args, String argument, String defaultValue) {
		int argIndex = args.indexOf(argument);
		if (argIndex != -1 && args.size() > argIndex+1) {
			return args.get(argIndex+1);
		}
		return defaultValue;
	}

	public static void runCode(String name, int repetitions, String code) throws Throwable {
		CtClass dummyClass = ClassPool.getDefault().get("org.jtimer.CommandLine.DummyClass");
		CtMethod timeMethod = dummyClass.getMethod("time", "()V");

		timeMethod.setBody("{ " + code + " }");

		ConstPool constPool = dummyClass.getClassFile().getConstPool();
		AnnotationsAttribute attributes = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);

		Annotation defName = new Annotation(DisplayName.class.getName(), constPool);
		defName.addMemberValue("value", new StringMemberValue(name, constPool));
		attributes.addAnnotation(defName);

		Annotation repe = new Annotation(Time.class.getName(), constPool);
		repe.addMemberValue("repeat", new IntegerMemberValue(constPool, repetitions));
		attributes.addAnnotation(repe);

		timeMethod.getMethodInfo().addAttribute(attributes);
		dummyClass.toClass();
		dummyClass.defrost();

		Runner.time(DummyClass.class);
	}
}
