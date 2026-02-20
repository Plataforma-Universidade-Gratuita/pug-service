package com.pug.shared.presenter.rest;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the details of an error response.
 *
 * <p>This wrapper can hold any structure (List of FieldError, a Map, a custom POJO, etc.).
 * The @JsonValue annotation ensures that the underlying object is serialized directly as the value
 * of the 'details' key in the ApiError, avoiding extra nesting.
 *
 * @param payload The actual error detail data.
 */
public record Details(@JsonValue Object payload) {}
