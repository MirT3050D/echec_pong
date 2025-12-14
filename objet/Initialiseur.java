// package objet;

// import reseau.PieceFactory;

// import javax.naming.NamingException;
// import java.util.ArrayList;

// /**
//  * Classe responsable de l'initialisation du plateau de jeu
//  * Peut initialiser depuis la base de données via EJB ou directement
//  */
// public class Initialiseur {
    
//     private static final String DEFAULT_SERVER_HOST = "localhost";
//     private static final int DEFAULT_SERVER_PORT = 8080;
//     private static final String DEFAULT_USERNAME = "Mir";
//     private static final String DEFAULT_PASSWORD = "admin123";
    
//     /**
//      * Initialise un plateau depuis la dernière configuration sauvegardée dans la base
//      * @return Plateau initialisé ou null si erreur
//      */
//     public static Plateau initialiserLocal(int nbPiece, int vieParPiece) {
//         Piece[][] pieces = PieceFactory.initialiserPieces(nbPiece, vieParPiece);
//         return new Plateau(0, nbPiece, pieces);
//     }

//     public static Plateau initialiserLocal(int nbPiece, ArrayList<Integer> vies) {
//         Piece[][] pieces = PieceFactory.(nbPiece, vies);
//         return new Plateau(0, nbPiece, pieces);
//     }
// }
