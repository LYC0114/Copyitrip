package com.lyc;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;

public class DemoSolr {
    public static void main(String[] args) throws IOException, SolrServerException {
        //创建solr的http客户端请求
        HttpSolrClient client = new HttpSolrClient.Builder("http://localhost:8080/solr").build();
        //创建查询参数
        SolrQuery query = new SolrQuery("*:*");
        //执行查询获得像一个结果
        QueryResponse response = client.query("core1", query, SolrRequest.METHOD.GET);
        //从相应中获取数据
        SolrDocumentList results = response.getResults();
        for (SolrDocument doc:results) {
            System.out.println(doc.get("id")+"--"+doc.get("hotelName")+"--"+doc.get("address"));
        }
    }
}
