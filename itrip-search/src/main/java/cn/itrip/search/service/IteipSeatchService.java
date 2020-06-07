package cn.itrip.search.service;

import cn.itrip.beans.vo.ItripHotelVO;
import cn.itrip.beans.vo.hotel.SearchHotelVO;
import cn.itrip.common.Page;

public interface IteipSeatchService  {
     Page<ItripHotelVO> getHotelListByPage(SearchHotelVO searchHotelVO)throws Exception;
}
