package com.pug.shared.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DuplicateResourceExceptionTest {

    private static class TestDuplicateResourceException extends DuplicateResourceException {
        public TestDuplicateResourceException(String code) {
            super(code);
        }

        public TestDuplicateResourceException(String code, Throwable cause) {
            super(code,cause);
        }
    }

    @Test
    public void testDuplicateResourceException() {
        String expectedCode = "Duplicate resource error occurred";
        DuplicateResourceException exception = new TestDuplicateResourceException(expectedCode);
        assertEquals(expectedCode, exception.getMessage(), "The message (code) should be correctly set.");
        assertEquals(expectedCode, exception.code(), "The code method should return the same value as the message.");
    }

    @Test
    public void testDuplicateResourceExceptionNullCode() {
        DuplicateResourceException exception = new TestDuplicateResourceException(null);
        assertNull(exception.getMessage(), "The message should be null when passed null.");
        assertNull(exception.code(), "The code method should return null for null code.");
    }

    @Test
    public void testDuplicateResourceExceptionEmptyCode() {
        String expectedCode = "";
        DuplicateResourceException exception = new TestDuplicateResourceException(expectedCode);
        assertEquals(expectedCode, exception.getMessage(), "The code should be an empty string.");
        assertEquals(expectedCode, exception.code(), "The code method should return an empty string.");
    }

    @Test
    public void testDuplicateResourceExceptionSpecialCharactersInCode() {
        String expectedCode = "Duplicate resource error: @!#$%^&*()";
        DuplicateResourceException exception = new TestDuplicateResourceException(expectedCode);
        assertEquals(expectedCode, exception.getMessage(), "The exception should handle special characters in the code.");
        assertEquals(expectedCode, exception.code(), "The code method should return the correct value.");
    }

    @Test
    public void testDuplicateResourceExceptionLongCode() {
        String expectedCode = "A".repeat(1000); // A long code with 1000 "A"s
        DuplicateResourceException exception = new TestDuplicateResourceException(expectedCode);
        assertEquals(expectedCode, exception.getMessage(), "The exception should handle very long codes.");
        assertEquals(expectedCode, exception.code(), "The code method should handle long codes correctly.");
    }

    @Test
    public void testDuplicateResourceExceptionWithNullCause() {
        String expectedCode = "Duplicate resource error occurred";
        DuplicateResourceException exception = new TestDuplicateResourceException(expectedCode, null);
        assertEquals(expectedCode, exception.getMessage(), "The message (code) should be set correctly with a null cause.");
        assertNull(exception.getCause(), "The cause should be null.");
        assertEquals(expectedCode, exception.code(), "The code method should return the correct value.");
    }

    @Test
    public void testDuplicateResourceExceptionWithCause() {
        String expectedCode = "Duplicate resource error occurred";
        Throwable cause = new Throwable("Cause of the duplicate resource error");
        DuplicateResourceException exception = new TestDuplicateResourceException(expectedCode, cause);
        assertEquals(expectedCode, exception.getMessage(), "The message (code) should be set correctly.");
        assertEquals(cause, exception.getCause(), "The cause should be passed correctly.");
        assertEquals(expectedCode, exception.code(), "The code method should return the correct value.");
    }
}
