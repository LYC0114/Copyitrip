package cn.itrip.service.itripHotel;
import cn.itrip.beans.pojo.ItripAreaDic;
import cn.itrip.beans.pojo.ItripLabelDic;
import cn.itrip.beans.vo.hotel.HotelVideoDescVO;
import cn.itrip.mapper.itripAreaDic.ItripAreaDicMapper;
import cn.itrip.mapper.itripHotel.ItripHotelMapper;
import cn.itrip.beans.pojo.ItripHotel;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import cn.itrip.mapper.itripLabelDic.ItripLabelDicMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.annotation.Resources;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import cn.itrip.common.Constants;
@Service
public class ItripHotelServiceImpl implements ItripHotelService {
    @Resource
    private ItripLabelDicMapper itripLabelDicMapper;
    @Resource
    private ItripAreaDicMapper itripAreaDicMapper;
    @Resource
    private ItripHotelMapper itripHotelMapper;

    public ItripHotel getItripHotelById(Long id)throws Exception{
        return itripHotelMapper.getItripHotelById(id);
    }

    public List<ItripHotel>	getItripHotelListByMap(Map<String,Object> param)throws Exception{
        return itripHotelMapper.getItripHotelListByMap(param);
    }

    public Integer getItripHotelCountByMap(Map<String,Object> param)throws Exception{
        return itripHotelMapper.getItripHotelCountByMap(param);
    }

    public Integer itriptxAddItripHotel(ItripHotel itripHotel)throws Exception{
            itripHotel.setCreationDate(new Date());
            return itripHotelMapper.insertItripHotel(itripHotel);
    }

    public Integer itriptxModifyItripHotel(ItripHotel itripHotel)throws Exception{
        itripHotel.setModifyDate(new Date());
        return itripHotelMapper.updateItripHotel(itripHotel);
    }

    public Integer itriptxDeleteItripHotelById(Long id)throws Exception{
        return itripHotelMapper.deleteItripHotelById(id);
    }

    public Page<ItripHotel> queryItripHotelPageByMap(Map<String,Object> param,Integer pageNo,Integer pageSize)throws Exception{
        Integer total = itripHotelMapper.getItripHotelCountByMap(param);
        pageNo = EmptyUtils.isEmpty(pageNo) ? Constants.DEFAULT_PAGE_NO : pageNo;
        pageSize = EmptyUtils.isEmpty(pageSize) ? Constants.DEFAULT_PAGE_SIZE : pageSize;
        Page page = new Page(pageNo, pageSize, total);
        param.put("beginPos", page.getBeginPos());
        param.put("pageSize", page.getPageSize());
        List<ItripHotel> itripHotelList = itripHotelMapper.getItripHotelListByMap(param);
        page.setRows(itripHotelList);
        return page;
    }


    //根据就酒店id查询视频描述信息
    @Override
    public HotelVideoDescVO getItripHotelVideoById(Long hotelId) throws Exception {
        //创建对象
        HotelVideoDescVO hotelVideoVO = new HotelVideoDescVO();
        //调用mapper查寻书信
        ItripHotel itripHotelById = itripHotelMapper.getItripHotelById(hotelId);
        //封装属性
        hotelVideoVO.setHotelName(itripHotelById.getHotelName());
        //查询商圈
        List<ItripAreaDic> list = itripAreaDicMapper.getItripAreaDicListByHotelId(hotelId);
        ArrayList<String> treadAreaNameList = new ArrayList<String>();
        for (ItripAreaDic areaDic:list) {
            treadAreaNameList.add(areaDic.getName());
        }
        //查询所属商圈
        hotelVideoVO.getTradingAreaNameList(treadAreaNameList);
        //查询特色
        List<ItripLabelDic>lableList=itripLabelDicMapper.getItripLableDicMapperByHotelId(hotelId);
        ArrayList<String> featureNameList = new ArrayList<>();
        for (ItripLabelDic labelDic:lableList) {
            featureNameList.add(labelDic.getName());
        }
        //查询酒店特色
        hotelVideoVO.getHotelFeatureList(featureNameList);

        return hotelVideoVO;
    }

}
