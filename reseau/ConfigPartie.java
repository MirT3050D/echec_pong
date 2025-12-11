package reseau;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Données de configuration de partie
 */
public class ConfigPartie implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int nbPiece;      // Nombre de colonnes
    private int nbVie;        // Vie initiale (défaut si pas de vies individuelles)
    private ArrayList<Integer> vies;  // Vies individuelles pour chaque position de pièce
    
    // Constructeur 1 : avec vies uniformes (compatible ancien code)
    public ConfigPartie(int nbPiece, int nbVie) {
        this.nbPiece = nbPiece;
        this.nbVie = nbVie;
        this.vies = null;
    }
    
    // Constructeur 2 : avec vies individuelles
    public ConfigPartie(int nbPiece, int nbVie, ArrayList<Integer> vies) {
        this.nbPiece = nbPiece;
        this.nbVie = nbVie;
        this.vies = vies;
    }
    
    public int getNbPiece() { return nbPiece; }
    public int getNbVie() { return nbVie; }
    public ArrayList<Integer> getVies() { return vies; }
    
    @Override
    public String toString() {
        return "ConfigPartie{nbPiece=" + nbPiece + ", nbVie=" + nbVie + 
               ", vies=" + (vies != null ? vies.size() + " elements" : "null") + "}";
    }
}
