package com.wts.entity.model;

import com.jfinal.plugin.activerecord.Page;
import com.wts.entity.base.BaseTeam;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Team extends BaseTeam<Team> {
	public static final Team dao = new Team().dao();
	public Page<Team> queryByName(int pageNumber, int pageSize, String name) {
		return paginate(pageNumber, pageSize, "SELECT *",
						"FROM team WHERE name LIKE '%"+name+"%' ORDER BY id DESC");
	}
}