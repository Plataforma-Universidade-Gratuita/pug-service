package com.pug.partner.infra.persistence;

import com.pug.shared.infra.persistence.BaseUuidV7Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.hibernate.type.SqlTypes;

/** Persistence entity representing an Entity in the partner domain. */
@Getter
@Setter
@Builder
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
public class EntityEntity extends BaseUuidV7Entity {

  @Size(min = 14, max = 14)
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(name = "cnpj", nullable = false, length = 14, unique = true)
  private String cnpj;

  @Size(max = 150)
  @FullTextField(analyzer = "pt_folded", searchAnalyzer = "pt_folded")
  @FullTextField(name = "name_auto", analyzer = "auto_ngram", searchAnalyzer = "pt_folded")
  @KeywordField(name = "name_exact", normalizer = "folding_lowercase")
  @KeywordField(name = "name_sort", normalizer = "folding_lowercase", sortable = Sortable.YES)
  @Column(name = "name", nullable = false, length = 150)
  private String name;

  @Column(name = "city_id", nullable = false, insertable = false, updatable = false)
  private UUID cityId;

  @Size(max = 254)
  @Column(name = "address", length = 254)
  private String address;
}
