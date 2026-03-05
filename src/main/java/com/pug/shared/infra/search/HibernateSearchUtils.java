package com.pug.shared.infra.search;

import com.pug.shared.utils.StringUtils;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;

/**
 * Utility class encapsulating complex Hibernate Search querying logic.
 *
 * <p>This class provides standardized, multi-field search strategies (like weighted boosting and
 * fuzzy matching) to ensure consistent and highly relevant search results across various domain
 * entities.
 */
public final class HibernateSearchUtils {

  /** Private constructor to prevent instantiation of utility class. */
  private HibernateSearchUtils() {}

  /**
   * Executes a robust search against an entity's name using a default set of mapped fields.
   *
   * <p>Assumes the entity has the following indexed fields configured: {@code "name"}, {@code
   * "name_exact"}, {@code "name_auto"}, and {@code "name_sort"}.
   *
   * @param em the current {@link EntityManager}
   * @param type the JPA entity class type to search against (must be indexed)
   * @param key the raw search string provided by the user
   * @param <T> the entity type
   * @return a scored and sorted list of matching entities
   */
  public static <T> List<T> searchByName(EntityManager em, Class<T> type, String key) {
    return searchByName(em, type, key, "name", "name_exact", "name_auto", "name_sort", 3);
  }

  /**
   * Executes a highly tuned, multi-field boolean search query.
   *
   * <p><b>Scoring/Boosting Strategy:</b>
   *
   * <ul>
   *   <li><i>Exact Prefix (Boost 8.0)</i>: The name starts exactly with the search key.
   *   <li><i>Exact Substring (Boost 6.0)</i>: The search key appears exactly anywhere in the name.
   *   <li><i>Fuzzy Match (Boost 4.0)</i>: Standard text matching allowing for 1 typo (Levenshtein
   *       distance).
   *   <li><i>Token Substring (Boost 3.0)</i>: Individual words from the search key appear inside
   *       the name.
   *   <li><i>N-Gram Auto (Boost 2.0)</i>: Partial word matches using edge n-grams.
   * </ul>
   *
   * @param em the current {@link EntityManager}
   * @param type the JPA entity class type to search against
   * @param key the raw search string provided by the user
   * @param nameField the standard analyzed text field used for fuzzy matching (e.g., analyzed with
   *     "pt_folded")
   * @param nameExactField the keyword or normalized field used for exact wildcard matching
   * @param nameAutoField the n-gram analyzed field used for autocomplete matching
   * @param nameSortField the keyword/normalized field used as a secondary sort tie-breaker
   * @param minTokenLength the minimum length a word must be to trigger individual token wildcard
   *     matching
   * @param <T> the entity type
   * @return a scored and sorted list of matching entities
   */
  public static <T> List<T> searchByName(
      EntityManager em,
      Class<T> type,
      String key,
      String nameField,
      String nameExactField,
      String nameAutoField,
      String nameSortField,
      int minTokenLength) {

    if (StringUtils.isEmpty(key)) {
      return List.of();
    }

    String[] tokens = key.split("\\s+");
    SearchSession s = Search.session(em);

    return s.search(type)
        .where(
            f -> {
              var bool = f.bool();
              // Highest priority: Starts exactly with the search term
              bool.should(f.wildcard().field(nameExactField).matching(key + "*").boost(8f));
              // High priority: Contains the exact phrase anywhere
              bool.should(f.wildcard().field(nameExactField).matching("*" + key + "*").boost(6f));
              // Medium priority: Contains individual words from the search phrase
              for (String t : tokens) {
                if (t.length() >= minTokenLength) {
                  bool.should(f.wildcard().field(nameExactField).matching("*" + t + "*").boost(3f));
                }
              }
              // Normal priority: Handles typos (1 character difference)
              bool.should(f.match().field(nameField).matching(key).fuzzy(1).boost(4f));
              // Base priority: Autocomplete prefix matching via n-grams
              bool.should(f.match().field(nameAutoField).matching(key).boost(2f));
              return bool;
            })
        .sort(
            f ->
                (nameSortField == null || nameSortField.isBlank())
                    ? f.score() // Sort exclusively by relevance score
                    : f.score().then().field(nameSortField)) // Tie-breaker using alphabetical sort
        .fetchAllHits();
  }
}
