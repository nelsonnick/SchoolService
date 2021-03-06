package com.wts.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.wts.entity.model.Permission;
import com.wts.entity.model.Teacher;
import com.wts.entity.model.Teacherpermission;

import javax.servlet.http.HttpSession;

public class PageCheck implements Interceptor {

    public void intercept(Invocation inv) {
        HttpSession session = inv.getController().getSession();
        Teacher teacher = (Teacher) session.getAttribute("Teacher");
        String url = inv.getTarget().getClass().getSimpleName() + "/" + inv.getMethodName();
        Permission permission = Permission.dao.findFirst("SELECT * FROM permission WHERE url=?", url);
        if (permission == null) {
            inv.getController().renderText("illegal");
        } else {
            if (Teacherpermission.dao.findById(permission.getId(), teacher.getId()).getState() == 1) {
                inv.invoke();
            } else {
                inv.getController().renderText("illegal");
            }
        }

    }
}
