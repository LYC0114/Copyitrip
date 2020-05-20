package cn.itrip.auth.service;

import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.ItripTokenVO;

public interface TokenService {
    String ganerateToken(String agent, ItripUser itripUser)throws Exception;

    void saveToken(String token,ItripUser itripUser)throws Exception;

    ItripTokenVO processToken(String agent, ItripUser user)throws Exception;
}
