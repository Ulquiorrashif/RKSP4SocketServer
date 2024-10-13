package com.example.rksp4socketserver;

import com.example.rksp4socketserver.entity.MatchStats;
import com.example.rksp4socketserver.repository.StatRepository;
import io.rsocket.frame.decoder.PayloadDecoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class Rksp4SocketServerApplicationTests {

	@Autowired
	private StatRepository statRepository;
	private RSocketRequester requester;
	@BeforeEach
	public void setup() {
		requester = RSocketRequester.builder()
				.rsocketStrategies(builder -> builder.decoder(new
						Jackson2JsonDecoder()))
				.rsocketStrategies(builder -> builder.encoder(new
						Jackson2JsonEncoder()))
				.rsocketConnector(connector -> connector
						.payloadDecoder(PayloadDecoder.ZERO_COPY)
						.reconnect(Retry.fixedDelay(2, Duration.ofSeconds(2))))
				.dataMimeType(MimeTypeUtils.APPLICATION_JSON)
				.tcp("localhost", 5200);
	}
	@AfterEach
	public void cleanup() {
		requester.dispose();
	}
	@Test
	public void testGetCat() {
		MatchStats stat = new MatchStats();
		stat.setId(999L);
		stat.setBestADR(1000);
		stat.setCountTeam1(12);
		stat.setCountTeam2(12);
		stat.setBestKD(2);

		MatchStats saveStat = statRepository.save(stat);
		Mono<MatchStats> result = requester.route("getStat")
				.data(saveStat.getId())
				.retrieveMono(MatchStats.class);
		assertNotNull(result.block());
	}
	@Test
	public void testAddCat() {
		MatchStats stat = new MatchStats();
		stat.setId(998L);
		stat.setBestADR(1100);
		stat.setCountTeam1(13);
		stat.setCountTeam2(13);
		stat.setBestKD(3);
		Mono<MatchStats> result = requester.route("addStat")
				.data(stat)
				.retrieveMono(MatchStats.class);
		MatchStats savedCat = result.block();
		assertNotNull(savedCat);
		assertNotNull(savedCat.getId());
		assertTrue(savedCat.getId() > 0);
	}
	@Test
	public void testGetCats() {
		Flux<MatchStats> result = requester.route("getStats")
				.retrieveFlux(MatchStats.class);
		assertNotNull(result.blockFirst());
	}
	@Test
	public void testDeleteCat() {
		MatchStats stat = new MatchStats();
		stat.setId(996L);
		stat.setBestADR(1600);
		stat.setCountTeam1(6);
		stat.setCountTeam2(13);
		stat.setBestKD(2);
		MatchStats savedCat = statRepository.save(stat);
		Mono<Void> result = requester.route("deleteStat")
				.data(savedCat.getId())
				.send();
		result.block();
		MatchStats deletedCat = statRepository.findStatById(savedCat.getId());
		assertNotSame(deletedCat, savedCat);
	}


}
