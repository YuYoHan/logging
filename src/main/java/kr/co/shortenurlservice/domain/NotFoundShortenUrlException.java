package kr.co.shortenurlservice.domain;

public class NotFoundShortenUrlException extends RuntimeException {

    public NotFoundShortenUrlException(String message) {
        super(message);
    }
}
