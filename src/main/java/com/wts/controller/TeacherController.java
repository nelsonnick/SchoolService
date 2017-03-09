package com.wts.controller;

import com.foxinmy.weixin4j.exception.WeixinException;
import com.foxinmy.weixin4j.qy.model.User;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.wts.entity.WP;
import com.wts.entity.model.Parent;
import com.wts.entity.model.Teacher;
import com.wts.interceptor.AjaxManager;
import com.wts.interceptor.AjaxTeacher;
import com.wts.util.ParamesAPI;
import com.wts.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.wts.util.Util.getUserId;

public class TeacherController extends Controller {
  private String getSQL(String sql) {
    return "FROM teacher WHERE name LIKE '%" + sql +
            "%' OR userId LIKE '%" + sql +
            "%' OR mobile LIKE '%" + sql +
            "%' OR weixinId LIKE '%" + sql +
            "%' OR qq LIKE '%" + sql +
            "%' OR email LIKE '%" + sql +
            "%' ORDER BY name ASC";
  }

  /**
   * 登录到教师页面
   */
  public void forManager() throws WeixinException {
    // 检测session中是否存在teacher
    if (getSessionAttr("manager") == null || ((Teacher) getSessionAttr("manager")).getIsManager() != 1) {
      // 检测cookie中是否存在EnterpriseId
      if (getCookie("die") == null || getCookie("die").equals("")) {
        // 检测是否来自微信请求
        if (!(getPara("code") == null || getPara("code").equals(""))) {
          User user = WP.me.getUserByCode(getPara("code"));
          Teacher teacher = Teacher.dao.findFirst(Teacher.dao.getSql("teacher.weixin_manager"), user.getUserId(), 1);
          // 检测是否有权限
          if (teacher != null) {
            setSessionAttr("manager", teacher);
            setCookie("die", teacher.getId().toString(), 60 * 30);
            render("/static/ManagerOfTeacher.html");
          } else {
            redirect("/");
          }
        } else {
          redirect("/");
        }
      } else {
        Teacher teacher = Teacher.dao.findById(getCookie("die"));
        setSessionAttr("manager", teacher);
        render("/static/ManagerOfTeacher.html");
      }
    } else {
      render("/static/ManagerOfTeacher.html");
    }
  }

  /**
   * 登录移动_管理_教师
   */
  public void Mobile_Manager_Teacher() throws WeixinException {
    if (getSessionAttr("manager") == null || ((Teacher) getSessionAttr("manager")).getIsManager() != 1) {
      if (getCookie("dim") == null || getCookie("dim").equals("")) {
        if (!(getPara("code") == null || getPara("code").equals(""))) {
          User user = WP.me.getUserByCode(getPara("code"));
          Teacher manager = Teacher.dao.findFirst("SELECT * FROM teacher WHERE userId = ? AND state = ? AND isManager = 1", user.getUserId(), 1);
          if (manager != null) {
            setSessionAttr("manager", manager);
            setCookie("dim", manager.getId().toString(), 60 * 30);
            if (user.getAvatar().equals(manager.getPicUrl())) {
              manager.set("picUrl", user.getAvatar()).update();
            }
            render("/static/html/mobile/manager/Mobile_Manager_Teacher.html");
          } else {
            redirect("/");
          }
        } else {
          redirect("/");
        }
      } else {
        Teacher manager = Teacher.dao.findById(getCookie("dim"));
        setSessionAttr("manager", manager);
        render("/static/html/mobile/manager/Mobile_Manager_Teacher.html");
      }
    } else {
      render("/static/html/mobile/manager/Mobile_Manager_Teacher.html");
    }
  }

  /**
   * 登录到教师个人页面
   */
  public void forTeacherPersonal() throws WeixinException {
    // 检测session中是否存在teacher
    if (getSessionAttr("teacher") == null) {
      // 检测cookie中是否存在EnterpriseId
      if (getCookie("die") == null || getCookie("die").equals("")) {
        // 检测是否来自微信请求
        if (!(getPara("code") == null || getPara("code").equals(""))) {
          User user = WP.me.getUserByCode(getPara("code"));
          Teacher teacher = Teacher.dao.findFirst(Teacher.dao.getSql("teacher.weixin_teacher"), user.getUserId(), 1);
          // 检测是否有权限
          if (teacher != null) {
            setSessionAttr("teacher", teacher);
            setCookie("die", teacher.getId().toString(), 60 * 30);
            render("/static/TeacherOfPersonal.html");
          } else {
            redirect("/");
          }
        } else {
          redirect("/");
        }
      } else {
        Teacher teacher = Teacher.dao.findById(getCookie("die"));
        setSessionAttr("teacher", teacher);
        render("/static/TeacherOfPersonal.html");
      }
    } else {
      render("/static/TeacherOfPersonal.html");
    }
  }

  /**
   * 保存
   */
  @Before({Tx.class, AjaxManager.class})
  public void save() {
    if (!getPara("name").matches("^[\\u4e00-\\u9fa5]{2,}$")) {
      renderText("教师姓名应为两个以上汉字!");
    } else if (!getPara("mobile").matches("^1(3|4|5|7|8)\\d{9}$")) {
      renderText("手机号码格式错误!");
    } else if (Teacher.dao.find("SELECT * FROM teacher WHERE mobile = ?", getPara("mobile")).size() != 0) {
      renderText("已有教师使用该手机号码!");
    } else if (Parent.dao.find("SELECT * FROM parent WHERE mobile = ?", getPara("mobile")).size() != 0) {
      renderText("已有家长使用该手机号码!");
    } else if (getPara("email") != null && Teacher.dao.find("SELECT * FROM teacher WHERE email = ?", getPara("email")).size() != 0) {
      renderText("已有教师使用该电子邮箱!");
    } else if (getPara("email") != null && Parent.dao.find("SELECT * FROM parent WHERE email = ?", getPara("email")).size() != 0) {
      renderText("已有家长使用该电子邮箱!");
    } else if (getPara("qq") != null && Teacher.dao.find("SELECT * FROM teacher WHERE qq = ?", getPara("qq")).size() != 0) {
      renderText("已有教师使用该QQ号!");
    } else if (getPara("qq") != null && Parent.dao.find("SELECT * FROM parent WHERE qq = ?", getPara("qq")).size() != 0) {
      renderText("已有家长使用该QQ号!");
    } else if (getPara("wx") != null && Teacher.dao.find("SELECT * FROM teacher WHERE weixinId = ?", getPara("wx")).size() != 0) {
      renderText("已有教师使用该微信号!");
    } else if (getPara("wx") != null && Parent.dao.find("SELECT * FROM parent WHERE weixinId = ?", getPara("wx")).size() != 0) {
      renderText("已有家长使用该微信号!");
    } else if (Teacher.dao.find("SELECT * FROM teacher WHERE userId = ?", getUserId(getPara("name"))).size() != 0) {
      renderText("已有教师使用该userID!");
    } else if (Parent.dao.find("SELECT * FROM parent WHERE userId = ?", getUserId(getPara("name"))).size() != 0) {
      renderText("已有家长使用该userID!");
    } else {
      User user = new User(getUserId(getPara("name")), getPara("name").trim());
      user.setMobile(getPara("mobile").trim());
      user.setWeixinId(getPara("wx").trim());
      user.setEmail(getPara("email").trim());
      user.setPartyIds(1);
      try {
        WP.me.createUser(user);
        List<String> userIds = new ArrayList<String>();
        userIds.add(getUserId(getPara("name")));
        WP.me.addTagUsers(ParamesAPI.teacherTagId, userIds, new ArrayList<Integer>());
        if (getPara("isManager").trim().equals("1")) {
          WP.me.addTagUsers(ParamesAPI.managerTagId, userIds, new ArrayList<Integer>());
        }
        Teacher teacher = new Teacher();
        teacher.set("name", getPara("name").trim())
                .set("mobile", getPara("mobile").trim())
                .set("qq", getPara("qq").trim())
                .set("email", getPara("email").trim())
                .set("weixinId", getPara("weixinId").trim())
                .set("userId", getUserId(getPara("name")))
                .set("pass", "0011223344")
                .set("state", 4)
                .set("isManager", getPara("isManager").trim())
                .save();
        renderText("OK");
      } catch (WeixinException e) {
        renderText(e.getErrorText());
      }
    }
  }

  /**
   * 管理员修改
   */
  @Before({Tx.class, AjaxManager.class})
  public void edit() {
    Teacher teacher = Teacher.dao.findById(getPara("id"));
    if (teacher == null) {
      renderText("要修改的教师不存在!");
    } else {
      if (Util.getString(teacher.getStr("name")).equals(getPara("name").trim())
              && Util.getString(teacher.getStr("mobile")).equals(getPara("mobile").trim())
              && Util.getString(teacher.getStr("email")).equals(getPara("email").trim())
              && Util.getString(teacher.getStr("qq")).equals(getPara("qq").trim())
              && Util.getString(teacher.getStr("weixinId")).equals(getPara("wx").trim())
              && Util.getString(teacher.get("isManager").toString()).equals(getPara("isManager").trim())
              ) {
        renderText("未找到修改内容!");
      } else if (!getPara("name").matches("^[\\u4e00-\\u9fa5]{2,}$")) {
        renderText("教师姓名应为两个以上汉字!");
      } else if (!getPara("mobile").matches("^1(3|4|5|7|8)\\d{9}$")) {
        renderText("手机号码格式错误!");
      } else if (!Util.getString(teacher.getStr("mobile")).equals(getPara("mobile"))
              && Teacher.dao.find("SELECT * FROM teacher WHERE mobile = ?", getPara("mobile")).size() != 0) {
        renderText("已有教师使用该手机号码!");
      } else if (!Util.getString(teacher.getStr("mobile")).equals(getPara("mobile"))
              && Parent.dao.find("SELECT * FROM parent WHERE mobile = ?", getPara("mobile")).size() != 0) {
        renderText("已有家长使用该手机号码!");
      } else if (!Util.getString(teacher.getStr("email")).equals(getPara("email"))
              && Teacher.dao.find("SELECT * FROM teacher WHERE email = ?", getPara("email")).size() != 0) {
        renderText("已有教师使用该电子邮箱!");
      } else if (!Util.getString(teacher.getStr("email")).equals(getPara("email"))
              && Parent.dao.find("SELECT * FROM parent WHERE email = ?", getPara("email")).size() != 0) {
        renderText("已有家长使用该电子邮箱!");
      } else if (!Util.getString(teacher.getStr("qq")).equals(getPara("qq"))
              && Teacher.dao.find("SELECT * FROM teacher WHERE qq = ?", getPara("qq")).size() != 0) {
        renderText("已有教师使用该QQ号!");
      } else if (!Util.getString(teacher.getStr("qq")).equals(getPara("qq"))
              && Parent.dao.find("SELECT * FROM parent WHERE qq = ?", getPara("qq")).size() != 0) {
        renderText("已有家长使用该QQ号!");
      } else if (!Util.getString(teacher.getStr("weixinId")).equals(getPara("wx"))
              && Teacher.dao.find("SELECT * FROM teacher WHERE weixinId = ?", getPara("wx")).size() != 0) {
        renderText("已有教师使用该微信号!");
      } else if (!Util.getString(teacher.getStr("weixinId")).equals(getPara("wx"))
              && Parent.dao.find("SELECT * FROM parent WHERE weixinId = ?", getPara("wx")).size() != 0) {
        renderText("已有家长使用该微信号!");
      } else if (!Util.getString(teacher.getStr("userId")).equals(getUserId(getPara("name")))
              && Teacher.dao.find("SELECT * FROM teacher WHERE userId = ?", getUserId(getPara("name"))).size() != 0) {
        renderText("已有教师使用该userId!");
      } else if (!Util.getString(teacher.getStr("userId")).equals(getUserId(getPara("name")))
              && Parent.dao.find("SELECT * FROM parent WHERE userId = ?", getUserId(getPara("name"))).size() != 0) {
        renderText("已有家长使用该userId!");
      } else {
        try {
          if (!Util.getString(teacher.getStr("name")).equals(getPara("name").trim())
                  || !Util.getString(teacher.getStr("mobile")).equals(getPara("mobile").trim())
                  || !Util.getString(teacher.getStr("email")).equals(getPara("email").trim())
                  || !Util.getString(teacher.getStr("weixinId")).equals(getPara("wx").trim())
                  ) {
            User user = new User(teacher.get("userId").toString(), teacher.get("name").toString());
            user.setMobile(getPara("mobile").trim());
            user.setEmail(getPara("email").trim());
            user.setWeixinId(getPara("wx").trim());
            WP.me.updateUser(user);
          }
          List<String> userIds = new ArrayList<String>();
          userIds.add(teacher.get("userId").toString().trim());
          if (!Util.getString(teacher.get("isManager").toString()).equals(getPara("isManager").trim())) {
            if (getPara("isManager").trim().equals("1")) {
              WP.me.addTagUsers(ParamesAPI.managerTagId, userIds, new ArrayList<Integer>());
            } else {
              WP.me.deleteTagUsers(ParamesAPI.managerTagId, userIds, new ArrayList<Integer>());
            }
          }
          teacher.set("name", getPara("name").trim())
                  .set("mobile", getPara("mobile").trim())
                  .set("email", getPara("email").trim())
                  .set("weixinId", getPara("wx").trim())
                  .set("qq", getPara("qq").trim())
                  .set("isManager", getPara("isManager").trim())
                  .update();
          renderText("OK");
        } catch (WeixinException e) {
          renderText(e.getErrorText());
        }
      }
    }
  }

  /**
   * 自主修改
   */
  @Before({Tx.class, AjaxTeacher.class})
  public void editSelf() {
    Teacher teacher = Teacher.dao.findById(((Teacher) getSessionAttr("teacher")).getId());
    if (!getPara("name").matches("^[\\u4e00-\\u9fa5]{2,}$")) {
      renderText("教师姓名应为两个以上汉字!");
    } else if (!getPara("mobile").matches("^1(3|4|5|7|8)\\d{9}$")) {
      renderText("手机号码格式错误!");
    } else if (!Util.getString(teacher.getStr("mobile")).equals(getPara("mobile"))
            && Teacher.dao.find(Teacher.dao.getSql("teacher.mobile"), getPara("mobile")).size() != 0
            && Parent.dao.find(Parent.dao.getSql("parent.mobile"), getPara("mobile")).size() != 0) {
      renderText("该手机号码已存在!");
    } else {
      if (!Util.getString(teacher.getStr("name")).equals(getPara("name").trim())
              || !Util.getString(teacher.getStr("mobile")).equals(getPara("mobile").trim())) {
        User user = new User(teacher.get("userId").toString(), teacher.get("name").toString());
        user.setMobile(getPara("mobile").trim());
        try {
          WP.me.updateUser(user);
        } catch (WeixinException e) {
          renderText(e.getErrorText());
        }
      }
      teacher.set("name", getPara("name").trim())
              .set("mobile", getPara("mobile").trim())
              .update();
      renderText("OK");
    }
  }

  /**
   * 查询
   */
  @Before(AjaxManager.class)
  public void query() {
    renderJson(Teacher.dao.paginate(getParaToInt("pageCurrent"), getParaToInt("pageSize"), "SELECT *", getSQL(getPara("queryString"))).getList());
  }

  /**
   * 计数
   */
  @Before(AjaxManager.class)
  public void total() {
    Long count = Db.queryLong("SELECT COUNT(*) " + getSQL(getPara("queryString")));
    if (count % getParaToInt("pageSize") == 0) {
      renderText((count / getParaToInt("pageSize")) + "");
    } else {
      renderText((count / getParaToInt("pageSize") + 1) + "");
    }
  }

  /**
   * 列表
   */
  @Before(AjaxManager.class)
  public void list() {
    renderJson(Teacher.dao.find("SELECT * FROM teacher WHERE state = 1 OR state = 2 ORDER BY id ASC"));
  }

  @Before(AjaxManager.class)
  public void inactive() {
    Teacher teacher = Teacher.dao.findById(getPara("id"));
    if (teacher == null) {
      renderText("要取消关注的人员不存在!");
    } else if (teacher.get("state").toString().equals("3")) {
      renderText("该人员已处于取消关注状态!");
    } else if (teacher.get("state").toString().equals("2")) {
      renderText("该人员已处于冻结状态!");
    } else {
      try {
        WP.me.deleteUser(teacher.getUserId());
        teacher.set("state", 3).update();
        renderText("OK");
      } catch (WeixinException e) {
        renderText(e.getErrorText());
      }
    }
  }

  @Before(AjaxManager.class)
  public void active() {
    Teacher teacher = Teacher.dao.findById(getPara("id"));
    if (teacher == null) {
      renderText("要重新邀请关注的人员不存在!");
    } else if (teacher.get("state").toString().equals("1")) {
      renderText("该人员已处于关注状态!");
    } else {
      User user = new User(teacher.get("userId").toString(), teacher.get("name").toString());
      user.setMobile(teacher.get("mobile").toString());
      user.setEmail(teacher.get("email").toString());
      user.setWeixinId(teacher.get("weixinId").toString());
      user.setPartyIds(1);
      try {
        WP.me.createUser(user);
        List<String> userIds = new ArrayList<String>();
        userIds.add(teacher.get("userId").toString());
        WP.me.addTagUsers(ParamesAPI.teacherTagId, userIds, new ArrayList<Integer>());
        if (teacher.get("isManager").toString().trim().equals("1")) {
          WP.me.addTagUsers(ParamesAPI.managerTagId, userIds, new ArrayList<Integer>());
        }
        teacher.set("state", 1).update();
        renderText("OK");
      } catch (WeixinException e) {
        renderText(e.getErrorText());
      }
    }
  }
  @Before(AjaxTeacher.class)
  public void get() {
    renderJson(Teacher.dao.findById(((Teacher) getSessionAttr("teacher")).getId()));
  }

  /**
   * 检测姓名
   */
  @Before(AjaxManager.class)
  public void checkName() {
    if (!Util.getString(getPara("name")).matches("^[\\u4e00-\\u9fa5]{2,}$")) {
      renderText("请输入两个以上的汉字!");
    } else {
      renderText("OK");
    }
  }

  /**
   * 检测手机_新增
   */
  @Before(AjaxManager.class)
  public void checkMobileForAdd() {
    if (!Util.getString(getPara("mobile")).matches("^1(3|4|5|7|8)\\d{9}$")) {
      renderText("手机号码格式错误!");
    } else if (Teacher.dao.find("select * from teacher where mobile=?", getPara("mobile")).size() != 0
            && Parent.dao.find("select * from parent where mobile=?", getPara("mobile")).size() != 0
            ) {
      renderText("该手机号码已存在!");
    } else {
      renderText("OK");
    }
  }

  /**
   * 检测手机_修改
   */
  @Before(AjaxManager.class)
  public void checkMobileForEdit() {
    if (!Util.getString(getPara("mobile")).matches("^1(3|4|5|7|8)\\d{9}$")) {
      renderText("手机号码格式错误!");
    } else if (!Teacher.dao.findById(getPara("id")).get("mobile").equals(getPara("mobile"))
            && Teacher.dao.find("select * from teacher where mobile=?", getPara("mobile")).size() != 0
            && Parent.dao.find("select * from parent where mobile=?", getPara("mobile")).size() != 0
            ) {
      renderText("该手机号码已存在!");
    } else {
      renderText("OK");
    }
  }

  /**
   * 检测邮箱_新增
   */
  @Before(AjaxManager.class)
  public void checkEmailForAdd() {
    if (!Util.getString(getPara("email")).matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\\\.[a-zA-Z0-9_-]{2,3}){1,2})$")) {
      renderText("电子邮箱格式错误!");
    } else if (Teacher.dao.find("select * from teacher where email=?", getPara("email")).size() != 0
            && Parent.dao.find("select * from parent where email=?", getPara("email")).size() != 0
            ) {
      renderText("该电子邮箱已存在!");
    } else {
      renderText("OK");
    }
  }

  /**
   * 检测邮箱_修改
   */
  @Before(AjaxManager.class)
  public void checkEmailForEdit() {
    if (!Util.getString(getPara("email")).matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\\\.[a-zA-Z0-9_-]{2,3}){1,2})$")) {
      renderText("电子邮箱格式错误!");
    } else if (!Teacher.dao.findById(getPara("id")).get("email").equals(getPara("email"))
            && Teacher.dao.find("select * from teacher where email=?", getPara("email")).size() != 0
            && Parent.dao.find("select * from parent where email=?", getPara("email")).size() != 0
            ) {
      renderText("该电子邮箱已存在!");
    } else {
      renderText("OK");
    }
  }
  /**
   * 检测QQ_新增
   */
  @Before(AjaxManager.class)
  public void checkQQForAdd() {
    if (!Util.getString(getPara("qq")).matches("^\\d{5,12}$")) {
      renderText("QQ号格式错误!");
    } else if (Teacher.dao.find("select * from teacher where qq=?", getPara("qq")).size() != 0
            && Parent.dao.find("select * from parent where qq=?", getPara("qq")).size() != 0
            ) {
      renderText("该QQ号已存在!");
    } else {
      renderText("OK");
    }
  }

  /**
   * 检测QQ_修改
   */
  @Before(AjaxManager.class)
  public void checkQQForEdit() {
    if (!Util.getString(getPara("qq")).matches("^\\d{5,12}$")) {
      renderText("QQ号格式错误!");
    } else if (!Teacher.dao.findById(getPara("id")).get("qq").equals(getPara("qq"))
            && Teacher.dao.find("select * from teacher where qq=?", getPara("qq")).size() != 0
            && Parent.dao.find("select * from parent where qq=?", getPara("qq")).size() != 0
            ) {
      renderText("该QQ号已存在!");
    } else {
      renderText("OK");
    }
  }
  /**
   * 检测微信_新增
   */
  @Before(AjaxManager.class)
  public void checkWXForAdd() {
    if (!Util.getString(getPara("wx")).matches("^[a-zA-Z]{1}[-_a-zA-Z0-9]{5,19}+$")) {
      renderText("微信号格式错误!");
    } else if (Teacher.dao.find("select * from teacher where weixinId=?", getPara("wx")).size() != 0
            && Parent.dao.find("select * from parent where weixinId=?", getPara("wx")).size() != 0
            ) {
      renderText("该微信号已存在!");
    } else {
      renderText("OK");
    }
  }

  /**
   * 检测微信_修改
   */
  @Before(AjaxManager.class)
  public void checkWXForEdit() {
    if (!Util.getString(getPara("wx")).matches("^[a-zA-Z]{1}[-_a-zA-Z0-9]{5,19}+$")) {
      renderText("微信号格式错误!");
    } else if (!Teacher.dao.findById(getPara("id")).get("weixinId").equals(getPara("wx"))
            && Teacher.dao.find("select * from teacher where weixinId=?", getPara("wx")).size() != 0
            && Parent.dao.find("select * from parent where weixinId=?", getPara("wx")).size() != 0
            ) {
      renderText("该微信号已存在!");
    } else {
      renderText("OK");
    }
  }
    /**
   * 检测账号
   * */
  @Before(AjaxManager.class)
  public void checkUserId() {
    if (!Util.getString(getPara("userId")).matches("^[A-Za-z0-9]+$")) {
      renderText("账号名应为字母或数字的组合!");
    } else if (Teacher.dao.find("select * from teacher where userId=?", getPara("userId")).size()!=0
            && Parent.dao.find("select * from parent where userId=?", getPara("userId")).size() != 0
            ) {
      renderText("该账号已存在");
    } else {
      renderText("OK");
    }
  }
}
