package com.example.rksp4socketserver.controllers;


import com.example.rksp4socketserver.entity.MatchStats;
import com.example.rksp4socketserver.repository.StatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class MainSocketController {
    private final StatRepository statRepository;
    @Autowired
    public MainSocketController(StatRepository statRepository) {
        this.statRepository = statRepository;
    }
    @MessageMapping("getStat")
    public Mono<MatchStats> getStat(Long id) {
        return Mono.justOrEmpty(statRepository.findStatById(id));
    }
    @MessageMapping("addStat")
    public Mono<MatchStats> addStat(MatchStats stat) {
        return Mono.justOrEmpty(statRepository.save(stat));
    }
    @MessageMapping("getStats")
    public Flux<MatchStats> getStats() {
        return Flux.fromIterable(statRepository.findAll());
    }
    @MessageMapping("deleteStat")
    public Mono<Void> deleteStat(Long id){
        MatchStats cat = statRepository.findStatById(id);
        statRepository.delete(cat);
        return Mono.empty();
    }
    @MessageMapping("statsChannel")
    public Flux<MatchStats> catChannel(Flux<MatchStats> cats){
        return cats.flatMap(cat -> Mono.fromCallable(() ->
                        statRepository.save(cat)))
                .collectList()
                .flatMapMany(savedCats -> Flux.fromIterable(savedCats));
    }

}
