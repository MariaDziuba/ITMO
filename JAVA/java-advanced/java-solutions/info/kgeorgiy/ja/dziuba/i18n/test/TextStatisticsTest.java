package info.kgeorgiy.ja.dziuba.i18n.test;

import info.kgeorgiy.ja.dziuba.i18n.*;
import org.junit.Assert;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class TextStatisticsTest extends Assert {
    private final Locale locale;

    @Parameterized.Parameters(name = "Locale: {0}")
    public static Collection<?> languages() {
        return Arrays.asList(new Object[][]{
                {"ru-RU"},
                {"en-US"}
        });
    }

    public TextStatisticsTest(final String languageTag) {
        this.locale = new Locale.Builder().setLanguageTag(languageTag).build();
    }

    private void validateStatistics(
            final Map<StatisticsType, StatisticsData<?>> statistics) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        ResourceBundle bundle = ResourceBundle
                .getBundle("info.kgeorgiy.ja.dziuba.i18n.test.resources." + "test", locale);

        final Class<?> clazz = StatisticsData.class;
        final List<String> fieldNames = Arrays.stream(clazz.getDeclaredFields()).map(Field::getName)
                .collect(Collectors.toList());
        for (StatisticsType type : StatisticsType.values()) {
            final StatisticsData<?> data = statistics.get(type);
            for (String fieldName : fieldNames) {
                try {
                    Method fieldGetter = clazz.getMethod("get" + Character.toUpperCase(fieldName.charAt(0))
                            + fieldName.substring(1));
                    final Object gotObject = fieldGetter.invoke(data);
                    final String gotString = (gotObject == null) ? "null" : gotObject.toString();

                    try {
                        final String expectedString = bundle.getString(type.toString().toLowerCase() + "_" +
                                fieldName);
                        assertEquals(expectedString, gotString);
                    } catch (final MissingResourceException e) {
                        // Ignored.
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw e;
                }
            }
        }
    }

    private String readResourceFile(final String resourceName) throws IOException {
        final URL url = TextStatisticsTest.class.getResource("resources/" + resourceName);
        if (url == null) {
            throw new FileNotFoundException("Resource file not found");
        }
        return Files.readString(Path.of(url.getPath()));
    }

    @Test
    public void test() throws IOException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        final String text = readResourceFile("test" + "_" +
                locale.getLanguage() + "_" + locale.getCountry() + ".txt");
        final Map<StatisticsType, StatisticsData<?>> statistics
                = new StatisticsManager().getStatistics(text, locale);
        validateStatistics(statistics);
    }
}

