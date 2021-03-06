package com.wts.controller.Mobile.Teacher;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.wts.entity.model.Course;
import com.wts.entity.model.Teacher;
import com.wts.interceptor.OverdueCheck;
import com.wts.interceptor.PageCheck;
import com.wts.interceptor.PermissionCheck;
import com.wts.validator.Course.Course_Edit;
import com.wts.validator.Course.Course_Exist;
import com.wts.validator.Course.Course_Save;
import com.wts.validator.Query;
import com.wts.validator.Total;
import org.apache.log4j.Logger;

import static com.wts.util.Util.PermissionString;


public class CourseMobile extends Controller {
  private static Logger logger = Logger.getLogger(CourseMobile.class);

  /**
   * 页面
   */
  @Before({OverdueCheck.class, PageCheck.class})
  public void Page() {
    // 未登录
    if (getSessionAttr("Teacher") == null) {
      // cookie过期
      if (getCookie("dit").equals("")) {
        redirect("/desktop");
      } else {
        Teacher teacher = Teacher.dao.findById(getCookie("dit"));
        setSessionAttr("Teacher", teacher);
        setCookie("dit", teacher.getId().toString(), 60 * 60 * 3);
        setCookie(super.getClass().getSimpleName(), PermissionString(super.getClass().getSimpleName(),teacher.getId().toString()), 60 * 6 * 10);
        render("/static/html/mobile/Teacher/Mobile_Teacher_Course.html");
      }
    } else {
      String teacherId = ((Teacher) getSessionAttr("Teacher")).getId().toString();
      setCookie(super.getClass().getSimpleName(), PermissionString(super.getClass().getSimpleName(),teacherId), 60 * 6 * 10);
      render("/static/html/mobile/Teacher/Mobile_Teacher_Course.html");
    }
  }

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
  @Before({OverdueCheck.class, PermissionCheck.class, Query.class})
  public void Query() {
    renderJson(Db.paginate(
            getParaToInt("pageCurrent"),
            getParaToInt("pageSize"),
            "SELECT *, " +
                    "(case type when 1 then '必修课' when 2 then '选修课' else '错误' end ) as tname, " +
                    "(case state when 1 then '可用' when 2 then '停用' else '错误' end ) as sname ",
            "FROM course WHERE del = 0 AND (name LIKE '%" + getPara("keyword") + "%' " +
                    "OR detail LIKE '%" + getPara("keyword") + "%') ORDER BY id ASC").getList());
  }

  /**
   * 计数
   */
  @Before({OverdueCheck.class, PermissionCheck.class, Total.class})
  public void Total() {
    Long count = Db.queryLong("SELECT COUNT(*) FROM course WHERE name LIKE '%" + getPara("keyword") + "%' OR detail LIKE '%" + getPara("keyword") + "%'");
    if (count % getParaToInt("pageSize") == 0) {
      renderText((count / getParaToInt("pageSize")) + "");
    } else {
      renderText((count / getParaToInt("pageSize") + 1) + "");
    }
  }

  /**
   * 读取
   */
  @Before({OverdueCheck.class, PermissionCheck.class, Course_Exist.class})
  public void Get() {
    renderJson(Course.dao.findById(getPara("id")));
  }

  /**
   * 激活
   */
  @Before({OverdueCheck.class, PermissionCheck.class, Course_Exist.class})
  public void Active() {
    Course object = Course.dao.findById(getPara("id"));
    if (object.get("state").toString().equals("1")) {
      renderText("该课程已激活!");
    } else {
      object.set("state", 1).update();
      logger.warn("function:"+this.getClass().getSimpleName()+"/Active;"+"teacher_id:"+((Teacher) getSessionAttr("Teacher")).getId().toString()+";course_id:"+getPara("id")+";");
      renderText("OK");
    }
  }

  /**
   * 注销
   */
  @Before({OverdueCheck.class, PermissionCheck.class, Course_Exist.class})
  public void Inactive() {
    Course object = Course.dao.findById(getPara("id"));
    if (object.get("state").toString().equals("0")) {
      renderText("该课程已注销!");
    } else {
      object.set("state", 0).update();
      logger.warn("function:"+this.getClass().getSimpleName()+"/Inactive;"+"teacher_id:"+((Teacher) getSessionAttr("Teacher")).getId().toString()+";course_id:"+getPara("id")+";");
      renderText("OK");
    }
  }

  /**
   * 删除
   */
  @Before({OverdueCheck.class, PermissionCheck.class, Course_Exist.class})
  public void Delete() {
    Course object = Course.dao.findById(getPara("id"));
    if (object.get("del").toString().equals("1")) {
      renderText("该课程已删除!");
    } else {
      object.set("del", 1).update();
      logger.warn("function:"+this.getClass().getSimpleName()+"/Delete;"+"teacher_id:"+((Teacher) getSessionAttr("Teacher")).getId().toString()+";course_id:"+getPara("id")+";");
      renderText("OK");
    }
  }

  /**
   * 保存
   */
  @Before({OverdueCheck.class, PermissionCheck.class, Course_Save.class})
  public void Save() {
    Course object = new Course();
    object.set("name", getPara("name"))
            .set("detail", getPara("detail"))
            .set("amount", getPara("amount"))
            .set("type", getPara("type"))
            .set("state", getPara("state"))
            .set("del", 0)
            .save();
    logger.warn("function:"+this.getClass().getSimpleName()+"/Save;"+"teacher_id:"+((Teacher) getSessionAttr("Teacher")).getId().toString()+";course_id:"+object.get("id")+";");
    renderText("OK");
  }

  /**
   * 修改
   */
  @Before({OverdueCheck.class, PermissionCheck.class, Course_Exist.class, Course_Edit.class})
  public void Edit() {
    Course object = Course.dao.findById(getPara("id"));
    object.set("name", getPara("name"))
            .set("detail", getPara("detail"))
            .set("amount", getPara("amount"))
            .set("type", getPara("type"))
            .update();
    logger.warn("function:"+this.getClass().getSimpleName()+"/Edit;"+"teacher_id:"+((Teacher) getSessionAttr("Teacher")).getId().toString()+";course_id:"+getPara("id")+";");
    renderText("OK");
  }

  /**
   * 检测名称_新增
   */
  @Before(OverdueCheck.class)
  public void checkNameForAdd() {
    if (Db.find("SELECT * FROM Course WHERE name = ?", getPara("name")).size() != 0) {
      renderText("该课程名称已存在!");
    } else {
      renderText("OK");
    }
  }

  /**
   * 检测名称_修改
   */
  @Before(OverdueCheck.class)
  public void checkNameForEdit() {
    if (!Course.dao.findById(getPara("id")).get("name").equals(getPara("name"))
            && Db.find("SELECT * FROM Course WHERE name = ?", getPara("name")).size() != 0) {
      renderText("该课程名称已存在!");
    } else {
      renderText("OK");
    }
  }
}
