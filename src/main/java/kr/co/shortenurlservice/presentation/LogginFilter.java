package kr.co.shortenurlservice.presentation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

@Component
@Slf4j
public class LogginFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // ContentCachingRequestWrapper로 요청 감싸기
        // HttpServletRequest를 감싸서 본문을 내부 버퍼에 캐싱
        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);

        // 필터 체인 진행
        filterChain.doFilter(cachingRequest, response);

        // 요청 URL과 메서드 로깅
        log.trace("Request Method {}, Request URL {}", cachingRequest.getMethod(), cachingRequest.getRequestURL());

        // 요청 헤더 로깅
        Enumeration<String> headerNames = cachingRequest.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = cachingRequest.getHeader(headerName);
                log.debug("{} : {}", headerName, headerValue);
            }
        }

        // 요청 본문 로깅 (캐싱된 본문 사용)
        String requestBody = getRequestBody(cachingRequest);
        if (!requestBody.isEmpty()) {
            log.debug("Request Body {}", requestBody);
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        // getContentAsByteArray()로 본문을 여러 번 읽을 수 있다.
        byte[] buf = request.getContentAsByteArray();
        if (buf.length > 0) {
            return new String(buf, StandardCharsets.UTF_8);
        }
        return "";
    }
}
