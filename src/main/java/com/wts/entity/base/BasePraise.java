package com.wts.entity.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BasePraise<M extends BasePraise<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setContent(java.lang.String content) {
		set("content", content);
	}

	public java.lang.String getContent() {
		return get("content");
	}

	public void setTime(java.util.Date time) {
		set("time", time);
	}

	public java.util.Date getTime() {
		return get("time");
	}

	public void setStudentId(java.lang.Integer studentId) {
		set("student_id", studentId);
	}

	public java.lang.Integer getStudentId() {
		return get("student_id");
	}

	public void setTeacherId(java.lang.Integer teacherId) {
		set("teacher_id", teacherId);
	}

	public java.lang.Integer getTeacherId() {
		return get("teacher_id");
	}

	public void setCourseId(java.lang.Integer courseId) {
		set("course_id", courseId);
	}

	public java.lang.Integer getCourseId() {
		return get("course_id");
	}

	public void setSchoolId(java.lang.Integer schoolId) {
		set("school_id", schoolId);
	}

	public java.lang.Integer getSchoolId() {
		return get("school_id");
	}

	public void setRoomId(java.lang.Integer roomId) {
		set("room_id", roomId);
	}

	public java.lang.Integer getRoomId() {
		return get("room_id");
	}

}
