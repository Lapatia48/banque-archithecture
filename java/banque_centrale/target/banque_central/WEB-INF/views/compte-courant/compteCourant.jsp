<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Compte Courant - Banque Centrale</title>
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
            <p>Gestion de Compte Courant</p>
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
                <div class="balance-label">Solde actuel du compte</div>
                <div class="balance-amount">${solde}</div>
                <div class="balance-currency">Ariary</div>
            </div>

            <!-- Grille d'actions -->
            <div class="actions-grid">
                <!-- Section dépôt -->
                <div class="action-card">
                    <h3 class="section-title">Faire un dépôt</h3>
                    <form action="${pageContext.request.contextPath}/depot-courant" method="post">
                        <div class="form-group">
                            <label class="form-label">Montant</label>
                            <input type="number" name="montant" step="0.01" required class="form-input" placeholder="0.00">
                        </div>

                        <!-- À ajouter à côté de chaque input number -->
                        <div class="form-group">
                            <label class="form-label">Devise</label>
                            <select name="devise" class="form-input" required>
                                <c:forEach var="devise" items="${devises}">
                                    <option value="${devise}">${devise}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Détailss</label>
                            <input type="text" name="details" class="form-input" placeholder="Description de l'opération">
                        </div>
                        <button type="submit" class="button button-success" style="width: 100%;">
                            💰 Déposer
                        </button>
                    </form>
                </div>

                <!-- Section retrait -->
                <div class="action-card">
                    <h3 class="section-title">Faire un retrait</h3>
                    <form action="${pageContext.request.contextPath}/retrait-courant" method="post">
                        <div class="form-group">
                            <label class="form-label">Montant</label>
                            <input type="number" name="montant" step="0.01" required class="form-input" placeholder="0.00">
                        </div>

                        <!-- À ajouter à côté de chaque input number -->
                        <div class="form-group">
                            <label class="form-label">Devise</label>
                            <select name="devise" class="form-input" required>
                                <c:forEach var="devise" items="${devises}">
                                    <option value="${devise}">${devise}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Détails</label>
                            <input type="text" name="details" class="form-input" placeholder="Description de l'opération">
                        </div>
                        <button type="submit" class="button button-danger" style="width: 100%;">
                            💳 Retirer
                        </button>
                    </form>
                </div>
            </div>

            <!-- Actions rapides -->
            <div class="quick-actions">
                <div class="quick-card">
                    <div class="quick-icon">📊</div>
                    <div class="quick-title">Historique des opérations</div>
                    <div class="quick-description">Consultez l'historique complet de vos transactions</div>
                    <form action="${pageContext.request.contextPath}/operations-courant" method="get" style="margin: 0;">
                        <button class="button button-outline" type="submit" style="width: 100%;">
                            Voir les opérations
                        </button>
                    </form>
                </div>
                
                <!-- Section virement -->
                <div class="action-card">
                    <h3 class="section-title">Faire un virement</h3>
                    <form action="${pageContext.request.contextPath}/virement-courant" method="post">
                        <div class="form-group">
                            <label class="form-label">Montant</label>
                            <input type="number" name="montant" step="0.01" required class="form-input" placeholder="0.00">
                        </div>

                        <!-- À ajouter à côté de chaque input number -->
                        <div class="form-group">
                            <label class="form-label">Devise</label>
                            <select name="devise" class="form-input" required>
                                <c:forEach var="devise" items="${devises}">
                                    <option value="${devise}">${devise}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Destinataire</label>
                            <select name="identifiantDest" id="identifiantDest" required class="form-input">
                                <option value="">Sélectionnez un destinataire</option>
                                <c:forEach items="${utilisateurs}" var="utilisateur">
                                    <option value="${utilisateur.identifiant}">
                                        ${utilisateur.identifiant} - ${utilisateur.nom}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Détails</label>
                            <input type="text" name="details" class="form-input" placeholder="Description de l'opération">
                        </div>
                        <button type="submit" class="button button-danger" style="width: 100%;">
                            💳 Virer
                        </button>
                    </form>
                </div>

                <div class="quick-card">
                    <div class="quick-icon">📊</div>
                    <div class="quick-title">Virements</div>
                    <div class="quick-description">Valider, annuler ou modifier un virement</div>
                    <form action="${pageContext.request.contextPath}/virement/list" method="get" style="margin: 0;">
                        <button class="button button-outline" type="submit" style="width: 100%;">
                            Voir les virements
                        </button>
                    </form>
                </div>

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

        <div>
            <h6>Voir banquier connectee</h6>
            <a href="${pageContext.request.contextPath}/getBanquier">Voir le banquier</a>
        </div>

    </div>
</body>
</html>