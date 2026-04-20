package br.org.catolicasc.pug.geo.infra.persistence;

import br.org.catolicasc.pug.geo.domain.City;
import br.org.catolicasc.pug.shared.infra.persistence.BaseUuidV7Entity;
import br.org.catolicasc.pug.shared.infra.search.EsAnalysis;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.hibernate.type.SqlTypes;

/**
 * JPA entity representing a geographic City within the persistence layer.
 *
 * <p>This class acts as the database-mapped counterpart to the {@link City} domain aggregate. It
 * inherits a time-ordered UUIDv7 primary key and enforces strict uniqueness on the IBGE code at the
 * database level.
 *
 * <p>Additionally, this entity is marked with {@code @Indexed}, meaning Hibernate Search will
 * automatically synchronize its state with the underlying Elasticsearch/OpenSearch indices to
 * support advanced full-text queries.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(
    callSuper = true,
    of = {"name", "ibgeCode"})
@Entity
@Table(
    name = "cities",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_cities_ibge_code",
          columnNames = {"ibge_code"})
    })
@Indexed
public class CityEntity extends BaseUuidV7Entity {

  /**
   * The name of the city.
   *
   * <p>This field is heavily indexed for optimized searching using custom analyzers defined in
   * {@link EsAnalysis}. It projects into four distinct index fields:
   *
   * <ul>
   *   <li><b>name:</b> Standard full-text search (fuzzy matching, accent-insensitive).
   *   <li><b>name_auto:</b> Edge n-gram indexing for fast autocomplete ("type-as-you-go").
   *   <li><b>name_exact:</b> Wildcard and exact phrase matching.
   *   <li><b>name_sort:</b> Normalized keyword field used exclusively for alphabetical sorting.
   * </ul>
   */
  @FullTextField(analyzer = "pt_folded", searchAnalyzer = "pt_folded")
  @FullTextField(name = "name_auto", analyzer = "auto_ngram", searchAnalyzer = "pt_folded")
  @KeywordField(name = "name_exact", normalizer = "folding_lowercase")
  @KeywordField(name = "name_sort", normalizer = "folding_lowercase", sortable = Sortable.YES)
  @Column(name = "name", nullable = false, length = 100)
  private String name;

  /**
   * The unique 7-digit IBGE code of the city.
   *
   * <p>Mapped as a fixed-length {@code CHAR(7)} at the database level for optimal storage and
   * indexing. This serves as the natural key for the city, enforced by the {@code
   * uq_cities_ibge_code} unique constraint.
   */
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(name = "ibge_code", nullable = false, length = 7, unique = true)
  private String ibgeCode;
}
