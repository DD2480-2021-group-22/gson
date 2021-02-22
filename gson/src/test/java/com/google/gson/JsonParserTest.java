/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson;

import com.google.gson.common.TestTypes.BagOfPrimitives;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import junit.framework.TestCase;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.StringReader;

/**
 * Unit test for {@link JsonParser}
 *
 * @author Inderjeet Singh
 */
public class JsonParserTest extends TestCase {

  public void testParseInvalidJson() {
    try {
      JsonParser.parseString("[[]");
      fail();
    } catch (JsonSyntaxException expected) { }
  }

  public void testParseUnquotedStringArrayFails() {
    JsonElement element = JsonParser.parseString("[a,b,c]");
    assertEquals("a", element.getAsJsonArray().get(0).getAsString());
    assertEquals("b", element.getAsJsonArray().get(1).getAsString());
    assertEquals("c", element.getAsJsonArray().get(2).getAsString());
    assertEquals(3, element.getAsJsonArray().size());
  }

  public void testParseString() {
    String json = "{a:10,b:'c'}";;
    JsonElement e = JsonParser.parseString(json);
    assertTrue(e.isJsonObject());
    assertEquals(10, e.getAsJsonObject().get("a").getAsInt());
    assertEquals("c", e.getAsJsonObject().get("b").getAsString());
  }

  public void testParseEmptyString() {
    JsonElement e = JsonParser.parseString("\"   \"");
    assertTrue(e.isJsonPrimitive());
    assertEquals("   ", e.getAsString());
  }

  public void testParseEmptyWhitespaceInput() {
    JsonElement e = JsonParser.parseString("     ");
    assertTrue(e.isJsonNull());
  }

  public void testParseUnquotedSingleWordStringFails() {
    assertEquals("Test", JsonParser.parseString("Test").getAsString());
  }

  public void testParseUnquotedMultiWordStringFails() {
    String unquotedSentence = "Test is a test..blah blah";
    try {
      JsonParser.parseString(unquotedSentence);
      fail();
    } catch (JsonSyntaxException expected) { }
  }

  public void testParseMixedArray() {
    String json = "[{},13,\"stringValue\"]";
    JsonElement e = JsonParser.parseString(json);
    assertTrue(e.isJsonArray());

    JsonArray  array = e.getAsJsonArray();
    assertEquals("{}", array.get(0).toString());
    assertEquals(13, array.get(1).getAsInt());
    assertEquals("stringValue", array.get(2).getAsString());
  }

  public void testParseReader() {
    StringReader reader = new StringReader("{a:10,b:'c'}");
    JsonElement e = JsonParser.parseReader(reader);
    assertTrue(e.isJsonObject());
    assertEquals(10, e.getAsJsonObject().get("a").getAsInt());
    assertEquals("c", e.getAsJsonObject().get("b").getAsString());
  }

  public void testReadWriteTwoObjects() throws Exception {
    Gson gson = new Gson();
    CharArrayWriter writer = new CharArrayWriter();
    BagOfPrimitives expectedOne = new BagOfPrimitives(1, 1, true, "one");
    writer.write(gson.toJson(expectedOne).toCharArray());
    BagOfPrimitives expectedTwo = new BagOfPrimitives(2, 2, false, "two");
    writer.write(gson.toJson(expectedTwo).toCharArray());
    CharArrayReader reader = new CharArrayReader(writer.toCharArray());

    JsonReader parser = new JsonReader(reader);
    parser.setLenient(true);
    JsonElement element1 = Streams.parse(parser);
    JsonElement element2 = Streams.parse(parser);
    BagOfPrimitives actualOne = gson.fromJson(element1, BagOfPrimitives.class);
    assertEquals("one", actualOne.stringValue);
    BagOfPrimitives actualTwo = gson.fromJson(element2, BagOfPrimitives.class);
    assertEquals("two", actualTwo.stringValue);
  }


  public void SAMPLEDELETEAFTER() {
    String json = "f";
    JsonElement e = JsonParser.parseString(json);
    assertTrue(e.isJsonObject());
    assertEquals(10, e.getAsJsonObject().get("a").getAsInt());
    assertEquals("c", e.getAsJsonObject().get("b").getAsString());
  }

  /**
   * Test to increase branch coverage for the method peekNumber(),
   * Since the method is private, the coverage test have to utilize explicit methods in order to reach them.
   * found at com/google/gson/stream/JsonReader.java.
   * The branch is located on line 679.
   * Requirements that are needed is that the current position in the array buffer is "-",
   * second condition is that the previous index of the buffer is not 0,
   * third condition is that is not NUMBER_CHAR_EXP_E,.
   * If all these 3 conditions holds true, the test will fall to the return statement of PEEKED_NONE.
   * When PEEKED_NONE is returned, the method will look at the next index of the stack.
   * The JsonElement.getString() should return correctly parsed string.
   * Expected, JsonElement == JsonPrimitive and getAsString() = String json.
   */
  public void testBranchCoverageLine679() {
    String json = "1-3";
    JsonElement e = JsonParser.parseString(json);
    assertTrue(e.isJsonPrimitive());
    assertEquals(json,e.getAsString());;
  }


  /**
   * Test to increase branch coverage for the method peekNumber(),
   * Since the method is private, the coverage test have to utilize explicit methods in order to reach them.
   * found at com/google/gson/stream/JsonReader.java.
   * The branch is located on line 686.
   * Requirements that are needed is that the current position in the array buffer is "+",
   * second condition is that the previous index of the buffer is not NUMBER_CHAR_EXP_E,
   * If all these 2 conditions holds true, the test will fall to the return statement of PEEKED_NONE.
   * When PEEKED_NONE is returned, the method will look at the next index of the stack.
   * The JsonElement.getString() should return correctly parsed string.
   * Expected, JsonElement == JsonPrimitive and getAsString() = String json.
   */
  public void testBranchCoverageLine686() {
      String json = "1+3";
      JsonElement e = JsonParser.parseString(json);
      assertTrue(e.isJsonPrimitive());
      assertEquals(json,e.getAsString());;
  }

  /**
   * Test to increase branch coverage for the method peekNumber(),
   * Since the method is private, the coverage test have to utilize explicit methods in order to reach them.
   * found at com/google/gson/stream/JsonReader.java.
   * The branch is located on line 715.
   * There are alot of requirements for this value since it is the default value in the end -
   * of a function with Cyclomatic Complexity of 38.
   * First, it cannot contain any special characters in the previous switch-case list.
   * Second, it can't be (c < '0' || c > '9') , and it can't be a literal.
   * Third, previous sign can't be 1 and previous number can't be None.
   * Fourth, if previous number is the digit of it and the calculated value == 0, then we return PEEKED_NONE.
   * When PEEKED_NONE is returned, the method will look at the next index of the stack.
   * Since the String has a leading 0, it will continue parsing since it could be a octal sequence.
   * The parse omits the leading 0 when converting to int
   * Expected, JsonElement == JsonPrimitive and getAsInt() = 23
   */
  public void testBranchCoverageLine715() {
    String json = "023";
    JsonElement e = JsonParser.parseString(json);
    assertTrue(e.isJsonPrimitive());
    assertEquals(23,e.getAsInt());;
  }

  /**
   * Test to increase branch coverage for the method peekKeyword(),
   * Since the method is private, the coverage test have to utilize explicit methods in order to reach them.
   * found at com/google/gson/stream/JsonReader.java.
   * The branch is located on line 622.
   * First step is that peekKeyword() checks if the letter can be converted to a keyword.
   * Second step is that it checks if the length of the char matches the keyword.
   * If the length of the char is less then allowed buffered depending on the keyword,
   * and if the pos + i:th positoin of the keyword index is less then the limit for all i:s.
   * The function will then return PEEKED_NONE, since the keyword does not correspond to the letter.
   * The sequence then continues until the end of the string. Since it is not a keyword, it will return the string "f".
   * Expected, JsonElement == JsonPrimitive and String json == e.getAsString()).
   */
  public void testBranchCoverageLine622() {
    String json = "f";
    JsonElement e = JsonParser.parseString(json);
    assertTrue(e.isJsonPrimitive());
    assertEquals(json,e.getAsString());;
  }
}
