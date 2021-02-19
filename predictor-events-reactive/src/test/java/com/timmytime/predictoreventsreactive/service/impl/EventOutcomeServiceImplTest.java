package com.timmytime.predictoreventsreactive.service.impl;

import com.timmytime.predictoreventsreactive.model.EventOutcome;
import com.timmytime.predictoreventsreactive.repo.EventOutcomeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


class EventOutcomeServiceImplTest {

    @Mock
    private EventOutcomeRepo eventOutcomeRepo;

    @InjectMocks
    private EventOutcomeServiceImpl eventOutcomeService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);

        List<EventOutcome> eventOutcomeList
                = Arrays.asList(
                EventOutcome.builder().date(LocalDateTime.now().minusDays(1)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(200)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(300)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(400)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(5)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(6)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(7)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(8)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(9)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(10)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(11)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(12)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(13)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(14)).build()
        );

        List<EventOutcome> eventOutcomeList2
                = Arrays.asList(
                EventOutcome.builder().date(LocalDateTime.now().minusDays(1)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(2)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(30)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(4)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(5)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(60)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(7)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(80)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(9)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(100)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(11)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(12)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(130)).build(),
                EventOutcome.builder().date(LocalDateTime.now().minusDays(14)).build()
        );

        when(eventOutcomeRepo.findByAwayAndSuccessNotNullOrderByDateDesc(any(UUID.class))).thenReturn(Flux.fromStream(eventOutcomeList.stream()));
        when(eventOutcomeRepo.findByHomeAndSuccessNotNullOrderByDateDesc(any(UUID.class))).thenReturn(Flux.fromStream(eventOutcomeList2.stream()));

    }

    @Test
    public void limitTest() {

        eventOutcomeService.previousEventsByTeam(UUID.randomUUID()).subscribe(
                s -> System.out.println(s.getDate())
        );
    }

}