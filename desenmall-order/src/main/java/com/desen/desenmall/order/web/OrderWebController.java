package com.desen.desenmall.order.web;

import com.desen.common.exception.NotStockException;
import com.desen.desenmall.order.service.OrderService;
import com.desen.desenmall.order.vo.OrderConfirmVo;
import com.desen.desenmall.order.vo.OrderSubmitVo;
import com.desen.desenmall.order.vo.SubmitOrderResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

@Slf4j
@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    /**
     * 用于测试各个页面是否能正常访问
     * http://order.desenmall.com/confirm.html
     * http://order.desenmall.com/detai.html
     * http://order.desenmall.com/list.html
     * http://order.desenmall.com/pay.html
     */
    @GetMapping("/{page}.html")
    public String listPage(@PathVariable("page") String page){
        return page;
    }


    /**
     * 去结算确认页
     * @param model
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {

        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", confirmVo);
        return "confirm";
    }

    /**
     * 下单功能
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo submitVo, Model model, RedirectAttributes redirectAttributes){

        try {
            SubmitOrderResponseVo responseVo = orderService.submitOrder(submitVo);
            // 下单失败回到订单重新确认订单信息
            if(responseVo.getCode() == 0){
                // 下单成功取支付选项
                model.addAttribute("submitOrderResp", responseVo);
                return "pay";
            }else{
                String msg = "下单失败";
                switch (responseVo.getCode()){
                    case 1: msg += "订单信息过期,请刷新在提交";break;
                    case 2: msg += "订单商品价格发送变化,请确认后再次提交";break;
                    case 3: msg += "商品库存不足";break;
                }
                log.debug(msg);
                redirectAttributes.addFlashAttribute("msg", msg);
                return "redirect:http://order.desenmall.com/toTrade";
            }
        } catch (Exception e) {
            if (e instanceof NotStockException){
                String message = e.getMessage();
                redirectAttributes.addFlashAttribute("msg", message);
                log.debug(message);
            }
            log.error("订单提交异常：",e);
            return "redirect:http://order.desenmall.com/toTrade";
        }
    }
}
