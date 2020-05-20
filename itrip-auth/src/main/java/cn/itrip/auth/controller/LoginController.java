package cn.itrip.auth.controller;

import cn.itrip.auth.service.TokenService;
import cn.itrip.auth.service.UserService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.ItripTokenVO;
import cn.itrip.common.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class LoginController {
    @Resource
    private TokenService tokenService;
    @Resource
    private UserService userService;
    Logger logger = Logger.getLogger(String.valueOf(Controller.class));
//登录
    @PostMapping("dologin")
    public Dto doLogin(@RequestParam("name")String userCode, @RequestParam("password")String userPassword, HttpServletRequest request )throws Exception {
        String agent = request.getHeader("User-Agent");
        logger.info("User-Agent" + agent);
        //验证登录
        ItripUser user = userService.getItripUserByUserCode(userCode);
        if (user == null || user.getUserPassword().equals(MD5.getMd5(userPassword, 32))) {
            return DtoUtil.returnFail("用户名或密码错误", ErrorCode.AUTH_PARAMETER_ERROR);
        }
        //处理token
        ItripTokenVO tokenVo = tokenService.processToken(agent, user);
        return DtoUtil.returnDataSuccess(tokenVo);
    }





//退出
    @GetMapping(value = "/logout",headers = "token")
    public Dto dologout(HttpServletRequest request) throws Exception {
        //获取到浏览器中的token和User-Agent数据
        String token = request.getHeader("token");
        String agent = request.getHeader("User-Agent");
        //验证token有效性
        Boolean isOK = tokenService.validatetoken(token, agent);
        //删除token缓存
        if(isOK){
            tokenService.deltoken(token);
            return DtoUtil.returnSuccess("退出成功");
        }else{
            return DtoUtil.returnSuccess("token过期，已退出",ErrorCode.AUTH_TOKEN_INVALID);
        }
    }
}

