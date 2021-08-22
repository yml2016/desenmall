/*
package com.desen.desenmall.seckill.config;


import com.alibaba.fastjson.JSON;
import com.desen.common.exception.BizCode;
import com.desen.common.utils.R;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SecKillSentinelConfig {

	public SecKillSentinelConfig(){
		    WebCallbackManager.setUrlBlockHandler((request, response, exception) -> {
			R error = R.error(BizCode.TO_MANY_REQUEST);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			response.getWriter().write(JSON.toJSONString(error));
		});
	}
}
*/
