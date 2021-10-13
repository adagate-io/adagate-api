package io.adagate.utils;

import io.adagate.exceptions.CardanoApiModuleException;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public final class ExceptionHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    public static void handleError(Throwable cause, Message<Object> message) {
        int code = CardanoApiModuleException.INTERNAL_SERVER_500_ERROR.getStatusCode();
        String statusMessage = CardanoApiModuleException.INTERNAL_SERVER_500_ERROR.getStatusMessage();
        if (cause instanceof CardanoApiModuleException) {
            final CardanoApiModuleException e = (CardanoApiModuleException) cause;
            LOGGER.warn(e);
            code = e.getStatusCode();
            statusMessage = e.getStatusMessage();
        } else {
            LOGGER.error("Unknown Error: ", cause);
        }
        message.fail(code, statusMessage);
    }
}
