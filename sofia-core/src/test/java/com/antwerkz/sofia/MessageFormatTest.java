package com.antwerkz.sofia;

import java.text.Format;
import java.text.MessageFormat;

import org.testng.Assert;
import org.testng.annotations.Test;

public class MessageFormatTest {
  @Test
  public void argCount() {
    count("Today''s date is {0,date,full} and now a number {1,number}");
  }

  private void count(final String pattern) {
    MessageFormat messageFormat = new MessageFormat(pattern);
    Format[] formats = messageFormat.getFormats();
    Assert.assertEquals(formats.length, 2);
  }
}
