package br.org.catolicasc.pug.shared.exceptions;

import br.org.catolicasc.pug.shared.domain.enums.GenericFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AppValidationException Tests")
class AppValidationExceptionTest {

    @Test
    @DisplayName("Should throw IllegalArgumentException if fieldErrors list is null or empty")
    void shouldValidateConstructorInput() {
        assertThatThrownBy(() -> new AppValidationException((List<GenericFieldErrorCodes>) null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new AppValidationException(Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should store field errors correctly")
    void shouldStoreErrors() {
        List<GenericFieldErrorCodes> errors = List.of(SharedFieldErrorCodes.INVALID_NAME_BLANK);
        AppValidationException ex = new AppValidationException(errors);

        assertThat(ex.getFieldErrors()).containsExactly(SharedFieldErrorCodes.INVALID_NAME_BLANK);
    }
}