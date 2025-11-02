<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Tableau de bord - Banque Centrale</title>
    <style>
        /* Reset et base */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            color: #333;
        }
        
        /* Container principal */
        .dashboard-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 30px 20px;
        }
        
        /* En-tête */
        .header {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 20px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
            border: 1px solid rgba(255, 255, 255, 0.2);
        }
        
        .welcome-title {
            font-size: 2.5rem;
            font-weight: 300;
            color: #2c3e50;
            margin-bottom: 10px;
        }
        
        .welcome-subtitle {
            font-size: 1.1rem;
            color: #7f8c8d;
            font-weight: 400;
        }
        
        /* Section des comptes */
        .accounts-section {
            margin-bottom: 40px;
        }
        
        .section-title {
            font-size: 1.5rem;
            color: white;
            margin-bottom: 25px;
            text-align: center;
            font-weight: 500;
        }
        
        .accounts-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 25px;
            margin-bottom: 30px;
        }
        
        .account-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 16px;
            padding: 30px;
            text-decoration: none;
            color: inherit;
            transition: all 0.3s ease;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
            border: 1px solid rgba(255, 255, 255, 0.3);
            position: relative;
            overflow: hidden;
        }
        
        .account-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #667eea, #764ba2);
        }
        
        .account-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
            background: rgba(255, 255, 255, 1);
        }
        
        .account-icon {
            font-size: 2.5rem;
            margin-bottom: 15px;
            color: #667eea;
        }
        
        .account-name {
            font-size: 1.3rem;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 10px;
        }
        
        .account-description {
            color: #7f8c8d;
            line-height: 1.5;
            font-size: 0.95rem;
        }
        
        /* Boutons d'action */
        .actions-section {
            text-align: center;
        }
        
        .logout-btn {
            background: linear-gradient(135deg, #e74c3c, #c0392b);
            color: white;
            border: none;
            padding: 15px 40px;
            border-radius: 50px;
            font-size: 1rem;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(231, 76, 60, 0.3);
        }
        
        .logout-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(231, 76, 60, 0.4);
        }
        
        /* Responsive */
        @media (max-width: 768px) {
            .dashboard-container {
                padding: 20px 15px;
            }
            
            .welcome-title {
                font-size: 2rem;
            }
            
            .accounts-grid {
                grid-template-columns: 1fr;
            }
            
            .account-card {
                padding: 25px;
            }
        }
        
        /* Animations */
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .account-card {
            animation: fadeInUp 0.6s ease-out;
        }
        
        .account-card:nth-child(1) { animation-delay: 0.1s; }
        .account-card:nth-child(2) { animation-delay: 0.2s; }
        .account-card:nth-child(3) { animation-delay: 0.3s; }
    </style>
</head>
<body>
    <div class="dashboard-container">
        <!-- En-tête -->
        <div class="header">
            <h1 class="welcome-title">Bonjour, ${sessionScope.utilisateur.nom} !</h1>
            <p class="welcome-subtitle">Bienvenue sur votre espace personnel Banque Centrale</p>
        </div>
        
        <!-- Section des comptes -->
        <div class="accounts-section">
            <h2 class="section-title">Vos Comptes Bancaires</h2>
            
            <div class="accounts-grid">
                <!-- Carte Compte Dépôt -->

                <!-- <a href="${pageContext.request.contextPath}/depot" class="account-card">
                    <div class="account-icon">💰</div>
                    <h3 class="account-name">Compte Dépôt</h3>
                    <p class="account-description">
                        Gérez vos epargnes et consultez l'historique de vos transactions sécurisées.
                    </p>
                </a> -->
                
                <!-- Carte Compte Prêt -->

                <!-- <a href="${pageContext.request.contextPath}/pret" class="account-card">
                    <div class="account-icon">🏦</div>
                    <h3 class="account-name">Compte Prêt</h3>
                    <p class="account-description">
                        Accédez à vos services de prêt et suivez vos échéances de remboursement.
                    </p>
                </a> -->
                
                <!-- Carte Compte Courant -->
                <a href="${pageContext.request.contextPath}/compte-courant" class="account-card">
                    <div class="account-icon">💳</div>
                    <h3 class="account-name">Compte Courant</h3>
                    <p style="color: #e74c3c;"><b>${erreur}</b></p>
                    <p style="color: #e74c3c;"><b>${error}</b></p>
                    <p class="account-description">
                        Effectuez vos opérations courantes : dépôts, retraits et consultation du solde.
                    </p>
                </a>

                <!-- Carte Compte General -->

                <!-- <a href="${pageContext.request.contextPath}/central" class="account-card">
                    <div class ="account-icon">Central</div>
                    <h3 class = "account-name">Compte general</h3>
                    <p class="account-description">
                        Visualiser tout vos comptes
                    </p>
                </a> -->

            </div>
        </div>
        
        <!-- Actions -->
        <div class="actions-section">
            <form action="${pageContext.request.contextPath}/logout" method="get">
                <button class="logout-btn" type="submit">
                    🚪 Se Déconnecter
                </button>
            </form>
        </div>

        <div>
            <h6>Voir banquier connectee</h6>
            <a href="${pageContext.request.contextPath}/getBanquier">Voir le banquier</a>
        </div>

    </div>
    <p><a href="${pageContext.request.contextPath}/pourcentage/form">modifierr des pourcentages</a></p>
</body>
</html>