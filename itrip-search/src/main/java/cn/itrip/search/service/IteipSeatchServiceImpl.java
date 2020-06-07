package cn.itrip.search.service;

import cn.itrip.beans.vo.ItripHotelVO;
import cn.itrip.beans.vo.hotel.SearchHotelVO;
import cn.itrip.common.Page;
import cn.itrip.search.dao.BaseQuery;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.stereotype.Service;

@Service
public class IteipSeatchServiceImpl implements IteipSeatchService {
    private BaseQuery baseQuery;
    @Override
    public Page<ItripHotelVO> getHotelListByPage(SearchHotelVO searchHotelVO) throws Exception {
        //查询参数
        SolrQuery query =new SolrQuery("*:*");
        //destination:北京AND keyword:首都|宾馆  用StringBuffer拼接这个字符串，
        //拼接关键字
        StringBuffer sb = new StringBuffer("distination"+searchHotelVO.getDestination());
        //从searchHotelVO中的getkeyword方法中得到keyword
        String keywords = searchHotelVO.getKeywords();
        //将keyword中的空格字符换成或者（|）字符
        String replace = keywords.replace(" ", "|");
        if(keywords!=null){
            //如果不为空则在字符串后拼接 AND 和 | 字符
            sb.append(" AND "+replace);
        }
        //将同String为Object类中的方法而所有类都继承Object类所以调用同String方法将sb装换成字符串
        query.setQuery(sb.toString());
        //执行DAO查询
        Page<ItripHotelVO>page=baseQuery.query(query,searchHotelVO.getPageNo(),searchHotelVO.getPageSize());


        //处理价格
        StringBuffer sb2 = new StringBuffer();
        //设置150以下  minPrice<150
        Double maxPrice = searchHotelVO.getMaxPrice();
        if(maxPrice!=null){
        sb2.append("minPrice:[*TO"+searchHotelVO.getMaxPrice()+"]");
        }
        //设置450以上  450>
        Double minPrice = searchHotelVO.getMinPrice();
        if(minPrice!=null){
            sb2.append("mixPrice:["+minPrice+"to*]");
        }
        query.addFilterQuery(sb2.toString());



        //处理商圈
        //前端参数: 3619, 3620要执行: tradingAreaIds: (*3619* OR *3620* )
        StringBuffer sb3 = new StringBuffer();
        String tradeAreaIds = searchHotelVO.getTradeAreaIds();
        if(tradeAreaIds!=null){
            String[] split = tradeAreaIds.split(",");
            sb3.append("tradingAreaIds: (");
            for (int i=0;split.length<i;i++){
                if(i==0){
                    sb3.append("*"+split[i]+"*");
                }else{
                    sb3.append(" OR*"+split[i]+"*");
                }
            }
            sb3.append(")");
        }
        query.addFilterQuery(sb3.toString());

        //返回
        return page;
    }
}
