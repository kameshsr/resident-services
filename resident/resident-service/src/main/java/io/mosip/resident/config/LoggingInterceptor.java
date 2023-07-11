package io.mosip.resident.config;

import io.mosip.kernel.core.logger.spi.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Kamesh Shekhar Prasad
 */
@Component
@ConditionalOnProperty(value = "resident.logging.interceptor.filter.enabled", havingValue = "true", matchIfMissing = false)
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private final Logger logger = LoggerConfiguration.logConfig(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(
            HttpRequest req, byte[] reqBody, ClientHttpRequestExecution ex) throws IOException {

        Thread currentThread = Thread.currentThread();
        StackTraceElement[] stackTrace = currentThread.getStackTrace();
        String stackTraceString = Stream.of(stackTrace).map(String::valueOf).collect(Collectors.joining("\n"));
        logger.debug("#request-log#"+ ","+ req.getMethod() + ","+ req.getURI() + ","+stackTraceString);
        ClientHttpResponse response = ex.execute(req, reqBody);
        return response;
    }
}
