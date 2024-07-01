<%--
  Created by IntelliJ IDEA.
  User: 吴柳航
  Date: 2022/4/21
  Time: 19:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*" %>
<html>
<head>
    <title>LogIn</title>
</head>
<body style="background: url(Image/bg.png);background-size: cover">
<form action="DoRegister.jsp" method="post">
用户名:<input type="text" name="name" size="20">  <br> <br>
密码：<input type="password" name ="password" size="20">  <br> <br>
性别：<select name="sex">
    <option value="man">男</option>
    <option value="woman">女</option>
    </select>
    <br><br>
<button>注册</button>
</form>

<form action="LogIn.jsp">
    <button>登录</button>
</form>

</body>
</html>
