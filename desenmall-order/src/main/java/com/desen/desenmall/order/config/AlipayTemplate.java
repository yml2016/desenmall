package com.desen.desenmall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;

import com.desen.desenmall.order.vo.PayVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
@Slf4j
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000118606206";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC98S8ASQEIylaTk40Abch0dhIDtApyK/2Q4k23/LGuvuBQhNfllf+70XoS8OvVnE/pCRgqZ4bTPdCLrjpvEz3OG4oJ/hT4hqCMfoWBy7weY+5u9Hj3PyBc2uV5RCQDYChXr0sI+Ah9zsxLPT3GP5KCExtd9pwgIEnd5oddcmtupsVYDG4Xhp9XbNgJ+rD4IvN7HIf6nMlP/5OdfrZ1h7nYJ6A++Gr1cxUzXS5mLbTtioh7yOBlsl6kazTLkpKqO+OJn/ejXDibXewA8pDMU5+N7dZnc7YW5vukb3PioIpc24CqVD1KyjViJeZUI2TjBafgYDqtPyLhS7LTueDG6tWZAgMBAAECggEAFQnQ1exAJBQdUM1IEeMXD+dACY74lTrQJCscxqNDnwJfYCvGqhRaNhh2uABdbtSYeUxbk97RrTYskjwjFsh/0QN137N36SsoP0eITB8f4BHbiAimGWSxlfdc8XidsI4HCedNnVmzvxOXx0GqRPonpSauolK9akWJSP3Owof45FF9TYACYJoGOuLSufNb0GNitawlCpEUCrHYMXFoYZaz/jLSzp1WKhZ1vGK5AEIU0QrYmPIdtyIlYOyzNyrHdWnwLVPNkbFuerAWybL0bI8WOzLVs+mesMeIVy8plxuIIwhPTswCXKPuH2KDgKWr4lJkhoXehsLhNBOxsCkdwOAEgQKBgQD6b7bI0vyWU3imwPoi3JdixS3iHO/wdvzEE0tz+Yyid0eqe8qwJmwRzU/x5a4dQjWYRzKLw9jKBvBloy4XYifN9Gy6gZDMPtSu2i3QKz7JML3xN1Zy5Gmu1CV0STzEZZyc5ewCyP6p3V89qYg1ZrSD2wJxWPqZSn0d24fTy1J/KQKBgQDCKWz2CrkfPI4J+I5eye5p/GZjU/2D+BW3sIOIWPVJfuSmd70Gi9ubOgXaG+FHOMxB/vT2djHetS0Tpm92cXeoAA79CQCQXUq60E3USjOq8NiLIESrU0eMsP1oS8yKqa3Be/5jUqImgQzy96hSOTHmYMNsRhOEpz6q1wQ+Evwg8QKBgFUS1B3+hm2up2LGVXK4SvjzkLWqLNwcH5ugDla0ZFDggkjoyp7yPm4Bt/Gtm2s3DavQK9yMyD1BBDMSrtqkrm7pZHIrB3xKr8dCOKPpvOdtlmi16rkW3MvWJ28MXgc9mWk3NehwTQp5OBxjVfEGFbZhFMBagK7IPh6Bb+/Dtx+ZAoGBAKoSm00Rq4J/CnQVcq/49YuE+TK8WW6FmpBiX3TJZ2JPcn2WmM7pJx3bP1ED5tr6F4EfkPdqJNjNnoR931jCn2tbUcW3YvURounsmKABtyxe9282frALrLrCNdZrvukfJAI9WGEr+rj7hZo18QzE6L7Bt6xZ3bLuxGflG+Q7wPohAoGAI2Cz4wZ/vy9zvGCAc0b3jUuhgaP+pfBSzkVbvHX6Px8J8H8ZtlSizAU8zihpy+4OFEZnwGEXp7HDNcPGFjm5TB1l2+I9PIhipxTMZGb0QHqoKkpUpsAqY0tf9I07J9uHIXAX4KtMVdevfx00B8445OVXUZQNDYcGLTxsCCW3BwI=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlZVFeDZxGA7P3dv90lqxP37E7OgVEDBrQ1kC2+AkEVVQt+gCzXy9VEiAbbA6puV20h43IF66++zXbg5uLEQgfT42AFjgioorVl0CvuIt+3VU9YNFdSUdiPbUWvtuBzIfEuBlgxLlfeTLdi0+wr8zOxg6hllKiJVIbuTz5S6dG3BrFCThE03HF3LBeZO3PrAn/Ub6UPP8aw/Hm6po6k5FXIpb6SQJ/NhyUZ85UA+vzWTyjCtHKJfzdQm/eW67Vnx3kis2qugwSZQEI1eWbbO/6RvU4pqW7XKW22qP6RFLj8fD8HsrfxtYVSOcgzDel9weQXE+Xwu6U1eFtSrh1ymC6QIDAQAB";

	// 服务器【异步通知】页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们后台发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://member.desenmall.com/memberOrder.html";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url = "http://member.desenmall.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 自动关单时间（支付宝收单）
    private String timeout = "2m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";


    /**
     * 调用支付宝进行支付
     * @param vo
     * @return
     * @throws AlipayApiException
     */
    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        // 30分钟内不付款就会自动关单
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\"" + timeout + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        return result;
    }


    public String close(String out_trade_no, String trade_no) throws AlipayApiException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl, app_id, merchant_private_key, "json", charset, alipay_public_key, sign_type);

        //设置请求参数
        AlipayTradeCloseRequest alipayRequest = new AlipayTradeCloseRequest();
        //商户订单号，商户网站订单系统中唯一订单号
        //String out_trade_no = new String(request.getParameter("WIDTCout_trade_no").getBytes("ISO-8859-1"),"UTF-8");
        //支付宝交易号
        //String trade_no = new String(request.getParameter("WIDTCtrade_no").getBytes("ISO-8859-1"),"UTF-8");
        //请二选一设置

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\"," +"\"trade_no\":\""+ trade_no +"\"}");

        //请求
        String result = alipayClient.execute(alipayRequest).getBody();
        log.info(result);
        return result;
    }
}
