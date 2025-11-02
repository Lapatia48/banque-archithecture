<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Profil Banquier</title>
    <style>
        .banquier-container {
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
            background-color: #f9f9f9;
        }
        .info-item {
            margin: 10px 0;
            padding: 8px;
            background: white;
            border: 1px solid #eee;
            border-radius: 3px;
        }
        .info-label {
            font-weight: bold;
            color: #333;
        }
        .info-value {
            color: #666;
        }
    </style>
</head>
<body>
    <div class="banquier-container">
        <h1>Profil du Banquier</h1>
        
        <div class="info-item">
            <span class="info-label">Identifiant:</span>
            <span class="info-value">${banquier.identifiant}</span>
        </div>
        
        <div class="info-item">
            <span class="info-label">Nom:</span>
            <span class="info-value">${banquier.nom}</span>
        </div>
        
        <div class="info-item">
            <span class="info-label">Mot de passe:</span>
            <span class="info-value">${banquier.motDePasse}</span>
        </div>
        
        <div class="info-item">
            <span class="info-label">Niveau:</span>
            <span class="info-value">${banquier.niveau}</span>
        </div>
        
        <div class="info-item">
            <span class="info-label">Rôle:</span>
            <span class="info-value">${banquier.role}</span>
        </div>
        
        <div class="info-item">
            <span class="info-label">ID Direction:</span>
            <span class="info-value">${banquier.idDirection}</span>
        </div>
        
        <br>
        <a href="${pageContext.request.contextPath}/accueil">Retour à l'accueil</a>
    </div>
</body>
</html>