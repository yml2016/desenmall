package com.desen.desenmall.search.controller;


import com.desen.desenmall.search.service.MallService;
import com.desen.desenmall.search.vo.SearchParam;
import com.desen.desenmall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 @Description
 @see SearchController
 @author yangminglin
 @date 2021/7/11
 @version V
**/
@Controller
public class SearchController {

    @Autowired
    private MallService masllService;

    @GetMapping({"/list.html","/"})
    public String listPage(SearchParam searchParam, Model model, HttpServletRequest request){

        // 获取路径原生的查询属性
        searchParam.set_queryString(request.getQueryString());
        // ES中检索到的结果 传递给页面
        SearchResult result = masllService.search(searchParam);
        model.addAttribute("result", result);
        return "list";
    }
}