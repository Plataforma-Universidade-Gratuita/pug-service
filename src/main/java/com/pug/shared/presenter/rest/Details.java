package com.pug.shared.presenter.rest;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * A polymorphic wrapper for granular error details in REST API responses.
 * <p>
 * This record acts as a flexible container capable of holding any structured data
 * (such as a {@link java.util.List} of {@link FieldErrorsResponse}, a specific Map, or a custom POJO).
 * The {@link JsonValue} annotation ensures that Jackson serializes the underlying {@code payload}
 * directly into the {@code details} field of the parent {@link ApiError}, preventing unnecessary
 * JSON object nesting.
 *
 * @param payload The underlying structured data representing the error details.
 */
public record Details(@JsonValue Object payload) {
}