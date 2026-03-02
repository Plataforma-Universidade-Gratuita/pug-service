package com.pug.shared.utils;

import java.util.List;
import java.util.Locale;

/**
 * Utility class containing helper methods for the presentation layer (e.g., REST controllers or presenters).
 * <p>
 * Provides standardized, stateless logic for handling UI/API-related tasks such as
 * client locale resolution, ensuring consistent behavior across different API endpoints.
 */
public final class PresenterUtils {

  /**
   * Private constructor to prevent instantiation of this utility class.
   */
  private PresenterUtils() {
  }

  /**
   * Resolves the most suitable client locale from a provided list of acceptable options.
   * <p>
   * This method is typically used in conjunction with the HTTP {@code Accept-Language} header
   * to determine the client's preferred language. If the client does not specify any acceptable
   * locales, or if the provided list is null/empty, the system strictly defaults to
   * Brazilian Portuguese ({@code pt-BR}).
   *
   * @param acceptable a prioritized {@link List} of acceptable {@link Locale}s requested by the client
   * @return the most preferred {@link Locale} from the list, or the {@code pt-BR} fallback if the list is empty
   */
  public static Locale pickLocale(List<Locale> acceptable) {
    return CollectionUtils.isEmpty(acceptable)
            ? Locale.forLanguageTag("pt-BR")
            : acceptable.getFirst();
  }
}