package com.pug.shared.presenter.rest;

/**
 * Represents a specific field error in a validation context.
 *
 * @param field The name of the field that caused the error.
 * @param code The error code related to the field.
 * @param message The error message related to the field.
 */
public record FieldError(String field, String code, String message){
}