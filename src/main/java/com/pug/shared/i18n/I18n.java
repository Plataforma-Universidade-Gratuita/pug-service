package com.pug.shared.i18n;

import jakarta.enterprise.context.ApplicationScoped;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** A simple internationalization (i18n) utility class for retrieving localized messages. */
@ApplicationScoped
public class I18n {
  private static final String BUNDLE = "messages";

  /**
   * Retrieve the translation for the given key and format it with the provided arguments.
   *
   * @param key the key to look up in the resource bundle.
   * @param args optional arguments to format the message.
   * @return the translated and formatted message.
   */
  public String translation(String key, Object... args) {
    try {
      ResourceBundle rb = ResourceBundle.getBundle(BUNDLE, Locale.getDefault());
      String pat = rb.containsKey(key) ? rb.getString(key) : key;
      return args == null || args.length == 0 ? pat : MessageFormat.format(pat, args);
    } catch (MissingResourceException e) {
      return key;
    }
  }
}
