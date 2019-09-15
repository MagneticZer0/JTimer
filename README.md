# JTimer

A way to time Java methods as well as outputting the data gathered in a rather manageable fashion. The way you use JTimer is basically the same way that you use JUnit.

## Annotations

There are multiple [annotations](https://magneticzer0.github.io/JTimer/org/jtimer/Annotations/package-summary.html) available in JTimer. The use cases can be viewed in the documentation pages available [here](https://magneticzer0.github.io/JTimer/). This is the current list of usable annotations in the current order of execution.
* <a href="https://magneticzer0.github.io/JTimer/org/jtimer/Annotations/Warmup.html">Warmup</a>
* <a href="https://magneticzer0.github.io/JTimer/org/jtimer/Annotations/BeforeClass.html">BeforeClass</a>
* <a href="https://magneticzer0.github.io/JTimer/org/jtimer/Annotations/Before.html">Before</a>
* <a href="https://magneticzer0.github.io/JTimer/org/jtimer/Annotations/DisplayName.html">DisplayName</a>
* <a href="https://magneticzer0.github.io/JTimer/org/jtimer/Annotations/Time.html">Time</a>
* <a href="https://magneticzer0.github.io/JTimer/org/jtimer/Annotations/After.html">After</a>
* <a href="https://magneticzer0.github.io/JTimer/org/jtimer/Annotations/AfterClass.html">AfterClass</a>
* <a href="https://magneticzer0.github.io/JTimer/org/jtimer/Annotations/Settings.html">Settings</a>


## Output

The data that is recorded is output to the graph as it is being recorded. There are ways to zoom in on the data and even save a graph of the data. The window of the graph is, by default, limited to 2 standard deviations of visible data. This will remove most outliers and make the graph not look completely flat. This is a graph that was created by running the Test class in the time package. ![Example](https://i.imgur.com/Jf4JK4g.png)

## License
```
JTimer is a library that allows you to gather timings for various methods and then output them in a easy to read graph.
Copyright (C) 2019  Harley Merkaj

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```
