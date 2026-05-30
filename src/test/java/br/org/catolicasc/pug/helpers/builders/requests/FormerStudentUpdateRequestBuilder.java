package br.org.catolicasc.pug.helpers.builders.requests;

import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentUpdateRequest;
import br.org.catolicasc.pug.helpers.TestNameGenerator;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

/**
 * Builder class for creating {@link FormerStudentUpdateRequest} DTOs in test scenarios.
 *
 * <p>Provides a fluent API with sensible defaults for partial updates. Fields default to {@code
 * null} (no change) except for commonly tested fields like name and campus.
 */
public class FormerStudentUpdateRequestBuilder {
  private String name = TestNameGenerator.generateRandomName();
  private String cpf = null;
  private String email = null;
  private String academicRegistration = null;
  private Campi campus = getRandomCampus();
  private UUID courseId = null;
  private BigDecimal requiredHours = null;
  private LocalDate startDate = null;
  private LocalDate dueDate = null;

  private FormerStudentUpdateRequestBuilder() {}

  /**
   * Initializes a new builder with sensible defaults for a partial update.
   *
   * @return a new {@link FormerStudentUpdateRequestBuilder} instance
   */
  public static FormerStudentUpdateRequestBuilder aFormerStudentUpdateRequest() {
    return new FormerStudentUpdateRequestBuilder();
  }

  /**
   * Sets the formerStudent name.
   *
   * @param name the new name, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public FormerStudentUpdateRequestBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets the CPF.
   *
   * @param cpf the new CPF, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public FormerStudentUpdateRequestBuilder withCpf(String cpf) {
    this.cpf = cpf;
    return this;
  }

  /**
   * Sets the email address.
   *
   * @param email the new email, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public FormerStudentUpdateRequestBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  /**
   * Sets the academic registration.
   *
   * @param academicRegistration the new registration, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public FormerStudentUpdateRequestBuilder withAcademicRegistration(String academicRegistration) {
    this.academicRegistration = academicRegistration;
    return this;
  }

  /**
   * Sets the campus assignment.
   *
   * @param campus the new {@link Campi}, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public FormerStudentUpdateRequestBuilder withCampus(Campi campus) {
    this.campus = campus;
    return this;
  }

  /**
   * Sets the enrolled course identifier.
   *
   * @param courseId the new course UUID, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public FormerStudentUpdateRequestBuilder withCourseId(UUID courseId) {
    this.courseId = courseId;
    return this;
  }

  /**
   * Sets the required counterpart hours.
   *
   * @param requiredHours the new required hours, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public FormerStudentUpdateRequestBuilder withRequiredHours(BigDecimal requiredHours) {
    this.requiredHours = requiredHours;
    return this;
  }

  /**
   * Sets the enrollment start date.
   *
   * @param startDate the new start date, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public FormerStudentUpdateRequestBuilder withStartDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Sets the enrollment due date.
   *
   * @param dueDate the new due date, or {@code null} to leave unchanged
   * @return this builder instance
   */
  public FormerStudentUpdateRequestBuilder withDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
    return this;
  }

  /**
   * Constructs the {@link FormerStudentUpdateRequest} using the current builder state.
   *
   * @return a configured {@link FormerStudentUpdateRequest} instance
   */
  public FormerStudentUpdateRequest build() {
    return new FormerStudentUpdateRequest(
        name,
        cpf,
        email,
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
