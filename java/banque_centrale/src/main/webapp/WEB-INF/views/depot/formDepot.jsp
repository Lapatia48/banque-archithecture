<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dépot</title>
    <style>
        body{font-family:Arial,sans-serif;display:flex;justify-content:center;align-items:center;height:100vh;margin:0;}
        .login-container{background:#fff;padding:30px;border-radius:12px;box-shadow:0 6px 15px rgba(0,0,0,0.2);width:350px;}
        h1{text-align:center;color:#2d3436;margin-bottom:20px;}
        label{display:block;margin-bottom:8px;font-weight:bold;color:#2d3436;}
        input[type="text"],input[type="number"],textarea{width:100%;padding:10px;margin-bottom:15px;border:1px solid #dfe6e9;border-radius:8px;outline:none;transition:.3s;}
        input[type="text"]:focus,input[type="number"]:focus,textarea:focus{border-color:#0984e3;box-shadow:0 0 5px rgba(9,132,227,.5);}
        input[type="submit"]{width:100%;background:#0984e3;color:#fff;padding:12px;border:none;border-radius:8px;font-size:16px;cursor:pointer;transition:.3s;}
        input[type="submit"]:hover{background:#74b9ff;}
        p{text-align:center;margin-top:15px;font-size:14px;}
        p[style]{color:red;font-weight:bold;}
    </style>
</head>
<body>
    <div class="login-container">
        <h1>Faire un dépôt</h1>
        <form action="${pageContext.request.contextPath}/faireDepot" method="post">
            <input type="hidden" name="identifiant" value="${sessionScope.utilisateur.identifiant}">
            <input type="hidden" name="compte" value="depot">
            <input type="hidden" name="action" value="depot">

            <label for="montant">Montant :</label>
            <input type="number" id="montant" name="montant" placeholder="Montant: ariary" required>


            <label for="detail">Détail :</label>
            <textarea id="detail" name="detail" placeholder="Ex: Versement especes" rows="3" required></textarea>

            <input type="submit" value="Valider le dépôt">
        </form>

        <form action="${pageContext.request.contextPath}/depot" method="get">
            <button class="button" type="submit">Retour</button>
        </form>

    </div>
</body>
</html>
