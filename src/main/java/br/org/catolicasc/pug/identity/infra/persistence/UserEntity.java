package br.org.catolicasc.pug.identity.infra.persistence;

import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.shared.infra.persistence.BaseAuditedEntity;
import br.org.catolicasc.pug.shared.infra.search.EsAnalysis;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
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
 * JPA entity representing a User within the persistence layer.
 *
 * <p>This class acts as the database-mapped counterpart to the {@link User} domain aggregate. It
 * inherits a time-ordered UUIDv7 primary key and standard audit tracking fields from {@link
 * BaseAuditedEntity}. It enforces strict uniqueness on the CPF at the database level.
 *
 * <p>Additionally, this entity is marked with {@code @Indexed}, meaning Hibernate Search will
 * automatically synchronize its state with the underlying Elasticsearch/OpenSearch indices to
 * support advanced full-text queries on the user's name.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(
    callSuper = true,
    of = {"name"})
@Entity
@Table(
    name = "users",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_users_cpf",
          columnNames = {"cpf"}),
    },
    indexes = {
      @Index(name = "idx_users_name", columnList = "name"),
      @Index(name = "idx_users_cpf", columnList = "cpf")
    })
@Indexed
@SuperBuilder
public class UserEntity extends BaseAuditedEntity {

  /**
   * The unique 11-digit Brazilian CPF of the user.
   *
   * <p>Mapped as a fixed-length {@code CHAR(11)} at the database level for optimal storage and
   * indexing. This serves as a natural key for the user, enforced by the {@code uq_users_cpf}
   * unique constraint.
   */
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(name = "cpf", nullable = false, length = 11)
  private String cpf;

  /**
   * The full name of the user.
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
  @Column(name = "name", nullable = false, length = 150)
  private String name;
}
