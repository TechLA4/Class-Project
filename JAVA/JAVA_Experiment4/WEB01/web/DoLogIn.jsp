<%@ page import="java.sql.*" %>
<%--
  Created by IntelliJ IDEA.
  User: 吴柳航
  Date: 2022/4/21
  Time: 20:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    static Connection con=null;
    static Statement sql=null;
    static ResultSet rs=null;
    String user="root";
    String password="123";
    String url="jdbc:mysql://localhost:3306/java_experiment4_user?useUnicode=true&characterEncoding=gbk";

    public void Initial()
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (Exception e){System.out.println(e);}
    }
    public void Connect() {
        try {
            con = DriverManager.getConnection(url, user, password);
            sql=con.createStatement();
        } catch (Exception f) {System.out.println(f);}
    }
    public boolean Judge_Read(String user,String password)
    {
        boolean flag=false;
        try {
            rs=sql.executeQuery("select *from user ");
            while(rs.next())
            {
                if(user.equals(rs.getString(1))&&password.equals(rs.getString(2)))
                    flag=true;
            }
        }
        catch (Exception e){System.out.println(e);}
        return flag;
    }


%>
<html>
<head>
    <title>DoRegister</title>
</head>
<body style="background: url(Image/bg.png);background-size: cover">
<%
    Initial();
    Connect();
    if(Judge_Read(request.getParameter("name"),request.getParameter("password")))
        out.print("登 陆 成 功  !   !   !  ");
    else
    {
        out.print("用户名或者密码错误!!!   5s后自行跳转至登录界面");
        response.setHeader("Refresh","10;URL=LogIn.jsp");
    }

    sql.close();
    rs.close();
    con.close();

%>

<br>

</body>
</html>
