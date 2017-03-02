package com.wts.entity.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseAssessmentdetail<M extends BaseAssessmentdetail<M>> extends Model<M> implements IBean {

	public void setAssessmentId(java.lang.Integer assessmentId) {
		set("assessment_id", assessmentId);
	}

	public java.lang.Integer getAssessmentId() {
		return get("assessment_id");
	}

	public void setStudentId(java.lang.Integer studentId) {
		set("student_id", studentId);
	}

	public java.lang.Integer getStudentId() {
		return get("student_id");
	}

	public void setTime(java.util.Date time) {
		set("time", time);
	}

	public java.util.Date getTime() {
		return get("time");
	}

	public void setContent(java.lang.String content) {
		set("content", content);
	}

	public java.lang.String getContent() {
		return get("content");
	}

	public void setScore(java.lang.Integer score) {
		set("score", score);
	}

	public java.lang.Integer getScore() {
		return get("score");
	}

	public void setState(java.lang.Integer state) {
		set("state", state);
	}

	public java.lang.Integer getState() {
		return get("state");
	}

}