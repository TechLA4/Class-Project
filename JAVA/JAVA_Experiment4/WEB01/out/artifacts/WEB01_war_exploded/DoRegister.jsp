<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
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
    public boolean Judge_Read(String s)
    {
        boolean flag=true;
        try {
            rs=sql.executeQuery("select *from user ");
            while(rs.next())
            {
                if(s.equals(rs.getString(1)))
                    flag=false;
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
    if(Judge_Read(request.getParameter("name")))
    {
        out.print("已成功注册!   请点击:   继续注册");
        String path=request.getContextPath();
        String u=request.getParameter("name");
        String p=request.getParameter("password");
        String g=request.getParameter("sex");
        String register="insert into user values " + "('"+u+"',"+"'"+p+"',"+"'"+g+"')";
        int ok=sql.executeUpdate(register);
    }
    else
        out.print("用户已经存在!   请点击:    重新注册");

    sql.close();
    rs.close();
    con.close();

%>
<br>
<br>
<form action="Register.jsp"><button>继续注册</button></form>
<br>
<form action="Register.jsp"><button>重新注册</button></form>

</body>
</html>
