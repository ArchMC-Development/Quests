package com.codepunisher.quests.util;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilChat {
  private static final Pattern HEX_PATTERN = Pattern.compile("&#([0-9a-fA-F]{6}|[0-9a-fA-F]{3})");

  /** This allows the string to work with hex and with color codes */
  @SuppressWarnings("deprecation")
  public static String colorize(String text) {
    Matcher matcher = HEX_PATTERN.matcher(text);
    StringBuilder buffer = new StringBuilder();

    while (matcher.find()) {
      String colorCode = matcher.group(1).toLowerCase();
      matcher.appendReplacement(buffer, ChatColor.of("#" + colorCode).toString());
    }

    return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
  }

  public static String capitalize(String input) {
    String lowerCaseInput = input.replace("_", " ").toLowerCase();
    return Character.toUpperCase(lowerCaseInput.charAt(0)) + lowerCaseInput.substring(1);
  }
}
