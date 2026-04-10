package com.pug.shared.infra.audit;

/**
 * Represents a granular change for a specific domain field.
 *
 * <p>This record acts as a container for tracking the transition of a field's value from an old
 * state to a new state within the audit system.
 *
 * @param oldValue the original value of the field before the modification
 * @param newValue the new value of the field after the modification
 */
public record FieldChange(Object oldValue, Object newValue) {}
