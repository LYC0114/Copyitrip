package cn.itrip.search.dao;

import cn.itrip.beans.vo.ItripHotelVO;
import cn.itrip.common.Constants;
import cn.itrip.common.Page;
import cn.itrip.common.PropertiesUtils;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class BaseQuery {
    private HttpSolrClient client;
    public BaseQuery() {
        //创建客户端对象
       client= new HttpSolrClient.Builder().withBaseSolrUrl(PropertiesUtils.get("database.properties", "baseUrl"))
               //设置连接超时时间10s
                .withConnectionTimeout(10000)//单位ms
               //设置socket超时时间60s
                .withSocketTimeout(600000)//单位ms
                .build();
    }

    public Page<ItripHotelVO> query(SolrQuery query, Integer pageNo, Integer pageSize) throws Exception {
        //使用三目运算符判断页面行数是否为空，则pagesize为DEFAULT_PAGE_SIZE,否则为传入的pagesize
        pageSize=pageSize==null? Constants.DEFAULT_PAGE_SIZE:pageSize;
        //使用三目运算符判断pageNo是否为空，为空则默认为DEFAULT_PAGE_SIZE,否则为传入pageNO
        pageNo=pageNo==null?Constants.DEFAULT_PAGE_SIZE:pageNo;
        //设置开始查询位置
        int beginPos=(pageNo-1)*pageSize;

        query.setStart(beginPos);
        query.setRows(pageSize);
        QueryResponse response = client.query(query);
        List<ItripHotelVO> beans = response.getBeans(ItripHotelVO.class);
        long numFound = response.getResults().getNumFound();
        Page page = new Page(beginPos, pageSize, (int) numFound);
        page.setRows(beans);
        return page;
    }
}
