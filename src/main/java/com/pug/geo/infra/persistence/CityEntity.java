package com.pug.geo.infra.persistence;

import com.pug.shared.infra.persistence.BaseUuidV7Entity;
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

/** Cities Entity representing a city with its name and IBGE code. */
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

  @FullTextField(analyzer = "pt_folded", searchAnalyzer = "pt_folded")
  @FullTextField(name = "name_auto", analyzer = "auto_ngram", searchAnalyzer = "pt_folded")
  @KeywordField(name = "name_exact", normalizer = "folding_lowercase")
  @KeywordField(name = "name_sort", normalizer = "folding_lowercase", sortable = Sortable.YES)
  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(name = "ibge_code", nullable = false, length = 7, unique = true)
  private String ibgeCode;
}
