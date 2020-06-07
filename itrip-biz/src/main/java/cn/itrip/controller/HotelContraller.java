package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripAreaDic;
import cn.itrip.beans.pojo.ItripImage;
import cn.itrip.beans.vo.ItripAreaDicVO;
import cn.itrip.beans.vo.ItripImageVO;
import cn.itrip.beans.vo.hotel.HotelVideoDescVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.service.itripAreaDic.ItripAreaDicService;
import cn.itrip.service.itripHotel.ItripHotelService;
import cn.itrip.service.itripImage.ItripImageServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
@Restcontroller，相当于普通@Controller+@RresponseBody,
* @RestController可以直接返回数据但是不能跳转页面
* @Controller可以跳转页面但是返回数据需要借助@ResponseBody
* */
@RestController
@RequestMapping("/api/hotel")
public class HotelContraller {
        @Resource
        private ItripHotelService itripHotelService;
        @Resource
        private ItripImageServiceImpl itripImageService;
        //创建ItripAreaDicService类
        @Resource
        private ItripAreaDicService itripAreaDicService;
        //查询热门城市
        @GetMapping("/queryhotcity/{type}")
        //@pathVariable当传入数据与方法参数不一致时可以用这个标签，写明传入参数对应的方法参数
        public Dto queryhotcity(@PathVariable("type") String type) throws Exception {
            //传入参数判空
            if(type==null){
                DtoUtil.returnFail("type不能为空","10201");
            }
            //查询热门城市l
            //2.创建一个HashMap作为方法参数
            Map<String, Object> param = new HashMap<String, Object>();

            param.put("isChina",type);//Type代表国内还是国外，0代表国外，1代表国内。
            param.put("isHot",1);//1代表热门城市
            //1.调用mapper代理开发，itripAreaDicService类中的方法，通过传入一个Map类型参数param返回List
            List<ItripAreaDic> List = itripAreaDicService.getItripAreaDicListByMap(param);
            //封装结果
            //创建一个ArrayList集合封装结果
            List<Object> result = new ArrayList<Object>();
            for (ItripAreaDic areaDic: List) {
                //创建一个ItripAreaDicVO
                ItripAreaDicVO dicVO = new ItripAreaDicVO();
                //把遍历出来的结果Copy到dicVO
                BeanUtils.copyProperties(areaDic,dicVO);
                //将dicVO存进result
                result.add(dicVO);
            }
            //讲储存了结果的list返回
            return DtoUtil.returnDataSuccess(result);
        }


        //查询商圈
        @GetMapping("/querytradearea/{cityId}")
    public Dto querytradearea (@PathVariable("cityId") Long cityId) throws Exception {
            if(cityId==null){
                DtoUtil.returnFail("cityId不能为空","10203");
            }
            Map<String, Object> param = new HashMap<String,Object>();
            param.put("parent",cityId);//城市
            param.put("isTradingArea",1);//是否为商圈
            List<ItripAreaDic> list = itripAreaDicService.getItripAreaDicListByMap(param);
            List<ItripAreaDicVO> result = new ArrayList<ItripAreaDicVO>();
            for (ItripAreaDic areaDic:list) {
                ItripAreaDicVO DicVO = new ItripAreaDicVO();
                BeanUtils.copyProperties(areaDic,DicVO);
                result.add(DicVO);
            }
            return DtoUtil.returnDataSuccess(result);
    }


    //查询酒店图片
    @GetMapping("/getimg/{targetId}")
    public Dto getHotelImg(@PathVariable("targetId") Long targetId) throws Exception {
            if(targetId==null){
                DtoUtil.returnFail("获取酒店图片失败","100212");
            }
            //调用数据库查询图片
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("targetId",targetId);//酒店id
            param.put("type","0");//酒店图片
            List<ItripImage> List = itripImageService.getItripImageListByMap(param);
        //封装结果
        ArrayList<ItripImageVO> images = new ArrayList<ItripImageVO>();
        for(ItripImage image:List){
            ItripImageVO imageVO = new ItripImageVO();
            BeanUtils.copyProperties(image,imageVO);
            images.add(imageVO);
        }
        return DtoUtil.returnDataSuccess(images);
    }



    //获得酒店视频
    @GetMapping("/getvideodesc/{hotelId}")
    public Dto getvideodesc(@PathVariable("hotelId") Long hotelId) throws Exception {
            if(hotelId==null){
                DtoUtil.returnFail("酒店id不能为空","100215");
            }
            //调用业务逻辑查询
        HotelVideoDescVO  hotelVideo=itripHotelService.getItripHotelVideoById(hotelId);
            return null;
    }
}
