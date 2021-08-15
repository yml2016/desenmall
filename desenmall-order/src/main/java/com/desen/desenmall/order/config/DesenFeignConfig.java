package com.desen.desenmall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class DesenFeignConfig {

	@Bean("requestInterceptor")
	public RequestInterceptor requestInterceptor(){

		return new RequestInterceptor() {
			// Feign在远程调用之前都会先经过这个方法
			@Override
			public void apply(RequestTemplate template) {
				// RequestContextHolder请求上下文的持有者，本质也是ThreadLocal，同一个请求是相同一个线程，可以拿到刚进来这个请求的属性
				ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
				if(attributes != null){
					HttpServletRequest request = attributes.getRequest();
					if(request != null){
						// 同步请求头数据
						String cookie = request.getHeader("Cookie");
						// 给新请求同步Cookie
						template.header("Cookie", cookie);
					}
				}
			}
		};
	}
}
