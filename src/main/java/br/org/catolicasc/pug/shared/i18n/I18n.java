package br.org.catolicasc.pug.shared.i18n;

import jakarta.enterprise.context.ApplicationScoped;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Application-scoped utility component for handling internationalization (i18n).
 *
 * <p>This class provides a centralized mechanism to retrieve localized messages from the
 * application's resource bundles (e.g., {@code messages_en_US.properties}, {@code
 * messages_pt_BR.properties}). It leverages {@link ResourceBundle} for key resolution and {@link
 * MessageFormat} for dynamic variable interpolation.
 */
@ApplicationScoped
public class I18n {

  private static final String BUNDLE = "messages";

  /**
   * Retrieves the localized translation using the system's default locale.
   *
   * @param key the unique property key to look up in the resource bundle
   * @param args optional dynamic arguments to inject into the localized message
   * @return the fully translated and formatted string, or the raw {@code key} if not found
   */
  public String translation(String key, Object... args) {
    return translation(key, Locale.getDefault(), args);
  }

  /**
   * Retrieves the localized translation for a specific client {@link Locale}.
   *
   * <p>If the specific key is not found within the resolved resource bundle, or if the bundle
   * itself is missing, this method gracefully degrades by returning the raw {@code key} instead of
   * throwing an exception.
   *
   * @param key the unique property key to look up in the resource bundle (e.g., "error.internal")
   * @param locale the specific {@link Locale} used to resolve the correct properties file and
   *     format variables
   * @param args optional dynamic arguments to inject into the localized message (replaces
   *     placeholders like {@code {0}}, {@code {1}} in the properties file)
   * @return the fully translated and formatted string, or the raw {@code key} if no translation is
   *     found
   */
  public String translation(String key, Locale locale, Object... args) {
    if (locale == null) {
      locale = Locale.getDefault();
    }
    try {
      ResourceBundle rb = ResourceBundle.getBundle(BUNDLE, locale);
      String pat = rb.containsKey(key) ? rb.getString(key) : key;
      if (args == null || args.length == 0) {
        return pat;
      }
      MessageFormat formatter = new MessageFormat(pat, locale);
      return formatter.format(args);
    } catch (MissingResourceException e) {
      return key;
    }
  }
}
