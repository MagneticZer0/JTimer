package time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.jtimer.Grapher;
import org.jtimer.Runner;
import org.jtimer.Annotations.AfterClass;
import org.jtimer.Annotations.Before;
import org.jtimer.Annotations.BeforeClass;
import org.jtimer.Annotations.Time;

public class Test {
	
	Grapher grapher = Grapher.start(); // The graph to put data in
	long counter = 0; // If you want some variable to keep the repitition that you're on.
	
	ArrayList<Double> list; // Just a variable I'm using for fun

	public static void main(String[] args) throws Throwable {
		Runner.time("time");
	}
	
	// From here
	@BeforeClass
	public void startup() {
		//grapher.setMax(25000);
		list = new ArrayList<>();
	}
	
	@Before
	public void add() {
		for(int i=0; i<counter; i++) {
			list.add(Math.random());
		}
	}
	
	@Time(repeat=1000)
	public void mapPut() {
		HashMap<String, String> map = new HashMap<>();
		map.put("a", "b");
	}
	
	@Time(repeat=10, timeout=1)
	public void threadSleep() throws Throwable {
		Thread.sleep(2000);
	}
	
	@Time(repeat=1000)
	public void listAdd() {
		list.add(1.1);
	}
	// To here is optional
	
	@Time(repeat = 1000)
	public void sort() {
		Collections.sort(list);
	}
	
	@AfterClass
	public void showGrapher() {
		grapher.finish();
	}
}
