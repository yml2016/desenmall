package com.desen.desenmall.seckill.config;


import org.springframework.context.annotation.Configuration;




@Configuration
public class SecKillSentinelConfig {
    /*//老版本
	public SecKillSentinelConfig(){

		   WebCallbackManager.setUrlBlockHandler((request, response, exception) -> {
			R error = R.error(BizCode.TO_MANY_REQUEST);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			response.getWriter().write(JSON.toJSONString(error));
		});
	}*/
}
