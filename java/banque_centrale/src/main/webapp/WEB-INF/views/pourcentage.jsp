<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Gestion des Pourcentages</title>
    <style>
        .container {
            max-width: 900px;
            margin: 0 auto;
            padding: 20px;
        }
        .type-section {
            border: 2px solid #007bff;
            margin: 20px 0;
            padding: 15px;
            border-radius: 8px;
            background-color: #f8f9fa;
        }
        .type-title {
            color: #007bff;
            border-bottom: 1px solid #dee2e6;
            padding-bottom: 10px;
            margin-bottom: 15px;
        }
        .pourcentage-item {
            border: 1px solid #dee2e6;
            padding: 15px;
            margin: 10px 0;
            border-radius: 5px;
            background-color: white;
        }
        .form-group {
            margin-bottom: 15px;
            display: flex;
            align-items: center;
        }
        label {
            display: inline-block;
            width: 120px;
            font-weight: bold;
            margin-right: 10px;
        }
        input[readonly] {
            background-color: #e9ecef;
            color: #495057;
            border: 1px solid #ced4da;
        }
        input {
            padding: 6px 10px;
            margin: 0 5px;
            width: 120px;
            border: 1px solid #ced4da;
            border-radius: 4px;
        }
        .btn {
            background-color: #007bff;
            color: white;
            padding: 8px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .message {
            color: #155724;
            font-weight: bold;
            margin: 10px 0;
            padding: 12px;
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
            border-radius: 4px;
        }
        .error {
            color: #721c24;
            font-weight: bold;
            margin: 10px 0;
            padding: 12px;
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Gestion des Taux d'Intérêt</h1>
        
        <!-- Messages -->
        <c:if test="${not empty message}">
            <div class="message">${message}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>

        <!-- Version avec regroupement par type de compte -->
        <c:if test="${not empty pourcentagesParType}">
            <c:forEach var="typeEntry" items="${pourcentagesParType}">
                <div class="type-section">
                    <h2 class="type-title">Type: ${typeEntry.key}</h2>
                    <c:forEach var="pourc" items="${typeEntry.value}">
                        <div class="pourcentage-item">
                            <form action="${pageContext.request.contextPath}/pourcentage/update" method="post">
                                <!-- Champs cachés pour l'ancienne période et type -->
                                <input type="hidden" name="typeCompte" value="${pourc.typeCompte}" required>
                                <input type="hidden" name="anciennePeriode" value="${pourc.periode}" required>
                                
                                <div class="form-group">
                                    <label>Type Compte:</label>
                                    <input type="text" value="${pourc.typeCompte}" readonly>
                                    <span style="color: #6c757d; font-size: 12px;">(non modifiable)</span>
                                </div>
                                <div class="form-group">
                                    <label>Période (mois):</label>
                                    <input type="number" name="nouvellePeriode" value="${pourc.periode}" min="1" max="120" required>
                                    <span style="color: #6c757d; font-size: 12px;">(modifiable)</span>
                                </div>
                                <div class="form-group">
                                    <label>Pourcentage:</label>
                                    <input type="number" name="pourcentage" value="${pourc.pourcentage}" step="0.01" min="0" max="100" required>
                                    <span>%</span>
                                </div>
                                <button type="submit" class="btn">Mettre à jour</button>
                            </form>
                        </div>
                    </c:forEach>
                </div>
            </c:forEach>
        </c:if>


        <form action="${pageContext.request.contextPath}/accueil">
            <input type="submit" value="Retour a l'accueil">
        </form>
        <!-- Message si aucune donnée -->
        <c:if test="${empty pourcentagesParType and empty pourcentagesList}">
            <div class="message">Aucun pourcentage trouvé dans la base de données.</div>
        </c:if>
    </div>
</body>
</html>