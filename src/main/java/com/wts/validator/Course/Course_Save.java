package com.wts.validator.Course;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

public class Course_Save implements Interceptor {
  public void intercept(Invocation inv) {
    if (!StrKit.isBlank(inv.getController().getPara("name"))
            && !StrKit.isBlank(inv.getController().getPara("detail"))
            && !StrKit.isBlank(inv.getController().getPara("amount"))
            && !StrKit.isBlank(inv.getController().getPara("type"))
            && !StrKit.isBlank(inv.getController().getPara("state"))
            ) {
      String name = inv.getController().getPara("name");
      if (Db.find("SELECT * FROM Course WHERE name = ?", name).size() != 0) {
        inv.getController().renderText("该名称已存在!");
      } else {
        inv.invoke();
      }
    } else {
      inv.getController().renderText("缺少参数!");
    }
  }
}
