<!DOCTYPE html>
<html>
    <head>
        <title>Forgot Password</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<link rel="general stylesheet" href="generalStyles.css" type="text/css">
        <link rel="waitlist stylesheet" href="GreenStyle.css" type="text/css">
    </head>
    <body class="background">
	    <form class="content_container" id="login_container" method="POST" action="ForgotPasswordServlet">
                <header class="content_title_bar" id="login_header"><div class="titles3">Forgot Password?</div></header><br><br>
                <center><div class="basic8">${message}</div><br>
                <div id="forgotText" class="titles3">Type in your email address</div><br>
                <input class="basic8" type="email" name="emailAddress"><br><br>                                  
                <button class="buttons2" type="button" id="resetGoBack" name="goback" onclick="location.href='loginScreen.jsp';">Cancel</button>
                <input class="buttons2" type="submit" value="Submit" name="submit"></center>
            </form>
    </body>
</html>
