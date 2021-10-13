package com.timmytime.predictorclientreactive.service.impl;

import com.timmytime.predictorclientreactive.facade.IS3Facade;
import com.timmytime.predictorclientreactive.service.ShutdownService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CompetitionServiceImplTest {

    private final ShutdownService shutdownService = mock(ShutdownService.class);
    private final TestFacade testFacade = new TestFacade();
    private String result;
    private CompetitionServiceImpl competitionService
            = new CompetitionServiceImpl(testFacade, shutdownService);

    @Test
    public void competitionTest() {

        competitionService.load();

        verify(shutdownService, atLeastOnce()).receive(anyString());

        assertEquals(result,
                "[{\"countryResponse\":{\"country\":\"england\"},\"competitionResponses\":[{\"competition\":\"england_1\",\"country\":\"ENGLAND\",\"label\":\"Premier League\",\"fantasyLeague\":true},{\"competition\":\"england_2\",\"country\":\"ENGLAND\",\"label\":\"Championship\",\"fantasyLeague\":true},{\"competition\":\"england_3\",\"country\":\"ENGLAND\",\"label\":\"League One\",\"fantasyLeague\":false},{\"competition\":\"england_4\",\"country\":\"ENGLAND\",\"label\":\"League Two\",\"fantasyLeague\":false}]},{\"countryResponse\":{\"country\":\"scotland\"},\"competitionResponses\":[{\"competition\":\"scotland_1\",\"country\":\"SCOTLAND\",\"label\":\"Premiership\",\"fantasyLeague\":false},{\"competition\":\"scotland_2\",\"country\":\"SCOTLAND\",\"label\":\"Championship\",\"fantasyLeague\":false},{\"competition\":\"scotland_3\",\"country\":\"SCOTLAND\",\"label\":\"League One\",\"fantasyLeague\":false},{\"competition\":\"scotland_4\",\"country\":\"SCOTLAND\",\"label\":\"League Two\",\"fantasyLeague\":false}]},{\"countryResponse\":{\"country\":\"italy\"},\"competitionResponses\":[{\"competition\":\"italy_1\",\"country\":\"ITALY\",\"label\":\"Serie A\",\"fantasyLeague\":true},{\"competition\":\"italy_2\",\"country\":\"ITALY\",\"label\":\"Serie B\",\"fantasyLeague\":false}]},{\"countryResponse\":{\"country\":\"france\"},\"competitionResponses\":[{\"competition\":\"france_1\",\"country\":\"FRANCE\",\"label\":\"Ligue 1\",\"fantasyLeague\":true},{\"competition\":\"france_2\",\"country\":\"FRANCE\",\"label\":\"Ligue 2\",\"fantasyLeague\":false}]},{\"countryResponse\":{\"country\":\"spain\"},\"competitionResponses\":[{\"competition\":\"spain_1\",\"country\":\"SPAIN\",\"label\":\"La Liga\",\"fantasyLeague\":true},{\"competition\":\"spain_2\",\"country\":\"SPAIN\",\"label\":\"La Liga 2\",\"fantasyLeague\":false}]},{\"countryResponse\":{\"country\":\"german\"},\"competitionResponses\":[{\"competition\":\"german_1\",\"country\":\"GERMAN\",\"label\":\"Bundesliga\",\"fantasyLeague\":true},{\"competition\":\"german_2\",\"country\":\"GERMAN\",\"label\":\"2. Bundesliga\",\"fantasyLeague\":false}]},{\"countryResponse\":{\"country\":\"holland\"},\"competitionResponses\":[{\"competition\":\"holland_1\",\"country\":\"HOLLAND\",\"label\":\"Eredivisie\",\"fantasyLeague\":false}]},{\"countryResponse\":{\"country\":\"belgium\"},\"competitionResponses\":[{\"competition\":\"belgium_1\",\"country\":\"BELGIUM\",\"label\":\"First Division A\",\"fantasyLeague\":false}]},{\"countryResponse\":{\"country\":\"portugal\"},\"competitionResponses\":[{\"competition\":\"portugal_1\",\"country\":\"PORTUGAL\",\"label\":\"Primeira Liga\",\"fantasyLeague\":true}]},{\"countryResponse\":{\"country\":\"greece\"},\"competitionResponses\":[{\"competition\":\"greece_1\",\"country\":\"GREECE\",\"label\":\"Superleague\",\"fantasyLeague\":false}]},{\"countryResponse\":{\"country\":\"russia\"},\"competitionResponses\":[{\"competition\":\"russia_1\",\"country\":\"RUSSIA\",\"label\":\"Чемпионат России по футболу\",\"fantasyLeague\":false}]},{\"countryResponse\":{\"country\":\"turkey\"},\"competitionResponses\":[{\"competition\":\"turkey_1\",\"country\":\"TURKEY\",\"label\":\"Süper Lig\",\"fantasyLeague\":false}]},{\"countryResponse\":{\"country\":\"norway\"},\"competitionResponses\":[{\"competition\":\"norway_1\",\"country\":\"NORWAY\",\"label\":\"Eliteserien\",\"fantasyLeague\":false}]},{\"countryResponse\":{\"country\":\"sweden\"},\"competitionResponses\":[{\"competition\":\"sweden_1\",\"country\":\"SWEDEN\",\"label\":\"Allsvenskan\",\"fantasyLeague\":false}]},{\"countryResponse\":{\"country\":\"denmark\"},\"competitionResponses\":[{\"competition\":\"denmark_1\",\"country\":\"DENMARK\",\"label\":\"1st Division\",\"fantasyLeague\":false}]}]");

    }

    private class TestFacade implements IS3Facade {

        @Override
        public void put(String key, String json) {
            result = json;

        }

        @Override
        public void put(String bucket, String key, String csv) {

        }

        @Override
        public void delete(String folder) {

        }

        @Override
        public void archive(String directory) {

        }
    }

}