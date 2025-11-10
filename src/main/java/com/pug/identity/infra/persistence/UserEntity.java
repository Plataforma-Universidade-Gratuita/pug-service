package com.pug.identity.infra.persistence;

import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.infra.persistence.BaseUuidV7Entity;
import com.pug.shared.infra.persistence.TimestampColumnsListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
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

/** Entity representing a user in the system. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(
    callSuper = true,
    of = {"email", "name"})
@Entity
@Table(
    name = "users",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_users_email",
          columnNames = {"email"}),
      @UniqueConstraint(
          name = "uq_users_cpf_acc_email",
          columnNames = {"cpf", "account_type", "email"})
    },
    indexes = {
      @Index(name = "idx_users_name", columnList = "name"),
      @Index(name = "idx_users_cpf", columnList = "cpf")
    })
@EntityListeners(TimestampColumnsListener.class)
@Indexed
public class UserEntity extends BaseUuidV7Entity {

  @Size(min = 11, max = 11)
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(name = "cpf", nullable = false, length = 11)
  private String cpf;

  @NotBlank
  @Size(max = 150)
  @FullTextField(analyzer = "pt_folded", searchAnalyzer = "pt_folded")
  @FullTextField(name = "name_auto", analyzer = "auto_ngram", searchAnalyzer = "pt_folded")
  @KeywordField(name = "name_exact", normalizer = "folding_lowercase")
  @KeywordField(name = "name_sort", normalizer = "folding_lowercase", sortable = Sortable.YES)
  @Column(name = "name", nullable = false, length = 150)
  private String name;

  @Size(max = 254)
  @Column(name = "email", nullable = false, length = 254)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(name = "account_type", nullable = false, length = 16)
  private AccountType accountType;

  @Size(max = 255)
  @Column(name = "password_hash")
  private String passwordHash;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;
}
