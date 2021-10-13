package io.adagate.handlers.routes;

import io.adagate.exceptions.CardanoApiModuleException;
import io.adagate.handlers.database.AbstractDatabaseHandler;
import io.adagate.models.QueryOrder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import static io.adagate.exceptions.CardanoApiModuleException.BAD_REQUEST_400_ERROR;
import static io.adagate.exceptions.CardanoApiModuleException.NOT_FOUND_404_ERROR;
import static io.vertx.core.http.HttpHeaders.createOptimized;
import static io.vertx.core.json.Json.encodePrettily;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.time.ZoneId.of;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Locale.ENGLISH;
import static java.util.Objects.isNull;

@AllArgsConstructor
public abstract class AbstractRouteHandler implements Handler<RoutingContext> {
    protected final static Logger LOGGER = LoggerFactory.getLogger(AbstractDatabaseHandler.class);

    /* Constants */

    private final static DateTimeFormatter formatter = ofPattern("EEE, dd MMM yyyy HH:mm:ss z", ENGLISH).withZone(of("GMT"));
    public final static String GZIP = "gzip";
    public final static CharSequence APPLICATION_JSON = createOptimized("application/json; charset=utf-8");

    /* Properties */
    protected final Vertx vertx;

    /* Protected Methods */

    protected final String encode(Object object) {
        // FIXME: In production change to encode
        return encodePrettily(object);
    }

    protected final byte[] compress(String response, RoutingContext context) {
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final GZIPOutputStream gzip = new GZIPOutputStream(bos);
            final OutputStreamWriter osw = new OutputStreamWriter(gzip, StandardCharsets.UTF_8);
            osw.write(response);
            osw.close();
            return bos.toByteArray();
        } catch (IOException e) {
            handleError(e, context);
        }
        return new byte[0];
    }

    protected final HttpServerResponse addResponseHeaders(HttpResponseStatus status, RoutingContext context) {
        return context
                .response()
                .setStatusCode(status.code())
                .setStatusMessage(status.reasonPhrase())
                .putHeader(HttpHeaders.CONTENT_ENCODING, GZIP)
                .putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                .putHeader(HttpHeaders.DATE, formatter.format(new Date().toInstant()))
                .putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
    }

    protected final void handleError(Throwable cause, RoutingContext context) {
        handleError(
            NOT_FOUND_404_ERROR,
            "The requested component has not been found.",
            context
        );
    }

    protected final void handleError(CardanoApiModuleException error, String message, RoutingContext context) {
        context
            .response()
            .putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
            .setStatusCode(error.getStatusCode())
            .setStatusMessage(error.getStatusMessage())
            .end(getErrorResponse(error, message).encodePrettily());
    }


    protected final JsonObject getErrorResponse(CardanoApiModuleException exception, String message) {
        return new JsonObject()
                    .put("status_code", exception.getStatusCode())
                    .put("error", exception.getStatusMessage())
                    .put("message", message);
    }

    protected final <T> T getParameter(String parameter, Class<T> type, T defaultValue) throws CardanoApiModuleException {
        if (isNull(parameter)) { return defaultValue; } // fallback to default value

        if (type.equals(Integer.class)) {
            try {
                return type.cast(parseInt(parameter));
            } catch (NumberFormatException e) {
                throw BAD_REQUEST_400_ERROR;
            }
        }

        if (type.equals(QueryOrder.class)) {
            try {
                return type.cast(QueryOrder.valueOf(parameter.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw BAD_REQUEST_400_ERROR;
            }
        }

        throw new UnsupportedOperationException(
            format("No handler implemented for type: '%s' and param: '%s'", type.getSimpleName(), parameter)
        );
    }
}
