package com.desen.desenmall.order.listener;


import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.desen.desenmall.order.config.AlipayTemplate;
import com.desen.desenmall.order.service.OrderService;
import com.desen.desenmall.order.vo.PayAsyncVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RestController
public class OrderPayedListener {

	@Autowired
	private OrderService orderService;

	@Autowired
	private AlipayTemplate alipayTemplate;

	@PostMapping("/payed/notify")
	public String handleAliPayed(PayAsyncVo vo, HttpServletRequest request) throws UnsupportedEncodingException, AlipayApiException {
		log.info("收到支付宝最后的通知数据：" + vo);
//		Map<String, String[]> result = request.getParameterMap();
//		String map = "";
//		for (String key : result.keySet()) {
//			map += key + "-->" + request.getParameter(key) + "\n";
//		}
//		System.out.println(map);

		// 验签
		Map<String,String> params = new HashMap<>();
		Map<String,String[]> requestParams = request.getParameterMap();
		Iterator<String> iter = requestParams.keySet().iterator();
		for (;iter.hasNext();) {
			String name = iter.next();
			String[] values = requestParams.get(name);
			String valueStr = Arrays.stream(values).collect(Collectors.joining(","));
			//乱码解决，这段代码在出现乱码时使用
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		// 只要我们收到了支付宝给我们的异步通知 验签成功
		// 我们就要给支付宝返回success(没有success，支付宝会尽最大努力不停的通知你)
		if(AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(), alipayTemplate.getCharset(), alipayTemplate.getSign_type())){
			log.info("AlipaySignature->签名验证攻击...");
			return orderService.handlePayResult(vo);
		}
		log.warn("受到恶意验签攻击");
		return "fail";
	}
}
