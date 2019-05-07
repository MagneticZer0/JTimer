# JTimer

A way to time Java methods as well as outputting the data gathered in a rather manageable fashion. The way you use JTimer is basically the same way that you use JUnit.

## Annotations

There are multiple [annotations](https://magneticzer0.github.io/JTimer/org/jtimer/Annotations/package-summary.html) available in JTimer. The use cases can be viewed in the documentation pages available [here](https://magneticzer0.github.io/JTimer/). This is the current list of usable annotations in the current order of execution.
* <a href="https://magneticzer0.github.io/JTimer/org/jtimer/Annotations/BeforeClass.html">BeforeClass</a>
* <a href="https://magneticzer0.github.io/JTimer/org/jtimer/Annotations/Before.html">Before</a>
* <a href="https://magneticzer0.github.io/JTimer/org/jtimer/Annotations/Time.html">Time</a>
* <a href="https://magneticzer0.github.io/JTimer/org/jtimer/Annotations/After.html">After</a>
* <a href="https://magneticzer0.github.io/JTimer/org/jtimer/Annotations/AfterClass.html">AfterClass</a>

## Output

The data that is recorded is output to the graph as it is being recorded. There are ways to zoom in on the data and even save a graph of the data. The window of the graph is, by default, limited to 2 standard deviations of visible data. This will remove most outliers and make the graph not look completely flat. This is a graph that was created by running the Test class in the time package. ![Test](https://i.imgur.com/Jf4JK4g.png)
