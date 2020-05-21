package cn.itrip.auth.controller;

import cn.itrip.auth.service.UserService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.userinfo.ItripUserVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.ErrorCode;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.regex.Pattern;

//@Controller
@RestController//@RestController等同于Controller+ResponseBody
@RequestMapping("/api")
public class UserController {
    @Resource
    private UserService userService;
    @RequestMapping(value = "/registerbyphone",method= RequestMethod.POST)
    @ResponseBody
    public Dto registerByPhone(@RequestBody ItripUserVO userVO) throws Exception {
        //验证手机号是否合法
        String userCode=userVO.getUserCode();

        if (!validatePhone(userCode)){
            return DtoUtil.returnFail("请输入正确的手机号", ErrorCode.AUTH_ILLEGAL_USERCODE);
        }
        //验证用胡是否存在
        ItripUser user = userService.getItripUserByUserCode(userCode);
        if(EmptyUtils.isNotEmpty(user)){
            return DtoUtil.returnFail("已注册过此用户",ErrorCode.AUTH_USER_ALREADY_EXISTS);
        }
        //注册用户（写入数据库-未激活）
        ItripUser itripUser = new ItripUser();
        BeanUtils.copyProperties(userVO,itripUser);
        userService.itriptxCreateItripUser(itripUser);
        //返回结果
        return DtoUtil.returnSuccess("注册成功!");
    }




//正则验证手机号码
    private boolean validatePhone(String phone) {
        String regex="^1[3578]{1}\\d{9}$";
        return Pattern.compile(regex).matcher(phone).find();
    }



    //手机注册短信验证
@RequestMapping("/Validatephone")
//传入用户名和短信验证码
    public Dto Validatephone(@RequestParam("name") String userCode,@RequestParam("code") String smscode) throws Exception {
        //调用验证方法，获取返回值
        Boolean b=userService.itriptxValidateSmsCode(userCode,smscode);
        if(b){
            //返回为true成功
            return DtoUtil.returnSuccess("短信验证成功");
        }else{
            //返回为false不成功
            return DtoUtil.returnFail("短信验证码失败",ErrorCode.AUTH_ACTIVATE_FAILED);
        }
    }
}
