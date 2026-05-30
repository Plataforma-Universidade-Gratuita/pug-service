package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentCreateRequest;
import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import com.github.f4b6a3.uuid.UuidCreator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

/**
 * Builder class for creating {@link FormerStudentCreateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with random defaults for all fields, generating valid CPF, unique email,
 * and sensible academic data. Tests can override individual fields as needed.
 */
public class FormerStudentCreateRequestBuilder {
  private String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
  private String name = TestNameGenerator.generateRandomName();
  private String email = TestNameGenerator.generateUniqueEmail("test.com");
  private String password = "test-password";
  private String academicRegistration = TestNameGenerator.generateUniqueRegistration();
  private Campi campus = getRandomCampus();
  private UUID courseId = UuidCreator.getTimeOrderedEpoch();
  private BigDecimal requiredHours = new BigDecimal("100");
  private LocalDate startDate = LocalDate.now();
  private LocalDate dueDate = LocalDate.now().plusMonths(6);

  private FormerStudentCreateRequestBuilder() {}

  /**
   * Initializes a new builder with random defaults.
   *
   * @return a new {@link FormerStudentCreateRequestBuilder} instance
   */
  public static FormerStudentCreateRequestBuilder aFormerStudentCreateRequest() {
    return new FormerStudentCreateRequestBuilder();
  }

  /**
   * Sets the CPF.
   *
   * @param cpf the 11-digit CPF string
   * @return this builder instance
   */
  public FormerStudentCreateRequestBuilder withCpf(String cpf) {
    this.cpf = cpf;
    return this;
  }

  /**
   * Sets the formerStudent name.
   *
   * @param name the full name
   * @return this builder instance
   */
  public FormerStudentCreateRequestBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets the email address.
   *
   * @param email the email address
   * @return this builder instance
   */
  public FormerStudentCreateRequestBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  /**
   * Sets the password.
   *
   * @param password the raw password
   * @return this builder instance
   */
  public FormerStudentCreateRequestBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  /**
   * Sets the academic registration string.
   *
   * @param academicRegistration the university-issued registration
   * @return this builder instance
   */
  public FormerStudentCreateRequestBuilder withAcademicRegistration(String academicRegistration) {
    this.academicRegistration = academicRegistration;
    return this;
  }

  /**
   * Sets the campus assignment.
   *
   * @param campus the {@link Campi} enum value
   * @return this builder instance
   */
  public FormerStudentCreateRequestBuilder withCampus(Campi campus) {
    this.campus = campus;
    return this;
  }

  /**
   * Sets the enrolled course identifier.
   *
   * @param courseId the UUID of the course
   * @return this builder instance
   */
  public FormerStudentCreateRequestBuilder withCourseId(UUID courseId) {
    this.courseId = courseId;
    return this;
  }

  /**
   * Sets the required counterpart hours.
   *
   * @param requiredHours the total required hours
   * @return this builder instance
   */
  public FormerStudentCreateRequestBuilder withRequiredHours(BigDecimal requiredHours) {
    this.requiredHours = requiredHours;
    return this;
  }

  /**
   * Sets the enrollment start date.
   *
   * @param startDate the start date
   * @return this builder instance
   */
  public FormerStudentCreateRequestBuilder withStartDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Sets the enrollment due date.
   *
   * @param dueDate the due date
   * @return this builder instance
   */
  public FormerStudentCreateRequestBuilder withDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
    return this;
  }

  /**
   * Constructs the {@link FormerStudentCreateRequest} using the current builder state.
   *
   * @return a configured {@link FormerStudentCreateRequest} instance
   */
  public FormerStudentCreateRequest build() {
    return new FormerStudentCreateRequest(
        cpf,
        name,
        email,
        password,
        academicRegistration,
        campus,
        courseId,
        requiredHours,
        startDate,
        dueDate);
  }

  private static Campi getRandomCampus() {
    Campi[] values = Campi.values();
    return values[new Random().nextInt(values.length)];
  }
}
