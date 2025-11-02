<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Gestion des Prêts</title>
    <style>
        :root {
            --primary-color: #1a4f8c;
            --primary-dark: #0d2b4d;
            --primary-light: #2e7dcc;
            --secondary-color: #f8f9fa;
            --accent-color: #28a745;
            --warning-color: #ffc107;
            --danger-color: #dc3545;
            --text-color: #333;
            --text-light: #6c757d;
            --border-color: #dee2e6;
            --shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            --shadow-hover: 0 8px 15px rgba(0, 0, 0, 0.15);
            --border-radius: 10px;
            --transition: all 0.3s ease;
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

        .user-welcome {
            text-align: center;
            margin-top: 10px;
            font-size: 1.1em;
            color: rgba(255, 255, 255, 0.8);
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
            border-left-color: var(--primary-light);
        }

        .balance-section {
            text-align: center;
            background: linear-gradient(135deg, #f8f9fa, #e9ecef);
            padding: 40px;
            border-radius: var(--border-radius);
            margin-bottom: 40px;
            border: 2px solid var(--border-color);
            position: relative;
            overflow: hidden;
        }

        .balance-section::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, var(--accent-color), var(--primary-color));
        }

        .balance-label {
            font-size: 1.3em;
            color: var(--text-light);
            margin-bottom: 15px;
            font-weight: 500;
        }

        .balance-amount {
            font-size: 3.5em;
            font-weight: 700;
            color: var(--accent-color);
            margin: 20px 0;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.1);
        }

        .balance-currency {
            font-size: 1.3em;
            color: var(--text-light);
            font-weight: 500;
        }

        .negative-balance {
            color: var(--danger-color) !important;
        }

        .actions-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
            gap: 30px;
            margin-bottom: 40px;
        }

        .action-card {
            background: white;
            padding: 30px;
            border-radius: var(--border-radius);
            border: 1px solid var(--border-color);
            box-shadow: var(--shadow);
            transition: var(--transition);
        }

        .action-card:hover {
            transform: translateY(-5px);
            box-shadow: var(--shadow-hover);
        }

        .section-title {
            color: var(--primary-color);
            border-bottom: 2px solid var(--primary-light);
            padding-bottom: 15px;
            margin-bottom: 25px;
            font-size: 1.4em;
            font-weight: 600;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: var(--text-color);
            font-size: 1em;
        }

        .form-input, .form-select, .form-textarea {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid var(--border-color);
            border-radius: var(--border-radius);
            font-size: 1em;
            transition: var(--transition);
            background: white;
        }

        .form-input:focus, .form-select:focus, .form-textarea:focus {
            outline: none;
            border-color: var(--primary-color);
            box-shadow: 0 0 0 3px rgba(26, 79, 140, 0.1);
        }

        .form-textarea {
            resize: vertical;
            min-height: 80px;
        }

        .form-row {
            display: flex;
            gap: 15px;
            align-items: end;
        }

        .form-row .form-group {
            flex: 1;
        }

        .button {
            padding: 14px 25px;
            border: none;
            border-radius: var(--border-radius);
            font-size: 1em;
            font-weight: 600;
            cursor: pointer;
            transition: var(--transition);
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            text-align: center;
        }

        .button-primary {
            background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
            color: white;
        }

        .button-primary:hover {
            transform: translateY(-2px);
            box-shadow: var(--shadow-hover);
        }

        .button-success {
            background: linear-gradient(135deg, var(--accent-color), #218838);
            color: white;
        }

        .button-success:hover {
            transform: translateY(-2px);
            box-shadow: var(--shadow-hover);
        }

        .button-danger {
            background: linear-gradient(135deg, var(--danger-color), #bd2130);
            color: white;
        }

        .button-danger:hover {
            transform: translateY(-2px);
            box-shadow: var(--shadow-hover);
        }

        .button-outline {
            background: transparent;
            color: var(--primary-color);
            border: 2px solid var(--primary-color);
        }

        .button-outline:hover {
            background: var(--primary-color);
            color: white;
            transform: translateY(-2px);
        }

        .quick-actions {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 20px;
            margin-top: 40px;
        }

        .quick-card {
            background: white;
            padding: 25px;
            border-radius: var(--border-radius);
            border: 1px solid var(--border-color);
            text-align: center;
            transition: var(--transition);
        }

        .quick-card:hover {
            transform: translateY(-3px);
            box-shadow: var(--shadow-hover);
        }

        .quick-icon {
            font-size: 2.5em;
            margin-bottom: 15px;
            display: block;
        }

        .quick-title {
            font-size: 1.3em;
            font-weight: 600;
            color: var(--primary-color);
            margin-bottom: 10px;
        }

        .quick-description {
            color: var(--text-light);
            margin-bottom: 20px;
            line-height: 1.5;
        }

        .navigation {
            background: var(--secondary-color);
            padding: 25px 30px;
            border-top: 1px solid var(--border-color);
        }

        .nav-buttons {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .simulation-result {
            background: linear-gradient(135deg, #e3f2fd, #bbdefb);
            padding: 20px;
            border-radius: var(--border-radius);
            margin-top: 20px;
            border-left: 4px solid var(--primary-color);
        }

        .simulation-result h4 {
            color: var(--primary-color);
            margin-bottom: 10px;
        }

        @media (max-width: 768px) {
            .main-content {
                padding: 20px 15px;
            }
            
            .actions-grid {
                grid-template-columns: 1fr;
            }
            
            .form-row {
                flex-direction: column;
            }
            
            .nav-buttons {
                flex-direction: column;
                gap: 15px;
            }
            
            .balance-amount {
                font-size: 2.5em;
            }
        }
    </style>
</head>
<body>
    <div class="dashboard-container">
        <!-- En-tête -->
        <div class="dashboard-header">
            <h1>Banque Centrale</h1>
            <p>Gestion des Prêts</p>
            <div class="user-welcome">
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

            <!-- Section solde -->
            <div class="balance-section">
                <div class="balance-label">Solde actuel du compte prêt</div>
                <c:choose>
                    <c:when test="${not empty solde && solde.startsWith('-')}">
                        <div class="balance-amount negative-balance">${solde} Ariary</div>
                        <div class="balance-currency">(Dette)</div>
                    </c:when>
                    <c:otherwise>
                        <div class="balance-amount">${solde} Ariary</div>
                        <div class="balance-currency">Aucune dette</div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Grille d'actions -->
            <div class="actions-grid">
                <!-- Section demande de prêt -->
                <div class="action-card">
                    <h3 class="section-title">Demander un prêt</h3>
                    <form action="${pageContext.request.contextPath}/demanderPret" method="post">
                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label">Montant</label>
                                <input type="number" name="montant" step="0.01" required class="form-input" placeholder="0.00">
                            </div>
                            <div class="form-group">
                                <label class="form-label">Période</label>
                                <select name="periode" required class="form-select">
                                    <c:forEach var="pourcentage" items="${pourcentagesPret}">
                                        <option value="${pourcentage.periode}">
                                            ${pourcentage.periode} mois (${pourcentage.pourcentage}%)
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <button type="submit" class="button button-primary" style="width: 100%;">
                            💰 Demander le prêt
                        </button>
                    </form>
                </div>

                <!-- Section remboursement -->
                <div class="action-card">
                    <h3 class="section-title">Rembourser un prêt</h3>
                    <form action="${pageContext.request.contextPath}/rembourserPret" method="post">
                        <div class="form-group">
                            <label class="form-label">Montant à rembourser</label>
                            <input type="number" name="montant" step="0.01" required class="form-input" placeholder="0.00">
                        </div>
                        <button type="submit" class="button button-danger" style="width: 100%;">
                            💳 Rembourser
                        </button>
                    </form>
                </div>

                <!-- Section simulation -->
                <div class="action-card">
                    <h3 class="section-title">Simuler la dette future</h3>
                    <form action="${pageContext.request.contextPath}/simulerPret" method="post">
                        <div class="form-row">
                            <div class="form-group">
                                <label class="form-label">Date future</label>
                                <input type="date" name="date" required class="form-input">
                            </div>
                            <div class="form-group">
                                <label class="form-label">Période du prêt</label>
                                <select name="periode" required class="form-select">
                                    <c:forEach var="pourcentage" items="${pourcentagesPret}">
                                        <option value="${pourcentage.periode}">
                                            ${pourcentage.periode} mois (${pourcentage.pourcentage}%)
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <button type="submit" class="button button-outline" style="width: 100%;">
                            📈 Simuler
                        </button>
                    </form>
                    
                    <c:if test="${not empty simulation}">
                        <div class="simulation-result">
                            <h4>📊 Résultat de la simulation</h4>
                            <p><strong>Date cible :</strong> ${simulationDate}</p>
                            <p><strong>Dette estimée :</strong> <span style="color: var(--danger-color); font-weight: bold; font-size: 1.2em;">${simulation} Ariary</span></p>
                            <p><strong>Période :</strong> ${simulationPeriode} mois</p>
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- Actions rapides -->
            <div class="quick-actions">
                <div class="quick-card">
                    <div class="quick-icon">📊</div>
                    <div class="quick-title">Historique des opérations</div>
                    <div class="quick-description">Consultez l'historique complet de vos prêts et remboursements</div>
                    <form action="${pageContext.request.contextPath}/listOperationPret" method="get" style="margin: 0;">
                        <button class="button button-outline" type="submit" style="width: 100%;">
                            Voir les opérations
                        </button>
                    </form>
                </div>
                
                <!-- <div class="quick-card">
                    <div class="quick-icon">🏠</div>
                    <div class="quick-title">Retour à l'accueil</div>
                    <div class="quick-description">Retournez au tableau de bord principal</div>
                    <form action="${pageContext.request.contextPath}/accueil" method="get" style="margin: 0;">
                        <button class="button button-outline" type="submit" style="width: 100%;">
                            Accueil
                        </button>
                    </form>
                </div> -->
            </div>
        </div>

        <!-- Navigation -->
        <div class="navigation">
            <div class="nav-buttons">
                <form action="${pageContext.request.contextPath}/accueil" method="get" style="margin: 0;">
                    <button class="button button-outline" type="submit">
                        ← Retour à l'accueil
                    </button>
                </form>
                <form action="${pageContext.request.contextPath}/logout" method="get" style="margin: 0;">
                    <button class="button button-primary" type="submit">
                        🚪 Se déconnecter
                    </button>
                </form>
            </div>
        </div>
    </div>
</body>
</html>