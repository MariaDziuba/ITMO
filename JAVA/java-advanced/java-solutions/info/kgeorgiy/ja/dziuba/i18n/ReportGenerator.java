package info.kgeorgiy.ja.dziuba.i18n;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static info.kgeorgiy.ja.dziuba.i18n.StatisticsType.*;

public class ReportGenerator {
    // :NOTE: Глобальные переменные
    private static final String NL = System.lineSeparator();
    private static ResourceBundle resourceBundle;
    private static Locale inputLocale;
    private static Locale outputLocale;

    static final String HEAD = "<head>" + NL +
            "<meta charset=" + "\"UTF-8\">" + NL +
            "<!--Text statistics-->" + NL +
            "<title>" + NL +
            "%s" + NL +
            "</title>" + NL +
            "</head>";

    static final String TITLE = "<h3>" + NL +
            "<!--Analyzed file: filename-->" + NL +
            "%s" + NL +
            "</h3>";

    static final String SUMMARY = "<h4>" + NL +
            "<!--Summary statistics-->" + NL +
            "%s" + NL +
            "</h4>" + NL +
            "<!--sentences-->" + NL +
            "<p>%s</p>" + NL +
            "<!--lines-->" + NL +
            "<p>%s</p>" + NL +
            "<!--words-->" + NL +
            "<p>%s</p>" + NL +
            "<!--numbers-->" + NL +
            "<p>%s</p>" + NL +
            "<!--money-->" + NL +
            "<p>%s</p>" + NL +
            "<!--dates-->" + NL +
            "<p>%s</p>";
    static final String SECTION = "<h4>" + NL +
            "<!--Sentence statistics-->" + NL +
            "%s" + NL +
            "</h4>" + NL +
            "<!--count (unique)-->" + NL +
            "<p>%s</p>" + NL +
            "<!--min-->" + NL +
            "<p>%s</p>" + NL +
            "<!--max-->" + NL +
            "<p>%s</p>" + NL +
            "<!--minLen-->" + NL +
            "<p>%s</p>" + NL +
            "<!--maxLen-->" + NL +
            "<p>%s</p>" + NL +
            "<!--mean-->" + NL +
            "<p>%s</p>";
    static final String REPORT = "<!DOCTYPE html>" + NL +
            "<html>" + NL +
            "<!--head-->" + NL +
            "%s" + NL +
            "<body>" + NL +
            "<!--title-->" + NL +
            "%s" + NL +

            "<!--summary-->" + NL +
            "%s" + NL +

            "<!--sentences statistics-->" + NL +
            "%s" + NL +

            "<!--lines statistics-->" + NL +
            "%s" + NL +

            "<!--words statistics-->" + NL +
            "%s" + NL +

            "<!--numbers statistics-->" + NL +
            "%s" + NL +

            "<!--money statistics-->" + NL +
            "%s" + NL +

            "<!--dates statistics-->" + NL +
            "%s" + NL +

            "</body>" + NL +
            "</html>";

    static String generateSection(final StatisticsType type, final Map<StatisticsType, StatisticsData<?>> data,
                                  final MessageFormat numberFormat, final String statFormat,
                                  Function<StatisticsType, String> sectionNameFunction) {
        final BiFunction<Object, MessageFormat, Object> defaultFilter =
                (o, f) -> o == null ? resourceBundle.getString("notAvailable") : f.format(new Object[]{o});
        final StatisticsData<?> stats = data.get(type);
        final String statFormatUnique = resourceBundle.getString("fmtStatUnique");
        final String statFormatExample = resourceBundle.getString("fmtStatEx");
        final MessageFormat dataFormat;
        final String mean;
        final String meanKey;
        final String sectionName = sectionNameFunction.apply(type);
        switch (type) {
            case SENTENCE:
            case LINE:
            case WORD: {
                dataFormat = new MessageFormat(resourceBundle.getString("fmtString"), inputLocale);
                mean = numberFormat.format(new Object[]{stats.getMeanLength()});
                meanKey = "meanLen" + sectionName;
                break;
            }
            default:
                dataFormat = new MessageFormat(resourceBundle.getString("fmt" + sectionName),
                        type == StatisticsType.MONEY ? inputLocale : outputLocale);
                mean = defaultFilter.apply(stats.getMeanValue(), dataFormat).toString();
                meanKey = "mean" + sectionName;
        }


        return String.format(SECTION,
                resourceBundle.getString("stats" + sectionName),
                MessageFormat.format(statFormatUnique,
                        resourceBundle.getString("sum" + sectionName),
                        stats.getCountTotal(),
                        stats.getCountUnique(),
                        resourceBundle.getString("unique")),
                MessageFormat.format(statFormat,
                        resourceBundle.getString("min" + sectionName),
                        defaultFilter.apply(stats.getMinValue(), dataFormat)),
                MessageFormat.format(statFormat,
                        resourceBundle.getString("max" + sectionName),
                        defaultFilter.apply(stats.getMaxValue(), dataFormat)),
                MessageFormat.format(statFormatExample,
                        resourceBundle.getString("minLen" + sectionName),
                        numberFormat.format(new Object[]{stats.getMinLength()}),
                        defaultFilter.apply(stats.getMinLengthValue(), dataFormat)),
                MessageFormat.format(statFormatExample,
                        resourceBundle.getString("maxLen" + sectionName),
                        numberFormat.format(new Object[]{stats.getMaxLength()}),
                        defaultFilter.apply(stats.getMaxLengthValue(), dataFormat)),
                MessageFormat.format(statFormat,
                        resourceBundle.getString(meanKey),
                        mean));
    }


    static String generateReport(final Locale inputLocale_, final Locale outputLocale_,
                                 final Map<StatisticsType, StatisticsData<?>> data,
                                 final String fileName)
            throws IOException {

        inputLocale = inputLocale_;
        outputLocale = outputLocale_;
        resourceBundle = ResourceBundle
                .getBundle("info.kgeorgiy.ja.dziuba.i18n.resources.Bundle", outputLocale);

        final String generatedHead = String.format(HEAD,
                resourceBundle.getString("title"));

        final String generatedTitle = String.format(TITLE,
                MessageFormat.format(resourceBundle.getString("analyzedFile"), fileName));


        final MessageFormat numberFormat = new MessageFormat(resourceBundle.getString("fmtNumber"), outputLocale);
        final Function<StatisticsType, String> sectionNameFunction =
                type1 -> type1.toString().charAt(0) + type1.toString().substring(1).toLowerCase();

        final List<String> summaryArgs = new ArrayList<>();
        summaryArgs.add(resourceBundle.getString("summaryStats"));
        final String statFormat = resourceBundle.getString("fmtStat");
        Arrays.stream(StatisticsType.values())
                .map(t -> MessageFormat.format(statFormat, resourceBundle.getString("sum" + sectionNameFunction.apply(t)),
                        numberFormat.format(new Object[]{data.get(t).getCountTotal()})))
                .forEach(summaryArgs::add);
        final String generatedSummary = String.format(SUMMARY, summaryArgs.toArray(new Object[0]));

        return String.format(REPORT,
                generatedHead,
                generatedTitle,
                generatedSummary,
                generateSection(SENTENCE, data, numberFormat, statFormat, sectionNameFunction),
                generateSection(LINE, data, numberFormat, statFormat, sectionNameFunction),
                generateSection(WORD, data, numberFormat, statFormat, sectionNameFunction),
                generateSection(NUMBER, data, numberFormat, statFormat, sectionNameFunction),
                generateSection(MONEY, data, numberFormat, statFormat, sectionNameFunction),
                generateSection(DATE, data, numberFormat, statFormat, sectionNameFunction));
    }
}
