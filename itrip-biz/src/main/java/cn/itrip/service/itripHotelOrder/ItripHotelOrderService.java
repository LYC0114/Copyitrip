package cn.itrip.service.itripHotelOrder;
import cn.itrip.beans.pojo.ItripHotelOrder;
import java.util.List;
import java.util.Map;

import cn.itrip.beans.pojo.ItripUserLinkUser;
import cn.itrip.beans.vo.order.ItripModifyHotelOrderVO;
import cn.itrip.beans.vo.order.RoomStoreVO;
import cn.itrip.beans.vo.order.ValidateRoomStoreVO;
import cn.itrip.common.Page;
import org.springframework.core.annotation.Order;

/**
* Created by shang-pc on 2015/11/7.
*/
public interface ItripHotelOrderService {

    public ItripHotelOrder getItripHotelOrderById(Long id)throws Exception;

    public List<ItripHotelOrder>	getItripHotelOrderListByMap(Map<String,Object> param)throws Exception;

    public Integer getItripHotelOrderCountByMap(Map<String,Object> param)throws Exception;

    public Integer itriptxAddItripHotelOrder(ItripHotelOrder itripHotelOrder)throws Exception;

    public Integer itriptxModifyItripHotelOrder(ItripHotelOrder itripHotelOrder)throws Exception;

    public Integer itriptxDeleteItripHotelOrderById(Long id)throws Exception;

    public Page<ItripHotelOrder> queryItripHotelOrderPageByMap(Map<String,Object> param,Integer pageNo,Integer pageSize)throws Exception;
//获取订单预定信息
    RoomStoreVO getPreOrderInfo(ValidateRoomStoreVO storeVO)throws Exception;
//查询是否有库存
    boolean validateRoomStore(ValidateRoomStoreVO storeVO)throws Exception;
//计算支付房费总金额
    Double calcPayAmount(Double roomPrice, int bookingDays, Integer count)throws Exception;
//生成订单
    Long itriptxAddItripHotelOrder(ItripHotelOrder order, List<ItripUserLinkUser> linkUsers) throws Exception;

//验证是否支持到店支付
boolean isSupportPayType(Long roomId,int payType)throws Exception;
//预定成功后修改订单状态和实时库存表
    void itriptxModifyItripHotelOrderAndTemoStore(ItripHotelOrder order)throws Exception;
}
