package com.desen.desenmall.order.web;

import com.alipay.api.AlipayApiException;
import com.desen.desenmall.order.config.AlipayTemplate;
import com.desen.desenmall.order.service.OrderService;
import com.desen.desenmall.order.vo.PayVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 @Description 支付宝调用
 @see PayWebController
 @author yangminglin
 @date 2021/8/21
 @version V
**/
@Slf4j
@Controller
public class PayWebController {

	@Autowired
	private AlipayTemplate alipayTemplate;

	@Autowired
	private OrderService orderService;

	/**
	 * 告诉浏览器我们会返回一个html页面(produces = "text/html")
	 */
	@ResponseBody
	@GetMapping(value = "/payOrder", produces = "text/html")
	public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {

		PayVo payVo = orderService.getOrderPay(orderSn);
		String res = alipayTemplate.pay(payVo);
		log.debug("支付宝响应信息：{}",res);
		return res;
	}
}
