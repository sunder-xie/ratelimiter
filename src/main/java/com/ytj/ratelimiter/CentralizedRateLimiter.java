package com.ytj.ratelimiter;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;

public class CentralizedRateLimiter {

	public static class RateLimitException extends Exception {

		private static final long serialVersionUID = 3090218538190772610L;
		
		private final String resource;

		public String getResource() {
			return resource;
		}

		public RateLimitException(String resource) {
			super(resource + " should not be visited so frequently");
			this.resource = resource;
		}

		@Override
		public synchronized Throwable fillInStackTrace() {
			return this;
		}
	}

	private static final ConcurrentMap<String, RateLimiter> resourceLimiterMap = Maps.newConcurrentMap();

	/**
	 * 新增或者更新限流API
	 * @param resource API映射的key，标记一个限流资源
	 * @param qps
	 */
	public static void addOrUpdateResourceQps(String resource, double qps) {
		RateLimiter limiter = resourceLimiterMap.get(resource);
		if (limiter == null) {
			limiter = RateLimiter.create(qps);
			RateLimiter r = resourceLimiterMap.putIfAbsent(resource, limiter);
			if (r != null) {
				limiter = r;
			}
		}
		limiter.setRate(qps);
	}

	/**
	 * 移除指定的API限流
	 * @param resource
	 */
	public static void removeRateLimiter(String resource) {
		resourceLimiterMap.remove(resource);
	}
	
	public static void clearRateLimiter() {
		resourceLimiterMap.clear();
	}

	/**
	 * 限流检测：流量未超限则放行，否则抛出限流异常
	 * @param resource
	 * @throws RateLimitException
	 */
	public static void enter(String resource) throws RateLimitException {
		if (!canAccess(resource)) {
			throw new RateLimitException(resource);
		}
	}
	
	/**
	 * 判断访问请求是否予以放行
	 * @param resource
	 * @return
	 */
	public static boolean canAccess(String resource)
	{
		RateLimiter limiter = resourceLimiterMap.get(resource);
		
		return null==limiter || limiter.tryAcquire();
	}

}
