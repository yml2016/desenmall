package com.desen.common.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.alibaba.fastjson.JSON;
import com.desen.common.exception.BizCode;
import com.desen.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class DesenBlockExceptionHandler implements BlockExceptionHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {

        response.setStatus(429);

        /*StringBuffer url = request.getRequestURL();
        if ("GET".equals(request.getMethod()) && StringUtil.isNotBlank(request.getQueryString())) {
            url.append("?").append(request.getQueryString());
        }
        PrintWriter out = response.getWriter();
        out.print("我被阻塞了啊啊啊啊啊啊啊");
        out.flush();
        out.close();*/
        String msg = null;
        if (e instanceof FlowException) {
            msg = "您访问过快-限流中";
        } else if (e instanceof DegradeException) {
            msg = "您访问过快-降级中";
        } else if (e instanceof ParamFlowException) {
            msg = "热点参数限流";
        } else if (e instanceof SystemBlockException) {
            msg = "系统规则（负载/...不满足要求）";
        } else if (e instanceof AuthorityException) {
            msg = "授权规则不通过";
        }
        log.warn("DesenBlockExceptionHandler->{}", msg);
        R error = R.error(BizCode.TO_MANY_REQUEST);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(JSON.toJSONString(error));
    }
}
