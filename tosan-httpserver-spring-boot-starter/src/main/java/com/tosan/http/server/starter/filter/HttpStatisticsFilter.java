package com.tosan.http.server.starter.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tosan.http.server.starter.statistics.ServiceExecutionInfo;
import com.tosan.http.server.starter.statistics.Statistics;
import com.tosan.http.server.starter.util.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import tools.jackson.core.JacksonException;
import tools.jackson.core.util.DefaultPrettyPrinter;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author mina khoshnevisan
 * @since 7/20/2022
 */
@Order(-290)
public class HttpStatisticsFilter extends OncePerRequestFilterBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpStatisticsFilter.class);
    private static final AtomicLong activeRequestsCount = new AtomicLong(0);
    private static final DefaultPrettyPrinter printer;
    private static final ObjectWriter writer;
    private static final ObjectMapper mapper;

    private static final ArrayList<String> STATISTICS_DEFAULT_EXCLUDE_URL_PATTERNS = new ArrayList<>() {{
        add(Constants.DEFAULT_SWAGGER_EXCLUDE_PATTERN);
        add(Constants.DEFAULT_API_DOCS_EXCLUDE_PATTERN);
        add(Constants.DEFAULT_API_DOCS_V3_EXCLUDE_PATTERN);
        add(Constants.DEFAULT_FAVICON_EXCLUDE_PATTERN);
        add(Constants.DEFAULT_ACTUATOR_EXCLUDE_PATTERN);
    }};

    static {
        printer = new DefaultPrettyPrinter().withObjectIndenter(new DefaultPrettyPrinter.FixedSpaceIndenter());
        mapper = JsonMapper.builder()
                .changeDefaultPropertyInclusion(incl ->
                        JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        writer = mapper.writer().with(printer);
    }

    public HttpStatisticsFilter() {
        super.addExcludeUrlPatterns(STATISTICS_DEFAULT_EXCLUDE_URL_PATTERNS);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String serviceName = request.getMethod() + " " + request.getServletPath();
        long startTime = System.currentTimeMillis();
        start(serviceName);
        try {
            filterChain.doFilter(request, response);
        } finally {
            end(serviceName, response.getStatus(), startTime);
        }
    }

    private void start(String serviceName) {
        final Map<String, Object> inwardLog = new LinkedHashMap<>();
        inwardLog.put("+service", serviceName);
        inwardLog.put("active-requests", activeRequestsCount.incrementAndGet());
        LOGGER.info(writeJson(inwardLog));
    }

    private void end(String serviceName, int statusCode, long startTime) {
        double duration = (System.currentTimeMillis() - startTime) / 1000.0;
        final Map<String, Object> outwardLog = new LinkedHashMap<>();
        outwardLog.put("-service", serviceName);
        outwardLog.put("status", statusCode);
        List<ServiceExecutionInfo> serviceExecutionInfos = Statistics.getApplicationStatistics().getServiceExecutionsInfo();
        outwardLog.put("duration", duration + "s");
        outwardLog.put("active-requests", activeRequestsCount.decrementAndGet());
        if (!serviceExecutionInfos.isEmpty()) {
            outwardLog.put("statistics", generateStatisticsDetail(serviceExecutionInfos));
        }
        LOGGER.info(writeJson(outwardLog));
        Statistics.cleanupSession();
    }

    private List<String> generateStatisticsDetail(List<ServiceExecutionInfo> serviceExecutionInfos) {
        List<String> statisticsDetail = new ArrayList<>();
        for (ServiceExecutionInfo serviceExecutionInfo : serviceExecutionInfos) {
            if (serviceExecutionInfo != null) {
                if (StringUtils.isEmpty(serviceExecutionInfo.serviceType())) {
                    statisticsDetail.add(serviceExecutionInfo.serviceName() + ": " +
                            serviceExecutionInfo.duration() + "s");
                } else {
                    statisticsDetail.add(serviceExecutionInfo.serviceType() + "." + serviceExecutionInfo.serviceName() +
                            ": " + serviceExecutionInfo.duration() + "s");
                }
            }
        }
        return statisticsDetail;
    }

    protected static String writeJson(Object object) {
        try {
            return writer.writeValueAsString(object);
        } catch (JacksonException e) {
            return "error creating json. " + e.getMessage();
        }
    }
}
