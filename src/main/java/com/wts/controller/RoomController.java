package com.wts.controller;

import com.foxinmy.weixin4j.exception.WeixinException;
import com.foxinmy.weixin4j.qy.model.User;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.wts.entity.WP;
import com.wts.entity.model.*;
import com.wts.interceptor.AjaxFunction;
import com.wts.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.jfinal.plugin.activerecord.Db.find;

public class RoomController extends Controller {

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
          render("/static/RoomManage.html");
        } else {
          redirect("/");
        }
      } else {
        Enterprise teacher = Enterprise.dao.findById(getCookie("die"));
        setSessionAttr("teacher", teacher);
        render("/static/RoomManage.html");
      }
    } else {
      render("/static/RoomManage.html");
    }
  }
  @Before(AjaxFunction.class)
  public void queryByName() {
    Page<Room> rooms= Room.dao.queryByName(getParaToInt("pageCurrent"),getParaToInt("pageSize"),getPara("queryString"));
    renderJson(rooms.getList());
  }
  @Before(AjaxFunction.class)
  public void totalByName() {
    Long count = Db.queryLong("select count(*) from room where name like '%"+ getPara("queryString") +"%'");
    if (count%getParaToInt("pageSize")==0) {
      renderText((count/getParaToInt("pageSize"))+"");
    } else {
      renderText((count/getParaToInt("pageSize")+1)+"");
    }
  }
  @Before(AjaxFunction.class)
  public void checkNameForNew() {
    if (!getPara("name").matches("\\d{4}[\\u7ea7]\\d{1,2}[\\u73ed]")) {
      renderText("班级名称格式应为：XXXX级XX班");
    } else if (Room.dao.find("select * from room where name=?", getPara("name")).size()!=0) {
      renderText("该班级已存在!");
    } else {
      renderText("OK");
    }
  }
  @Before(AjaxFunction.class)
  public void checkNameForEdit() {
    if (!getPara("name").matches("\\d{4}[\\u7ea7]\\d{1,2}[\\u73ed]")) {
      renderText("班级名称格式应为：XXXX级XX班");
    } else if (!Room.dao.findById(getPara("id")).getName().equals(getPara("name"))
            && Room.dao.find("select * from room where name=?", getPara("name")).size() != 0) {
      renderText("该班级已存在!");
    } else {
      renderText("OK");
    }
  }
  @Before(AjaxFunction.class)
  public void getById() {
    Room room = Room.dao.findById(getPara("id"));
    renderJson(room);
  }
  @Before(AjaxFunction.class)
  public void deleteById() {
    if (Room.dao.findById(getPara("id")) == null) {
      renderText("未找到指定id的班级");
    } else {
      Room room = Room.dao.findById(getPara("id"));
      room.set("state",2).update();
      renderText("OK");
    }
  }
  @Before(AjaxFunction.class)
  public void teacherList() {
    List<Enterprise> teachers = Enterprise.dao.find("select * from enterprise where (isTeacher=1 or isManager=1) and (state=1 or state=2)");
    renderJson(teachers);
  }
  @Before(AjaxFunction.class)
  public  void courseList() {
    List<Course> courses = Course.dao.find("select * from course");
    renderJson(courses);
  }

  public void getCourseTeachers() {
    List<Courseplan> courseplan = Courseplan.dao.find("select * from courseplan where room_id=? and course_id=?",getPara("room"),getPara("course"));
    if (courseplan.size()!=0) {
      String sp1 = "";
      for (int i = 0; i < courseplan.size(); i++) {
        sp1 = sp1 + "'" + courseplan.get(i).get("teacher_id") + "',";
      }
      renderText("{course: [" + sp1.substring(0,sp1.length()-1) + "]}");
    } else {
      renderText("{}");
    }
  }
  @Before(AjaxFunction.class)
  public void getCourseTeacher() {
    List<Courseplan> courseplan1 = Courseplan.dao.find("select * from courseplan where room_id=? and course_id=1",getPara("room"));
    List<Courseplan> courseplan2 = Courseplan.dao.find("select * from courseplan where room_id=? and course_id=2",getPara("room"));
    List<Courseplan> courseplan3 = Courseplan.dao.find("select * from courseplan where room_id=? and course_id=3",getPara("room"));
    List<Courseplan> courseplan4 = Courseplan.dao.find("select * from courseplan where room_id=? and course_id=4",getPara("room"));
    String sp1 = "";
    String sp2 = "";
    String sp3 = "";
    String sp4 = "";

    if (courseplan1.size()!=0) {
      for (int i = 0; i < courseplan1.size(); i++) {
        sp1 = sp1 + "'" + courseplan1.get(i).get("teacher_id") + "',";
      }
      sp1 = "course1: [" + sp1.substring(0,sp1.length()-1) + "]";
    }
    if (courseplan2.size()!=0) {
      for (int i = 0; i < courseplan2.size(); i++) {
        sp2 = sp2 + "'" + courseplan2.get(i).get("teacher_id") + "',";
      }
      sp2 = "course2: [" + sp2.substring(0,sp2.length()-1) + "]";
    }
    if (courseplan3.size()!=0) {
      for (int i = 0; i < courseplan3.size(); i++) {
        sp3 = sp3 + "'" + courseplan3.get(i).get("teacher_id") + "',";
      }
      sp3 = "course3: [" + sp3.substring(0,sp3.length()-1) + "]";
    }
    if (courseplan4.size()!=0) {
      for (int i = 0; i < courseplan4.size(); i++) {
        sp4 = sp4 + "'" + courseplan4.get(i).get("teacher_id") + "',";
      }
      sp4 = "course4: [" + sp4.substring(0,sp4.length()-1) + "]";
    }
    renderText("{"+sp1+","+sp2+","+sp3+","+sp4+"}");
  }
  @Before({Tx.class,AjaxFunction.class})
  public void save()  {
    if (!getPara("name").matches("\\d{4}[\\u7ea7]\\d{1,2}[\\u73ed]")) {
      renderText("班级名称格式应为：XXXX级XX班");
    } else if (Room.dao.find("select * from room where name=?", getPara("name")).size()!=0) {
      renderText("该班级已存在!");
    } else {
      Room room = new Room();
      room.set("name",getPara("name")).set("state",1).save();
      String[] course1 = getParaValues("course1[]");
      String[] course2 = getParaValues("course2[]");
      String[] course3 = getParaValues("course3[]");
      String[] course4 = getParaValues("course4[]");
      if (course1!=null){
        for (String i : course1){
          Courseplan courseplan = new Courseplan();
          courseplan.set("course_id",1).set("room_id",room.get("id")).set("teacher_id",i).save();
        }
      }
      if (course2!=null){
        for (String i : course2){
          Courseplan courseplan = new Courseplan();
          courseplan.set("course_id",2).set("room_id",room.get("id")).set("teacher_id",i).save();
        }
      }
      if (course3!=null){
        for (String i : course3){
          Courseplan courseplan = new Courseplan();
          courseplan.set("course_id",3).set("room_id",room.get("id")).set("teacher_id",i).save();
        }
      }
      if (course4!=null){
        for (String i : course4){
          Courseplan courseplan = new Courseplan();
          courseplan.set("course_id",4).set("room_id",room.get("id")).set("teacher_id",i).save();
        }
      }
      renderText("OK");
    }
  }
  @Before({Tx.class,AjaxFunction.class})
  public void edit()  {
    Room room = Room.dao.findById(getPara("id"));
    if (room == null) {
      renderText("要修改的班级不存在!");
    } else {
      if (!getPara("name").matches("\\d{4}[\\u7ea7]\\d{1,2}[\\u73ed]")) {
        renderText("班级名称格式应为：XXXX级XX班");
      } else if (!Util.getString(room.getStr("name")).equals(getPara("name"))
              &&  Room.dao.find("select * from room where name=?", getPara("name")).size()!=0) {
        renderText("该班级已存在!");
      } else {
        room.set("name",getPara("name")).update();
        Db.update("delete from courseplan where room_id = ?", getPara("id"));
        String[] course1 = getParaValues("course1[]");
        String[] course2 = getParaValues("course2[]");
        String[] course3 = getParaValues("course3[]");
        String[] course4 = getParaValues("course4[]");
        if (course1!=null){
          for (String i : course1){
            Courseplan courseplan = new Courseplan();
            courseplan.set("course_id",1).set("room_id",room.get("id")).set("teacher_id",i).save();
          }
        }
        if (course2!=null){
          for (String i : course2){
            Courseplan courseplan = new Courseplan();
            courseplan.set("course_id",2).set("room_id",room.get("id")).set("teacher_id",i).save();
          }
        }
        if (course3!=null){
          for (String i : course3){
            Courseplan courseplan = new Courseplan();
            courseplan.set("course_id",3).set("room_id",room.get("id")).set("teacher_id",i).save();
          }
        }
        if (course4!=null){
          for (String i : course4){
            Courseplan courseplan = new Courseplan();
            courseplan.set("course_id",4).set("room_id",room.get("id")).set("teacher_id",i).save();
          }
        }
        renderText("OK");
      }
    }
  }
}
