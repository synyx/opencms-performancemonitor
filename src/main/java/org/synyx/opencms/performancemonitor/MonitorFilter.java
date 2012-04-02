package org.synyx.opencms.performancemonitor;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.perf4j.StopWatch;
import org.perf4j.commonslog.CommonsLogStopWatch;

/**
 * Monitors incoming requests and measures the time for executing the filter chain.
 * @author Florian Hopf, Synyx GmbH & Co. KG, hopf@synyx.de
 */
public class MonitorFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        StopWatch stopWatch = new CommonsLogStopWatch(buildWatchName(request));
        chain.doFilter(request, response);
        stopWatch.stop();
    }

    private String buildWatchName(ServletRequest request) {
        String include = getIncludeUri(request);
        if (include != null) {
            return String.format("INCLUDE: %s", include);
        } else {
            return String.format("REQUEST: %s", getPathInfo(request));
        }
    }

    private String getIncludeUri(ServletRequest request) {
        return (String) request.getAttribute("javax.servlet.include.request_uri");
    }

    private String getPathInfo(ServletRequest request) {
        return ((HttpServletRequest) request).getPathInfo();
    }

    @Override
    public void destroy() {
    }
}
