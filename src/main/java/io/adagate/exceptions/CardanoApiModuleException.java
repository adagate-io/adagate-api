package io.adagate.exceptions;

import lombok.Getter;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

@Getter
public class CardanoApiModuleException extends Exception {

    private int statusCode;
    private String statusMessage;

    public CardanoApiModuleException(int statusCode) {
        this(statusCode, CardanoApiModuleException.getStatusMessage(statusCode));
    }

    public CardanoApiModuleException(int statusCode, String message) {
        super(message);

        this.statusCode = statusCode;
        this.statusMessage = CardanoApiModuleException.getStatusMessage(statusCode);
    }

    public static final CardanoApiModuleException BAD_REQUEST_400_ERROR = new CardanoApiModuleException(400);
    public static final CardanoApiModuleException FORBIDDEN_403_ERROR = new CardanoApiModuleException(403);
    public static final CardanoApiModuleException NOT_FOUND_404_ERROR = new CardanoApiModuleException(404);
    public static final CardanoApiModuleException REQUEST_BANNED_418_ERROR = new CardanoApiModuleException(418);
    public static final CardanoApiModuleException PROJECT_OVER_LIMIT_429_ERROR = new CardanoApiModuleException(429);
    public static final CardanoApiModuleException INTERNAL_SERVER_500_ERROR = new CardanoApiModuleException(500);

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
