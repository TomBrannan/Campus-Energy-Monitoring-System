<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<html>
    <head>
        <title>Login</title>
         <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="general stylesheet" href="generalStyles.css" type="text/css">
        <link rel="waitlist stylesheet" href="GreenStyle.css" type="text/css">  
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <noscript>
            <meta http-equiv="refresh" content="0; URL=javascriptDisabled.html">
        </noscript>
    </head>
    <body class="background">
        

        <section class = "content_container" id = "login_container">

            <header class = "content_title_bar" id="login_header"> 
                <div class = "titles3" >
                    Login
                </div> 
            </header>

            <form id="login_form" action="ControlServlet" method = "POST">              
                <div>
                    <table><tr>
                            <td><img src="Images/HuskiesLogo2-2.png" class="logoLogin"></td>
                            <td><div id="image_message_container" class="basic">
                                ${errorMessage}
                                </div></td>
                    </tr></table>
                </div>
                <div id = "login_text_container" class="basic">
                    Username: <input type = "text" class="basic8" name="username"  placeholder="Enter Username" value="${username}"/> <br>
                    Password: <input id="loginPassword" class="basic8" type="password" name="password"  placeholder="Enter Password"/><br>
                    <a href="ForgotPassword.jsp" id ="forgot_password_link">Forgot Password</a> <br>
                </div>
                    
                    <input type ="submit" name = "login" value="Login" class="buttons2" style="margin-left: 80px"/>
                    <button type='button' class='buttons2' onclick="location.reload();location.href='graphs.jsp'">Go Back</button>
            </form>

        </section>      
    </body>
</html>
