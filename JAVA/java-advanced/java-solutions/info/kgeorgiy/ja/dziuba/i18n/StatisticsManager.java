package info.kgeorgiy.ja.dziuba.i18n;

import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class StatisticsManager {


    private Number parseMoney(final String text, final ParsePosition position, final Locale locale) {
        final NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        return format.parse(text, position);
    }

    private Date parseDate(final String text, final ParsePosition position, final Locale locale) {
        final DateFormat formatFull = DateFormat.getDateInstance(DateFormat.FULL, locale);
        final DateFormat formatLong = DateFormat.getDateInstance(DateFormat.LONG, locale);
        final DateFormat formatMedium = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        final DateFormat formatShort = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        Date date;
        if ((date = formatFull.parse(text, position)) == null) {
            if ((date = formatLong.parse(text, position)) == null) {
                if ((date = formatMedium.parse(text, position)) == null) {
                    date = formatShort.parse(text, position);
                }
            }
        }
        return date;
    }

    private StatisticsData<String> getStringStatistics(final StatisticsType type,
                                                       final BreakIterator breakIterator, final String text,
                                                       final Locale locale, final Predicate<String> filter) {
        breakIterator.setText(text);
        final List<String> samples = new ArrayList<>();
        for (int begin = breakIterator.first(), end = breakIterator.next(); end != BreakIterator.DONE;
             begin = end, end = breakIterator.next()) {
            final String sample = text.substring(begin, end).trim();
            if (filter.test(sample)) {
                if (!sample.isEmpty()) {
                    samples.add(type == StatisticsType.WORD ? sample.toLowerCase() : sample);
                }
            }
        }
        return StatisticsData.calculateStringStatistics(type, samples, locale);
    }

    private StatisticsData<Number> getNumberStatistics(final String text, final Locale locale) {
        final BreakIterator breakIterator = BreakIterator.getWordInstance(locale);
        breakIterator.setText(text);
        final List<Number> samples = new ArrayList<>();
        final NumberFormat format = NumberFormat.getNumberInstance(locale);

        for (int begin = breakIterator.first(), end = breakIterator.next(), ignoreBorder = 0;
             end != BreakIterator.DONE;
             begin = end, end = breakIterator.next()) {
            if (begin < ignoreBorder) {
                continue;
            }
            final ParsePosition position = new ParsePosition(begin);
            if (parseDate(text, position, locale) != null || parseMoney(text, position, locale) != null) {
                ignoreBorder = position.getIndex();
                continue;
            }
            final Number sample = format.parse(text, new ParsePosition(begin));
            if (sample != null) {
                samples.add(sample);
            }
        }

        return StatisticsData.calculateNumberStatistics(StatisticsType.NUMBER, samples);
    }

    private <T> List<T> getParsableSamples(final String text, final Locale locale,
                                                  final BiFunction<String, ParsePosition, T> parser) {
        final BreakIterator breakIterator = BreakIterator.getWordInstance(locale);
        breakIterator.setText(text);
        final List<T> samples = new ArrayList<>();

        for (int begin = breakIterator.first(), end = breakIterator.next(), ignoreBorder = 0;
             end != BreakIterator.DONE;
             begin = end, end = breakIterator.next()) {
            if (begin < ignoreBorder) {
                continue;
            }
            final ParsePosition position = new ParsePosition(begin);
            final T sample = parser.apply(text, position);
            if (sample != null) {
                samples.add(sample);
                ignoreBorder = position.getIndex();
            }
        }

        return samples;
    }

    private StatisticsData<Date> getDateStatistics(final String text, final Locale locale) {
        List<Date> samples = getParsableSamples(text, locale, (t, p) -> parseDate(t, p, locale));
        return StatisticsData.calculateDateStatistics(samples);
    }

    private StatisticsData<Number> getMoneyStatistics(final String text, final Locale locale) {
        List<Number> samples = getParsableSamples(text, locale, (t, p) -> parseMoney(t, p, locale));
        return StatisticsData.calculateNumberStatistics(StatisticsType.MONEY, samples);
    }

    private void putStringStatisticsInMap(final Map<StatisticsType, StatisticsData<?>> map, final StatisticsType type,
                                          final Function<Locale, BreakIterator> breakIteratorFactory,
                                          final String text, final Locale inputLocale,
                                          final Predicate<String> predicate) {
        map.put(type, getStringStatistics(type, breakIteratorFactory.apply(inputLocale), text, inputLocale, predicate));
    }

    public Map<StatisticsType, StatisticsData<?>> getStatistics(final String text, final Locale inputLocale) {
        final Map<StatisticsType, StatisticsData<?>> map = new HashMap<>();

        putStringStatisticsInMap(map, StatisticsType.SENTENCE, BreakIterator::getSentenceInstance, text, inputLocale,
                s -> true);
        putStringStatisticsInMap(map, StatisticsType.LINE, BreakIterator::getLineInstance, text, inputLocale, s -> true);
        putStringStatisticsInMap(map, StatisticsType.WORD, BreakIterator::getWordInstance, text, inputLocale,
                s -> !s.isEmpty() && Character.isLetter(s.charAt(0)));

        map.put(StatisticsType.NUMBER, getNumberStatistics(text, inputLocale));
        map.put(StatisticsType.MONEY, getMoneyStatistics(text, inputLocale));
        map.put(StatisticsType.DATE, getDateStatistics(text, inputLocale));

        return map;
    }
}
