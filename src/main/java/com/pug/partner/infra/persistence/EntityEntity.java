package com.pug.partner.infra.persistence;

import com.pug.shared.infra.persistence.BaseUuidV7Entity;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * Persistence entity representing an Entity in the partner domain.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(
        callSuper = true,
        of = {"name", "cnpj"})
@Entity
@Indexed
@Table(
        name = "entities",
        indexes = {
                @Index(name = "idx_entities_name", columnList = "name"),
                @Index(name = "idx_entities_city", columnList = "city_id")
        })
@SuperBuilder
public class EntityEntity extends BaseUuidV7Entity {

  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(name = "cnpj", nullable = false, length = 14, unique = true)
  private String cnpj;

  @FullTextField(analyzer = "pt_folded", searchAnalyzer = "pt_folded")
  @FullTextField(name = "name_auto", analyzer = "auto_ngram", searchAnalyzer = "pt_folded")
  @KeywordField(name = "name_exact", normalizer = "folding_lowercase")
  @KeywordField(name = "name_sort", normalizer = "folding_lowercase", sortable = Sortable.YES)
  @Column(name = "name", nullable = false, length = 150)
  private String name;

  @Column(name = "city_id", nullable = false)
  private UUID cityId;

  @Column(name = "address", length = 254)
  private String address;
}