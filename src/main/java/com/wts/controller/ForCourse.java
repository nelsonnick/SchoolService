//package com.wts.controller;
//
//import com.foxinmy.weixin4j.exception.WeixinException;
//import com.foxinmy.weixin4j.qy.model.User;
//import com.jfinal.aop.Before;
//import com.jfinal.core.Controller;
//import com.jfinal.plugin.activerecord.Db;
//import com.jfinal.plugin.activerecord.tx.Tx;
//import com.wts.entity.WP;
//import com.wts.entity.model.Course;
//import com.wts.entity.model.Teacher;
//import com.wts.interceptor.AjaxManager;
//import com.wts.interceptor.AjaxTeacher;
//
//import java.util.List;
//
//public class ForCourse extends Controller {
//    /**
//     * 登录移动_管理_课程
//     */
//    public void Mobile_Manager_Course() throws WeixinException {
//        if (getSessionAttr("manager") == null || ((Teacher) getSessionAttr("manager")).getIsManager() != 1) {
//            if (getCookie("dim") == null || getCookie("dim").equals("")) {
//                if (!(getPara("code") == null || getPara("code").equals(""))) {
//                    User user = WP.me.getUserByCode(getPara("code"));
//                    Teacher manager = Teacher.dao.findFirst("SELECT * FROM Teacher WHERE userId = ? AND state = ? AND isManager = 1", user.getUserId(), 1);
//                    if (manager != null) {
//                        setSessionAttr("manager", manager);
//                        setCookie("dim", manager.getId().toString(), 60 * 30);
//                        if (user.getAvatar().equals(manager.getPicUrl())) {
//                            manager.set("picUrl", user.getAvatar()).update();
//                        }
//                        render("/static/html/mobile/manager/Mobile_Manager_Course.html");
//                    } else {
//                        redirect("/");
//                    }
//                } else {
//                    redirect("/");
//                }
//            } else {
//                Teacher manager = Teacher.dao.findById(getCookie("dim"));
//                setSessionAttr("manager", manager);
//                render("/static/html/mobile/manager/Mobile_Manager_Course.html");
//            }
//        } else {
//            render("/static/html/mobile/manager/Mobile_Manager_Course.html");
//        }
//    }
//    /**
//     * 登录移动_教师_课程
//     */
//    public void Mobile_Teacher_Course() throws WeixinException {
//        if (getSessionAttr("Teacher") == null) {
//            if (getCookie("dit") == null || getCookie("dim").equals("")) {
//                if (!(getPara("code") == null || getPara("code").equals(""))) {
//                    User user = WP.me.getUserByCode(getPara("code"));
//                    Teacher Teacher = Teacher.dao.findFirst("SELECT * FROM Teacher WHERE userId = ? AND state = ? ", user.getUserId(), 1);
//                    if (Teacher != null) {
//                        setSessionAttr("Teacher", Teacher);
//                        setCookie("dit", Teacher.getId().toString(), 60 * 30);
//                        if (user.getAvatar().equals(Teacher.getPicUrl())) {
//                            Teacher.set("picUrl", user.getAvatar()).update();
//                        }
//                        render("/static/html/mobile/Teacher/Mobile_Teacher_Course.html");
//                    } else {
//                        redirect("/");
//                    }
//                } else {
//                    redirect("/");
//                }
//            } else {
//                Teacher Teacher = Teacher.dao.findById(getCookie("dit"));
//                setSessionAttr("Teacher", Teacher);
//                render("/static/html/mobile/Teacher/Mobile_Teacher_Course.html");
//            }
//        } else {
//            render("/static/html/mobile/Teacher/Mobile_Teacher_Course.html");
//        }
//    }
//
//
//
//
//    /**
//     * 列表
//     */
//    @Before(AjaxManager.class)
//    public void list() {
//        renderJson(Course.dao.find("SELECT * FROM Course WHERE state = ? AND type= ? ",getPara("state"),getPara("type")));
//    }
//
//
//    /**
//     * 手机_查询
//     */
//    public void Mobile_Query() {
//        renderJson(Course.dao.paginate(
//                getParaToInt("pageCurrent"),
//                getParaToInt("pageSize"),
//                "SELECT *, " +
//                        "(case type when 1 then '必修课' when 2 then '选修课' else '错误' end ) as tname, " +
//                        "(case state when 1 then '可用' when 2 then '停用' else '错误' end ) as sname ",
//                "FROM Course WHERE name LIKE '%" + getPara("keyword") + "%' OR detail LIKE '%" + getPara("keyword") + "%' ORDER BY id ASC").getList());
//    }
//
//
//    /**
//     * 手机_计数
//     */
//    public void Mobile_Total() {
//        Long count = Db.queryLong("SELECT COUNT(*) FROM Course WHERE name LIKE '%" + getPara("queryString") + "%' OR detail LIKE '%" + getPara("queryString") + "%'");
//        if (count % getParaToInt("pageSize") == 0) {
//            renderText((count / getParaToInt("pageSize")) + "");
//        } else {
//            renderText((count / getParaToInt("pageSize") + 1) + "");
//        }
//    }
//    /**
//     * 获取
//     */
//    public void get() {
//        renderJson(Course.dao.findById(getPara("id")));
//    }
//
//    /**
//     * 激活
//     */
//    @Before(AjaxTeacher.class)
////    @Before({Tx.class, AjaxManager.class})
//    public void active() {
//        Course Course = Course.dao.findById(getPara("id"));
//        if (Course == null) {
//            renderText("要重新激活的课程不存在!");
//        } else if (Course.get("state").toString().equals("1")) {
//            renderText("该课程已激活!");
//        } else {
//            Course.set("state", 1).update();
//            renderText("OK");
//        }
//    }
//
//    /**
//     * 注销
//     */
////    @Before({Tx.class, AjaxManager.class})
//    public void inactive() {
//        Course Course = Course.dao.findById(getPara("id"));
//        if (Course == null) {
//            renderText("要重新注销的课程不存在!");
//        } else if (Course.get("state").toString().equals("0")) {
//            renderText("该课程已注销!");
//        } else {
//            Course.set("state", 0).update();
//            renderText("OK");
//        }
//    }
//
//    /**
//     * 检测名称_新增
//     */
//
//    public void checkNameForAdd() {
//        if (Course.dao.find("SELECT * FROM Course WHERE name = ?", getPara("name")).size() != 0) {
//            renderText("该课程名称已存在!");
//        } else {
//            renderText("OK");
//        }
//    }
//
//    /**
//     * 检测名称_修改
//     */
//    public void checkNameForEdit() {
//        if (!Course.dao.findById(getPara("id")).get("name").equals(getPara("name"))
//                && Course.dao.find("SELECT * FROM Course WHERE name = ?", getPara("name")).size() != 0) {
//            renderText("该课程名称已存在!");
//        } else {
//            renderText("OK");
//        }
//    }
//
//    /**
//     * 保存
//     */
//    @Before({Tx.class, AjaxManager.class})
//    public void save() {
//        if (Course.dao.find("SELECT * FROM Course WHERE name = ?", getPara("name")).size() != 0) {
//            renderText("该名称已存在!");
//        } else {
//            Course Course = new Course();
//            Course.set("name", getPara("name"))
//                    .set("detail", getPara("detail"))
//                    .set("type", getPara("type"))
//                    .set("state", 1)
//                    .save();
//            renderText("OK");
//        }
//    }
//
//
//
//
//    /**
//     * 获取全选时的字符串
//     */
//    @Before(AjaxManager.class)
//    public void all() {
//        List<Course> courses = Course.dao.find("SELECT id FROM Course WHERE state=1");
//        String Course = "";
//        if (courses.size() > 0) {
//            for (Course c : courses) {
//                Course = Course + c.getId() + ",";
//            }
//            renderJson("[" + Course.substring(0, Course.length() - 1) + "]");
//        } else {
//            renderJson("[]");
//        }
//    }
//
//}
