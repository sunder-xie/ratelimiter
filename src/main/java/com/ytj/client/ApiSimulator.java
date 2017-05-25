package com.ytj.client;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ytj.ratelimiter.RateLimitServiceActor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class ApiSimulator {

	private Set<String> rateLimitApis = new HashSet<>();
	private Set<String> commonApis = new HashSet<>();
	private Set<String> apis = new HashSet<>();
	private RateLimitAgent agent;

	private final ExecutorService executorService = Executors.newFixedThreadPool(3);

	public ApiSimulator() {
		String prefix = "api_";
		for (int i = 0; i < 10; i++) {
			rateLimitApis.add(prefix + i);
		}

		commonApis.add("free_api_0");
		commonApis.add("free_api_1");

		apis.addAll(rateLimitApis);
		apis.addAll(commonApis);

		init();
	}

	private void init() {
		ActorSystem system = ActorSystem.create("rateLimiter");
		ActorRef rateLimitActorRef = system.actorOf(Props.create(RateLimitServiceActor.class), "rateLimitServices");
		ActorRef agentActorRef = system.actorOf(Props.create(RateLimitAgentActor.class, rateLimitActorRef),
				"rateLimitAgent");

		agent = new RateLimitAgent(rateLimitActorRef, agentActorRef);
	}

	public void registeRateLimitService() {
		for (String api : rateLimitApis) {
			double qps = Math.random() * 10;
			agent.registeRateLimitService(api, qps);
		}
	}

	public void accessService() {
		Random r = new Random();
		for (String api : apis) {
			int j = r.nextInt(20);
			for (int i = 0; i < j; j++)
				executorService.submit(() -> {
					agent.canAccess(api);
				});
		}
	}

	public void accessService(String resource, int num) {
		for (int i = 0; i < num; i++)
			executorService.submit(() -> {
				agent.canAccess(resource);
			});
	}

	public static void main(String[] args) {
		ApiSimulator apiSimulator = new ApiSimulator();
		apiSimulator.registeRateLimitService();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		apiSimulator.accessService("api_0", 20);
	}

}
