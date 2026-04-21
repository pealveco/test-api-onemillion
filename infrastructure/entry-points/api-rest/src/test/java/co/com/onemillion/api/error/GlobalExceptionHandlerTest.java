package co.com.onemillion.api.error;

import co.com.onemillion.api.dto.CreateLeadRequest;
import co.com.onemillion.api.dto.ErrorResponse;
import co.com.onemillion.model.lead.exceptions.DuplicateLeadException;
import co.com.onemillion.model.lead.exceptions.LeadBusinessException;
import co.com.onemillion.model.lead.exceptions.LeadNotFoundException;
import co.com.onemillion.model.lead.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleValidationException() {
        ResponseEntity<ErrorResponse> response = handler.handleValidation(new ValidationException("Dato invalido"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VALIDATION_ERROR", response.getBody().code());
    }

    @Test
    void shouldHandleDuplicateException() {
        ResponseEntity<ErrorResponse> response = handler.handleDuplicate(new DuplicateLeadException("ana@test.com"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("DUPLICATE_LEAD", response.getBody().code());
    }

    @Test
    void shouldHandleNotFoundException() {
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(new LeadNotFoundException(7L));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("LEAD_NOT_FOUND", response.getBody().code());
    }

    @Test
    void shouldHandleBusinessException() {
        ResponseEntity<ErrorResponse> response = handler.handleBusiness(
                new LeadBusinessException("BUSINESS_ERROR", "Regla incumplida")
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("BUSINESS_ERROR", response.getBody().code());
    }

    @Test
    void shouldHandleRequestValidationException() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(
                new CreateLeadRequest("A", "ana@test.com", null, "instagram", null, BigDecimal.ONE),
                "request"
        );
        bindingResult.rejectValue("nombre", "Size", "El nombre debe tener al menos 2 caracteres");

        Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("sampleEndpoint", CreateLeadRequest.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleRequestValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VALIDATION_ERROR", response.getBody().code());
        assertEquals("El nombre debe tener al menos 2 caracteres", response.getBody().message());
    }

    @Test
    void shouldHandleInvalidJson() {
        ResponseEntity<ErrorResponse> response = handler.handleInvalidJson();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_REQUEST", response.getBody().code());
    }

    @Test
    void shouldHandleTypeMismatch() {
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                "abc",
                Long.class,
                "id",
                null,
                null
        );

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_REQUEST", response.getBody().code());
        assertEquals("El parametro id tiene un formato invalido", response.getBody().message());
    }

    @Test
    void shouldHandleUnexpectedException() {
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().code());
    }

    private void sampleEndpoint(CreateLeadRequest request) {
    }
}
