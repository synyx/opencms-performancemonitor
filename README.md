# Introduction

Programmers are notoriously bad at finding the parts of an application that really deserve performance optimization. That is why it is important to measure first.

This is a simple tool to log statistics about calls to JSPs in OpenCms. It can be used to find JSP elements that are still eligible for caching or that need some performance optimization. Internally it uses the performance statistics library <a href="http://perf4j.codehaus.org/index.html">Perf4J</a>.

Most OpenCms pages consist of several sub-elements that are included by the main template. Some JSPs might be cached and some might not. The OpenCms administration view can guide you in seeing which JSPs are already cached but it won't help you with deciding which JSP should be cached.

# Setup

To setup the monitoring you need to add the [project jar](http://nexus.synyx.de/content/repositories/public-releases/org/synyx/opencms/performance-monitor/0.1/performance-monitor-0.1.jar) and the [current version of Perf4J](http://perf4j.codehaus.org/downloads.html) to you WEB-INF/lib folder. 

As an alternative, if you are using Maven add the following dependency:

<pre><code>
	&lt;dependency&gt;
            &lt;groupId&gt;org.synyx.opencms&lt;/groupId&gt;
            &lt;artifactId&gt;performance-monitor&lt;/artifactId&gt;
            &lt;version&gt;0.1&lt;/version&gt;
        &lt;/dependency&gt;
</code></pre>

And the following repo:

<pre><code>
	&lt;repository&gt;
            &lt;id&gt;nexus.synyx.public-releases&lt;/id&gt;
            &lt;name&gt;Synyx Nexus&lt;/name&gt;
            &lt;url&gt;http://nexus.synyx.de/content/repositories/public-releases/&lt;/url&gt;
        &lt;/repository&gt;
</code></pre>

Also, add the following filter declaration to your web.xml:

<pre><code>
    &lt;filter&gt;
        &lt;filter-name&gt;performanceMonitor&lt;/filter-name&gt;
        &lt;filter-class&gt;org.synyx.opencms.performancemonitor.MonitorFilter&lt;/filter-class&gt;
    &lt;/filter&gt;

    &lt;filter-mapping&gt;
        &lt;filter-name&gt;performanceMonitor&lt;/filter-name&gt;
        &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
        &lt;dispatcher&gt;REQUEST&lt;/dispatcher&gt;
        &lt;dispatcher&gt;INCLUDE&lt;/dispatcher&gt;
    &lt;/filter-mapping&gt;
</code></pre>

The filter will catch all requests that are dispatched via REQUEST (an incoming request to the complete page) and INCLUDE (the elements of the template).

Finally, add a section like this to your log4j.properties \(located in WEB-INF/classes/\):

	# configuration for performance monitoring
	log4j.logger.org.perf4j.TimingLogger=INFO,PERF
	log4j.additivity.org.perf4j.TimingLogger=false
	
	log4j.appender.PERF=org.apache.log4j.RollingFileAppender
	log4j.appender.PERF.File=performance.log

	# This specifies the logfile layout
	log4j.appender.PERF.layout=org.apache.log4j.PatternLayout
	log4j.appender.PERF.layout.ConversionPattern=%d{DATE} %5p [%30.30C:%4L] %m%n

This instructs log4j to write all data for Perf4J to a seperate logfile named performance.log \(Note: this file will be located in the directory to start your Tomcat from\).

# Monitoring

Perf4J will now write the data for any requests to the logfile. To test it simply call your page some times.

To get an overview of the requests you can run the Perf4J parser as described in the [Developer Guide](http://perf4j.codehaus.org/devguide.html#Parsing_Log_Files_to_Generate_Performance_Statistics):

	java -jar webapps/opencms/WEB-INF/lib/perf4j-0.9.16.jar performance.log

This will generate the statistics and write those to the standard output.

# Analyzing Statistics

The parser will generate output that looks like this:

	Performance Statistics   2012-04-02 17:32:00 - 2012-04-02 17:32:30
	Tag                                                  Avg(ms)         Min         Max     Std Dev       Count
	REQUEST: /flower-de/flower-today/                     1827,0        1827        1827         0,0           1
	INCLUDE: /opencms803/WEB-INF/jsp/offline/system/modules/com.alkacon.opencms.v8.article/formatters/detail.jsp        18,0           4          32        14,0           2
	INCLUDE: /opencms803/WEB-INF/jsp/offline/system/modules/com.alkacon.opencms.v8.article/formatters/side.jsp         9,7           2          43        14,9           6
	INCLUDE: /opencms803/WEB-INF/jsp/offline/system/modules/com.alkacon.opencms.v8.list/elements/center_singlepage.jsp       173,0          20         326       153,0           2

If the line starts with REQUEST this is the data for the incoming request, which is the overall rendering of the page. The most important numbers are the first and the last \(in the case of the example 1827,0 and 1\). The first numer is the average request time, the last number is the amount of times the page or element is called.

## How can you use it? 

If there is an element that is either called a lot of times or takes some time for rendering this might be a possible candidate to look into. See if it can be cached or optimize the code in it.

You won't see cached elements in the code as these calls are not dispatched to JSPs.

# License 

opencms-performancemonitor is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
