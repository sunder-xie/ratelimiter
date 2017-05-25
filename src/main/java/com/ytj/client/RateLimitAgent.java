package com.ytj.client;

import java.util.UUID;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import entity.RateLimitCancelMsg;
import entity.RateLimitRegisterMsg;
import entity.RateLimitRequestMsg;
import entity.RateLimiterResponseMsg;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class RateLimitAgent {
	private static final int TMOUT = 3;
	private final ActorRef rateLimiterActorRef;
	private final ActorRef agenActorRef;
	
	public RateLimitAgent(ActorRef rateLimiterActorRef, ActorRef agenActorRef)
	{
		this.rateLimiterActorRef = rateLimiterActorRef;
		this.agenActorRef = agenActorRef;
	}

	public void registeRateLimitService(String resource, double qps) {
		RateLimitRegisterMsg msg = new RateLimitRegisterMsg(resource, qps);
		rateLimiterActorRef.tell(msg, agenActorRef);
	}

	public void cancelRateLimitService(String resource) {
		RateLimitCancelMsg msg = new RateLimitCancelMsg(resource);
		rateLimiterActorRef.tell(msg, agenActorRef);
	}

	public boolean canAccess(String resource) {
		UUID id = UUID.randomUUID();
		RateLimitRequestMsg msg = new RateLimitRequestMsg(id, resource);
		Timeout timeout = new Timeout(Duration.create(TMOUT, "seconds"));
		Future<Object> future = Patterns.ask(rateLimiterActorRef, msg, timeout);
		Object resp = null;
		try {
			resp = Await.result(future, timeout.duration());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != resp && resp instanceof RateLimiterResponseMsg) {
			boolean res = ((RateLimiterResponseMsg) resp).isCanAccess();
			System.out.println("Access: "+resource+" "+res);
			return res;
		}

		System.out.println("Access error: "+resource+" false");
		return false;
	}

}
