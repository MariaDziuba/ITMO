package info.kgeorgiy.ja.dziuba.i18n;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.Collator;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class StatisticsData<T> {
    private StatisticsType type;
    private int countTotal;
    private int countUnique;
    private T minValue;
    private T maxValue;
    private T meanValue;
    private int minLength;
    private T minLengthValue;
    private int maxLength;
    private T maxLengthValue;
    private double meanLength;

    private StatisticsData() {}

    private static <T> StatisticsData<T> calculateStatistics(final StatisticsType type, final List<T> samples,
                                                             final Function<T, Integer> sampleLength,
                                                             final Comparator<T> valueComparator) {
        final StatisticsData<T> stats = new StatisticsData<>();
        stats.type = type;
        if (samples.isEmpty()) {
            return stats;
        }

        stats.countTotal = samples.size();

        samples.sort(Comparator.comparing(sampleLength));

        stats.minLength = sampleLength.apply(samples.get(0));
        stats.minLengthValue = samples.get(0);
        stats.maxLength = sampleLength.apply(samples.get(samples.size() - 1));
        stats.maxLengthValue = samples.get(samples.size() - 1);
        stats.meanLength = (double) samples.stream().map(sampleLength).reduce(0, Integer::sum) /
                samples.size();

        samples.sort(valueComparator);

        stats.minValue = samples.get(0);
        stats.maxValue = samples.get(samples.size() - 1);

        stats.countUnique = 1;
        for (int i = 0; i < samples.size() - 1; i++) {
            if (valueComparator.compare(samples.get(i), samples.get(i + 1)) != 0) {
                stats.countUnique++;
            }
        }

        return stats;
    }

    static StatisticsData<Date> calculateDateStatistics(final List<Date> samples) {
        StatisticsData<Date> stats = calculateStatistics(StatisticsType.DATE, samples,
                n -> n.toString().length(), Comparator.comparingLong(Date::getTime));

        stats.meanValue = samples.isEmpty() ? null : new Date(samples.stream().map(n -> BigInteger.valueOf(n.getTime()))
                .reduce(BigInteger.ZERO, BigInteger::add)
                .divide(BigInteger.valueOf(stats.countTotal)).longValue());
        return stats;
    }

    static StatisticsData<String> calculateStringStatistics(final StatisticsType type, final List<String> samples,
                                                                           final Locale locale) {
        return calculateStatistics(type, samples, String::length,
                (String s, String t) -> Collator.getInstance(locale).compare(s, t));
    }

    static StatisticsData<Number> calculateNumberStatistics(final StatisticsType type, final List<Number> samples) {
        StatisticsData<Number> stats = calculateStatistics(type, samples, n -> n.toString().length(),
                Comparator.comparingDouble(Number::doubleValue));

        stats.meanValue = samples.isEmpty() ? null : samples.stream().map(n -> BigDecimal.valueOf(n.doubleValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(stats.countTotal), RoundingMode.HALF_EVEN);
        return stats;
    }

    public StatisticsType getType() {
        return type;
    }

    public int getCountTotal() {
        return countTotal;
    }

    public int getCountUnique() {
        return countUnique;
    }

    public T getMinValue() {
        return minValue;
    }

    public T getMaxValue() {
        return maxValue;
    }

    public T getMeanValue() {
        return meanValue;
    }

    public int getMinLength() {
        return minLength;
    }

    public T getMinLengthValue() {
        return minLengthValue;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public T getMaxLengthValue() {
        return maxLengthValue;
    }

    public double getMeanLength() {
        return meanLength;
    }
}