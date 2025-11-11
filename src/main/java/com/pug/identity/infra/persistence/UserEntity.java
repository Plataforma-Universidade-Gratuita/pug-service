package com.pug.identity.infra.persistence;

import com.pug.shared.infra.persistence.BaseUuidV7Entity;
import com.pug.shared.infra.persistence.TimestampColumnsListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;
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

import java.time.OffsetDateTime;

/**
 * PersonEntity represents a person in the identity system.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(
        callSuper = true,
        of = {"name"})
@Entity
@Table(
        name = "people",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_people_cpf",
                        columnNames = {"cpf"}),
        },
        indexes = {
                @Index(name = "idx_people_name", columnList = "name"),
                @Index(name = "idx_people_cpf", columnList = "cpf")
        })
@EntityListeners(TimestampColumnsListener.class)
@Indexed
public class UserEntity extends BaseUuidV7Entity {

    @Size(min = 11, max = 11)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "cpf", nullable = false, length = 11)
    private String cpf;

    @Size(max = 150)
    @FullTextField(analyzer = "pt_folded", searchAnalyzer = "pt_folded")
    @FullTextField(name = "name_auto", analyzer = "auto_ngram", searchAnalyzer = "pt_folded")
    @KeywordField(name = "name_exact", normalizer = "folding_lowercase")
    @KeywordField(name = "name_sort", normalizer = "folding_lowercase", sortable = Sortable.YES)
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
