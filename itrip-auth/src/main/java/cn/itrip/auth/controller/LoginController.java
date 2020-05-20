package cn.itrip.auth.controller;

import cn.itrip.auth.service.TokenService;
import cn.itrip.auth.service.UserService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.ItripTokenVO;
import cn.itrip.common.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("dologin")
    public Dto doLogin(@RequestParam("name")String userCode, @RequestParam("password")String userPassword, HttpServletRequest request )throws Exception{
        //验证登录
        String agent = request.getHeader("User-Agent");
        logger.info("User-Agent"+agent);
        ItripUser user = userService.getItripUserByUserCode(userCode);
        if(user==null||user.getUserPassword().equals(MD5.getMd5(userPassword,32))){
            return DtoUtil.returnFail("用户名或密码错误", ErrorCode.AUTH_PARAMETER_ERROR);
        }
        //处理token
        ItripTokenVO tokenVo = tokenService.processToken(agent, user);
        return DtoUtil.returnDataSuccess(tokenVo);
    }
}

