package io.adagate.utils;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.DecodeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public final class GZipUtils {

    public static byte[] decompress(Buffer response) throws IOException {
        try {
            return response.toJson().toString().getBytes();
        } catch (DecodeException jpe) {
            /* Is GZip format - continue */
        }

        byte[] buffer = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (
                ByteArrayInputStream bis = new ByteArrayInputStream(response.getBytes());
                GZIPInputStream gzipInputStream = new GZIPInputStream(bis)
        ) {
            int bytes_read;
            while ((bytes_read = gzipInputStream.read(buffer)) > 0) {
                baos.write(buffer, 0, bytes_read);
            }
            gzipInputStream.close();
            baos.close();
        } catch (IOException ioe) {
            throw ioe;
        }
        return baos.toByteArray();
    }

    public static Future<byte[]> decompress(Vertx vertx, Buffer compressedData) {
        Promise<byte[]> result = Promise.promise();
        vertx
            .executeBlocking(future -> {
                try {
                    result.complete(decompress(compressedData));
                } catch (IOException ioe) {
                    result.fail(ioe);
                }
            });
        return result.future();
    }
}
