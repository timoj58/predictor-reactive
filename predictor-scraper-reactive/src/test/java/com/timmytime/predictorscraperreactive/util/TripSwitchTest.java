package com.timmytime.predictorscraperreactive.util;

import com.timmytime.predictorscraperreactive.enumerator.CompetitionFixtureCodes;
import com.timmytime.predictorscraperreactive.enumerator.ScraperType;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class TripSwitchTest {

    private final TripSwitch tripSwitch = new TripSwitch();

    @Test
    void activateTest(){
        var list = new ArrayList<String>();
        list.add("");
        var matches = new ArrayDeque();
        var players = new ArrayDeque();
        var map = new HashMap<CompetitionFixtureCodes, Pair<AtomicInteger, List<String>>>();
        List.of(CompetitionFixtureCodes.values())
                .forEach(c -> {
                    matches.add(Triple.of(c, ScraperType.RESULTS, ""));
                    players.add(Triple.of(c, ScraperType.MATCH, ""));
                    map.put(c, Pair.of(new AtomicInteger(5), list));

                });


        tripSwitch.activate(matches, players, map);

        assertTrue(matches.size() == 0);
        assertTrue(players.size() == 0);

        List.of(CompetitionFixtureCodes.values())
                .forEach(c ->
                        assertTrue(map.get(c).getRight().isEmpty() &&
                                map.get(c).getLeft().get() == 0));
    }

}