package com.pug.shared.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NormalizationTest {

  @Test
  public void nullInputReturnsEmpty() {
    assertEquals("", Normalization.fold(null));
  }

  @Test
  public void emptyInputReturnsEmpty() {
    assertEquals("", Normalization.fold(""));
  }

  @Test
  public void trimsLeadingAndTrailingWhitespace() {
    assertEquals("abc", Normalization.fold("  abc  "));
    assertEquals("abc", Normalization.fold("\n\t abc \r\n"));
  }

  @Test
  public void preservesInternalWhitespace() {
    assertEquals("a   b", Normalization.fold("A   B"));
  }

  @Test
  public void lowercasesAndRemovesAccents_Portuguese() {
    assertEquals("sao jose", Normalization.fold("São José"));
    assertEquals("jaragua do sul", Normalization.fold("Jaraguá do Sul"));
    assertEquals("araquari", Normalization.fold("Araquári"));
  }

  @Test
  public void removesCombiningMarks() {
    // 'e' + COMBINING ACUTE ACCENT
    assertEquals("e", Normalization.fold("e\u0301"));
  }

  @Test
  public void preservesPunctuationAndSymbols() {
    assertEquals("cafe!", Normalization.fold("Café!"));
    assertEquals("ola-mundo?", Normalization.fold("Olá-Mundo?"));
    assertEquals("cafe ☕", Normalization.fold("Café ☕"));
  }

  @Test
  public void nonLatinLettersKeptButDeaccented() {
    // Greek tonos removed, letters preserved
    assertEquals("μαθημα", Normalization.fold("μάθημα"));
  }

  @Test
  public void germanEszettLowercases() {
    assertEquals("straße", Normalization.fold("STRAẞE"));
  }

  @Test
  public void idempotentOnAlreadyFolded() {
    assertEquals("sao jose", Normalization.fold("sao jose"));
  }
}
