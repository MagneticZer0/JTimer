package time;
import java.util.ArrayList;
import java.util.HashMap;

import org.jtimer.Grapher;
import org.jtimer.Runner;
import org.jtimer.MethodTimer;
import org.jtimer.Annotations.AfterClass;
import org.jtimer.Annotations.BeforeClass;
import org.jtimer.Annotations.Time;

public class Test {
	
	ArrayList<String> list;
	Grapher grapher;
	long y;

	public static void main(String[] args) throws Throwable {
		Runner.time("time");
	}
	
	@BeforeClass
	public void startup() {
		grapher = Grapher.start();
		grapher.setMax(25000);
		list = new ArrayList<>();
	}
	
	@Time
	public void first() throws Throwable {
		HashMap<String, String> map = new HashMap<>();
		for(int i=0; i<10000; i++) {
			grapher.addData(i, MethodTimer.time(map, "put", "key", "value", Object.class, Object.class));
		}
	}
	
	@Time(graph = true, repeat=1000)
	public void second() throws Throwable {
		long time = MethodTimer.timeStatic(Thread.class, "sleep", 0, long.class);
		y = time;
	}
	
	@Time(graph = true, repeat=1000)
	public void arraylist() throws Throwable {
		long time = MethodTimer.time(list, "add", "a", Object.class);
		y = time;
	}
	
	@AfterClass
	public void showGrapher() {
		grapher.finish();
	}
}
