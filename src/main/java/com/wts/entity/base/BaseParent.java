package com.wts.entity.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseParent<M extends BaseParent<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setUserId(java.lang.String userId) {
		set("userId", userId);
	}

	public java.lang.String getUserId() {
		return get("userId");
	}

	public void setLogin(java.lang.String login) {
		set("login", login);
	}

	public java.lang.String getLogin() {
		return get("login");
	}

	public void setPass(java.lang.String pass) {
		set("pass", pass);
	}

	public java.lang.String getPass() {
		return get("pass");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setSex(java.lang.Integer sex) {
		set("sex", sex);
	}

	public java.lang.Integer getSex() {
		return get("sex");
	}

	public void setMobile(java.lang.String mobile) {
		set("mobile", mobile);
	}

	public java.lang.String getMobile() {
		return get("mobile");
	}

	public void setEmail(java.lang.String email) {
		set("email", email);
	}

	public java.lang.String getEmail() {
		return get("email");
	}

	public void setWeixinId(java.lang.String weixinId) {
		set("weixinId", weixinId);
	}

	public java.lang.String getWeixinId() {
		return get("weixinId");
	}

	public void setQq(java.lang.String qq) {
		set("qq", qq);
	}

	public java.lang.String getQq() {
		return get("qq");
	}

	public void setAddress(java.lang.String address) {
		set("address", address);
	}

	public java.lang.String getAddress() {
		return get("address");
	}

	public void setState(java.lang.Integer state) {
		set("state", state);
	}

	public java.lang.Integer getState() {
		return get("state");
	}

	public void setPicUrl(java.lang.String picUrl) {
		set("picUrl", picUrl);
	}

	public java.lang.String getPicUrl() {
		return get("picUrl");
	}

}
