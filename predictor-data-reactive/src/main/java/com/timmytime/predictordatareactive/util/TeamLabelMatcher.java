package com.timmytime.predictordatareactive.util;

import com.timmytime.predictordatareactive.model.Team;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@UtilityClass
public class TeamLabelMatcher {

    public Optional<Team> match(String label, List<Team> teams) {
        //grab items of same length.
        var possibleMatches = teams.stream().filter(f -> f.getLabel().length() == label.length()).collect(Collectors.toList());
        //next check how many of the characters match.  allow 75% match as ok...
        var threshold = Math.floor(label.length() / 100.0 * 75.0);

        var meetsThreshold = possibleMatches.stream().filter(f -> checkThreshold((int) threshold, f.getLabel(), label)).collect(Collectors.toList());

        log.info("we have {} matches for {}", meetsThreshold.size(), label);

        return meetsThreshold.size() > 1 ? Optional.empty() : meetsThreshold.stream().findFirst();
    }

    private Boolean checkThreshold(Integer threshold, String label, String labelToMatch) {
        AtomicInteger match = new AtomicInteger(0);

        IntStream.range(0, label.length()).forEach(i -> {
            if (label.charAt(i) == labelToMatch.charAt(i)) {
                match.incrementAndGet();
            }
        });

        return match.get() >= threshold;
    }

}
