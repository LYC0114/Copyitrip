import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.junit.Test;

import java.util.HashMap;
import java.util.Set;

public class TestSMS {
    public static void main(String[] args) {

        //默认生产环境请求地址：app.cloopen.com
        String serverIp = "app.cloopen.com";
        //请求端口
        String serverPort = "8883";
        //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
        String accountSId = "8a216da8721783b301722705e55f0459";             //修改对应accountSId
        String accountToken = "80cdaee3da83492a95031e345e9831b1";           //修改对应accountToken
        //请使用管理控制台中已创建应用的APPID
        String appId = "8a216da8721783b301722705e5ce0460";                  //修改appId
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);
        sdk.setBodyType(BodyType.Type_JSON);
        //设置验证的手机号码
        String to = "18660261332";                                          //修改为测试手机号
        //设置模板（免费测试默认为1）
        String templateId= "1";                                             //修改设置模板为1
        //参数1.设置验证码，参数二2。设置有效时间（单位：分钟）               //修改验证码及有效期
        String[] datas = {"12345","1"};
        HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println(key +" = "+object);
            }
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }

    }
}
