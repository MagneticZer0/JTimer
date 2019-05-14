package time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.jtimer.Runner;
import org.jtimer.Annotations.Before;
import org.jtimer.Annotations.BeforeClass;
import org.jtimer.Annotations.DisplayName;
import org.jtimer.Annotations.Settings;
import org.jtimer.Annotations.Time;
import org.jtimer.Annotations.Warmup;
import org.jtimer.Misc.Setting;

@Settings(Setting.BEST_FIT)
@Warmup(iterations = 500)
public class Test {
	
	long counter = 0; // If you want some variable to keep the repitition that you're on.
	
	ArrayList<Double> list; // Just a variable I'm using for fun

	public static void main(String[] args) throws Throwable {
		Runner.time("time");
	}
	
	@BeforeClass
	public void startup() {
		//Runner.getGrapher().setMax(25000);
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
	
	@Time(repeat=100, timeout=1)
	public void threadSleep() throws Throwable {
		Thread.sleep(2000);
	}
	
	@Time(repeat=1000)
	public void listAdd() {
		list.add(1.1);
	}
	
	@DisplayName("Tim Sort")
	@Time(repeat = 3000)
	public void sort() {
		Collections.sort(list);
	}
}
