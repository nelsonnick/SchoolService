package com.wts.common;

import com.foxinmy.weixin4j.exception.WeixinException;
import com.foxinmy.weixin4j.http.weixin.ApiResult;
import com.foxinmy.weixin4j.qy.model.User;
import com.wts.entity.WP;
import com.wts.entity.model.Parent;
import com.wts.entity.model.Teacher;
import com.wts.util.PinyinTool;
import com.wts.util.Util;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.wts.controller.MainController.getUserId;

public class Test {
  private static Logger logger = Logger.getLogger(Test.class);
  public static String get(String personName) {
    try {
      String UserId = new PinyinTool().toPinYin(personName, "", PinyinTool.Type.FIRSTUPPER);
      return UserId;
    } catch (Exception e) {
      return "";
    }
  }

  public static void main(String[] args) throws Exception {
//    if (!getPara("name").matches("\\d{4}[\\u7ea7]\\d{1,2}[\\u73ed]")) {
//      renderText("班级名称格式应为：XXXX级XX班");
    // 电子邮箱的正则
    // System.out.println("nelson@126.com".matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$"));
    // 两个以上汉字的正则
    // System.out.println("王".matches("^[\\u4e00-\\u9fa5]{2,}$"));
    // 联系电话的正则
    // System.out.println("123123124".matches("^1(3|4|5|7|8)\\d{9}$"));
//    String aa = null;
//    System.out.println(Util.getString(aa));
//    // 记录debug级别的信息
//    logger.debug("This is debug message.");
//    // 记录info级别的信息
//    logger.info("This is info message.");
//    // 记录error级别的信息
//    logger.error("This is error message.?");

//    User user =new User("c","王王");
//    user.setPartyIds(1);
//    user.setWeixinId("chnwts");
   // WP.me.inviteUser("chnwts","济南市育明小学欢迎您");
    //ApiResult ret = WP.me.createUser(user);
    //System.out.println(ret.getReturnMsg());

     //System.out.println("20t6级43班".matches("\\d{4}[\\u7ea7]\\d{1,2}[\\u73ed]"));
      //System.out.println(new PinyinTool().toPinYin("王燚硕","",PinyinTool.Type.FIRSTUPPER));
//    int[] integers = {11};
//    for (int i = 0; i < integers.length; i++){
//      System.out.println("第一组："+integers[i]);
//    }
//    for (int i = 0; i<4-integers.length; i++){
//      System.out.println("第二组："+i);
//    }
    System.out.println(get("吕东杰"));
  }

}
