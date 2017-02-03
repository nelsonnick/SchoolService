package com.wts.controller;

import com.foxinmy.weixin4j.exception.WeixinException;
import com.foxinmy.weixin4j.qy.message.NotifyMessage;
import com.foxinmy.weixin4j.qy.model.IdParameter;
import com.foxinmy.weixin4j.qy.model.User;
import com.foxinmy.weixin4j.tuple.Text;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.template.ext.directive.Str;
import com.wts.entity.WP;
import com.wts.entity.model.*;
import com.wts.interceptor.AjaxManager;
import com.wts.interceptor.AjaxTeacher;
import com.wts.util.ParamesAPI;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class RoomworkController extends Controller {
    private static String BASIC = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+ParamesAPI.corpId+"&redirect_uri=http%3a%2f%2f"+ParamesAPI.URL+"%2f"+"XXXXX"+"&response_type=code&scope=snsapi_base&state=1#wechat_redirect";

    public void forTeacher() throws WeixinException {
        // 检测session中是否存在teacher
        if (getSessionAttr("teacher") == null || ((Enterprise) getSessionAttr("teacher")).getIsTeacher() != 1) {
            // 检测cookie中是否存在EnterpriseId
            if (getCookie("die") == null || getCookie("die").equals("")) {
                // 检测是否来自微信请求
                if (!(getPara("code") == null || getPara("code").equals(""))) {
                    User user = WP.me.getUserByCode(getPara("code"));
                    Enterprise enterprise = Enterprise.dao.findFirst("select * from enterprise where state=1 and isTeacher=1 and userId=?", user.getUserId());
                    // 检测是否有权限
                    if (enterprise != null) {
                        setSessionAttr("teacher", enterprise);
                        setCookie("die", enterprise.getId().toString(), 60 * 30);
                        render("/static/RoomworkForTeacher.html");
                    } else {
                        redirect("/");
                    }
                } else {
                    redirect("/");
                }
            } else {
                Enterprise enterprise = Enterprise.dao.findById(getCookie("die"));
                setSessionAttr("teacher", enterprise);
                render("/static/RoomworkForTeacher.html");
            }
        } else {
            render("/static/RoomworkForTeacher.html");
        }
    }
    @Before(AjaxTeacher.class)
    public void getById() {
        Roomwork roomwork = Roomwork.dao.findById(getPara("id"));
        renderJson(roomwork);
    }
    @Before(AjaxTeacher.class)
    public void queryByRoomId() {
        Page<Record> roomworks = Db.paginate(getParaToInt("pageCurrent"), getParaToInt("pageSize"), "SELECT roomwork.*,course.name", "FROM roomwork left join course on roomwork.course_id=course.id WHERE roomwork.room_id = "+ getPara("roomId") +" and roomwork.content LIKE '%"+getPara("queryString")+"%' and roomwork.teacher_id = "+ ((Enterprise) getSessionAttr("teacher")).getId() +" ORDER BY course.id DESC");
        renderJson(roomworks.getList());
    }
    @Before(AjaxTeacher.class)
    public void totalByRoomId() {
        Long count = Db.queryLong("select count(*) from roomwork where room_id = "+ getPara("roomId") + " and content like '%"+ getPara("queryString") +"%' and roomwork.teacher_id = "+ ((Enterprise) getSessionAttr("teacher")).getId());
        if (count%getParaToInt("pageSize")==0) {
            renderText((count/getParaToInt("pageSize"))+"");
        } else {
            renderText((count/getParaToInt("pageSize")+1)+"");
        }
    }
    @Before({Tx.class,AjaxTeacher.class})
    public void save(){
        if (getPara("content").length()>500){
            renderText("输入内容超过500字符!");
        }else{
            Roomwork roomwork = new Roomwork();
            roomwork.set("content",getPara("content"))
                    .set("room_id",getPara("room_id"))
                    .set("course_id",getPara("course_id"))
                    .set("state",1)
                    .set("time",new Date())
                    .set("teacher_id",((Enterprise) getSessionAttr("teacher")).getId())
                    .save();
            String[] studentId = getParaValues("student_id[]");
            List<Relation> relations = Relation.dao.find("select parent_id from relation where identity_id=999");
            for (String i : studentId) {
                if (Relation.dao.find("select parent_id from relation where student_id=?", i).size() != 0) {
                    relations.addAll(Relation.dao.find("select parent_id from relation where student_id=?", i));
                }
            }
            List<Relation> relationNew = new LinkedList<Relation>();
            for(Relation s: relations){
                if(Collections.frequency(relationNew, s) < 1) relationNew.add(s);
            }
            IdParameter idParameter = new IdParameter();
            for (Relation relation :relationNew){
                if (Enterprise.dao.findById(relation.getParentId()).getState()==1){
                    idParameter.putUserIds(Enterprise.dao.findById(relation.getParentId()).getUserId());
                    Roomworkread roomworkread = new Roomworkread();
                    roomworkread.set("roomwork_id",roomwork.getId())
                            .set("parent_id",relation.getParentId())
                            .set("state",0)
                            .save();
                }
            }
            SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //格式化当前系统日期
            String roomworkRead = BASIC.replaceAll("XXXXX","roomworkread%2freadRoomwork%3froomworkId%3d"+roomwork.getId().toString());
            StringBuffer buffer = new StringBuffer();
            buffer.append("班级："+Room.dao.findById(getPara("room_id")).getName()).append("\n");
            buffer.append("类型："+Course.dao.findById(getPara("course_id")).getName()).append("\n");
            buffer.append("教师："+Enterprise.dao.findById(((Enterprise) getSessionAttr("teacher")).getId()).getName()).append("\n");
            buffer.append("时间："+dateFm.format(roomwork.get("time"))).append("\n");
            buffer.append("内容："+getPara("content")).append("\n");
            buffer.append("<a href=\""+roomworkRead+"\">确认已读点击这里</a>");
            try {
                WP.me.sendNotifyMessage(new NotifyMessage(ParamesAPI.parentId, new Text(buffer.toString()), idParameter, false));
            } catch (Exception e) {
                renderText(e.getMessage());
            }
            renderText("OK");
        }
    }
}
