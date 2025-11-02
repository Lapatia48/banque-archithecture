<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Vue Centrale - Banque</title>
    <style>
        :root {
            --primary-color: #1a4f8c;
            --primary-dark: #0d2b4d;
            --accent-color: #28a745;
            --danger-color: #dc3545;
            --warning-color: #ffc107;
            --text-color: #333;
            --text-light: #6c757d;
            --border-color: #dee2e6;
            --shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            --shadow-hover: 0 8px 15px rgba(0, 0, 0, 0.15);
            --border-radius: 10px;
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }

        .dashboard-container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: var(--border-radius);
            box-shadow: var(--shadow-hover);
            overflow: hidden;
        }

        .dashboard-header {
            background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
            color: white;
            padding: 40px 30px;
            text-align: center;
        }

        .dashboard-header h1 {
            margin: 0;
            font-size: 2.5em;
            font-weight: 300;
            margin-bottom: 10px;
        }

        .dashboard-header p {
            margin: 0;
            font-size: 1.2em;
            opacity: 0.9;
        }

        .main-content {
            padding: 40px 30px;
        }

        .message-container {
            margin-bottom: 30px;
        }

        .alert {
            padding: 15px 20px;
            border-radius: var(--border-radius);
            margin-bottom: 20px;
            border-left: 4px solid;
        }

        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border-left-color: var(--accent-color);
        }

        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            border-left-color: var(--danger-color);
        }

        .alert-info {
            background-color: #d1ecf1;
            color: #0c5460;
            border-left-color: var(--primary-color);
        }

        .overview-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 25px;
            margin-bottom: 40px;
        }

        .account-card {
            background: white;
            padding: 25px;
            border-radius: var(--border-radius);
            border: 2px solid var(--border-color);
            box-shadow: var(--shadow);
            transition: all 0.3s ease;
            text-align: center;
        }

        .account-card:hover {
            transform: translateY(-5px);
            box-shadow: var(--shadow-hover);
        }

        .account-icon {
            font-size: 2.5em;
            margin-bottom: 15px;
        }

        .account-title {
            font-size: 1.3em;
            font-weight: 600;
            color: var(--primary-color);
            margin-bottom: 10px;
        }

        .account-amount {
            font-size: 2em;
            font-weight: 700;
            margin: 15px 0;
        }

        .positive {
            color: var(--accent-color);
        }

        .negative {
            color: var(--danger-color);
        }

        .fortune-section {
            background: linear-gradient(135deg, #f8f9fa, #e9ecef);
            padding: 30px;
            border-radius: var(--border-radius);
            margin-bottom: 40px;
            text-align: center;
            border: 2px solid var(--border-color);
        }

        .fortune-label {
            font-size: 1.3em;
            color: var(--text-light);
            margin-bottom: 15px;
            font-weight: 500;
        }

        .fortune-amount {
            font-size: 3em;
            font-weight: 700;
            margin: 20px 0;
        }

        .simulation-section {
            background: white;
            padding: 30px;
            border-radius: var(--border-radius);
            border: 1px solid var(--border-color);
            box-shadow: var(--shadow);
        }

        .section-title {
            color: var(--primary-color);
            border-bottom: 2px solid var(--primary-light);
            padding-bottom: 15px;
            margin-bottom: 25px;
            font-size: 1.4em;
            font-weight: 600;
        }

        .form-row {
            display: flex;
            gap: 20px;
            margin-bottom: 20px;
            align-items: end;
        }

        .form-group {
            flex: 1;
        }

        .form-label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: var(--text-color);
        }

        .form-input, .form-select {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid var(--border-color);
            border-radius: var(--border-radius);
            font-size: 1em;
            transition: all 0.3s ease;
        }

        .form-input:focus, .form-select:focus {
            outline: none;
            border-color: var(--primary-color);
            box-shadow: 0 0 0 3px rgba(26, 79, 140, 0.1);
        }

        .button {
            padding: 14px 25px;
            border: none;
            border-radius: var(--border-radius);
            font-size: 1em;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
        }

        .button-primary {
            background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
            color: white;
        }

        .button-primary:hover {
            transform: translateY(-2px);
            box-shadow: var(--shadow-hover);
        }

        .simulation-result {
            background: linear-gradient(135deg, #e3f2fd, #bbdefb);
            padding: 25px;
            border-radius: var(--border-radius);
            margin-top: 25px;
            border-left: 4px solid var(--primary-color);
        }

        .result-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-top: 15px;
        }

        .result-item {
            text-align: center;
            padding: 15px;
            background: white;
            border-radius: var(--border-radius);
            border: 1px solid var(--border-color);
        }

        .result-label {
            font-size: 0.9em;
            color: var(--text-light);
            margin-bottom: 5px;
        }

        .result-value {
            font-size: 1.3em;
            font-weight: 600;
        }

        .navigation {
            background: #f8f9fa;
            padding: 25px 30px;
            border-top: 1px solid var(--border-color);
            text-align: center;
        }

        @media (max-width: 768px) {
            .form-row {
                flex-direction: column;
            }
            
            .overview-grid {
                grid-template-columns: 1fr;
            }
            
            .result-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="dashboard-container">
        <!-- En-tête -->
        <div class="dashboard-header">
            <h1>Banque Centrale</h1>
            <p>Vue Centrale - Tableau de Bord Complet</p>
            <div class="user-welcome" style="margin-top: 10px; color: rgba(255,255,255,0.8);">
                Bienvenue, ${sessionScope.utilisateur.nom}
            </div>
        </div>

        <!-- Contenu principal -->
        <div class="main-content">
            <!-- Messages -->
            <div class="message-container">
                <c:if test="${not empty message}">
                    <div class="alert alert-success">${message}</div>
                </c:if>
                
                <c:if test="${not empty error}">
                    <div class="alert alert-error">${error}</div>
                </c:if>
            </div>

            <!-- Overview des comptes -->
            <div class="overview-grid">
                <div class="account-card">
                    <div class="account-icon">💰</div>
                    <div class="account-title">Compte Dépôt</div>
                    <div class="account-amount positive">${soldeDepot} Ariary</div>
                </div>

                <div class="account-card">
                    <div class="account-icon">🏦</div>
                    <div class="account-title">Compte Courant</div>
                    <div class="account-amount positive">${soldeCourant} Ariary</div>
                </div>

                <div class="account-card">
                    <div class="account-icon">📝</div>
                    <div class="account-title">Compte Prêt</div>
                    <div class="account-amount negative">${soldePret} Ariary</div>
                </div>
            </div>

            <!-- Fortune totale -->
            <div class="fortune-section">
                <div class="fortune-label">Fortune Totale Actuelle</div>
                <c:choose>
                    <c:when test="${fortuneTotale >= 0}">
                        <div class="fortune-amount positive">${fortuneTotale} Ariary</div>
                    </c:when>
                    <c:otherwise>
                        <div class="fortune-amount negative">${fortuneTotale} Ariary</div>
                    </c:otherwise>
                </c:choose>
                <div style="color: var(--text-light); font-size: 1.1em;">
                    (Dépôt + Courant) - Prêt
                </div>
            </div>

            <!-- Simulation de fortune future -->
            <div class="simulation-section">
                <h3 class="section-title">Simulation de Fortune Future</h3>
                <form action="${pageContext.request.contextPath}/simulerFortune" method="post">
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label">Date future</label>
                            <input type="date" name="date" required class="form-input">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Période Dépôt</label>
                            <select name="periodeDepot" required class="form-select">
                                <c:forEach var="pourcentage" items="${pourcentagesDepot}">
                                    <option value="${pourcentage.periode}">
                                        ${pourcentage.periode} mois (${pourcentage.pourcentage}%)
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Période Prêt</label>
                            <select name="periodePret" required class="form-select">
                                <c:forEach var="pourcentage" items="${pourcentagesPret}">
                                    <option value="${pourcentage.periode}">
                                        ${pourcentage.periode} mois (${pourcentage.pourcentage}%)
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <button type="submit" class="button button-primary" style="height: 46px;">
                                📊 Simuler
                            </button>
                        </div>
                    </div>
                </form>

                <c:if test="${not empty fortuneFuture}">
                    <div class="simulation-result">
                        <h4>📈 Résultat de la Simulation au ${simulationDate}</h4>
                        <div class="result-grid">
                            <div class="result-item">
                                <div class="result-label">Dépôt futur</div>
                                <div class="result-value positive">${depotFutur} Ar</div>
                            </div>
                            <div class="result-item">
                                <div class="result-label">Prêt futur</div>
                                <div class="result-value negative">${pretFutur} Ar</div>
                            </div>
                            <div class="result-item">
                                <div class="result-label">Courant</div>
                                <div class="result-value positive">${soldeCourant} Ar</div>
                            </div>
                            <div class="result-item">
                                <div class="result-label">Fortune future</div>
                                <div class="result-value ${fortuneFuture >= 0 ? 'positive' : 'negative'}">
                                    ${fortuneFuture} Ar
                                </div>
                            </div>
                        </div>
                        <div style="margin-top: 15px; font-size: 0.9em; color: var(--text-light);">
                            Périodes: Dépôt ${periodeDepot} mois, Prêt ${periodePret} mois
                        </div>
                    </div>
                </c:if>
            </div>
        </div>

        <!-- Navigation -->
        <div class="navigation">
            <form action="${pageContext.request.contextPath}/accueil" method="get" style="margin: 0;">
                <button class="button button-primary" type="submit">
                    🏠 Retour à l'accueil
                </button>
            </form>
        </div>
    </div>
</body>
</html>