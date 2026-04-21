package br.org.catolicasc.pug.helpers;

import java.util.UUID;

/**
 * Stateless utility class responsible for generating unique test data strings.
 *
 * <p>This helper ensures that entity names, project titles, and other identifying strings remain
 * unique across test runs, preventing database unique constraint violations and improving log
 * readability during failure analysis.
 */
public final class TestNameGenerator {

  /** Private constructor to prevent instantiation of utility class. */
  private TestNameGenerator() {}

  private static final String[] NAMES = {
    "Ana", "Bruno", "Carlos", "Diana", "Eduardo", "Fernanda", "Gabriel", "Heloisa",
    "Igor", "Juliana", "Kevin", "Larissa", "Mateus", "Natalia", "Otavio", "Patricia"
  };

  private static final String[] SURNAMES = {
    "Silva", "Santos", "Oliveira", "Souza", "Pereira", "Ferreira", "Alves", "Rodrigues"
  };

  /**
   * Generates a random human name with a unique UUID suffix.
   *
   * @return a unique string containing a name, surname, and 4-char suffix
   */
  public static String generateRandomName() {
    String firstName = NAMES[(int) (Math.random() * NAMES.length)];
    String lastName = SURNAMES[(int) (Math.random() * SURNAMES.length)];
    String suffix = UUID.randomUUID().toString().substring(0, 4);

    return firstName + " " + lastName + " " + suffix;
  }

  private static final String[] SCHOOL_PREFIXES = {
    "Escola", "Centro", "Instituto", "Faculdade", "Academia"
  };

  private static final String[] SCHOOL_THEMES = {
    "de Tecnologia", "de Engenharia", "de Negócios", "de Artes", "de Ciências", "Politécnico"
  };

  /**
   * Generates a random academic school name with a unique UUID suffix.
   *
   * @return a unique string representing a school name
   */
  public static String generateRandomSchoolName() {
    String prefix = SCHOOL_PREFIXES[(int) (Math.random() * SCHOOL_PREFIXES.length)];
    String theme = SCHOOL_THEMES[(int) (Math.random() * SCHOOL_THEMES.length)];
    String suffix = UUID.randomUUID().toString().substring(0, 4);

    return prefix + " " + theme + " " + suffix;
  }

  private static final String[] COURSE_SUBJECTS = {
    "Sistemas de Informação",
    "Engenharia de Software",
    "Administração",
    "Engenharia Mecânica",
    "Design Digital",
    "Ciência de Dados",
    "Logística"
  };

  /**
   * Generates a random course name with a unique UUID suffix.
   *
   * @return a unique string representing a course name
   */
  public static String generateRandomCourseName() {
    String subject = COURSE_SUBJECTS[(int) (Math.random() * COURSE_SUBJECTS.length)];
    String suffix = UUID.randomUUID().toString().substring(0, 4);

    return subject + " " + suffix;
  }

  private static final String[] ENTITY_PREFIXES = {
    "WEG", "Tech", "Soluções", "Logística", "Inovação", "Global", "Indústria"
  };

  private static final String[] ENTITY_SUFFIXES = {
    "S.A.", "Ltda", "Group", "Systems", "Solutions", "Services"
  };

  /**
   * Generates a random partner entity name with a unique ID suffix.
   *
   * @return a unique string representing a partner entity name
   */
  public static String generateRandomEntityName() {
    String prefix = ENTITY_PREFIXES[(int) (Math.random() * ENTITY_PREFIXES.length)];
    String suffix = ENTITY_SUFFIXES[(int) (Math.random() * ENTITY_SUFFIXES.length)];
    String id = UUID.randomUUID().toString().substring(0, 4).toUpperCase();

    return prefix + " " + id + " " + suffix;
  }

  private static final String[] PROJECT_ADJECTIVES = {
    "Alpha", "Beta", "Sigma", "Global", "NextGen", "Green", "Smart"
  };

  private static final String[] PROJECT_NOUNS = {
    "Project", "Initiative", "Stream", "Horizon", "Lab", "Hub"
  };

  /**
   * Generates a random project name with a unique ID suffix.
   *
   * @return a unique string representing a project name
   */
  public static String generateRandomProjectName() {
    String adj = PROJECT_ADJECTIVES[(int) (Math.random() * PROJECT_ADJECTIVES.length)];
    String noun = PROJECT_NOUNS[(int) (Math.random() * PROJECT_NOUNS.length)];
    String id = UUID.randomUUID().toString().substring(0, 4).toUpperCase();

    return adj + " " + noun + " " + id;
  }
}
