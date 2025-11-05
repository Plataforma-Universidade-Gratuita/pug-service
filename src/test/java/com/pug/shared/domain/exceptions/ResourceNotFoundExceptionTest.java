package com.pug.shared.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ResourceNotFoundExceptionTest {

    private static class TestResourceNotFoundException extends ResourceNotFoundException {
        public TestResourceNotFoundException(String code) {
            super(code);
        }

        public TestResourceNotFoundException(String code, Throwable cause) {
            super(code, cause);
        }
    }

    @Test
    public void testResourceNotFoundException() {
        String expectedCode = "Resource not found error occurred";
        ResourceNotFoundException exception = new TestResourceNotFoundException(expectedCode);
        assertEquals(expectedCode, exception.getMessage(), "The message (code) should be correctly set.");
        assertEquals(expectedCode, exception.code(), "The code method should return the same value as the message.");
    }

    @Test
    public void testResourceNotFoundExceptionNullCode() {
        ResourceNotFoundException exception = new TestResourceNotFoundException(null);
        assertNull(exception.getMessage(), "The message should be null when passed null.");
        assertNull(exception.code(), "The code method should return null for null code.");
    }

    @Test
    public void testResourceNotFoundExceptionEmptyCode() {
        String expectedCode = "";
        ResourceNotFoundException exception = new TestResourceNotFoundException(expectedCode);
        assertEquals(expectedCode, exception.getMessage(), "The code should be an empty string.");
        assertEquals(expectedCode, exception.code(), "The code method should return an empty string.");
    }

    @Test
    public void testResourceNotFoundExceptionSpecialCharactersInCode() {
        String expectedCode = "Resource not found error: @!#$%^&*()";
        ResourceNotFoundException exception = new TestResourceNotFoundException(expectedCode);
        assertEquals(expectedCode, exception.getMessage(), "The exception should handle special characters in the code.");
        assertEquals(expectedCode, exception.code(), "The code method should return the correct value.");
    }

    @Test
    public void testResourceNotFoundExceptionLongCode() {
        String expectedCode = "A".repeat(1000); // A long code with 1000 "A"s
        ResourceNotFoundException exception = new TestResourceNotFoundException(expectedCode);
        assertEquals(expectedCode, exception.getMessage(), "The exception should handle very long codes.");
        assertEquals(expectedCode, exception.code(), "The code method should handle long codes correctly.");
    }

    @Test
    public void testResourceNotFoundExceptionWithNullCause() {
        String expectedCode = "Resource not found error occurred";
        ResourceNotFoundException exception = new TestResourceNotFoundException(expectedCode, null);
        assertEquals(expectedCode, exception.getMessage(), "The message (code) should be set correctly with a null cause.");
        assertNull(exception.getCause(), "The cause should be null.");
        assertEquals(expectedCode, exception.code(), "The code method should return the correct value.");
    }

    @Test
    public void testResourceNotFoundExceptionWithCause() {
        String expectedCode = "Resource not found error occurred";
        Throwable cause = new Throwable("Cause of the resource not found error");
        ResourceNotFoundException exception = new TestResourceNotFoundException(expectedCode, cause);
        assertEquals(expectedCode, exception.getMessage(), "The message (code) should be set correctly.");
        assertEquals(cause, exception.getCause(), "The cause should be passed correctly.");
        assertEquals(expectedCode, exception.code(), "The code method should return the correct value.");
    }
}
