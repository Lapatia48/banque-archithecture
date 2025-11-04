<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Liste des Virements</title>
    <style>
        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .en_attente { background-color: #fff3cd; }
        .valide { background-color: #d1edff; }
        .execute { background-color: #d4edda; }
        .annule { background-color: #f8d7da; }
        .refuse { background-color: #f8d7da; }
        .actions { display: flex; gap: 5px; }
        .button { padding: 5px 10px; text-decoration: none; border: none; cursor: pointer; }
        .button-success { background: #28a745; color: white; }
        .button-danger { background: #dc3545; color: white; }
        .button-warning { background: #ffc107; color: black; }
        .navigation {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border-top: 1px solid var(--border);
            text-align: center;
        }
        .filters {
            margin: 20px 0;
            padding: 15px;
            background-color: #f8f9fa;
            border-radius: 5px;
            display: flex;
            gap: 15px;
            align-items: center;
            flex-wrap: wrap;
        }
        .filter-group {
            display: flex;
            flex-direction: column;
            gap: 5px;
        }
        .filter-group label {
            font-weight: bold;
            font-size: 0.9em;
        }
        .filter-group select, .filter-group input {
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .reset-btn {
            background: #6c757d;
            color: white;
            padding: 8px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .pagination-info {
            margin: 10px 0;
            font-style: italic;
            color: #666;
        }
    </style>
</head>
<body>
    <h1>📋 Liste des Virements</h1>

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

    <!-- Filtres -->
    <div class="filters">
        <div class="filter-group">
            <label for="itemsPerPage">Nombre d'éléments :</label>
            <select id="itemsPerPage">
                <option value="10">10</option>
                <option value="25">25</option>
                <option value="50">50</option>
                <option value="100">100</option>
                <option value="all">Tous</option>
            </select>
        </div>
        
        <div class="filter-group">
            <label for="statutFilter">Statut :</label>
            <select id="statutFilter">
                <option value="">Tous les statuts</option>
                <option value="1">En attente</option>
                <option value="11">Validé</option>
                <option value="21">Exécuté</option>
                <option value="0">Annulé</option>
                <option value="-1">Refusé</option>
            </select>
        </div>
        
        <div class="filter-group">
            <label for="destinataireFilter">Destinataire :</label>
            <input type="text" id="destinataireFilter" placeholder="Rechercher par destinataire...">
        </div>
        
        <button class="reset-btn" onclick="resetFilters()">Réinitialiser</button>
    </div>

    <div class="pagination-info" id="paginationInfo"></div>

    <table id="virementsTable">
        <thead>
            <tr>
                <th>ID</th>
                <th>Source</th>
                <th>Destination</th>
                <th>Montant</th>
                <th>Statut</th>
                <th>Date création</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody id="virementsBody">
            <c:forEach var="virement" items="${tousVirements}">
                <tr class="${virement.statutToText().toLowerCase().replace('_', '')}" 
                    data-statut="${virement.statut}" 
                    data-destinataire="${virement.identifiantDestination}">
                    <td>${virement.idVirement}</td>
                    <td>${virement.identifiantSource}</td>
                    <td>${virement.identifiantDestination}</td>
                    <td>${virement.montant} ${virement.devise}</td>
                    <td>${virement.statutToText()}</td>
                    <td>${virement.dateCreation}</td>
                    <td class="actions">
                        <!-- Voir détails -->
                        <a href="${pageContext.request.contextPath}/virement/details/${virement.idVirement}" 
                           class="button" style="background: #007bff; color: white;"> Voir</a>
                        
                        <!-- Actions selon statut -->
                        <c:if test="${virement.statut == 1}">
                            <form action="${pageContext.request.contextPath}/virement/valider/${virement.idVirement}" method="post" style="display: inline;">
                                <button type="submit" class="button button-success"> Valider</button>
                            </form>
                            <form action="${pageContext.request.contextPath}/virement/annuler/${virement.idVirement}" method="post" style="display: inline;">
                                <input type="hidden" name="motif" value="Annulation depuis liste">
                                <button type="submit" class="button button-danger"> Annuler</button>
                            </form>
                        </c:if>
                        
                        <c:if test="${virement.statut == 11}">
                            <form action="${pageContext.request.contextPath}/virement/executer/${virement.idVirement}" method="post" style="display: inline;">
                                <button type="submit" class="button button-success"> Exécuter</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty tousVirements}">
        <p>Aucun virement trouvé.</p>
    </c:if>

    <script>
        // Stocker toutes les lignes originales
        const allRows = Array.from(document.querySelectorAll('#virementsBody tr'));
        let currentVisibleRows = [...allRows];
        
        // Éléments DOM
        const itemsPerPageSelect = document.getElementById('itemsPerPage');
        const statutFilter = document.getElementById('statutFilter');
        const destinataireFilter = document.getElementById('destinataireFilter');
        const virementsBody = document.getElementById('virementsBody');
        const paginationInfo = document.getElementById('paginationInfo');
        
        // Initialisation
        document.addEventListener('DOMContentLoaded', function() {
            updateDisplay();
            setupEventListeners();
        });
        
        function setupEventListeners() {
            itemsPerPageSelect.addEventListener('change', updateDisplay);
            statutFilter.addEventListener('change', updateDisplay);
            destinataireFilter.addEventListener('input', updateDisplay);
        }
        
        function updateDisplay() {
            const itemsPerPage = itemsPerPageSelect.value;
            const selectedStatut = statutFilter.value;
            const destinataireSearch = destinataireFilter.value.toLowerCase();
            
            // Filtrer les lignes
            currentVisibleRows = allRows.filter(row => {
                const statut = row.getAttribute('data-statut');
                const destinataire = row.getAttribute('data-destinataire').toLowerCase();
                
                // Filtre par statut
                if (selectedStatut && statut !== selectedStatut) {
                    return false;
                }
                
                // Filtre par destinataire
                if (destinataireSearch && !destinataire.includes(destinataireSearch)) {
                    return false;
                }
                
                return true;
            });
            
            // Afficher/masquer les lignes
            allRows.forEach(row => row.style.display = 'none');
            currentVisibleRows.forEach(row => row.style.display = '');
            
            // Gérer la pagination
            if (itemsPerPage === 'all') {
                // Afficher tous les éléments
                currentVisibleRows.forEach((row, index) => {
                    row.style.display = '';
                });
                updatePaginationInfo(currentVisibleRows.length, currentVisibleRows.length, 1);
            } else {
                const itemsPerPageNum = parseInt(itemsPerPage);
                const totalItems = currentVisibleRows.length;
                const totalPages = Math.ceil(totalItems / itemsPerPageNum);
                
                // Afficher seulement les éléments de la première page
                currentVisibleRows.forEach((row, index) => {
                    row.style.display = index < itemsPerPageNum ? '' : 'none';
                });
                
                updatePaginationInfo(totalItems, Math.min(itemsPerPageNum, totalItems), 1);
            }
        }
        
        function updatePaginationInfo(total, showing, currentPage) {
            if (total === 0) {
                paginationInfo.textContent = 'Aucun virement trouvé';
            } else if (itemsPerPageSelect.value === 'all') {
                paginationInfo.textContent = `Affichage de tous les ${total} virement(s)`;
            } else {
                paginationInfo.textContent = `Affichage des virements ${showing > 0 ? 1 : 0}-${showing} sur ${total} total`;
            }
        }
        
        function resetFilters() {
            itemsPerPageSelect.value = '10';
            statutFilter.value = '';
            destinataireFilter.value = '';
            updateDisplay();
        }
        
        // Exposer les fonctions globalement pour les boutons
        window.resetFilters = resetFilters;
    </script>
</body>
</html>