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

public class Demo3solr {
    public static void main(String[] args) throws IOException, SolrServerException {
        HttpSolrClient client = new HttpSolrClient.Builder("Http://localHost:8080/solr").build();
        SolrQuery query = new SolrQuery("*:*");
        QueryResponse response = client.query("hotel", query, SolrRequest.METHOD.GET);
        SolrDocumentList results = response.getResults();
        long numFound = results.getNumFound();
        System.out.println("numFound"+numFound);
        for (SolrDocument result:results) {
            System.out.println(result.get("id")+"--"+result.get("hotelName")+"___"+result.get("maxPrice")+"----"+result.get("minPrice"));
        }
        List<ItripHotelVO> beans = response.getBeans(ItripHotelVO.class);
        for (ItripHotelVO bean:beans) {
            System.out.println(bean.getId()+"---"+bean.getHotelName());
        }
    }
}
