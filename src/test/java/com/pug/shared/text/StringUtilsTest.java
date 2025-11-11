package com.pug.shared.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.shared.utils.StringUtils;
import org.junit.jupiter.api.Test;

public class StringUtilsTest {

  @Test
  void nullInputReturnsEmpty() {
    assertEquals("", StringUtils.fold(null));
  }

  @Test
  void emptyInputReturnsEmpty() {
    assertEquals("", StringUtils.fold(""));
  }

  @Test
  void trimsLeadingAndTrailingWhitespace() {
    assertEquals("abc", StringUtils.fold("  abc  "));
    assertEquals("abc", StringUtils.fold("\n\t abc \r\n"));
  }

  @Test
  void preservesInternalWhitespace() {
    assertEquals("a   b", StringUtils.fold("A   B"));
  }

  @Test
  void lowercasesAndRemovesAccents_Portuguese() {
    assertEquals("sao jose", StringUtils.fold("São José"));
    assertEquals("jaragua do sul", StringUtils.fold("Jaraguá do Sul"));
    assertEquals("araquari", StringUtils.fold("Araquári"));
  }

  @Test
  void removesCombiningMarks() {
    assertEquals("e", StringUtils.fold("e\u0301"));
  }

  @Test
  void preservesPunctuationAndSymbols() {
    assertEquals("cafe!", StringUtils.fold("Café!"));
    assertEquals("ola-mundo?", StringUtils.fold("Olá-Mundo?"));
    assertEquals("cafe ☕", StringUtils.fold("Café ☕"));
  }

  @Test
  void nonLatinLettersKeptButDeaccented() {
    assertEquals("μαθημα", StringUtils.fold("μάθημα"));
  }

  @Test
  void germanEszettLowercases() {
    assertEquals("straße", StringUtils.fold("STRAẞE"));
  }

  @Test
  void idempotentOnAlreadyFolded() {
    assertEquals("sao jose", StringUtils.fold("sao jose"));
  }

  @Test
  void isEmptyReturnsTrueForNullOrWhitespace() {
    assertTrue(StringUtils.isEmpty(null));
    assertTrue(StringUtils.isEmpty(""));
    assertTrue(StringUtils.isEmpty("   "));
  }

  @Test
  void isEmptyReturnsFalseForNonWhitespace() {
    assertFalse(StringUtils.isEmpty("a"));
    assertFalse(StringUtils.isEmpty(" abc "));
  }

  @Test
  void trimNullReturnsNull() {
    assertNull(StringUtils.trim(null));
  }

  @Test
  void trimRemovesOuterWhitespaceOnly() {
    assertEquals("a  b", StringUtils.trim("  a  b  "));
  }
}
