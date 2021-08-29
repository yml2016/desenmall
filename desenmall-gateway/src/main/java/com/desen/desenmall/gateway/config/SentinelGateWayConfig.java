package com.desen.desenmall.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import com.desen.common.exception.BizCode;
import com.desen.common.utils.R;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Configuration
public class SentinelGateWayConfig {

	public SentinelGateWayConfig(){

		//Mono 响应式编程相关
		GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
			@Override
			public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
				// 网关限流了请求 就会回调这个方法
				R error = R.error(BizCode.TO_MANY_REQUEST);
				String errJson = JSON.toJSONString(error);
				Mono<ServerResponse> responseMono = ServerResponse.ok().body(Mono.just(errJson), String.class);
				return responseMono;
			}
		});
	}
}
