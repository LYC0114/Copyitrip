package cn.itrip.service.itripHotelOrder;
import cn.itrip.beans.pojo.*;
import cn.itrip.beans.vo.order.ItripModifyHotelOrderVO;
import cn.itrip.beans.vo.order.RoomStoreVO;
import cn.itrip.beans.vo.order.ValidateRoomStoreVO;
import cn.itrip.common.BigDecimalUtil;
import cn.itrip.mapper.itripHotelOrder.ItripHotelOrderMapper;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import cn.itrip.mapper.itripOrderLinkUser.ItripOrderLinkUserMapper;
import cn.itrip.service.itripHotel.ItripHotelService;
import cn.itrip.service.itripHotelRoom.ItripHotelRoomService;
import cn.itrip.service.itripHotelTempStore.ItripHotelTempStoreService;
import org.apache.logging.log4j.core.config.Scheduled;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.itrip.common.Constants;
@Service
public class ItripHotelOrderServiceImpl implements ItripHotelOrderService {
    @Resource
    private ItripOrderLinkUserMapper itripOrderLinkUserMapper;
    @Resource
    private ItripHotelRoomService itripHotelRoomService;
    @Resource
    private ItripHotelService itripHotelService;
    @Resource
    private ItripHotelOrderMapper itripHotelOrderMapper;
    @Resource
    private ItripHotelTempStoreService itripHotelTempStoreService;

    public ItripHotelOrder getItripHotelOrderById(Long id)throws Exception{
        return itripHotelOrderMapper.getItripHotelOrderById(id);
    }

    public List<ItripHotelOrder>	getItripHotelOrderListByMap(Map<String,Object> param)throws Exception{
        return itripHotelOrderMapper.getItripHotelOrderListByMap(param);
    }

    public Integer getItripHotelOrderCountByMap(Map<String,Object> param)throws Exception{
        return itripHotelOrderMapper.getItripHotelOrderCountByMap(param);
    }

    public Integer itriptxAddItripHotelOrder(ItripHotelOrder itripHotelOrder)throws Exception{
            itripHotelOrder.setCreationDate(new Date());
            return itripHotelOrderMapper.insertItripHotelOrder(itripHotelOrder);
    }

    public Integer itriptxModifyItripHotelOrder(ItripHotelOrder itripHotelOrder)throws Exception{
        itripHotelOrder.setModifyDate(new Date());
        return itripHotelOrderMapper.updateItripHotelOrder(itripHotelOrder);
    }

    public Integer itriptxDeleteItripHotelOrderById(Long id)throws Exception{
        return itripHotelOrderMapper.deleteItripHotelOrderById(id);
    }

    public Page<ItripHotelOrder> queryItripHotelOrderPageByMap(Map<String,Object> param,Integer pageNo,Integer pageSize)throws Exception{
        Integer total = itripHotelOrderMapper.getItripHotelOrderCountByMap(param);
        pageNo = EmptyUtils.isEmpty(pageNo) ? Constants.DEFAULT_PAGE_NO : pageNo;
        pageSize = EmptyUtils.isEmpty(pageSize) ? Constants.DEFAULT_PAGE_SIZE : pageSize;
        Page page = new Page(pageNo, pageSize, total);
        param.put("beginPos", page.getBeginPos());
        param.put("pageSize", page.getPageSize());
        List<ItripHotelOrder> itripHotelOrderList = itripHotelOrderMapper.getItripHotelOrderListByMap(param);
        page.setRows(itripHotelOrderList);
        return page;
    }

    @Override
    public RoomStoreVO getPreOrderInfo(ValidateRoomStoreVO storeVO) throws Exception {
        RoomStoreVO roomStoreVO = new RoomStoreVO();
        roomStoreVO.setHotelId(storeVO.getHotelId());
        roomStoreVO.setRoomId(storeVO.getRoomId());
        roomStoreVO.setCheckInDate(storeVO.getCheckInDate());
        roomStoreVO.setCheckOutDate(storeVO.getCheckOutDate());
        roomStoreVO.setCount(1);
        //调用ItripHotilService查询数据
        ItripHotel hotel = itripHotelService.getItripHotelById(storeVO.getHotelId());
        roomStoreVO.setHotelName(hotel.getHotelName());
        //获取酒店价格
        ItripHotelRoom room = itripHotelRoomService.getItripHotelRoomById(storeVO.getRoomId());
        //房间价格
        roomStoreVO.setPrice(BigDecimal.valueOf(room.getRoomPrice()));
        //获取可预定的剩余库存（实时库存表-已经预定但是未支付的库存）
        Map<String, Object> param = new HashMap<>();
        param.put("hotelId",storeVO.getHotelId());
        param.put("roomId",storeVO.getRoomId());
        param.put("checkInDate",storeVO.getCheckInDate());
        param.put("checkOutDate",storeVO.getCheckOutDate());
        List<ItripHotelTempStore> HotelRoom =itripHotelTempStoreService.getItripHotelStoreListByMap(param);
        roomStoreVO.setStore(null);
        return roomStoreVO;
    }
//验证是否有库存
    @Override
    public boolean validateRoomStore(ValidateRoomStoreVO storeVO) throws Exception {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("hotelId",storeVO.getHotelId());
        param.put("roomId",storeVO.getRoomId());
        param.put("checkInDate",storeVO.getCheckInDate());
        param.put("checkOutDate",storeVO.getCheckOutDate());
        List<ItripHotelTempStore> storeList = itripHotelTempStoreService.getItripHotelTempStoreListByMap(param);
        if(storeList.get(0).getStore()-storeVO.getCount()>0){
            return true;
        }
        return false;
    }
//计算支付房费总金额
    @Override
    public Double calcPayAmount(Double roomPrice, int bookingDays, Integer count) throws Exception {
        BigDecimal bigDecimal = BigDecimalUtil.OperationASMD(roomPrice, bookingDays * count, BigDecimalUtil.BigDecimalOprations.multiply, 2, BigDecimal.ROUND_FLOOR);
        return bigDecimal.doubleValue();
    }
//生成订单
    @Override
    public Long itriptxAddItripHotelOrder(ItripHotelOrder order, List<ItripUserLinkUser> linkUsers) throws Exception {
        Long orderId = order.getId();
        if(orderId==null) {
            //数据库插入订单记录
            itripHotelOrderMapper.insertItripHotelOrder(order);
            orderId=order.getId();
        }else{//已有订单id，则修改订单
            itripHotelOrderMapper.insertItripHotelOrder(order);
            //删除订单入住人记录
            itripOrderLinkUserMapper.deleteItripOrderLinkUserByOrderId(orderId);

            }
        //数据库插入联系人记录
            for (ItripUserLinkUser linkUser : linkUsers) {
                ItripOrderLinkUser itripOrderLinkUser = new ItripOrderLinkUser();
                itripOrderLinkUser.setOrderId(order.getId());
                itripOrderLinkUser.setLinkUserId(order.getUserId());
                itripOrderLinkUser.setLinkUserName(order.getLinkUserName());
                itripOrderLinkUser.setCreationDate(new Date());
                itripOrderLinkUserMapper.insertItripOrderLinkUser(itripOrderLinkUser);
        }
        return order.getId();
    }
//判断房间支付方式是否支持线下支付
    @Override
    public boolean isSupportPayType(Long roomId, int payType) throws Exception {
        //通过房间id获取房间信息
        ItripHotelRoom room = itripHotelRoomService.getItripHotelRoomById(roomId);
        //通过房间信息获取房间支持的支付类型
        Integer oldPayType = room.getPayType();
        //房间支付类型中1，2，3，中除1外均支持线下支付
        if(payType==1){//1类型不支持线下支付返回false
            return false;
        }
        return  true;
    }
//预定成功后修改订单状态和实时库存表
    @Override
    public void itriptxModifyItripHotelOrderAndTemoStore(ItripHotelOrder order)throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("roomId",order.getRoomId());
        param.put("checkInDate",order.getCheckInDate());
        param.put("checkOutDate",order.getCheckOutDate());
        param.put("count",order.getCount());
        itripHotelTempStoreService.updateTempStore(param);
    }
//添加定时任务（依赖没有添加进来）
    @Scheduled(cron="0 0/10 * * * ?")
    public void flushOrderStatus()throws Exception{//任务job
        System.out.println("hello----"+System.currentTimeMillis());
        itripHotelOrderMapper.flushOrderStatus();

    }

}
