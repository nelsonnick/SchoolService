package com.wts.controller.Desktop.Teacher;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.wts.entity.model.Teacher;
import com.wts.interceptor.OverdueCheck;
import com.wts.validator.Query;

import static com.wts.util.Util.PermissionString;


public class IdentityDesktop extends Controller {

  /**
   * 权限
   * */
  public void Permission() {
    String teacherId = ((Teacher) getSessionAttr("Teacher")).getId().toString();
    setCookie(super.getClass().getSimpleName(), PermissionString(super.getClass().getSimpleName(),teacherId), 60 * 6 * 10);
    setCookie("name",((Teacher) getSessionAttr("Teacher")).getName(),60 * 6 * 10);
  }

  /**
   * 查找
   */
  @Before({OverdueCheck.class, Query.class})
  public void Query() {
    renderJson(Db.paginate(
            getParaToInt("pageCurrent"),
            getParaToInt("pageSize"),
            "SELECT *",
            "FROM identity WHERE name LIKE '%" + getPara("keyword") + "%' ORDER BY id ASC").getList());
  }

  /**
   * 计数
   */
  @Before(OverdueCheck.class)
  public void Total() {
    Long count = Db.queryLong("SELECT COUNT(*) FROM identity WHERE name LIKE '%" + getPara("keyword") + "%'");
    renderText(count.toString());
  }


}
