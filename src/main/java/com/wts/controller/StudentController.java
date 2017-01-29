package com.wts.controller;

import com.foxinmy.weixin4j.exception.WeixinException;
import com.foxinmy.weixin4j.qy.model.User;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.wts.entity.WP;
import com.wts.entity.model.Enterprise;
import com.wts.entity.model.Room;
import com.wts.entity.model.Student;
import com.wts.entity.model.Team;
import com.wts.interceptor.AjaxFunction;
import com.wts.util.Util;

import java.util.List;

import static com.wts.util.IDNumber.*;
import static com.wts.util.Util.getString;

public class StudentController extends Controller {

    public void index() throws WeixinException {
        // 检测session中是否存在teacher
        if (getSessionAttr("teacher") == null) {
            // 检测cookie中是否存在EnterpriseId
            if (getCookie("die") == null || getCookie("die").equals("")) {
                // 检测是否来自微信请求
                if (!(getPara("code") == null || getPara("code").equals(""))) {
                    User user = WP.me.getUserByCode(getPara("code"));
                    Enterprise teacher = Enterprise.dao.findFirst("select * from enterprise where state=1 and userId=?", user.getUserId());
                    setSessionAttr("teacher", teacher);
                    render("/static/StudentManage.html");
                } else {
                    redirect("/");
                }
            } else {
                Enterprise teacher = Enterprise.dao.findById(getCookie("die"));
                setSessionAttr("teacher", teacher);
                render("/static/StudentManage.html");
            }
        } else {
            render("/static/StudentManage.html");
        }
    }
    @Before(AjaxFunction.class)
    public  void studentList() {
        List<Student> students = Student.dao.find("select * from student where room_id=?",getPara("id"));
        renderJson(students);
    }
    @Before(AjaxFunction.class)
    public void getById() {
        Student student = Student.dao.findById(getPara("id"));
        renderJson(student);
    }

    @Before(AjaxFunction.class)
    public void getNameById() {
        Student student = Student.dao.findById(getPara("id"));
        renderText(student.get("name").toString());
    }
    @Before(AjaxFunction.class)
    public void inactiveById() {
        Student student = Student.dao.findById(getPara("id"));
        if (student == null) {
            renderText("未找到指定id的学生");
        } else if (student.get("state").toString().equals("2")) {
            renderText("该学生已处于注销状态!");
        } else {
            student.set("state",2).update();
            renderText("OK");
        }
    }
    @Before(AjaxFunction.class)
    public void activeById() {
        Student student = Student.dao.findById(getPara("id"));
        if (student == null) {
            renderText("未找到指定id的学生");
        } else if (student.get("state").toString().equals("1")) {
            renderText("该学生已处于激活状态!");
        } else {
            student.set("state",1).update();
            renderText("OK");
        }
    }
    @Before(AjaxFunction.class)
    public void queryByName() {
        Page<Student> students= Student.dao.studentQuery(getParaToInt("pageCurrent"),getParaToInt("pageSize"),getPara("queryString"));
        renderJson(students.getList());
    }
    @Before(AjaxFunction.class)
    public void totalByName() {
        Long count = Db.queryLong("select count(*) from student where (name like '%"+ getPara("queryString") +"%' or number LIKE '%"+getPara("queryString")+"%' or code LIKE '%"+getPara("queryString")+"%') ORDER BY name ASC");
        if (count%getParaToInt("pageSize")==0) {
            renderText((count/getParaToInt("pageSize"))+"");
        } else {
            renderText((count/getParaToInt("pageSize")+1)+"");
        }
    }
    /**
     * 新增时检测学生证件号码
     * */
    @Before(AjaxFunction.class)
    public void checkNumberForNew() {
        if (!checkIDNumberDetailB(getPara("number"))){
            renderText(checkIDNumberDetail(getPara("number")));
        } else if (Student.dao.find("select * from student where number=?", getPara("number")).size()!=0) {
            renderText("该证件号码已存在!");
        } else {
            renderText("OK");
        }
    }
    /**
     * 修改时检测学生证件号码
     * */
    @Before(AjaxFunction.class)
    public void checkNumberForEdit() {
        if (!checkIDNumberDetailB(getPara("number"))){
            renderText(checkIDNumberDetail(getPara("number")));
        } else if (!Student.dao.findById(getPara("id")).get("number").equals(getPara("number"))
                && Student.dao.find("select * from student where number=?", getPara("number")).size()>=1) {
            renderText("该证件号码已存在!");
        } else {
            renderText("OK");
        }
    }
    /**
     * 新增时检测学生学籍号
     * */
    @Before(AjaxFunction.class)
    public void checkCodeForNew() {
        renderText("OK");
    }
    /**
     * 修改时检测学生学籍号
     * */
    @Before(AjaxFunction.class)
    public void checkCodeForEdit() {
        renderText("OK");
    }

    @Before({Tx.class,AjaxFunction.class})
    public void save()  {
//        if (!getString(getPara("name")).matches("^[\\u4e00-\\u9fa5]{2,}$")) {
//            renderText("请输入两个以上汉字!");
        if (!checkIDNumberDetailB(getPara("number"))){
            renderText(checkIDNumberDetail(getPara("number")));
        } else if (Student.dao.find("select * from student where number=?", getPara("number")).size()!=0) {
            renderText("该证件号码已存在!");
        } else {
            Student student = new Student();
            if (!getString(getPara("room_id")).equals("")) {
                student.set("room_id",getPara("room_id"));
            }
            if (!getString(getPara("team_id")).equals("")) {
                student.set("team_id",getPara("team_id"));
            }
            student.set("name",getPara("name"))
                    .set("number",getPara("number"))
                    .set("code",getPara("code"))
                    .set("address", getPara("address"))
                    .set("sex",getSex(getPara("number")))
                    .set("birth",getBirthDate(getPara("number")))
                    .set("state",1)
                    .save();
            renderText("OK");
        }
    }

    @Before({Tx.class, AjaxFunction.class})
    public void edit() {
        Student student = Student.dao.findById(getPara("id"));
        String room,team;
        if (student == null) {
            renderText("要修改的学生不存在!");
        } else {
            if (student.get("room_id")==null){
                room="";
            }else{
                room=student.get("room_id").toString();
            }
            if (student.get("team_id")==null){
                team="";
            }else{
                team=student.get("team_id").toString();
            }
            if (getString(student.getStr("name")).equals(getPara("name").trim())
                    && getString(student.getStr("number")).equals(getPara("number").trim())
                    && getString(student.getStr("code")).equals(getPara("code").trim())
                    && (getString(student.getStr("address")).equals(getPara("address").trim())
                    || ((student.getStr("address"))==null && getPara("address")==null))
                    && room.equals(getString(getPara("room_id")).trim())
                    && team.equals(getString(getPara("team_id")).trim())
                    ) {
                renderText("未找到修改内容!");
//            } else if (!getString(getPara("name")).matches("^[\\u4e00-\\u9fa5]{2,}$")) {
//                renderText("请输入两个以上汉字!");
            } else if (!checkIDNumberDetailB(getPara("number"))) {
                renderText(checkIDNumberDetail(getPara("number")));
            } else if (!student.getStr("number").equals(getPara("number"))
                    && Student.dao.find("select * from student where number=?", getPara("number")).size() != 0) {
                renderText("该证件号码已存在!");
            } else {
                if (!getString(getPara("room_id")).equals("")) {
                    student.set("room_id", getPara("room_id"));
                }
                if (!getString(getPara("team_id")).equals("")) {
                    student.set("team_id",getPara("team_id"));
                }
                student.set("name", getPara("name"))
                        .set("number", getPara("number"))
                        .set("code", getPara("code"))
                        .set("address", getPara("address"))
                        .set("sex", getSex(getPara("number")))
                        .set("birth", getBirthDate(getPara("number")))
                        .set("state", 1)
                        .update();
                renderText("OK");
            }
        }
    }
}
