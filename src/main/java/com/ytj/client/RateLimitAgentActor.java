package com.ytj.client;

import java.util.UUID;

import com.ytj.ratelimiter.CentralizedRateLimiter;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import entity.RateLimitCancelMsg;
import entity.RateLimitRegisterMsg;
import entity.RateLimitRequestMsg;
import entity.RateLimitResponseMsg;
import entity.RateLimiterResponseMsg;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class RateLimitAgentActor extends AbstractActor {
	private static final int TMOUT = 3;
	private final ActorRef rateLimiterActorRef;
	
	public RateLimitAgentActor(ActorRef ref)
	{
		this.rateLimiterActorRef = ref;
	      getContext().watch(ref);

	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(RateLimitResponseMsg.class, m -> {
			System.out.println("API[" + m.getResource() + " ] " + m.getMsgType() + " service success");
		}).build();
	}

	public void registeRateLimitService(String resource, double qps) {
		RateLimitRegisterMsg msg = new RateLimitRegisterMsg(resource, qps);
		rateLimiterActorRef.tell(msg, self());
	}

	public void cancelRateLimitService(String resource) {
		RateLimitCancelMsg msg = new RateLimitCancelMsg(resource);
		rateLimiterActorRef.tell(msg, self());
	}

	public boolean canAccess(String resource) {
		UUID id = UUID.randomUUID();
		RateLimitRequestMsg msg = new RateLimitRequestMsg(id, resource);
		Timeout timeout = new Timeout(Duration.create(TMOUT, "seconds"));
		Future<Object> future = Patterns.ask(self(), msg, timeout);
		Object resp = null;
		try {
			resp = Await.result(future, timeout.duration());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != resp && resp instanceof RateLimiterResponseMsg) {
			return ((RateLimiterResponseMsg) resp).isCanAccess();
		}

		return false;
	}

}
