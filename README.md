[PLAN - A]单机集中式流控
----------------------------------------------
CentralizedRateLimiter: 流控处理器<br />
* 在服务的filter/distributer处使用，调用CentralizedRateLimiter的enter方法检测是否予以放行
* 优点：实现简单，精度高，易控制
* 缺点：影响filter/distributer的性能，在高吞吐量的服务中容易造成性能瓶颈


[PLAN - B]分布式集中服务流控
----------------------------------------------
* CentralizedRateLimiter: 流控处理器<br />
    1. 流控作为独立服务，采用注册订阅的方式向其它组件提供服务;
    2. 流控基于guava的SmoothRateLimiter实现，它采用令牌桶算法且允许突增流量；
    3. 采用akka-actor通信: client和rateLimiter server之间是异步的，通过actor消息传递，订阅服务的消息使用fire-and-forget模式，流控服务请求消息使用ask模式（类似RPC）
* RateLimitAgentActor: 分布式部署的流控客户端代理，每个实例映射一个RateLimitAgentActor
* RateLimitServiceActor: 流控服务的监听控制器，接收并响应client的各类请求
* ApiSimulator: API流控使用示例，工程入口

[TODO]分布式集中服务流控优化
----------------------------------------
代理RateLimitAgentActor的优化实现<br />
1. 在本地维护一份enable流控的API列表（记为apiRateLimitMap），只有注册使能了流控服务的API才需要向CentralizedRateLimiter发送流控请求；
2. 维护各限流API的最近一次限流状态，用于timeout发生时限流结果参考；
3. 代理各API与CentralizedRateLimiter的注册/取消限流服务请求；
    + 3-1 消息发送采用fire-and-forget模式，消息的暂存采用guava的CacheBuilder构建超时缓存容器；
    + 3-2 agent向CentralizedRateLimiter发起注册/取消服务请求，并暂存此消息到Cache，主键为API对应的唯一标识，记为resource；发起请求之前先检测Cache，若已包含此API，则拒绝再次发起请求；
    + 3-3 若在超时时间内收到了CentralizedRateLimiter的响应，则在apiRateLimitMap中添加/删除此API；否则丢弃响应的消息；
    + 3-4 若发生了超时，则按失败处理，并从Cache中移除响应的请求消息。
4. 代理各API与CentralizedRateLimiter的canAccess服务请求。
    + 4-1 若client在超时前获取到了rateLimiter的响应，则按响应的结果执行是否放行；
    + 4-2 若client发生了超时，在特定时间内未获取到rateLimiter的响应，则根据上次的rateLimiter结果(记为lastResult)执行是否放行，并置lastResult为false，[记录上一次rateLimiter的结果，是因为本次超时的结果有更大的概率与上一次的rateLimiter结果相同，如果一味拒绝更容易引起误差（使得限流阀值小于设定值）]

