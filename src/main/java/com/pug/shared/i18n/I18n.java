package com.pug.shared.i18n;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.HttpHeaders;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@ApplicationScoped
public class I18n {
  private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
  private static final List<Locale> SUPPORTED = List.of(Locale.US, PT_BR);
  private static final Locale FALLBACK = Locale.US;

  public Locale resolve(HttpHeaders headers) {
    for (Locale req : headers.getAcceptableLanguages()) {
      for (Locale sup : SUPPORTED) {
        if (sup.equals(req) || sup.toLanguageTag().equalsIgnoreCase(req.toLanguageTag())) {
          return sup;
        }
      }
    }
    return FALLBACK;
  }

  public String msg(String key, Locale locale, Object... args) {
    var rb = ResourceBundle.getBundle("messages", locale);
    var pattern = rb.containsKey(key) ? rb.getString(key) : key;
    return (args == null || args.length == 0) ? pattern : MessageFormat.format(pattern, args);
  }
}
