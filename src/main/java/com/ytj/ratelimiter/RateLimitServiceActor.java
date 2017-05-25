package com.ytj.ratelimiter;

import akka.actor.AbstractActor;
import entity.Instruction;
import entity.RateLimitCancelMsg;
import entity.RateLimitRegisterMsg;
import entity.RateLimitRequestMsg;
import entity.RateLimitResponseMsg;
import entity.RateLimiterResponseMsg;

public class RateLimitServiceActor extends AbstractActor {

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(RateLimitRegisterMsg.class, m -> {
			System.out.println("Receive Registe Msg: " + m);

			CentralizedRateLimiter.addOrUpdateResourceQps(m.getResource(), m.getQps());

			RateLimitResponseMsg msg = new RateLimitResponseMsg(RateLimitResponseMsg.MsgType.REGISTER,
					m.getResource());
			sender().tell(msg, getSelf());
		}).match(RateLimitCancelMsg.class, m -> {
			System.out.println("Receive RateLimitCancel Msg: " + m);

			CentralizedRateLimiter.removeRateLimiter(m.getResource());

			RateLimitResponseMsg msg = new RateLimitResponseMsg(RateLimitResponseMsg.MsgType.CANCEL,
					m.getResource());
			sender().tell(msg, getSelf());
		}).match(RateLimitRequestMsg.class, m -> {
			System.out.println("Receive RateLimitRequest Msg: " + m);

			boolean canAccess = CentralizedRateLimiter.canAccess(m.getResource());

			RateLimiterResponseMsg resp = new RateLimiterResponseMsg(m.getId(), canAccess);
			sender().tell(resp, getSelf());
		}).matchEquals(Instruction.CLEAR_RATE_LIMIT_SERVICE, m -> {
			System.out.println("clear all rateLimite api...");

			CentralizedRateLimiter.clearRateLimiter();
		}).matchEquals(Instruction.FINISH, m -> {
			System.out.println("Service is done, then close...");

			CentralizedRateLimiter.clearRateLimiter();
			getContext().stop(getSelf());
		}).build();
	}
}
