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
            code = ((CardanoApiModuleException) cause).getStatusCode();
            statusMessage = ((CardanoApiModuleException) cause).getStatusMessage();
        } else {
            LOGGER.error("Unknown Error: ", cause);
        }
        message.fail(code, statusMessage);
    }
}
