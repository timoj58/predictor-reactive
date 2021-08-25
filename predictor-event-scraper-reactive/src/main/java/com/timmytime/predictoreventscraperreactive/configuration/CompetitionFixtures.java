package com.timmytime.predictoreventscraperreactive.configuration;

import com.timmytime.predictoreventscraperreactive.enumerator.CompetitionFixtureCodes;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompetitionFixtures {
    private CompetitionFixtureCodes code;
    private String url;
}
