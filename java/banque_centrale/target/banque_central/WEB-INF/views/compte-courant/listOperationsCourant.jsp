<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Liste des Opérations - Compte Courant</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --primary: #1a237e;
            --primary-light: #534bae;
            --primary-dark: #000051;
            --secondary: #00b0ff;
            --accent: #ff4081;
            --success: #00c853;
            --warning: #ff9100;
            --error: #d50000;
            --text-primary: #263238;
            --text-secondary: #546e7a;
            --text-light: #78909c;
            --bg-primary: #f8fafc;
            --bg-secondary: #ffffff;
            --bg-hover: #f1f5f9;
            --border: #e2e8f0;
            --shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
            --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
            --radius: 12px;
            --transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: var(--text-primary);
            line-height: 1.6;
            min-height: 100vh;
            padding: 20px;
        }

        .container {
            max-width: 1400px;
            margin: 0 auto;
            background: white;
            border-radius: var(--radius);
            box-shadow: var(--shadow-lg);
            overflow: hidden;
            animation: slideUp 0.6s ease-out;
        }

        @keyframes slideUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .header {
            background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
            color: white;
            padding: 40px;
            text-align: center;
            position: relative;
            overflow: hidden;
        }

        .header::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1000 100" fill="%23ffffff" opacity="0.1"><polygon points="0,0 1000,50 1000,100 0,100"/></svg>');
            background-size: cover;
        }

        .header h1 {
            margin: 0;
            font-size: 2.8em;
            font-weight: 700;
            letter-spacing: -0.5px;
            position: relative;
            text-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .header p {
            margin: 15px 0 0 0;
            font-size: 1.3em;
            opacity: 0.9;
            font-weight: 300;
            position: relative;
        }

        .stats-bar {
            background: linear-gradient(135deg, var(--secondary) 0%, #0091ea 100%);
            color: white;
            padding: 20px 40px;
            display: flex;
            justify-content: space-around;
            align-items: center;
            font-size: 1.1em;
            font-weight: 500;
        }

        .stat-item {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .stat-icon {
            font-size: 1.4em;
        }

        .content {
            padding: 40px;
        }

        .error-message {
            background: linear-gradient(135deg, #ff5252 0%, var(--error) 100%);
            color: white;
            padding: 20px;
            border-radius: var(--radius);
            margin-bottom: 30px;
            display: flex;
            align-items: center;
            gap: 15px;
            box-shadow: var(--shadow);
            animation: shake 0.5s ease-in-out;
        }

        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            25% { transform: translateX(-5px); }
            75% { transform: translateX(5px); }
        }

        .error-icon {
            font-size: 1.5em;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin: 30px 0;
            font-size: 1.05em;
            background: white;
            border-radius: var(--radius);
            overflow: hidden;
            box-shadow: var(--shadow);
        }

        th {
            background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
            color: white;
            padding: 20px;
            text-align: left;
            font-weight: 600;
            font-size: 1.1em;
            position: relative;
        }

        td {
            padding: 18px 20px;
            border-bottom: 1px solid var(--border);
            transition: var(--transition);
        }

        tbody tr {
            transition: var(--transition);
        }

        tbody tr:nth-child(even) {
            background-color: #fafbff;
        }

        tbody tr:hover {
            background: linear-gradient(135deg, #f0f4ff 0%, #f8faff 100%);
            transform: translateX(8px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }

        .montant {
            font-weight: 700;
            font-size: 1.2em;
            text-align: right;
            position: relative;
            padding-right: 30px;
        }

        .montant::before {
            content: '';
            position: absolute;
            right: 0;
            top: 50%;
            transform: translateY(-50%);
            width: 20px;
            height: 20px;
            border-radius: 50%;
            background: currentColor;
            opacity: 0.2;
        }

        .depot {
            color: var(--success);
        }

        .retrait {
            color: var(--error);
        }

        .pret {
            color: var(--warning);
        }
        .virement {
            color: blue;
        }

        .type-badge {
            display: inline-block;
            padding: 6px 16px;
            border-radius: 20px;
            font-size: 0.9em;
            font-weight: 500;
            background: #f8f9fa;
            color: var(--text-secondary);
            border: 1px solid var(--border);
        }

        .empty-state {
            text-align: center;
            padding: 80px 40px;
            color: var(--text-light);
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border-radius: var(--radius);
            margin: 30px 0;
        }

        .empty-icon {
            font-size: 5em;
            margin-bottom: 25px;
            opacity: 0.5;
            animation: pulse 2s infinite;
        }

        @keyframes pulse {
            0%, 100% { transform: scale(1); }
            50% { transform: scale(1.1); }
        }

        .empty-state h3 {
            font-size: 1.8em;
            margin-bottom: 15px;
            color: var(--text-secondary);
        }

        .empty-state p {
            font-size: 1.1em;
            max-width: 500px;
            margin: 0 auto;
            line-height: 1.6;
        }

        .navigation {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            padding: 30px 40px;
            border-top: 1px solid var(--border);
            text-align: center;
        }

        .button {
            padding: 14px 32px;
            border: none;
            border-radius: var(--radius);
            font-size: 1.1em;
            cursor: pointer;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 10px;
            margin: 0 10px;
            font-weight: 600;
            transition: var(--transition);
            position: relative;
            overflow: hidden;
        }

        .button::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
            transition: left 0.5s;
        }

        .button:hover::before {
            left: 100%;
        }

        .button-primary {
            background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
            color: white;
            box-shadow: var(--shadow);
        }

        .button-primary:hover {
            transform: translateY(-3px);
            box-shadow: var(--shadow-lg);
        }

        /* Largeur des colonnes optimisée */
        th:nth-child(1), td:nth-child(1) { width: 14%; }
        th:nth-child(2), td:nth-child(2) { width: 18%; }
        th:nth-child(3), td:nth-child(3) { width: 16%; }
        th:nth-child(4), td:nth-child(4) { width: 16%; }
        th:nth-child(5), td:nth-child(5) { width: 36%; }

        /* Responsive */
        @media (max-width: 1024px) {
            .container {
                margin: 10px;
            }
            
            .header, .content {
                padding: 30px 25px;
            }
            
            .stats-bar {
                flex-direction: column;
                gap: 15px;
                text-align: center;
            }
        }

        @media (max-width: 768px) {
            body {
                padding: 10px;
                background: white;
            }
            
            .container {
                border-radius: 0;
                box-shadow: none;
            }
            
            .header h1 {
                font-size: 2.2em;
            }
            
            table {
                display: block;
                overflow-x: auto;
            }
            
            th, td {
                padding: 15px;
            }
            
            th:nth-child(1), td:nth-child(1),
            th:nth-child(2), td:nth-child(2),
            th:nth-child(3), td:nth-child(3),
            th:nth-child(4), td:nth-child(4),
            th:nth-child(5), td:nth-child(5) {
                width: auto;
                min-width: 120px;
            }
            
            .montant::before {
                display: none;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Historique des Opérations</h1>
            <p>Compte Courant Professionnel</p>
        </div>

        <div class="stats-bar">
            <div class="stat-item">
                <span class="stat-icon">📊</span>
                <span>Total des opérations: ${not empty operations ? operations.size() : 0}</span>
            </div>
            <div class="stat-item">
                <span class="stat-icon">🕒</span>
                <span>Dernière mise à jour: <script>document.write(new Date().toLocaleDateString('fr-FR'))</script></span>
            </div>
        </div>

        <div class="content">
            <c:if test="${not empty erreur}">
                <div class="error-message">
                    <span class="error-icon">⚠️</span>
                    <div>${erreur}</div>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${not empty operations}">
                    <table>
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Compte</th>
                                <th>Type d'opération</th>
                                <th>Montant</th>
                                <th>Détails</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="operation" items="${operations}">
                                <tr>
                                    <td><strong>${operation[0]}</strong></td>
                                    <td>${operation[1]}</td>
                                    <td>
                                        <span class="type-badge">${operation[2]}</span>
                                    </td>
                                    <td class="montant ${operation[2] == 'depot' ? 'depot' : operation[2] == 'pret' ? 'pret' : operation[2] == 'virement' ? 'virement' : 'retrait'}">                                        ${operation[3]} Ariary
                                    </td>
                                    <td>${operation[4]}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <div class="empty-icon">💳</div>
                        <h3>Aucune opération trouvée</h3>
                        <p>Aucune transaction n'a été effectuée sur ce compte récemment. Les nouvelles opérations apparaîtront ici automatiquement.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="navigation">
            <form action="${pageContext.request.contextPath}/compte-courant" method="get">
                <button class="button button-primary" type="submit">
                    <span>←</span> Retour au compte courant
                </button>
            </form>
        </div>

        <div>
            <h6>Voir banquier connectee</h6>
            <a href="${pageContext.request.contextPath}/getBanquier">Voir le banquier</a>
        </div>

    </div>

    <script>
        // Animation au chargement
        document.addEventListener('DOMContentLoaded', function() {
            const rows = document.querySelectorAll('tbody tr');
            rows.forEach((row, index) => {
                row.style.animationDelay = `${index * 0.1}s`;
                row.style.animation = 'slideUp 0.5s ease-out forwards';
                row.style.opacity = '0';
            });
        });
    </script>
</body>
</html>