package br.org.catolicasc.pug.identity.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.domain.vos.Cpf;
import br.org.catolicasc.pug.identity.domain.vos.Email;
import br.org.catolicasc.pug.identity.infra.persistence.AccountEntity;
import br.org.catolicasc.pug.identity.infra.persistence.AdminEntity;
import br.org.catolicasc.pug.identity.infra.persistence.UserEntity;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import com.github.f4b6a3.uuid.UuidCreator;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Identity Infra Branch Coverage")
class IdentityInfraBranchCoverageTest {

  @Test
  @DisplayName("Should return null for mapper null inputs")
  void shouldReturnNullForMapperNullInputs() {
    assertThat(AccountMapper.toDomain(null)).isNull();
    assertThat(AccountMapper.toEntity(null)).isNull();
    assertThat(AccountMapper.toView(null)).isNull();

    assertThat(AdminMapper.toDomain(null)).isNull();
    assertThat(AdminMapper.toEntity(null)).isNull();
    assertThat(AdminMapper.toView(null, new AccountEntity())).isNull();
    assertThat(AdminMapper.toView(new AdminEntity(), null)).isNull();

    assertThat(UserMapper.toDomain(null)).isNull();
    assertThat(UserMapper.toEntity(null)).isNull();
    assertThat(UserMapper.toView(null)).isNull();
  }

  @Test
  @DisplayName("Should copy account fields into existing entity")
  void shouldCopyAccountFields() {
    Account account =
        Account.factory(
            UuidCreator.getTimeOrderedEpoch(),
            Email.factory("updated@pug.com"),
            AccountType.ADMIN,
            "new-hash");
    AccountEntity entity = new AccountEntity();

    AccountMapper.copy(account, entity);

    assertThat(entity.getEmail()).isEqualTo(account.getEmail().getValue());
    assertThat(entity.getAccountType()).isEqualTo(account.getAccountType());
    assertThat(entity.getPasswordHash()).isEqualTo(account.getPasswordHash());
    assertThat(entity.getActive()).isEqualTo(account.getActive());
    assertThat(entity.getCreatedAt()).isEqualTo(account.getAuditInfo().getCreatedAt());
    assertThat(entity.getUpdatedAt()).isEqualTo(account.getAuditInfo().getUpdatedAt());
  }

  @Test
  @DisplayName("Should copy admin fields into existing entity")
  void shouldCopyAdminFields() {
    Admin admin = Admin.factory(UuidCreator.getTimeOrderedEpoch(), Campi.JARAGUA_DO_SUL);
    AdminEntity entity = new AdminEntity();

    AdminMapper.copy(admin, entity);

    assertThat(entity.getCampus()).isEqualTo(admin.getCampus());
  }

  @Test
  @DisplayName("Should copy user fields into existing entity")
  void shouldCopyUserFields() {
    User user =
        User.factory(
            Cpf.factory(TestBrazilianIdentifierGenerator.generateValidCpf()), "Updated User");
    UserEntity entity = new UserEntity();

    UserMapper.copy(user, entity);

    assertThat(entity.getName()).isEqualTo(user.getName());
    assertThat(entity.getCpf()).isEqualTo(user.getCpf().getValue());
    assertThat(entity.getCreatedAt()).isEqualTo(user.getAuditInfo().getCreatedAt());
    assertThat(entity.getUpdatedAt()).isEqualTo(user.getAuditInfo().getUpdatedAt());
  }

  @Test
  @DisplayName("Should project account, admin, and user views correctly")
  void shouldProjectViews() {
    OffsetDateTime now = OffsetDateTime.now();

    AccountEntity accountEntity = new AccountEntity();
    accountEntity.setId(UuidCreator.getTimeOrderedEpoch());
    accountEntity.setUserId(UuidCreator.getTimeOrderedEpoch());
    accountEntity.setEmail("user@example.com");
    accountEntity.setAccountType(AccountType.ADMIN);
    accountEntity.setCreatedAt(now);
    accountEntity.setUpdatedAt(now);
    accountEntity.setActive(true);

    UserEntity userEntity = new UserEntity();
    userEntity.setId(UuidCreator.getTimeOrderedEpoch());
    userEntity.setCpf(TestBrazilianIdentifierGenerator.generateValidCpf());
    userEntity.setName("User");
    userEntity.setCreatedAt(now);
    userEntity.setUpdatedAt(now);

    AdminEntity adminEntity = new AdminEntity();
    adminEntity.setAccountId(accountEntity.getId());
    adminEntity.setGrantedAt(now);
    adminEntity.setCampus(Campi.JARAGUA_DO_SUL);

    var accountView = AccountMapper.toView(accountEntity);
    var adminView = AdminMapper.toView(adminEntity, accountEntity);
    var userView = UserMapper.toView(userEntity);

    assertThat(accountView).isNotNull();
    assertThat(accountView.email()).isEqualTo(accountEntity.getEmail());
    assertThat(adminView).isNotNull();
    assertThat(adminView.accountView()).isEqualTo(accountView);
    assertThat(adminView.campus()).isEqualTo(adminEntity.getCampus());
    assertThat(userView).isNotNull();
    assertThat(userView.name()).isEqualTo(userEntity.getName());
  }

  @Test
  @DisplayName("Should ignore copy when domain or entity is null")
  void shouldIgnoreCopyWithNulls() {
    AccountEntity accountEntity = new AccountEntity();
    AdminEntity adminEntity = new AdminEntity();
    UserEntity userEntity = new UserEntity();

    AccountMapper.copy(null, accountEntity);
    AccountMapper.copy(
        Account.factory(
            UuidCreator.getTimeOrderedEpoch(),
            Email.factory("noop@pug.com"),
            AccountType.ADMIN,
            "hash"),
        null);

    AdminMapper.copy(null, adminEntity);
    AdminMapper.copy(Admin.factory(UuidCreator.getTimeOrderedEpoch(), Campi.JARAGUA_DO_SUL), null);

    UserMapper.copy(null, userEntity);
    UserMapper.copy(
        User.factory(Cpf.factory(TestBrazilianIdentifierGenerator.generateValidCpf()), "Noop"),
        null);

    assertThat(accountEntity.getEmail()).isNull();
    assertThat(adminEntity.getCampus()).isNull();
    assertThat(userEntity.getName()).isNull();
  }
}
