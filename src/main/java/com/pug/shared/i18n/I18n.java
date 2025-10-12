package com.pug.shared.i18n;

import jakarta.enterprise.context.ApplicationScoped;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@ApplicationScoped
public class I18n {
  private static final String BUNDLE = "messages";

  public String t(String key, Object... args) {
    try {
      ResourceBundle rb = ResourceBundle.getBundle(BUNDLE, Locale.getDefault());
      String pat = rb.containsKey(key) ? rb.getString(key) : key;
      return args == null || args.length == 0 ? pat : MessageFormat.format(pat, args);
    } catch (MissingResourceException e) {
      return key;
    }
  }
}
