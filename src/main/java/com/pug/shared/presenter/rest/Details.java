package com.pug.shared.presenter.rest;

import java.util.List;

/**
 * Represents the details of an error response, specifically for validation errors.
 *
 * @param fieldErrors A list of field-specific errors that occurred during validation.
 */
public record Details(List<FieldError> fieldErrors) {
}
