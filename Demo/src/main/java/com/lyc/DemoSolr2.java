package com.lyc;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;
import java.util.List;

public class DemoSolr2 {
    public static void main(String[] args) throws IOException, SolrServerException {
        //创建solr的http客户端请求
        HttpSolrClient client = new HttpSolrClient.Builder("http://localhost:8080/solr").build();
        //创建查询参数
        SolrQuery query = new SolrQuery("*:*");
        //设置查询条件
        query.addFilterQuery("hotelName:北京");

        query.setRows(5);//显示查询行数
        query.setStart(2);//显示查询位置
        //根据某个字段排序，desc是倒叙
        query.setSort("id", SolrQuery.ORDER.desc);
        //执行查询获得像一个结果
        QueryResponse response = client.query("core1", query, SolrRequest.METHOD.GET);
        //从相应中获取数据
        List<Hotel> beans = response.getBeans(Hotel.class);
        for (Hotel hotel:beans) {
            System.out.println(beans);
        }
    }
}
