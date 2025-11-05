<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Détails Virement #${virement.idVirement}</title>
    <style>
        .details { max-width: 600px; margin: 20px 0; }
        .detail-item { margin: 10px 0; padding: 10px; background: #f8f9fa; }
        .actions { margin: 20px 0; }
        .button { padding: 8px 15px; margin: 5px; text-decoration: none; border: none; cursor: pointer; }
        .button-success { background: #28a745; color: white; }
        .button-danger { background: #dc3545; color: white; }
        .button-warning { background: #ffc107; color: black; }
        .button-primary { background: #007bff; color: white; }
        .navigation {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border-top: 1px solid var(--border);
            text-align: center;
        }
    </style>
</head>
<body>
    <h1>📄 Détails du Virement #${virement.idVirement}</h1>

    <div class="navigation">
        <form action="${pageContext.request.contextPath}/compte-courant" method="get">
            <button class="button button-primary" type="submit">
                <span>←</span> Retour au compte courant
            </button>
        </form>
    </div>    

    <c:if test="${not empty message}">
        <p style="color: green;">${message}</p>
    </c:if>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>

    <div class="details">
        <div class="detail-item">
            <strong>ID:</strong> ${virement.idVirement}
        </div>
        <div class="detail-item">
            <strong>Source:</strong> ${virement.identifiantSource}
        </div>
        <div class="detail-item">
            <strong>Destination:</strong> ${virement.identifiantDestination}
        </div>
        <div class="detail-item">
            <strong>Montant:</strong> ${virement.montant} ${virement.devise}
        </div>
        <div class="detail-item">
            <strong>Frais:</strong> ${virement.fraisDeVirement}
        </div>
        <div class="detail-item">
            <strong>Détails:</strong> ${virement.details}
        </div>
        <div class="detail-item">
            <strong>Statut:</strong> ${virement.statutToText()}
        </div>
        <div class="detail-item">
            <strong>Créé par:</strong> ${virement.createdBy}
        </div>
        <div class="detail-item">
            <strong>Date création:</strong> ${virement.dateCreation}
        </div>
        <c:if test="${virement.dateExecution != null}">
            <div class="detail-item">
                <strong>Date exécution:</strong> ${virement.dateExecution}
            </div>
        </c:if>
        <c:if test="${not empty virement.motifRefus}">
            <div class="detail-item">
                <strong>Motif:</strong> ${virement.motifRefus}
            </div>
        </c:if>
    </div>

    <div class="actions">
        <!-- Actions selon statut -->
        <c:if test="${virement.statut == 1}">
            <form action="${pageContext.request.contextPath}/virement/valider/${virement.idVirement}" method="post" style="display: inline;">
                <button type="submit" class="button button-success"> Valider</button>
            </form>
            
            <button onclick="showRefusModal()" class="button button-danger"> Refuser</button>
            
            <button onclick="showModificationModal()" class="button button-warning"> Modifier</button>
            
            <form action="${pageContext.request.contextPath}/virement/annuler/${virement.idVirement}" method="post" style="display: inline;">
                <input type="hidden" name="motif" value="Annulation depuis détails">
                <button type="submit" class="button button-danger"> Annuler</button>
            </form>
        </c:if>
        
        <c:if test="${virement.statut == 11}">
            <form action="${pageContext.request.contextPath}/virement/executer/${virement.idVirement}" method="post" style="display: inline;">
                <button type="submit" class="button button-success"> Exécuter</button>
            </form>
            
            <form action="${pageContext.request.contextPath}/virement/annuler/${virement.idVirement}" method="post" style="display: inline;">
                <input type="hidden" name="motif" value="Annulation après validation">
                <button type="submit" class="button button-danger"> Annuler</button>
            </form>
        </c:if>
        <c:if test="${virement.statut == 21}">
            <form action="${pageContext.request.contextPath}/virement/annuler-execution/${virement.idVirement}" method="post" style="display: inline;">
                <input type="hidden" name="motif" value="Rollback">
                <button type="submit" class="button button-success"> Rollback</button>
            </form>
        </c:if>

        <a href="${pageContext.request.contextPath}/virement/list" class="button button-primary"> Liste complete</a>
    </div>

    <!-- Modal Refus -->
    <div id="refusModal" style="display: none; position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); background: white; padding: 20px; border: 1px solid #ccc;">
        <h3>Refuser le virement</h3>
        <form action="${pageContext.request.contextPath}/virement/refuser/${virement.idVirement}" method="post">
            <textarea name="motif" rows="3" cols="40" placeholder="Motif du refus..." required></textarea><br>
            <button type="submit" class="button button-danger">Confirmer le refus</button>
            <button type="button" onclick="closeModal('refusModal')" class="button">Annuler</button>
        </form>
    </div>

    <!-- Modal Modification -->
    <div id="modificationModal" style="display: none; position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); background: white; padding: 20px; border: 1px solid #ccc;">
        <h3>Modifier le virement</h3>
        <form action="${pageContext.request.contextPath}/virement/modifier/${virement.idVirement}" method="post">
            <label>Nouveau montant:</label>
            <input type="number" name="nouveauMontant" value="${virement.montant}" step="0.01" required><br>
            <label>Nouveaux détails:</label><br>
            <textarea name="nouveauxDetails" rows="3" cols="40" required>${virement.details}</textarea><br>
            <button type="submit" class="button button-warning">Modifier</button>
            <button type="button" onclick="closeModal('modificationModal')" class="button">Annuler</button>
        </form>
    </div>

    <script>
        function showRefusModal() {
            document.getElementById('refusModal').style.display = 'block';
        }
        
        function showModificationModal() {
            document.getElementById('modificationModal').style.display = 'block';
        }
        
        function closeModal(modalId) {
            document.getElementById(modalId).style.display = 'none';
        }
    </script>
</body>
</html>