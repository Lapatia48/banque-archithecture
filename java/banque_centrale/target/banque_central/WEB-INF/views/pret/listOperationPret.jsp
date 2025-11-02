<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Liste des opérations de prêt</title>
    <style>
        body { 
            font-family: Arial, sans-serif; 
            background: #f4f6f8; 
            padding: 20px;
            margin: 0;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }
        .header h1 {
            margin: 0;
            font-size: 2.5em;
        }
        .content {
            padding: 30px;
        }
        .error-message {
            color: #dc3545;
            text-align: center;
            padding: 15px;
            background: #f8d7da;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        table { 
            width: 100%; 
            border-collapse: collapse; 
            margin: 20px 0;
        }
        th, td { 
            padding: 12px 15px; 
            border: 1px solid #ddd; 
            text-align: left; 
        }
        th { 
            background-color: #667eea; 
            color: white; 
            font-weight: bold;
        }
        tr:nth-child(even) { 
            background-color: #f8f9fa; 
        }
        tr:hover { 
            background-color: #e9ecef; 
        }
        .negative-amount {
            color: #dc3545;
            font-weight: bold;
        }
        .positive-amount {
            color: #28a745;
            font-weight: bold;
        }
        .navigation {
            background: #f8f9fa;
            padding: 20px;
            border-top: 1px solid #e9ecef;
            text-align: center;
        }
        .button {
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            font-size: 1em;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            margin: 0 5px;
        }
        .button-primary {
            background-color: #667eea;
            color: white;
        }
        .button-primary:hover {
            background-color: #5a6fd8;
        }
        .button-outline {
            background-color: transparent;
            color: #667eea;
            border: 2px solid #667eea;
        }
        .button-outline:hover {
            background-color: #667eea;
            color: white;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Liste des opérations de prêt</h1>
        </div>

        <div class="content">
            <div id="error" class="error-message">
                <c:if test="${not empty erreur}">
                    ${erreur}
                </c:if>
            </div>

            <table id="pretTable">
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
                    <!-- Les données seront insérées ici par JavaScript -->
                </tbody>
            </table>
        </div>

        <div class="navigation">
            <form action="${pageContext.request.contextPath}/pret" method="get" style="display: inline;">
                <button class="button button-primary" type="submit">Retour aux prêts</button>
            </form>
            <form action="${pageContext.request.contextPath}/accueil" method="get" style="display: inline;">
                <button class="button button-outline" type="submit">Accueil</button>
            </form>
        </div>
    </div>

    <script>
        // Récupérer le JSON depuis le modèle JSP
        let operationsJson = '${operations}';
        
        try {
            // Parser le JSON
            let operations = JSON.parse(operationsJson);

            let tbody = document.querySelector("#pretTable tbody");

            operations.forEach(op => {
                let tr = document.createElement("tr");

                // Date
                let dateTd = document.createElement("td");
                let date = new Date(op.date_operation);
                dateTd.textContent = date.toLocaleString();
                tr.appendChild(dateTd);

                // Compte
                let compteTd = document.createElement("td");
                compteTd.textContent = "Compte " + op.compte;
                tr.appendChild(compteTd);

                // Type d'opération
                let operationTd = document.createElement("td");
                operationTd.textContent = op.type_operation;
                tr.appendChild(operationTd);

                // Montant
                let montantTd = document.createElement("td");
                montantTd.textContent = op.montant + " Ariary";
                if (op.montant < 0) {
                    montantTd.className = "negative-amount";
                } else {
                    montantTd.className = "positive-amount";
                }
                tr.appendChild(montantTd);

                // Détails
                let detailsTd = document.createElement("td");
                detailsTd.textContent = op.details || "";
                tr.appendChild(detailsTd);

                tbody.appendChild(tr);
            });
        } catch (e) {
            document.getElementById("error").textContent = "Erreur lors de l'affichage des opérations : " + e.message;
        }
    </script>
</body>
</html>