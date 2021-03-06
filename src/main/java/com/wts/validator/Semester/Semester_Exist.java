package com.wts.validator.Semester;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;
import com.wts.entity.model.Semester;

public class Semester_Exist implements Interceptor {
  public void intercept(Invocation inv) {
    if (!StrKit.isBlank(inv.getController().getPara("id"))) {
      if (Semester.dao.findById(inv.getController().getPara("id")) == null) {
        inv.getController().renderText("指定学期不存在!");
      } else {
        if (Semester.dao.findById(inv.getController().getPara("id")).getDel()==0){
          inv.invoke();
        } else {
          inv.getController().renderText("指定学期已删除!");
        }
      }
    } else {
      inv.getController().renderText("缺少参数!");
    }
  }
}
