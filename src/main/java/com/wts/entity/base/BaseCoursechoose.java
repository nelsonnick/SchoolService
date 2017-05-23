package com.wts.entity.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCoursechoose<M extends BaseCoursechoose<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setStudentId(java.lang.Integer studentId) {
		set("student_id", studentId);
	}

	public java.lang.Integer getStudentId() {
		return get("student_id");
	}

	public void setCourseId(java.lang.Integer courseId) {
		set("course_id", courseId);
	}

	public java.lang.Integer getCourseId() {
		return get("course_id");
	}

	public void setParentId(java.lang.Integer parentId) {
		set("parent_id", parentId);
	}

	public java.lang.Integer getParentId() {
		return get("parent_id");
	}

	public void setSemesterId(java.lang.Integer semesterId) {
		set("semester_id", semesterId);
	}

	public java.lang.Integer getSemesterId() {
		return get("semester_id");
	}

	public void setTime(java.util.Date time) {
		set("time", time);
	}

	public java.util.Date getTime() {
		return get("time");
	}

}
