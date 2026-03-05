package com.pug.academic.infra.persistence;

import com.pug.shared.infra.persistence.BaseAuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import java.util.UUID;

/**
 * JPA entity representing an Academic Course within the persistence layer.
 * <p>
 * This class acts as the database-mapped counterpart to the {@link com.pug.academic.domain.Course}
 * domain aggregate. It inherits a time-ordered UUIDv7 primary key and standard audit tracking
 * fields from {@link BaseAuditedEntity}.
 * <p>
 * This entity is marked with {@code @Indexed}, allowing Hibernate Search to
 * synchronize its state with underlying Elasticsearch/OpenSearch indices to support
 * advanced full-text queries.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(
        callSuper = true,
        of = {"name", "schoolId"})
@Entity
@Table(
        name = "courses",
        indexes = {@Index(name = "idx_courses_school", columnList = "school_id")})
@Indexed
public class CourseEntity extends BaseAuditedEntity {

  /**
   * The name of the academic course.
   * <p>
   * Heavily indexed for optimized searching using custom analyzers. It projects into
   * multiple search fields to support fuzzy matching, exact phrases, and fast autocomplete.
   */
  @FullTextField(analyzer = "pt_folded", searchAnalyzer = "pt_folded")
  @FullTextField(name = "name_auto", analyzer = "auto_ngram", searchAnalyzer = "pt_folded")
  @KeywordField(name = "name_exact", normalizer = "folding_lowercase")
  @KeywordField(name = "name_sort", normalizer = "folding_lowercase", sortable = Sortable.YES)
  @Column(name = "name", nullable = false, length = 120, unique = true)
  private String name;

  /**
   * The unique identifier (UUID) of the associated {@link SchoolEntity}.
   * <p>
   * Acts as a foreign key linking this course to its parent academic school.
   */
  @Column(name = "school_id", nullable = false)
  private UUID schoolId;
}