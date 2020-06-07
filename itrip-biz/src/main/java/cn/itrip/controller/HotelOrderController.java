package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.beans.pojo.ItripHotelRoom;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.pojo.ItripUserLinkUser;
import cn.itrip.beans.vo.order.ItripAddHotelOrderVO;
import cn.itrip.beans.vo.order.ItripModifyHotelOrderVO;
import cn.itrip.beans.vo.order.RoomStoreVO;
import cn.itrip.beans.vo.order.ValidateRoomStoreVO;
import cn.itrip.common.*;
import cn.itrip.service.itripHotelOrder.ItripHotelOrderService;
import cn.itrip.service.itripHotelRoom.ItripHotelRoomService;
import cn.itrip.service.itripProductStore.ItripProductStoreService;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hotelorder")
public class HotelOrderController {
    @Resource
    private SystemConfig systemConfig;
    @Resource
    private ItripHotelRoomService itripHotelRoomService;
    @Resource
    private ItripHotelOrderService itripHotelOrderService;
    @Resource
    private ValidationToken validationToken;
@PostMapping("/getpreorderinfo")
    public Dto getpreorderinfo(@RequestBody ValidateRoomStoreVO storeVO, HttpServletRequest request) throws Exception {
//    ItripUser token = validationToken.getCurrentUser(request.getHeader("token"));
//    if(token==null){
//        DtoUtil.returnFail("token认证失败","100000");
//    }
    if(storeVO.getHotelId()==null||storeVO.getRoomId()==null){
        DtoUtil.returnFail("酒店id房型id不能为空","100510");
    }
    //调用数据库
    RoomStoreVO roomStoreVO =itripHotelOrderService.getPreOrderInfo(storeVO);
    //返回数据

    return DtoUtil.returnDataSuccess(roomStoreVO);
    }

//验证是否有库存
    @PostMapping("validateroomstore")
    public Dto validateroomstore(ValidateRoomStoreVO storeVO,HttpServletRequest request) throws Exception {
//    //判断token是否为空，若为空客户没有登录
//        ItripUser user = validationToken.getCurrentUser(request.getHeader("token"));
//        if(user==null){
//            DtoUtil.returnFail("token认证失败","100000");
//        }
        //判断酒店ID房间ID是否为空
        if(storeVO.getHotelId()==null||storeVO.getRoomId()==null){
            DtoUtil.returnFail("酒店id房型id不能为空","100510");
        }
        //判断入住和离开时间是否为空
        if(storeVO.getCheckInDate()==null||storeVO.getCheckOutDate()==null){
            DtoUtil.returnFail("入住和退房信息不能为空","100511");
        }
        //擦寻是否有库存
        boolean isHaving = itripHotelOrderService.validateRoomStore(storeVO);
        //封装返回结果
        HashMap<String, Object> result = new HashMap<>();
        result.put("success",isHaving);
        return DtoUtil.returnDataSuccess(result);
    }
    @PostMapping("/addhotelorder")
    public Dto addHotelOrder(@RequestBody ItripAddHotelOrderVO orderVO,HttpServletRequest request) throws Exception {
//    //判断token是否为空，若为空客户没有登录
        String token =request.getHeader("token");
        ItripUser user = validationToken.getCurrentUser(request.getHeader("token"));
        if(user==null){
            DtoUtil.returnFail("token认证失败","100000");
        }
        //验证必填数据是否为空
        if(orderVO.getHotelId()==null||orderVO.getRoomId()==null||orderVO.getCount()==null){
            return DtoUtil.returnFail("必填信息不能为空","100506");
        }
        //生成订单
        ItripHotelOrder order=new ItripHotelOrder();
        BeanUtils.copyProperties(orderVO,order);
        order.setUserId(user.getId());
        order.setOrderStatus(0);
        if(token.startsWith(Constants.TOKEN_PRIFIX+"PC")){
            order.setBookType(0);
        }else if (token.startsWith(Constants.TOKEN_PRIFIX+"MOBILE")){
            order.setBookType(1);
        }else {
            order.setBookType(2);
        }
        order.setCreationDate(new Date());
        order.setCreatedBy(user.getId());
        //订单天数
        int bookingDays = DateUtil.getBetweenDates(order.getCheckInDate(), order.getCheckOutDate()).size() - 1;
        order.setBookingDays(bookingDays);
        Long roomId = order.getRoomId();
        ItripHotelRoom room = itripHotelRoomService.getItripHotelRoomById(roomId);
        Double roomPrice = room.getRoomPrice();
        //计算总金额
        Double payAmount = itripHotelOrderService.calcPayAmount(roomPrice,bookingDays,order.getCount());
        order.setPayAmount(payAmount);
        //联系人
        List<ItripUserLinkUser> linkUsers = orderVO.getLinkUser();
        StringBuffer linkUserNames = new StringBuffer();
        for (ItripUserLinkUser linkUser:linkUsers) {
            linkUserNames.append(linkUser.getLinkUserName()+"0");
        }
        linkUserNames.substring(0,linkUserNames.length()-1);
        order.setLinkUserName(linkUserNames.toString());
        //订单编号
        StringBuffer orderNo = new StringBuffer();
        //机器码
        orderNo.append(systemConfig.getMachineCode());
        //时间戳
        orderNo.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        //使用MD5加密生成唯一订单编号
        orderNo.append(MD5.getMd5(""+roomId+System.currentTimeMillis()+(Math.random()*900000+1000000),6));
        order.setOrderNo(orderNo.toString());
        //写入数据库
        itripHotelOrderService.itriptxAddItripHotelOrder(order,linkUsers);
        Map<Object, Object> result = new HashMap<>();
        result.put("orderId",order);
        return DtoUtil.returnDataSuccess(result);
    }

    //3.2.10修改订单的支付方式和状态
    @PostMapping("updateorderstatusandpaytype")
    public Dto updateorderstatusandpaytype(@PathVariable ItripModifyHotelOrderVO orderVO,HttpServletRequest request) throws Exception {
    //登录验证
//        String token = request.getHeader("token");
//        ItripUser user= validationToken.getCurrentUser(token);
//        if(user==null){
//            DtoUtil.returnFail("token认证失败请重新登录","100000");
//        }
        //判空
        if(orderVO==null||orderVO.getId()==null||orderVO.getPayType()==null){
            DtoUtil.returnFail("必填参数不能提交空，请填写订单信息","100523");
        }
        //根据订单id获取order订单
        ItripHotelOrder order = itripHotelOrderService.getItripHotelOrderById(orderVO.getId());
        //验证支付类型是否支持到付
        boolean isSuppot = itripHotelOrderService.isSupportPayType(order.getRoomId(),orderVO.getPayType());
        if(!isSuppot){
            DtoUtil.returnFail("对不起，此房间不支持线下支付","100521");
        }
        //修改订单
        order.setPayType(orderVO.getPayType());
        order.setOrderStatus(2);
        order.setModifyDate(new Date());
        //预定成功后修改订单状态和实时库存表
        itripHotelOrderService.itriptxModifyItripHotelOrderAndTemoStore(order);
        return DtoUtil.returnSuccess("修改订单状态成功！");
    }

}
