package com.atguigu.springcloud.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;

@Component
@Slf4j
public class MyLogGateWayFilter implements GlobalFilter, Ordered {
    /**
     *
     * @param exchange t表示一次 HTTP 请求-响应交换的上下文对象，包含了客户端请求和处理结果等多个方面的信息。
     *                 在 Spring Cloud Gateway 的过滤器中，可以通过 ServerWebExchange 对象
     *                 获取请求和响应相关的信息，以及对请求和响应进行修改和控制等操作。
     * @param chain 表示一个过滤器链，包含了多个过滤器组成的序列，
     *              并提供了执行过滤器链中下一个过滤器或者请求处理方法的方法。
     *              当一个请求经过 Spring Cloud Gateway 的过滤器链时，
     *              每个过滤器都会对请求进行某个特定的处理或拦截，然后将请求传递给下一个过滤器或者请求处理方法，
     *              最终得到响应结果并返回给客户端。
     *              在 Spring Cloud Gateway 的过滤器中，可以通过 GatewayFilterChain 对象调用 filter 方法，
     *              来执行下一个过滤器或请求处理方法。
     *              同时也可以通过 GatewayFilterChain 对象获取当前过滤器在过滤器链中的位置信息，
     *              以及根据需要向后续过滤器或请求处理方法传递数据等操作。
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("*******come in MyLogGateWayFilter: " + new Date());
        String uname = exchange.getRequest().getQueryParams().getFirst("uname");
        if(uname==null){
            log.info("*******用户名为null,非法用户，哭泣");
            exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
            return exchange.getResponse().setComplete();//setComplete() 方法用于结束请求响应并停止后续的过滤器链和请求处理流程。
        }
        return chain.filter(exchange);
    }
//代表加载过滤器顺序，数字越小优先级越高
    @Override
    public int getOrder() {
        return 0;
    }
}
