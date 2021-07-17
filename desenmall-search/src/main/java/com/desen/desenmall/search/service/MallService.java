package com.desen.desenmall.search.service;

import com.desen.desenmall.search.vo.SearchParam;
import com.desen.desenmall.search.vo.SearchResult;

public interface MallService {
    /**
     * 检索所有参数
     */
    SearchResult search(SearchParam Param);
}
