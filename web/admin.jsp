<%-- 
    Document   : admin
    Created on : Mar 24, 2015, 10:19:37 AM
    Author     : sjf14670
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Admin Options</title>
        <link rel="stylesheet" type="text/css" href="GreenStyle.css">
        <script src="javascript_scripts/datePicker.js"></script>
        <script src="http://code.jquery.com/jquery-latest.min.js"></script>
        <script>
            $.get('Footer.html', {}, function (response) {
                $('div#footer').append(response);
            });
        </script>
    </head>
    <body class="background">
        <div class="wrapper">
            <audio src="Images/bark2.wav" id="sound" preload="auto"></audio>
            <br>
            <center>
                <table class="padMe">
                    <tr id="buffer"></tr>
                    <tr id="buffer"></tr>
                    <tr>
                        <td class="tables2">
                            <div class="boxxyLogo">
                                <img src="Images/HuskiesLogo2-2.png" alt="Image not found." ondrag="playMe()" class="logo">
                                <br>
                            </div><br><br><br>
                            <div class="basic" id="links">
                                <a class="buttons" href="graphs.jsp">Graphs Page</a><br><br>
                                <a class="buttons" href="reports.jsp">Download Data</a><br><br>
                                <a class="buttons" href="building.jsp">Building Settings</a><br><br>
                                <a class="buttons" href="tip.jsp">Tip Settings</a><br><br>
                            </div>
                        </td>
                        <td id="buffer"></td>
                        <td class="tables2">
                            <div class="boxxy">
                                <span class="greene">Page Settings</span>
                                <div class="basic">
                                    <span class="titles">Widget Options</span><br><br>
                                    <form action="PropertyServlet" method="post">
                                        Days in Average: <input type="text" class="basic3" id="days" name="DaysInAverage" value=""><br>
                                        Widget Meter: <select class="basic" id="meter" name="Meter">
                                            <option id="lowerCampus" name="lowerCampus">Lower Campus</option>
                                        </select><br><br><br>
                                        <span class="titles">Main Page Options</span><br><br>
                                        Change Green Tip every: <select class="basic" id="tipTime" name="TimeBetweenTips">
                                            <option id="15" value="15">15 seconds</option>
                                            <option id="30" value="30">30 seconds</option>
                                            <option id="45" value="45">45 seconds</option>
                                            <option id="60" value="60">1 minute</option>
                                            <option id="60" value="300">5 minutes</option>
                                            <option id="60" value="never">Never</option>
                                        </select><br>
                                        Default Graph Time: <select class="basic" id="tipTime" name="GraphTime">
                                            <option id="day" name="day" value="1">Today</option>
                                            <option id="week" name="week" value="2">This Week</option>
                                            <option id="month" name="month" value="3">This Month</option>
                                            <option id="year" name="year" value="4">This Year</option>
                                        </select><br><br><br>
                                        <input type="submit" class="buttons" value="Submit"/>
                                        </div></div><br>
                                    </form>
                                    <div class='boxxy'>
                                        <span class="greene">Add New User</span><br><br>
                                        <div class='basic'>
                                            <span class='titles'>Username:</span> <input type="text" class="basic3" id="loginName" name="loginName" size="50"><br>
                                            <span class='titles'>Password:</span> <input type="password" class="basic3" id="password" name="password" size="60"><br>
                                            <span class='titles'>First Name:</span> <input type="text" class="basic3" id="first" name="first" size="25"><br>
                                            <span class='titles'>Last Name:</span> <input type="text" class="basic3" id="last" name="last" size="35"><br>
                                            <span class='titles'>Email:</span> <input type="text" class="basic3" id="email" name="email" size="50"><br>
                                            <span class='titles'>User Role:</span> <select class="basic" id="userRole" name="userRole"><option value="SystemAdmin" id="SystemAdmin">System Admin</option></select><br><br>
                                            <input type="submit" class="buttons2" value="Submit"><br><br>
                                        </div>
                                    </div>
                                    </td>
                                    <td class="boxxy3"></td>
                                    </tr>
                                    </table>
                                    </center>
                                    <br><br><br>
                                </div>
                                <div class="footer" id="footer"></div>
                                </body>
                                </html>
