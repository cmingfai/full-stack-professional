package cc.oolong.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleException(
            ResourceNotFoundException e,
            HttpServletRequest request) {

        ApiError apiError=new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                now());

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiError> handleException(
            InsufficientAuthenticationException e,
            HttpServletRequest request) {

        ApiError apiError=new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                FORBIDDEN.value(),
                now());

        return new ResponseEntity<>(apiError,FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleException(
            BadCredentialsException e,
            HttpServletRequest request) {

        ApiError apiError=new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                UNAUTHORIZED.value(),
                now());

        return new ResponseEntity<>(apiError,UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(
            Exception e,
            HttpServletRequest request) {

        ApiError apiError=new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                now());

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
