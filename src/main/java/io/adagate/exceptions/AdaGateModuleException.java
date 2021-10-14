package io.adagate.exceptions;

import lombok.Getter;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

@Getter
public class AdaGateModuleException extends Exception {

    private int statusCode;
    private String statusMessage;

    public AdaGateModuleException(int statusCode) {
        this(statusCode, AdaGateModuleException.getStatusMessage(statusCode));
    }

    public AdaGateModuleException(int statusCode, String message) {
        super(message);

        this.statusCode = statusCode;
        this.statusMessage = AdaGateModuleException.getStatusMessage(statusCode);
    }

    public static final AdaGateModuleException BAD_REQUEST_400_ERROR = new AdaGateModuleException(400);
    public static final AdaGateModuleException FORBIDDEN_403_ERROR = new AdaGateModuleException(403);
    public static final AdaGateModuleException NOT_FOUND_404_ERROR = new AdaGateModuleException(404);
    public static final AdaGateModuleException REQUEST_BANNED_418_ERROR = new AdaGateModuleException(418);
    public static final AdaGateModuleException PROJECT_OVER_LIMIT_429_ERROR = new AdaGateModuleException(429);
    public static final AdaGateModuleException INTERNAL_SERVER_500_ERROR = new AdaGateModuleException(500);

    private static String getStatusMessage(int statusCode) {
        switch (statusCode) {
            case 400:
                return BAD_REQUEST.reasonPhrase();
            case 403:
                return FORBIDDEN.reasonPhrase();
            case 404:
                return NOT_FOUND.reasonPhrase();
            case 418:
                return "Requested Banned";
            case 429:
                return "Project Over Limit";
            case 500:
                return INTERNAL_SERVER_ERROR.reasonPhrase();
            default:
                return "Unknown";
        }
    }
}
