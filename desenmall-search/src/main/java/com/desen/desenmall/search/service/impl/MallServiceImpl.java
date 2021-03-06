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
import com.desen.desenmall.search.vo.BrandVo;
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
        // ????????????????????????????????????
        SearchRequest request = bulidSearchRequest(searchParam);
        try {
            SearchResponse searchResponse = esClient.search(request, ESConfig.COMMON_OPTIONS);
            // ???es???????????????????????????
            searchResult = bulidSearchResult(searchParam, searchResponse);
        } catch (IOException e) {
            log.error("ES?????????????????????", e);
        }
        return searchResult;

    }


    private SearchRequest bulidSearchRequest(SearchParam searchParam) {

        //????????????DSL
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //1.query bool ??????????????????????????????
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //1.1 bool must ?????????????????????
        if (!StringUtils.isEmpty(searchParam.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", searchParam.getKeyword()));
        }
        //1.2 bool filter ??????
        //1.2.1 term ??????????????????ID????????????
        if (searchParam.getCatalog3Id() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", searchParam.getCatalog3Id()));
        }
        //1.2.2 terms ????????????ID????????????
        if (!CollectionUtils.isEmpty(searchParam.getBrandId())) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", searchParam.getBrandId()));
        }
        //1.2.3 term ????????????????????????
        if (searchParam.getHasStock() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", searchParam.getHasStock() == 1));
        }
        //1.2.4 term ??????????????????????????????
        if (!StringUtils.isEmpty(searchParam.getSkuPrice())) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            String[] prices = searchParam.getSkuPrice().split("_");
            if (prices.length == 2) {
                // ???????????? ???????????? _500???????????????????????????2??????
                if (StringUtils.isEmpty(prices[0])) {
                    prices[0] = "0";
                }
                rangeQueryBuilder.gte(prices[0]).lte(prices[1]);
            } else if (prices.length == 1) {
                // ????????????
                if (searchParam.getSkuPrice().startsWith("_")) {
                    rangeQueryBuilder.lte(prices[0]);
                }
                if (searchParam.getSkuPrice().endsWith("_")) {
                    rangeQueryBuilder.gte(prices[0]);
                }
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        //1.2.5 nested ????????????
        if (!CollectionUtils.isEmpty(searchParam.getAttrs())) {
            for (String attrStr : searchParam.getAttrs()) {
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                String[] s = attrStr.split("_");
                // ?????????id  ?????????????????????
                String attrId = s[0];
                String[] attrValue = s[1].split(":");
                boolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                boolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValue));
                // ?????????????????????Query ???????????????????????????????????? nested ??????
                NestedQueryBuilder attrsQuery = QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None);
                boolQueryBuilder.filter(attrsQuery);
            }
        }
        // ??????????????????????????????????????????
        searchSourceBuilder.query(boolQueryBuilder);

        // 2.1 ??????
        if (!StringUtils.isEmpty(searchParam.getSort())) {
            String sort = searchParam.getSort();
            // sort=hotScore_asc/desc
            String[] s = sort.split("_");
            SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            searchSourceBuilder.sort(s[0], order);
        }
        // 2.2 ?????? pageSize ??? 5
        searchSourceBuilder.from((searchParam.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        // 2.3 ??????
        if (!StringUtils.isEmpty(searchParam.getKeyword())) {
            HighlightBuilder builder = new HighlightBuilder();
            builder.field("skuTitle");
            builder.preTags("<b style='color:red'>");
            builder.postTags("</b>");
            searchSourceBuilder.highlighter(builder);
        }
        // 3 ????????????
        // TODO 3.1.????????????
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg");
        brandAgg.field("brandId").size(50);
        // ????????????????????????
        brandAgg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        // ????????????????????? sourceBuilder
        searchSourceBuilder.aggregation(brandAgg);
        // TODO 3.2.????????????
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        // ????????????????????? sourceBuilder
        searchSourceBuilder.aggregation(catalogAgg);
        // TODO 3.3.???????????? attr_agg ?????????????????????
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attr_agg", "attrs");
        // 3.3.1 ????????????????????????attrId
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        // 3.3.1.1 ?????????????????????attrId?????????attrName
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        // 3.3.1.2 ?????????????????????attrId?????????????????????????????????attrValue	???????????????????????????????????? ?????????50
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        // 3.3.2 ???????????????????????????????????????
        attrAgg.subAggregation(attrIdAgg);
        searchSourceBuilder.aggregation(attrAgg);
        log.info("ES???????????????->{}", searchSourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);
        return searchRequest;
    }

    /**
     * ?????????????????? ??????catalogId ???brandId???attrs.attrId??????????????????????????????0-6000??????????????????????????????skuTitle???????????????
     */
    private SearchResult bulidSearchResult(SearchParam searchParam, SearchResponse response) {
        SearchResult result = new SearchResult();
        // 1.?????????????????????????????????
        SearchHits hits = response.getHits();

        List<SkuEsModel> esModels = new ArrayList<>();
        if (hits.getHits() != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                // ES????????????????????????
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if (!StringUtils.isEmpty(searchParam.getKeyword())) {
                    // 1.1 ???????????????????????????
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String highlightFields = skuTitle.getFragments()[0].string();
                    // 1.2 ??????????????????
                    esModel.setSkuTitle(highlightFields);
                }
                esModels.add(esModel);
            }
        }
        result.setProducts(esModels);

        // 2.????????????????????????????????????????????????
        ArrayList<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attrAgg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            // 2.1 ???????????????id
            attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
            // 2.2 ?????????????????????
            String attrName = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg"))
                    .getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);
            // 2.3 ????????????????????????
            List<String> attrValue = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg"))
                    .getBuckets().stream().map(item -> item.getKeyAsString()).collect(Collectors.toList());
            attrVo.setAttrValue(attrValue);
            attrVos.add(attrVo);
        }
        result.setAttrs(attrVos);

        // 3.????????????????????????????????????????????????
        ArrayList<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brandAgg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            // 3.1 ???????????????id
            long brnadId = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brnadId);
            // 3.2 ??????????????????
            String brandName = ((ParsedStringTerms) bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);
            // 3.3 ?????????????????????
            String brandImg = ((ParsedStringTerms) bucket.getAggregations().get("brand_img_agg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);
            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);

        // 4.??????????????????????????????????????????
        ParsedLongTerms catalogAgg = response.getAggregations().get("catalog_agg");
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
            // ????????????id
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            catalogVo.setCatalogId(Long.parseLong(bucket.getKeyAsString()));
            // ???????????????
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");
            String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalogName);
            catalogVos.add(catalogVo);
        }
        result.setCatalogs(catalogVos);
        // ================????????????????????????????????????
        // 5.????????????-??????
        result.setPageNum(searchParam.getPageNum());

        // ????????????
        long total = hits.getTotalHits().value;

        result.setTotal(total);

        // ????????????????????????
        int totalPages = (int) ((total - 1) / EsConstant.PRODUCT_PAGESIZE + 1);
        result.setTotalPages(totalPages);
        // ???????????????
        ArrayList<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);

        // 6.???????????????????????????
        if (searchParam.getAttrs() != null) {
            List<SearchResult.NavVo> navVos = searchParam.getAttrs().stream().map(attr -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.getAttrsInfo(Long.parseLong(s[0]));
                // ??????????????????????????????????????? ????????????????????????
                result.getAttrIds().add(Long.parseLong(s[0]));
                if (r.getCode() == 0) {
                    AttrResponseVo data = r.getData("attr", new TypeReference<AttrResponseVo>() {
                    });
                    navVo.setName(data.getAttrName());
                } else {
                    // ???????????????id????????????
                    navVo.setName(s[0]);
                }
                // ???????????????????????? ??????????????????
                String replace = replaceQueryString(searchParam, attr, "attrs");
                navVo.setLink("http://search.desenmall.com/list.html?" + replace);
                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(navVos);
        }

        // ???????????????
        if (!CollectionUtils.isEmpty(searchParam.getBrandId())) {
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setName("??????");
            // TODO ????????????????????????
            R r = productFeignService.brandInfo(searchParam.getBrandId());
            if (r.getCode() == 0) {
                List<BrandVo> brand = r.getData("brand", new TypeReference<List<BrandVo>>() {
                });
                StringBuffer buffer = new StringBuffer();
                // ??????????????????ID
                String replace = "";
                for (BrandVo brandVo : brand) {
                    buffer.append(brandVo.getName() + ";");
                    replace = replaceQueryString(searchParam, brandVo.getBrandId() + "", "brandId");
                }
                navVo.setNavValue(buffer.toString());
                navVo.setLink("http://search.desenmall.com/list.html?" + replace);
            }
            navs.add(navVo);
        }
        //todo ??????:??????????????????

        return result;
    }


    /**
     * ????????????
     * key ??????????????????key
     */
    private String replaceQueryString(SearchParam Param, String value, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            // ??????????????????????????????java????????????
            encode = encode.replace("+", "%20");
            encode = encode.replace("%28", "(").replace("%29", ")");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Param.get_queryString().replace("&" + key + "=" + encode, "");
    }

}
