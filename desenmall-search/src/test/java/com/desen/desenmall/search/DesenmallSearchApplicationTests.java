package com.desen.desenmall.search;

import com.alibaba.fastjson.JSON;
import com.desen.desenmall.search.config.ESConfig;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
class DesenmallSearchApplicationTests {

    @Autowired
    RestHighLevelClient esClient;
    @Test
    void indexData() throws IOException {
        System.out.println(esClient);
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("userName", "ymlin");
        jsonMap.put("age", 18);
        jsonMap.put("message", "hhhh哈哈哈");
        String jsonString = JSON.toJSONString(jsonMap);
        System.out.println(jsonString);
        IndexRequest indexRequest = new IndexRequest("users")
                .id("1").source(jsonMap);
        /*IndexRequest indexRequest = new IndexRequest("users")
                .id("1").source(jsonString, XContentType.JSON);*/
        IndexResponse indexResponse = esClient.index(indexRequest, ESConfig.COMMON_OPTIONS);
        System.out.println(indexResponse);
    }

    @Test
    void searchData() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchQuery("address","mill"));

        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg")
                .field("age").size(10);
        builder.aggregation(ageAgg);

        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        builder.aggregation(balanceAvg);

        System.out.println("builder = " + builder.toString());
        searchRequest.source(builder);
        SearchResponse response = esClient.search(searchRequest, ESConfig.COMMON_OPTIONS);
        System.out.println(response.toString());

        SearchHits searchHits = response.getHits();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String index = hit.getIndex();
            String id = hit.getId();
            String source = hit.getSourceAsString();
            System.out.println("source = " + source);
        }

        Aggregations aggregations = response.getAggregations();
        Terms ageAgg1 = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
            String key = bucket.getKeyAsString();
            long docCount = bucket.getDocCount();
            System.out.println("key="+key+",docCount="+docCount);
        }

        Avg balanceAvg1 = aggregations.get("balanceAvg");
        System.out.println("balanceAvg1.getValue() = " + balanceAvg1.getValue());
    }
}
