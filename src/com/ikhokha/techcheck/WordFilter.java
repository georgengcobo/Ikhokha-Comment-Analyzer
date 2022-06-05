package com.ikhokha.techcheck;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordFilter {

    private final List<BiConsumer<String, Map<String, Integer>>> appliedFilters;

    public WordFilter(List<BiConsumer<String, Map<String, Integer>>> filterMethods) {

        this.appliedFilters = filterMethods;
    }

    public Map<String, Integer> AggregateByFilter(String targetString, Map<String, Integer> resultsMap) {

        for (BiConsumer<String, Map<String, Integer>> filter : appliedFilters) {

            filter.accept(targetString, resultsMap);
        }

        return resultsMap;

    }

    public static class FilterBuilder {

        private List<BiConsumer<String, Map<String, Integer>>> applicableFilters;

        public FilterBuilder() {
            this.applicableFilters = new ArrayList<BiConsumer<String, Map<String, Integer>>>();
        }

        public FilterBuilder FilterByLessThan15() {

            BiConsumer<String, Map<String, Integer>> LessThanFilter = (String input,
                    Map<String, Integer> resultsMap) -> {

                if (input.length() < 15) {

                    incOccurrence(resultsMap, "SHORTER_THAN_15");
                }

            };

            applicableFilters.add(LessThanFilter);

            return this;
        }

        public FilterBuilder FilterByMoverMentions() {

            BiConsumer<String, Map<String, Integer>> MoverMentionsFilter = (String input,
                    Map<String, Integer> resultsMap) -> {

                if (input.contains("Mover")) {

                    incOccurrence(resultsMap, "MOVER_MENTIONS");
                }
            };
            applicableFilters.add(MoverMentionsFilter);
            return this;
        }

        public FilterBuilder FilterByShakerMentions() {

            BiConsumer<String, Map<String, Integer>> ShakerMentionsFilter = (String input,
                    Map<String, Integer> resultsMap) -> {
                if (input.contains("Shaker")) {

                    incOccurrence(resultsMap, "SHAKER_MENTIONS");

                }
            };

            applicableFilters.add(ShakerMentionsFilter);

            return this;
        }

        public FilterBuilder FilterByQuestions() {

            BiConsumer<String, Map<String, Integer>> QuestionsFilter = (String input,
                    Map<String, Integer> resultsMap) -> {
                if (input.contains("?")) {

                    incOccurrence(resultsMap, "QUESTIONS");

                }
            };

            applicableFilters.add(QuestionsFilter);

            return this;
        }

        public FilterBuilder FilterForSpam() {

            BiConsumer<String, Map<String, Integer>> SpamFilter = (String input,
                    Map<String, Integer> resultsMap) -> {

                final String URL_REGEX = "\\b((?:https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:, .;]*[-a-zA-Z0-9+&@#/%=~_|])";
                Pattern pattern = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(input);

                boolean hasSpam = false;

                while (matcher.find()) {

                    hasSpam = true;

                }

                if (hasSpam) {
                    incOccurrence(resultsMap, "SPAM");
                }

            };

            applicableFilters.add(SpamFilter);

            return this;
        }

        public WordFilter build() {

            WordFilter filter = new WordFilter(applicableFilters);

            return filter;

        }

        private void incOccurrence(Map<String, Integer> countMap, String key) {

            countMap.putIfAbsent(key, 0);
            countMap.put(key, countMap.get(key) + 1);
        }

    }

}
