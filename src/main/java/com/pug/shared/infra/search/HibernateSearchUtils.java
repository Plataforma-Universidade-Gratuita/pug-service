package com.pug.shared.infra.search;

import com.pug.shared.utils.StringUtils;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;

/** Utility class for Hibernate Search operations. */
public final class HibernateSearchUtils {
  /** Private constructor to prevent instantiation. */
  private HibernateSearchUtils() {}

  /**
   * Search entities by name using Hibernate Search with default field names and minimum token
   * length.
   *
   * @param em the EntityManager
   * @param type the entityId class type
   * @param key the search key
   * @param <T> the entityId type
   * @return a list of matching entities
   */
  public static <T> List<T> searchByName(EntityManager em, Class<T> type, String key) {
    return searchByName(em, type, key, "name", "name_exact", "name_auto", "name_sort", 3);
  }

  /**
   * Search entities by name using Hibernate Search.
   *
   * @param em the EntityManager
   * @param type the entityId class type
   * @param key the search key
   * @param nameField the name field for fuzzy matching
   * @param nameExactField the exact name field for wildcard matching
   * @param nameAutoField the auto-complete name field
   * @param nameSortField the field to sort results by
   * @param minTokenLength the minimum token length for partial matches
   * @param <T> the entityId type
   * @return a list of matching entities
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
              bool.should(f.wildcard().field(nameExactField).matching(key + "*").boost(8f));
              bool.should(f.wildcard().field(nameExactField).matching("*" + key + "*").boost(6f));

              for (String t : tokens) {
                if (t.length() >= minTokenLength) {
                  bool.should(f.wildcard().field(nameExactField).matching("*" + t + "*").boost(3f));
                }
              }

              bool.should(f.match().field(nameField).matching(key).fuzzy(1).boost(4f));
              bool.should(f.match().field(nameAutoField).matching(key).boost(2f));
              return bool;
            })
        .sort(
            f ->
                (nameSortField == null || nameSortField.isBlank())
                    ? f.score()
                    : f.score().then().field(nameSortField))
        .fetchAllHits();
  }
}
