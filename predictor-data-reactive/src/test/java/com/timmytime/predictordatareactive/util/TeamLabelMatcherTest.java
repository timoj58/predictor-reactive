package com.timmytime.predictordatareactive.util;

import com.timmytime.predictordatareactive.model.Team;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class TeamLabelMatcherTest {

    @Test
    void matchTest(){
        var label = "Almería";

        var match = TeamLabelMatcher.match(label, Arrays.asList(
                Team.builder().label("Almeria").build(),
                Team.builder().label("Albacet").build(),
                Team.builder().label("Andover").build(),
                Team.builder().label("Arseenal").build()
        ));

        assertThat(match.isPresent()).isTrue();
        assertThat(match.get().getLabel()).isEqualTo("Almeria");
    }

}