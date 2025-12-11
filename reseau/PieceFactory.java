package reseau;

import objet.Piece;
import java.util.*;

/**
 * Fabrique pour créer les pièces de jeu en mode réseau
 */
public class PieceFactory {
    
    /**
     * Initialise les pièces avec vies individuelles (pour réseau)
     * @param nbPiece nombre de colonnes (2, 4, 6, 8)
     * @param vies ArrayList des vies individuelles pour les 16 positions
     * @return tableau 2D [joueur][pièce] contenant toutes les pièces
     */
    public static Piece[][] initialiserPiecesAvecVies(int nbPiece, ArrayList<Integer> vies) {
        if (vies == null || vies.isEmpty()) {
            // Fallback : utiliser vie=3 pour tous
            return initialiserPieces(nbPiece, 3);
        }
        
        List<Piece> allPieces = Piece.getAll();
        Set<Integer> positionsToShow = getPositionsForNbPiece(nbPiece);
        
        // Compte le nombre de pièces : 2 exemplaires pour Tours/Cavaliers/Fous, 1 pour Roi/Reine, nbPiece pour pions
        int nbPiecesToCreate = 0;
        for (Piece p : allPieces) {
            if (positionsToShow.contains(p.getPosition())) {
                if (p.getPosition() == 9) {
                    nbPiecesToCreate += nbPiece;  // Pions
                } else if (p.getPosition() == 4 || p.getPosition() == 5) {
                    if (!hasBeenCounted(p.getPosition(), allPieces, positionsToShow)) {
                        nbPiecesToCreate += 1;  // Roi ou Reine : 1 exemplaire
                    }
                } else {
                    if (!hasBeenCounted(p.getPosition(), allPieces, positionsToShow)) {
                        nbPiecesToCreate += 2;  // Tours, Cavaliers, Fous : 2 exemplaires
                    }
                }
            }
        }
        
        // Crée deux joueurs avec le bon nombre de pièces
        Piece[][] pieces = new Piece[2][nbPiecesToCreate];
        
        for (int joueur = 0; joueur < 2; joueur++) {
            int index = 0;
            Set<Integer> processedPositions = new HashSet<>();
            
            for (Piece base : allPieces) {
                if (positionsToShow.contains(base.getPosition())) {
                    int pieceIndex = base.getPosition() - 1;  // Position dans le tableau vies (0-14 pour positions 1-15, position 9 = index 8)
                    
                    if (base.getPosition() == 9) {
                        // Créer nbPiece pions avec la même vie
                        int vieActuelle = (pieceIndex >= 0 && pieceIndex < vies.size()) ? vies.get(pieceIndex) : 3;
                        for (int i = 0; i < nbPiece; i++) {
                            pieces[joueur][index] = new Piece(base.getId(), vieActuelle, base.getNom(), base.getPosition());
                            index++;
                        }
                    } else if ((base.getPosition() == 4 || base.getPosition() == 5) && !processedPositions.contains(base.getPosition())) {
                        // Roi ou Reine : 1 exemplaire uniquement
                        int vieActuelle = (pieceIndex >= 0 && pieceIndex < vies.size()) ? vies.get(pieceIndex) : 3;
                        processedPositions.add(base.getPosition());
                        pieces[joueur][index] = new Piece(base.getId(), vieActuelle, base.getNom(), base.getPosition());
                        index++;
                    } else if (!processedPositions.contains(base.getPosition())) {
                        // Tours, Cavaliers, Fous : 2 exemplaires
                        int vieActuelle = (pieceIndex >= 0 && pieceIndex < vies.size()) ? vies.get(pieceIndex) : 3;
                        processedPositions.add(base.getPosition());
                        for (int i = 0; i < 2; i++) {
                            pieces[joueur][index] = new Piece(base.getId(), vieActuelle, base.getNom(), base.getPosition());
                            index++;
                        }
                    }
                }
            }
        }
        
        return pieces;
    }
    
    /**
     * Initialise les pièces pour une partie avec la configuration donnée
     * @param nbPiece nombre de colonnes (2, 4, 6, 8)
     * @param vieParPiece vie initiale de chaque pièce
     * @return tableau 2D [joueur][pièce] contenant toutes les pièces
     */
    public static Piece[][] initialiserPieces(int nbPiece, int vieParPiece) {
        List<Piece> allPieces = Piece.getAll();
        Set<Integer> positionsToShow = getPositionsForNbPiece(nbPiece);
        
        // Compte le nombre de pièces : 2 pour Tours/Cavaliers/Fous, 1 pour Roi/Reine, nbPiece pour pions
        int nbPiecesToCreate = 0;
        for (Piece p : allPieces) {
            if (positionsToShow.contains(p.getPosition())) {
                if (p.getPosition() == 9) {
                    // Les pions : nbPiece exemplaires
                    nbPiecesToCreate += nbPiece;
                } else if (p.getPosition() == 4 || p.getPosition() == 5) {
                    // Roi ou Reine : 1 exemplaire
                    if (!hasBeenCounted(p.getPosition(), allPieces, positionsToShow)) {
                        nbPiecesToCreate += 1;
                    }
                } else {
                    // Tours, Cavaliers, Fous : 2 exemplaires
                    if (!hasBeenCounted(p.getPosition(), allPieces, positionsToShow)) {
                        nbPiecesToCreate += 2;
                    }
                }
            }
        }
        
        // Crée deux joueurs avec le bon nombre de pièces
        Piece[][] pieces = new Piece[2][nbPiecesToCreate];
        
        for (int joueur = 0; joueur < 2; joueur++) {
            int index = 0;
            Set<Integer> processedPositions = new HashSet<>();
            
            for (Piece base : allPieces) {
                if (positionsToShow.contains(base.getPosition())) {
                    if (base.getPosition() == 9) {
                        // Créer nbPiece pions
                        for (int i = 0; i < nbPiece; i++) {
                            pieces[joueur][index] = new Piece(base.getId(), vieParPiece, base.getNom(), base.getPosition());
                            index++;
                        }
                    } else if ((base.getPosition() == 4 || base.getPosition() == 5) && !processedPositions.contains(base.getPosition())) {
                        // Roi ou Reine : 1 exemplaire
                        processedPositions.add(base.getPosition());
                        pieces[joueur][index] = new Piece(base.getId(), vieParPiece, base.getNom(), base.getPosition());
                        index++;
                    } else if (!processedPositions.contains(base.getPosition())) {
                        // Tours, Cavaliers, Fous : 2 exemplaires
                        processedPositions.add(base.getPosition());
                        for (int i = 0; i < 2; i++) {
                            pieces[joueur][index] = new Piece(base.getId(), vieParPiece, base.getNom(), base.getPosition());
                            index++;
                        }
                    }
                }
            }
        }
        
        return pieces;
    }
    
    /**
     * Vérifie si une position a déjà été traitée
     */
    private static boolean hasBeenCounted(int position, List<Piece> allPieces, Set<Integer> positionsToShow) {
        int count = 0;
        for (Piece p : allPieces) {
            if (p.getPosition() == position && positionsToShow.contains(position)) {
                count++;
                if (count > 1) return true;
            }
        }
        return false;
    }
    
    /**
     * Retourne les positions de pièces à afficher selon le nombre de colonnes
     */
    private static Set<Integer> getPositionsForNbPiece(int nbPiece) {
        Set<Integer> positions = new HashSet<>();
        
        if (nbPiece == 2) {
            positions.add(4);  // Reine
            positions.add(5);  // Roi
            positions.add(9);  // Pions
        } else if (nbPiece == 4) {
            positions.add(3);  // Fou
            positions.add(4);  // Reine
            positions.add(5);  // Roi
            positions.add(9);  // Pions
        } else if (nbPiece == 6) {
            positions.add(2);  // Cavalier
            positions.add(3);  // Fou
            positions.add(4);  // Reine
            positions.add(5);  // Roi
            positions.add(9);  // Pions
        } else { // 8 ou plus
            positions.add(1);  // Tour
            positions.add(2);  // Cavalier
            positions.add(3);  // Fou
            positions.add(4);  // Reine
            positions.add(5);  // Roi
            positions.add(9);  // Pions
        }
        
        return positions;
    }
}
