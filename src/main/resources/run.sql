### 操作数据表teacher
#namespace("Teacher")
  #sql("login_teacher")
    SELECT * FROM teacher
    WHERE (login = #p(lmewuq) AND pass = #p(pass) AND state = #p(state))
      OR (mobile = #p(lmewuq) AND pass = #p(pass) AND state = #p(state))
      OR (email = #p(lmewuq) AND pass = #p(pass) AND state = #p(state))
      OR (weixinId = #p(lmewuq) AND pass = #p(pass) AND state = #p(state))
      OR (userId = #p(lmewuq) AND pass = #p(pass) AND state = #p(state))
      OR (qq = #p(lmewuq) AND pass = #p(pass) AND state = #p(state))
  #end
  #sql("login_manager")
    SELECT * FROM teacher
    WHERE (login = #p(lmewuq) AND pass = #p(pass) AND state = #p(state) AND isManager = 1)
      OR (mobile = #p(lmewuq) AND pass = #p(pass) AND state = #p(state) AND isManager = 1)
      OR (email = #p(lmewuq) AND pass = #p(pass) AND state = #p(state) AND isManager = 1)
      OR (weixinId = #p(lmewuq) AND pass = #p(pass) AND state = #p(state) AND isManager = 1)
      OR (userId = #p(lmewuq) AND pass = #p(pass) AND state = #p(state) AND isManager = 1)
      OR (qq = #p(lmewuq) AND pass = #p(pass) AND state = #p(state) AND isManager = 1)
  #end
  #sql("weixin_teacher")
    select * FROM teacher WHERE userId = ? AND state = ?
  #end

  #sql("weixin_manager")
    SELECT * FROM teacher WHERE userId = ? AND state = ? AND isManager = 1
  #end
  #sql("mobile")
    SELECT * FROM teacher WHERE mobile = ?
  #end
  #sql("userId")
    SELECT * FROM teacher WHERE userId = ?
  #end
  #sql("email")
    SELECT * FROM teacher WHERE email = ?
  #end
  #sql("list")
    SELECT * FROM teacher WHERE state = 1 OR state = 2 ORDER BY id ASC
  #end
#end

### 操作数据表parent
#namespace("Parent")
  #sql("login_parent")
    SELECT * FROM parent
    WHERE (login = #p(lmewuq) AND pass = #p(pass) AND state = #p(state))
      OR (mobile = #p(lmewuq) AND pass = #p(pass) AND state = #p(state))
      OR (email = #p(lmewuq) AND pass = #p(pass) AND state = #p(state))
      OR (weixinId = #p(lmewuq) AND pass = #p(pass) AND state = #p(state))
      OR (userId = #p(lmewuq) AND pass = #p(pass) AND state = #p(state))
      OR (qq = #p(lmewuq) AND pass = #p(pass) AND state = #p(state))
  #end
  #sql("weixin_parent")
    SELECT * FROM parent WHERE userId = ? AND state = ?
  #end
  #sql("mobile")
    SELECT * FROM parent WHERE mobile = ?
  #end
  #sql("userId")
    SELECT * FROM parent WHERE userId = ?
  #end
  #sql("email")
    SELECT * FROM parent WHERE email = ?
  #end
#end

### 操作数据表relation
#namespace("relation")
  #sql("delete")
    DELETE FROM relation WHERE parent_id = ?
  #end
  #sql("parentId")
    SELECT * FROM relation WHERE parent_id = ?
  #end
  #sql("getParent")
    SELECT * FROM relation WHERE student_id = ? AND identity_id = ?
  #end
  #sql("getIdentity")
    SELECT * FROM relation WHERE student_id = ? AND parent_id = ?
  #end
#end

### 操作数据表student
#namespace("Student")
  #sql("id")
    SELECT * FROM student WHERE id = ?
  #end
#end

### 操作数据表identity
#namespace("identity")
  #sql("id")
    SELECT * FROM identity WHERE id = ?
  #end
#end

### 操作数据表roomStudent
#namespace("RoomStudent")
  #sql("studentId")
    SELECT * FROM roomstudent WHERE studentId = ?
  #end
#end

### 操作数据表semester
#namespace("Semester")
  #sql("used")
    SELECT * FROM semester WHERE state = 1
  #end
#end

### 操作数据表room
#namespace("Room")
  #sql("slogan")
    SELECT * FROM room WHERE slogan = ?
  #end
  #sql("year_order")
    SELECT * FROM room WHERE year = ? AND order = ?
  #end
  #sql("list")
    SELECT * FROM room WHERE state = 1 ORDER BY id DESC
  #end
#end

### 操作数据表roomStudent
#namespace("RoomStudent")
  #sql("list_parent")
    SELECT DISTINCT room.id, room.name, room.state
    FROM ((roomstudent
    LEFT JOIN room ON room.id = roomstudent.room_id)
    LEFT JOIN student ON student.id = roomstudent.student_id)
    WHERE room.state = 1 AND student.state = 1 AND student.id
      IN (SELECT DISTINCT student_id FROM relation WHERE parent_id = ?)
  #end
#end

### 操作数据表courseRoomTeacher
#namespace("CourseRoomTeacherSemester")
  #sql("list_teacher")
    SELECT DISTINCT room.id, room.name, room.state
    FROM (((courseroomteacher
    LEFT JOIN room ON room.id = courseroomteacher.room_id)
    LEFT JOIN teacher ON teacher.id = courseroomteacher.teacher_id)
    LEFT JOIN semester ON semester.id = courseroomteacher.semester_id)
    WHERE room.state = 1 AND teacher.id = ? AND semester.id = ?
  #end
#end