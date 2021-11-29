package com.timmytime.predictormessagereactive.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timmytime.predictormessagereactive.configuration.HostsConfiguration;
import com.timmytime.predictormessagereactive.facade.WebClientFacade;
import com.timmytime.predictormessagereactive.model.FileRequest;
import com.timmytime.predictormessagereactive.repo.FileRequestRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestApiService {

    private final WebClientFacade webClientFacade;
    private final HostsConfiguration hostsConfiguration;
    private final FileRequestRepo fileRequestRepo;

    public Mono<Void> trainTeams(
            @PathVariable UUID receipt,
            @PathVariable String to,
            @PathVariable String from,
            @PathVariable String country) {
        log.info("received training teams: {}", receipt);
        webClientFacade.receipt(
                hostsConfiguration.getTeams() + "/training?id=" + receipt.toString()
        );
        return Mono.empty();
    }

    public Mono<Void> predictTeamResult(
            @PathVariable UUID receipt,
            @PathVariable String country,
            Mono<JsonNode> body) {
        log.info("received predict team result: {}", receipt);
        JSONObject prediction = new JSONObject()
                .put("0", new JSONObject()
                        .put("label", "homeWin")
                        .put("score", "10"))
                .put("1", new JSONObject()
                        .put("label", "awayWin")
                        .put("score", "10"))
                .put("2", new JSONObject()
                        .put("label", "draw")
                        .put("score", "10"));
        JsonNode response = null;

        try {
            response = new ObjectMapper().readTree(prediction.toString());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        webClientFacade.receipt(
                hostsConfiguration.getTeamEvents() + "/prediction?id=" + receipt.toString(), response);
        return Mono.empty();
    }

    public Mono<Void> predictTeamGoals(
            @PathVariable UUID receipt,
            @PathVariable String country,
            Mono<JsonNode> body) {
        log.info("received predict team goals: {}", receipt);
        JSONObject prediction = new JSONObject()
                .put("0", new JSONObject()
                        .put("label", "0")
                        .put("score", "10"))
                .put("1", new JSONObject()
                        .put("label", "1")
                        .put("score", "10"))
                .put("2", new JSONObject()
                        .put("label", "2")
                        .put("score", "10"));
        JsonNode response = null;

        try {
            response = new ObjectMapper().readTree(prediction.toString());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        webClientFacade.receipt(
                hostsConfiguration.getTeamEvents() + "/prediction?id=" + receipt.toString(), response);
        return Mono.empty();
    }

    public Mono<Void> trainPlayers(
            @PathVariable UUID receipt,
            @PathVariable String to,
            @PathVariable String from) {
        log.info("received training players: {}", receipt);
        webClientFacade.receipt(
                hostsConfiguration.getPlayers() + "/training?id=" + receipt.toString()
        );
        return Mono.empty();
    }

    public Mono<Void> playerConfig(
            @PathVariable String type
    ) {
        log.info("config {}", type);
        return Mono.empty();
    }

    public Mono<Void> predictPlayer(
            @PathVariable UUID receipt,
            @PathVariable Boolean init,
            Mono<JsonNode> body) {
        log.info("received predict player: {}", receipt);
        JSONObject prediction = new JSONObject()
                .put("0", new JSONObject()
                        .put("label", "0")
                        .put("score", "10"))
                .put("1", new JSONObject()
                        .put("label", "1")
                        .put("score", "10"))
                .put("2", new JSONObject()
                        .put("label", "2")
                        .put("score", "10"));
        JsonNode response = null;

        try {
            response = new ObjectMapper().readTree(prediction.toString());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        webClientFacade.receipt(
                hostsConfiguration.getPlayerEvents() + "/prediction?id=" + receipt.toString(), response);
        return Mono.empty();
    }

    public Mono<Void> uploadFile(
            Mono<FileRequest> fileRequest
    ) {
        fileRequest.doOnNext(file -> fileRequestRepo.save(file).subscribe());
        return Mono.empty();
    }

}
