package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripImage;
import cn.itrip.beans.vo.ItripImageVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.service.itripImage.ItripImageService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hotelroom/")

//根据targetid查询酒店图片
public class HotelRoomController {
    @Resource
    private ItripImageService itripImageService;
    @GetMapping ("getimg/{targetId}")
    public Dto getimg (@PathVariable("targetId")Long targetId) throws Exception {
        if(targetId==null){
            DtoUtil.returnFail("酒店房型id不能为空","100302");
        }
        Map<String, Object> param = new HashMap<>();
        param.put("type","1");
        param.put("targetId",targetId);
        //调用数据库查询
        List<ItripImage> itripImageListByMap = itripImageService.getItripImageListByMap(param);
        ArrayList<ItripImageVO>imageList= new ArrayList<ItripImageVO>();
        for (ItripImage image: itripImageListByMap) {
            ItripImageVO itripImageVO = new ItripImageVO();
            BeanUtils.copyProperties(image,itripImageVO);
            imageList.add(itripImageVO);
        }
        return DtoUtil.returnDataSuccess(imageList);
    }
}
