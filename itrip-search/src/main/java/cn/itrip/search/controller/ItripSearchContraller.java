package cn.itrip.search.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.vo.ItripHotelVO;
import cn.itrip.beans.vo.hotel.SearchHotelVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.Page;
import cn.itrip.search.service.IteipSeatchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ItripSearchContraller {
    private IteipSeatchService iteipSeatchService;
    @RequestMapping("/Hotellist/searchHotelPage")
    public Dto SearchHotelList(@RequestBody SearchHotelVO searchHotelVO) throws Exception {
        //验证目的地
        if(searchHotelVO==null||searchHotelVO.getDestination()==null){
            return DtoUtil.returnFail("目的地不能为空","20001");
        }
        //调用Service查询酒店列表
        Page<ItripHotelVO> page=iteipSeatchService.getHotelListByPage(searchHotelVO);
        //返回数据
        return DtoUtil.returnDataSuccess(page);
    }
}
