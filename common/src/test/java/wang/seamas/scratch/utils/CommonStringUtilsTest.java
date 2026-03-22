package wang.seamas.scratch.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class CommonStringUtilsTest {

    @Test
    void toCamelCase_shouldReturnNullWhenInputIsNull() {
        assertNull(CommonStringUtils.toCamelCase(null));
    }

    @Test
    void toCamelCase_shouldReturnEmptyStringWhenInputIsEmpty() {
        assertEquals("", CommonStringUtils.toCamelCase(""));
    }

    @ParameterizedTest
    @CsvSource({
            "hello_world, helloWorld",
            "hello-world, helloWorld",
            "hello world, helloWorld",
            "helloWorld, helloWorld",
            "user_name, userName",
            "first_name_last, firstNameLast"
    })
    void toCamelCase_shouldConvertToCamelCase(String input, String expected) {
        assertEquals(expected, CommonStringUtils.toCamelCase(input));
    }

    @Test
    void toPascalCase_shouldReturnNullWhenInputIsNull() {
        assertNull(CommonStringUtils.toPascalCase(null));
    }

    @Test
    void toPascalCase_shouldReturnEmptyStringWhenInputIsEmpty() {
        assertEquals("", CommonStringUtils.toPascalCase(""));
    }

    @ParameterizedTest
    @CsvSource({
            "hello_world, HelloWorld",
            "hello-world, HelloWorld",
            "hello world, HelloWorld",
            "helloWorld, HelloWorld",
            "HelloWorld, HelloWorld",
            "user_name, UserName",
            "first_name_last, FirstNameLast"
    })
    void toPascalCase_shouldConvertToPascalCase(String input, String expected) {
        assertEquals(expected, CommonStringUtils.toPascalCase(input));
    }

    @Test
    void toSnakeCase_shouldReturnNullWhenInputIsNull() {
        assertNull(CommonStringUtils.toSnakeCase(null));
    }

    @Test
    void toSnakeCase_shouldReturnEmptyStringWhenInputIsEmpty() {
        assertEquals("", CommonStringUtils.toSnakeCase(""));
    }

    @ParameterizedTest
    @CsvSource({
            "helloWorld, hello_world",
            "HelloWorld, hello_world",
            "hello_world, hello_world",
            "hello-world, hello_world",
            "hello world, hello_world",
            "userName, user_name",
            "FirstNameLast, first_name_last"
    })
    void toSnakeCase_shouldConvertToSnakeCase(String input, String expected) {
        assertEquals(expected, CommonStringUtils.toSnakeCase(input));
    }

    @Test
    void isCamelCase_shouldReturnFalseForNull() {
        assertFalse(CommonStringUtils.isCamelCase(null));
    }

    @Test
    void isCamelCase_shouldReturnFalseForEmptyString() {
        assertFalse(CommonStringUtils.isCamelCase(""));
    }

    @ParameterizedTest
    @CsvSource({
            "helloWorld, true",
            "userName, true",
            "firstNameLast, true",
            "a, true",
            "hello, true",
            "HelloWorld, false",
            "hello_world, false",
            "hello-world, false",
            "hello world, false",
            "HELLO_WORLD, false",
            "_hello, false",
            "hello_, false"
    })
    void isCamelCase_shouldValidateCorrectly(String input, boolean expected) {
        assertEquals(expected, CommonStringUtils.isCamelCase(input));
    }

    @Test
    void isPascalCase_shouldReturnFalseForNull() {
        assertFalse(CommonStringUtils.isPascalCase(null));
    }

    @Test
    void isPascalCase_shouldReturnFalseForEmptyString() {
        assertFalse(CommonStringUtils.isPascalCase(""));
    }

    @ParameterizedTest
    @CsvSource({
            "HelloWorld, true",
            "UserName, true",
            "FirstNameLast, true",
            "A, true",
            "Hello, true",
            "helloWorld, false",
            "hello_world, false",
            "hello-world, false",
            "hello world, false",
            "HELLO_WORLD, false",
            "_Hello, false",
            "Hello_, false"
    })
    void isPascalCase_shouldValidateCorrectly(String input, boolean expected) {
        assertEquals(expected, CommonStringUtils.isPascalCase(input));
    }

    @Test
    void isSnakeCase_shouldReturnFalseForNull() {
        assertFalse(CommonStringUtils.isSnakeCase(null));
    }

    @Test
    void isSnakeCase_shouldReturnFalseForEmptyString() {
        assertFalse(CommonStringUtils.isSnakeCase(""));
    }

    @ParameterizedTest
    @CsvSource({
            "hello_world, true",
            "user_name, true",
            "first_name_last, true",
            "a, true",
            "hello, true",
            "helloWorld, false",
            "HelloWorld, false",
            "hello-world, false",
            "hello world, false",
            "HELLO_WORLD, false",
            "_hello, false",
            "hello_, false",
            "__hello, false",
            "hello__world, false"
    })
    void isSnakeCase_shouldValidateCorrectly(String input, boolean expected) {
        assertEquals(expected, CommonStringUtils.isSnakeCase(input));
    }
}