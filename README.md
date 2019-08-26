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
Copyright 2019 MagneticZero

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
