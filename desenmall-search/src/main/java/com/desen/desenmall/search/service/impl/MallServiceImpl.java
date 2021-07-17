package com.desen.desenmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.desen.common.to.es.SkuEsModel;
import com.desen.common.utils.R;
import com.desen.desenmall.search.config.ESConfig;
import com.desen.desenmall.search.constant.EsConstant;
import com.desen.desenmall.search.feign.ProductFeignService;
import com.desen.desenmall.search.service.MallService;
import com.desen.desenmall.search.vo.AttrResponseVo;
import com.desen.desenmall.search.vo.SearchParam;
import com.desen.desenmall.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MallServiceImpl implements MallService {

    @Autowired
    RestHighLevelClient esClient;

    @Autowired
    ProductFeignService productFeignService;

    @Override
    public SearchResult search(SearchParam searchParam) {
        SearchResult searchResult = null;
        // 通过请求参数构建查询请求
        SearchRequest request = bulidSearchRequest(searchParam);
        try {
            SearchResponse searchResponse = esClient.search(request, ESConfig.COMMON_OPTIONS);
            // 将es响应数据封装成结果
            searchResult = bulidSearchResult(searchParam, searchResponse);
        } catch (IOException e) {
            log.error("ES检索异常！！！", e);
        }
        return searchResult;

    }


    private SearchRequest bulidSearchRequest(SearchParam searchParam) {

        //用于构建DSL
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //1.query bool 用于组合多个查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //1.1 bool must 根据关键字检索
        if (!StringUtils.isEmpty(searchParam.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", searchParam.getKeyword()));
        }
        //1.2 bool filter 过滤
        //1.2.1 term 根据三级分类ID进行过滤
        if (searchParam.getCatalog3Id() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", searchParam.getCatalog3Id()));
        }
        //1.2.2 terms 根据品牌ID进行过滤
        if (!CollectionUtils.isEmpty(searchParam.getBrandId())) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", searchParam.getBrandId()));
        }
        //1.2.3 term 根据库存进行过滤
        if (searchParam.getHasStock() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", searchParam.getHasStock() == 1));
        }
        //1.2.4 term 根据价格区间进行过滤
        if (!StringUtils.isEmpty(searchParam.getSkuPrice())) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            String[] prices = searchParam.getSkuPrice().split("_");
            if (prices.length == 2) {
                // 有二个值 就是区间 _500这种情况切割出来是2个值
                if (StringUtils.isEmpty(prices[0])) {
                    prices[0] = "0";
                }
                rangeQueryBuilder.gte(prices[0]).lte(prices[1]);
            } else if (prices.length == 1) {
                // 单值情况
                if (searchParam.getSkuPrice().startsWith("_")) {
                    rangeQueryBuilder.lte(prices[0]);
                }
                if (searchParam.getSkuPrice().endsWith("_")) {
                    rangeQueryBuilder.gte(prices[0]);
                }
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        //1.2.5 nested 属性查询
        if (!CollectionUtils.isEmpty(searchParam.getAttrs())) {
            for (String attrStr : searchParam.getAttrs()) {
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                String[] s = attrStr.split("_");
                // 检索的id  属性检索用的值
                String attrId = s[0];
                String[] attrValue = s[1].split(":");
                boolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                boolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValue));
                // 构建一个嵌入式Query 每一个必须都得生成嵌入的 nested 查询
                NestedQueryBuilder attrsQuery = QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None);
                boolQueryBuilder.filter(attrsQuery);
            }
        }
        // 把以前所有条件都拿来进行封装
        searchSourceBuilder.query(boolQueryBuilder);

        // 2.1 排序
        if (!StringUtils.isEmpty(searchParam.getSort())) {
            String sort = searchParam.getSort();
            // sort=hotScore_asc/desc
            String[] s = sort.split("_");
            SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            searchSourceBuilder.sort(s[0], order);
        }
        // 2.2 分页 pageSize ： 5
        searchSourceBuilder.from((searchParam.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        // 2.3 高亮
        if (!StringUtils.isEmpty(searchParam.getKeyword())) {
            HighlightBuilder builder = new HighlightBuilder();
            builder.field("skuTitle");
            builder.preTags("<b style='color:red'>");
            builder.postTags("</b>");
            searchSourceBuilder.highlighter(builder);
        }
        // 3 聚合分析
        // TODO 3.1.品牌聚合
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg");
        brandAgg.field("brandId").size(50);
        // 品牌聚合的子聚合
        brandAgg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        // 将品牌聚合加入 sourceBuilder
        searchSourceBuilder.aggregation(brandAgg);
        // TODO 3.2.分类聚合
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        // 将分类聚合加入 sourceBuilder
        searchSourceBuilder.aggregation(catalogAgg);
        // TODO 3.3.属性聚合 attr_agg 构建嵌入式聚合
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attr_agg", "attrs");
        // 3.3.1 聚合出当前所有的attrId
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        // 3.3.1.1 聚合分析出当前attrId对应的attrName
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        // 3.3.1.2 聚合分析出当前attrId对应的所有可能的属性值attrValue	这里的属性值可能会有很多 所以写50
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        // 3.3.2 将这个子聚合加入嵌入式聚合
        attrAgg.subAggregation(attrIdAgg);
        searchSourceBuilder.aggregation(attrAgg);
        log.info("ES构建语句：->{}", searchSourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);
        return searchRequest;
    }

    /**
     * 构建结果数据 指定catalogId 、brandId、attrs.attrId、嵌入式查询、倒序、0-6000、每页显示两个、高亮skuTitle、聚合分析
     */
    private SearchResult bulidSearchResult(SearchParam searchParam, SearchResponse response) {
        SearchResult result = new SearchResult();
        // 1.返回的所有查询到的商品
        SearchHits hits = response.getHits();

        List<SkuEsModel> esModels = new ArrayList<>();
        if (hits.getHits() != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                // ES中检索得到的对象
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if (!StringUtils.isEmpty(searchParam.getKeyword())) {
                    // 1.1 获取标题的高亮属性
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String highlightFields = skuTitle.getFragments()[0].string();
                    // 1.2 设置文本高亮
                    esModel.setSkuTitle(highlightFields);
                }
                esModels.add(esModel);
            }
        }
        result.setProducts(esModels);

        // 2.当前所有商品涉及到的所有属性信息
        ArrayList<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attrAgg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            // 2.1 得到属性的id
            attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
            // 2.2 得到属性的名字
            String attrName = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg"))
                    .getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);
            // 2.3 得到属性的所有值
            List<String> attrValue = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg"))
                    .getBuckets().stream().map(item -> item.getKeyAsString()).collect(Collectors.toList());
            attrVo.setAttrValue(attrValue);
            attrVos.add(attrVo);
        }
        result.setAttrs(attrVos);

        // 3.当前所有商品涉及到的所有品牌信息
        ArrayList<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brandAgg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            // 3.1 得到品牌的id
            long brnadId = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brnadId);
            // 3.2 得到品牌的名
            String brandName = ((ParsedStringTerms) bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);
            // 3.3 得到品牌的图片
            String brandImg = ((ParsedStringTerms) bucket.getAggregations().get("brand_img_agg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);
            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);

        // 4.当前商品所有涉及到的分类信息
        ParsedLongTerms catalogAgg = response.getAggregations().get("catalog_agg");
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
            // 设置分类id
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            catalogVo.setCatalogId(Long.parseLong(bucket.getKeyAsString()));
            // 得到分类名
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");
            String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalogName);
            catalogVos.add(catalogVo);
        }
        result.setCatalogs(catalogVos);
        // ================以上信息从聚合信息中获取
        // 5.分页信息-页码
        result.setPageNum(searchParam.getPageNum());

        // 总记录数
        long total = hits.getTotalHits().value;

        result.setTotal(total);

        // 总页码：计算得到
        int totalPages = (int) ((total - 1) / EsConstant.PRODUCT_PAGESIZE + 1);
        result.setTotalPages(totalPages);
        // 设置导航页
        ArrayList<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);

        // 6.构建面包屑导航功能
        /*if (searchParam.getAttrs() != null) {
            List<SearchResult.NavVo> navVos = searchParam.getAttrs().stream().map(attr -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.getAttrsInfo(Long.parseLong(s[0]));
                // 将已选择的请求参数添加进去 前端页面进行排除
                result.getAttrIds().add(Long.parseLong(s[0]));
                if (r.getCode() == 0) {
                    AttrResponseVo data = r.getData(new TypeReference<AttrResponseVo>() {
                    });
                    navVo.setName(data.getAttrName());
                } else {
                    // 失败了就拿id作为名字
                    navVo.setName(s[0]);
                }
                // 拿到所有查询条件 替换查询条件
                String replace = replaceQueryString(searchParam, attr, "attrs");
                navVo.setLink("http://search.glmall.com/list.html?" + replace);
                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(navVos);
        }*/

        // 品牌、分类
        /*if (searchParam.getBrandId() != null && searchParam.getBrandId().size() > 0) {
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setName("品牌");
            // TODO 远程查询所有品牌
            R r = productFeignService.brandInfo(searchParam.getBrandId());
            if (r.getCode() == 0) {
                List<SearchResult.BrandVo> brand = r.getData("data", new TypeReference<List<SearchResult.BrandVo>>() {
                });
                StringBuffer buffer = new StringBuffer();
                // 替换所有品牌ID
                String replace = "";
                for (SearchResult.BrandVo brandVo : brand) {
                    buffer.append(brandVo.getBrandName() + ";");
                    replace = replaceQueryString(searchParam, brandVo.getBrandId() + "", "brandId");
                }
                navVo.setNavValue(buffer.toString());
                navVo.setLink("http://search.glmall.com/list.html?" + replace);
            }
            navs.add(navVo);
        }*/
        return result;
    }


    /**
     * 替换字符
     * key ：需要替换的key
     */
    private String replaceQueryString(SearchParam Param, String value, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            // 浏览器对空格的编码和java的不一样
            encode = encode.replace("+", "%20");
            encode = encode.replace("%28", "(").replace("%29", ")");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Param.get_queryString().replace("&" + key + "=" + encode, "");
    }

}
