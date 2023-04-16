package cc.oolong.exception;

import java.time.LocalDateTime;

public record ApiError(
        String path,
        String message,
        int StatusCode,
        LocalDateTime localDateTime
) {
}
