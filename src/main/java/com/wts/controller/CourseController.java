package com.wts.controller;

import com.foxinmy.weixin4j.exception.WeixinException;
import com.foxinmy.weixin4j.qy.model.User;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.wts.entity.WP;
import com.wts.entity.model.Course;
import com.wts.entity.model.Teacher;
import com.wts.interceptor.Ajax;
import com.wts.interceptor.AjaxManager;
import com.wts.interceptor.Login;

import java.util.List;

import static com.wts.util.Util.getString;

public class CourseController extends Controller {
    /**
     * 登录移动_管理_课程
     */
    public void Mobile_Manager_Course() throws WeixinException {
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
                        render("/static/html/mobile/manager/Mobile_Manager_Course.html");
                    } else {
                        redirect("/");
                    }
                } else {
                    redirect("/");
                }
            } else {
                Teacher manager = Teacher.dao.findById(getCookie("dim"));
                setSessionAttr("manager", manager);
                render("/static/html/mobile/manager/Mobile_Manager_Course.html");
            }
        } else {
            render("/static/html/mobile/manager/Mobile_Manager_Course.html");
        }
    }
    /**
     * 登录移动_教师_课程
     */
    public void Mobile_Teacher_Course() throws WeixinException {
        if (getSessionAttr("teacher") == null) {
            if (getCookie("dit") == null || getCookie("dim").equals("")) {
                if (!(getPara("code") == null || getPara("code").equals(""))) {
                    User user = WP.me.getUserByCode(getPara("code"));
                    Teacher teacher = Teacher.dao.findFirst("SELECT * FROM teacher WHERE userId = ? AND state = ? ", user.getUserId(), 1);
                    if (teacher != null) {
                        setSessionAttr("teacher", teacher);
                        setCookie("dit", teacher.getId().toString(), 60 * 30);
                        if (user.getAvatar().equals(teacher.getPicUrl())) {
                            teacher.set("picUrl", user.getAvatar()).update();
                        }
                        render("/static/html/mobile/teacher/Mobile_Teacher_Course.html");
                    } else {
                        redirect("/");
                    }
                } else {
                    redirect("/");
                }
            } else {
                Teacher teacher = Teacher.dao.findById(getCookie("dit"));
                setSessionAttr("teacher", teacher);
                render("/static/html/mobile/teacher/Mobile_Teacher_Course.html");
            }
        } else {
            render("/static/html/mobile/teacher/Mobile_Teacher_Course.html");
        }
    }

    /**
     * 登录电脑_管理_课程
     */
    public void PC_Manager_Course() {
        render("/static/html/pc/manager/PC_Manager_Course.html");
//        // 未登录
//        if (getSessionAttr("teacher") == null) {
//            // cookie过期
//            if (getCookie("dim").equals("")) {
//                redirect("/pc");
//            } else {
//                Teacher teacher = Teacher.dao.findById(getCookie("dim"));
//                setSessionAttr("teacher", teacher);
//                render("/static/html/pc/manager/PC_Manager_Course.html");
//            }
//        } else {
//            render("/static/html/pc/manager/PC_Manager_Course.html");
//        }
    }



    /**
     * 列表
     */
    @Before(AjaxManager.class)
    public void list() {
        renderJson(Course.dao.find("SELECT * FROM course WHERE state = ? AND type= ? ",getPara("state"),getPara("type")));
    }

    /**
     * 查询
     */
    @Before(AjaxManager.class)
    public void query() {
        renderJson(Course.dao.paginate(getParaToInt("pageCurrent"), getParaToInt("pageSize"), "SELECT *", "FROM course WHERE name LIKE '%" + getPara("queryString") + "%' OR detail LIKE '%" + getPara("queryString") + "%' ORDER BY id ASC").getList());
    }

    /**
     * 计数
     */
    @Before(AjaxManager.class)
    public void total() {
        Long count = Db.queryLong("SELECT COUNT(*) FROM course WHERE name LIKE '%" + getPara("queryString") + "%' OR detail LIKE '%" + getPara("queryString") + "%'");
        if (count % getParaToInt("pageSize") == 0) {
            renderText((count / getParaToInt("pageSize")) + "");
        } else {
            renderText((count / getParaToInt("pageSize") + 1) + "");
        }
    }

    /**
     * 获取
     */
    @Before({Login.class, Ajax.class})
    public void get() {
        renderJson(Course.dao.findById(getPara("id")));
    }

    /**
     * 激活
     */
    @Before({Tx.class, AjaxManager.class})
    public void active() {
        Course course = Course.dao.findById(getPara("id"));
        if (course == null) {
            renderText("要重新激活的课程不存在!");
        } else if (course.get("state").toString().equals("1")) {
            renderText("该课程已激活!");
        } else {
            course.set("state", 1).update();
            renderText("OK");
        }
    }

    /**
     * 注销
     */
    @Before({Tx.class, AjaxManager.class})
    public void inactive() {
        Course course = Course.dao.findById(getPara("id"));
        if (course == null) {
            renderText("要重新注销的课程不存在!");
        } else if (course.get("state").toString().equals("0")) {
            renderText("该课程已注销!");
        } else {
            course.set("state", 0).update();
            renderText("OK");
        }
    }

    /**
     * 检测名称_新增
     */
    @Before(AjaxManager.class)
    public void checkNameForAdd() {
        if (Course.dao.find("SELECT * FROM course WHERE name = ?", getPara("name")).size() != 0) {
            renderText("该课程名称已存在!");
        } else {
            renderText("OK");
        }
    }

    /**
     * 检测名称_修改
     */
    @Before(AjaxManager.class)
    public void checkNameForEdit() {
        if (!Course.dao.findById(getPara("id")).get("name").equals(getPara("name"))
                && Course.dao.find("SELECT * FROM course WHERE name = ?", getPara("name")).size() != 0) {
            renderText("该课程名称已存在!");
        } else {
            renderText("OK");
        }
    }

    /**
     * 保存
     */
    @Before({Tx.class, AjaxManager.class})
    public void save() {
        if (Course.dao.find("SELECT * FROM course WHERE name = ?", getPara("name")).size() != 0) {
            renderText("该名称已存在!");
        } else {
            Course course = new Course();
            course.set("name", getPara("name"))
                    .set("detail", getPara("detail"))
                    .set("type", getPara("type"))
                    .set("state", 1)
                    .save();
            renderText("OK");
        }
    }

    /**
     * 修改
     */
    @Before({Tx.class, AjaxManager.class})
    public void edit() {
        Course course = Course.dao.findById(getPara("id"));
        if (course == null) {
            renderText("要修改的课程不存在!");
        } else {
            if (!getString(course.getStr("name")).equals(getPara("name"))
                    && Course.dao.find("SELECT * FROM course WHERE name = ?", getPara("name")).size() != 0) {
                renderText("该名称已存在!");
            } else {
                course.set("name", getPara("name"))
                        .set("detail", getPara("detail"))
                        .set("type", getPara("type"))
                        .update();
                renderText("OK");
            }
        }
    }


    /**
     * 获取全选时的字符串
     */
    @Before(AjaxManager.class)
    public void all() {
        List<Course> courses = Course.dao.find("SELECT id FROM course WHERE state=1");
        String course = "";
        if (courses.size() > 0) {
            for (Course c : courses) {
                course = course + c.getId() + ",";
            }
            renderJson("[" + course.substring(0, course.length() - 1) + "]");
        } else {
            renderJson("[]");
        }
    }
}
