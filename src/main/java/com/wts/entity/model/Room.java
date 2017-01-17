package com.wts.entity.model;

import com.jfinal.plugin.activerecord.Page;
import com.wts.entity.base.BaseRoom;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Room extends BaseRoom<Room> {
	public static final Room dao = new Room();
	public Page<Room> queryByName(int pageNumber, int pageSize, String name) {
		return paginate(pageNumber, pageSize, "SELECT *",
				"FROM room WHERE name LIKE '%"+name+"%' ORDER BY id DESC");
	}
}