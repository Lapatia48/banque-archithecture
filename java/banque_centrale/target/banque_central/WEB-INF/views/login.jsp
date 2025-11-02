<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>login</title>
    <style>
        body{font-family:Arial,sans-serif;background:linear-gradient(135deg,#74b9ff,#0984e3);display:flex;justify-content:center;align-items:center;height:100vh;margin:0;}
        .login-container-max{background:#fff;padding:30px;border-radius:12px;box-shadow:0 6px 15px rgba(0,0,0,0.2);display:flex;gap:20px;}
        .login-container{background:#fff;padding:30px;border-radius:12px;box-shadow:0 6px 15px rgba(0,0,0,0.2);width:350px;}
        h1{text-align:center;color:#2d3436;margin-bottom:20px;}
        label{display:block;margin-bottom:8px;font-weight:bold;color:#2d3436;}
        input[type="text"],input[type="password"]{width:100%;padding:10px;margin-bottom:15px;border:1px solid #dfe6e9;border-radius:8px;outline:none;transition:.3s;}
        input[type="text"]:focus,input[type="password"]:focus{border-color:#0984e3;box-shadow:0 0 5px rgba(9,132,227,.5);}
        input[type="submit"]{width:100%;background:#0984e3;color:#fff;padding:12px;border:none;border-radius:8px;font-size:16px;cursor:pointer;transition:.3s;}
        input[type="submit"]:hover{background:#74b9ff;}
        p{text-align:center;margin-top:15px;font-size:14px;}
        p[style]{color:red;font-weight:bold;}
    </style>
</head>
<body>
    <div class="login-container-max">

        <div class="login-container" style="display: none;">
            <h1>Login utilisateur</h1>
            <form action="${pageContext.request.contextPath}/login" method="post">
                <label for="username">Nom d'utilisateur:</label>
                <input type="text" id="username" name="username" value="Lapatia" required>
                <label for="password">Mot de passe:</label>
                <input type="password" id="password" name="password" value="1234" required>
        </div>
        <div class="login-container">
            <!-- SECTION BANQUIER (visible) -->
                <h1>Connexion Banquier</h1>
                <label for="banquierIdentifiant">Identifiant Banquier:</label>
                <input type="text" id="banquierIdentifiant" name="banquierIdentifiant" value="admin" required>
                <label for="banquierPassword">Mot de passe Banquier:</label>
                <input type="password" id="banquierPassword" name="banquierPassword" value="admin123" required>

                <input type="submit" value="Se connecter">
            </form>
        </div>
        <p>${erreur}</p>

        <div>
            <h6>Voir banquier connectee</h6>
            <a href="${pageContext.request.contextPath}/getBanquieer">Voir le banquier</a>
        </div>
    </div>
</body>
</html>
