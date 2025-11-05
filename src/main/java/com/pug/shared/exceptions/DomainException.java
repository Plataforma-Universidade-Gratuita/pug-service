package com.pug.shared.exceptions;

import com.pug.shared.errors.GenericErrorCodes;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class DomainException extends RuntimeException {
  private final GenericErrorCodes code;
  private final Map<String, Object> details;

  protected DomainException(GenericErrorCodes code) {
    super(code.toString());
    this.code = code;
    this.details = Collections.emptyMap();
  }

  protected DomainException(GenericErrorCodes code, Map<String, Object> details) {
    super(code.toString());
    this.code = code;
    this.details = details != null ? new HashMap<>(details) : Collections.emptyMap();
  }

  protected DomainException(GenericErrorCodes code, Throwable cause) {
    super(code.toString(), cause);
    this.code = code;
    this.details = Collections.emptyMap();
  }

  public GenericErrorCodes code() {
    return code;
  }

  public Map<String, Object> getDetails() {
    return Collections.unmodifiableMap(details);
  }
}
