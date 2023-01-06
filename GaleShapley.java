
//Anouar


import java.io.*;
import java.util.*;

public class GaleShapley{

  private Stack sue ; //pile qui va contenir les employeurs non appariés
  private HeapPriorityQueue <Integer,Integer> pq[] ; //tableau de files à priorité
  private int[] students; //tableau pour représenter l'appariement des étudiants avec les employeurs
  private int[] employers; //tableau pour représenter l'appariement des employeurs avec les étudiants
  private int[][] a; //a[s][e] étant le rang donné par l'étudiant s à l'employeur e
  private Scanner x; //afin de scanner les fichiers
  private String[] studentsNames; //tableau qui enregistre les noms des etudiants
  private String[] employersNames; //tableau qui enregistre les noms des employeurs


  //effectue la lecture du fichier d'entrée et qui effectue les etapes d initialisation
  public void initialize(File fileName){
      try{
        x = new Scanner(fileName);
      }
      catch(Exception e){ //lance une exception lorsque le fichier est introuvable
        System.out.println("file not found");
      }
      int nbSe = x.nextInt(); //nombre d etudiants et d employeurs
      x.nextLine(); //pour revenir à la ligne
      studentsNames = new String[nbSe];
      employersNames = new String[nbSe];
      for(int i=0 ;i<nbSe; i++){
        employersNames[i]= x.nextLine(); //les noms des employeurs sont lus en premier chacun sur une ligne
      }
      for(int i=0 ;i<nbSe; i++){
        studentsNames[i]= x.nextLine(); //les noms des etudiants sont lus ensuite chacun sur une ligne
      }

      pq = new HeapPriorityQueue[nbSe];
      for (int i=0 ; i<nbSe;i++){ //on cree une file de priorite pour chaque etudiant/employeur
        pq[i]= createPriorityQueue(nbSe);
      }

      a = new int[nbSe][nbSe];
      String[] parts;
      for(int i=0 ; i<nbSe ; i++){
        for(int j=0 ; j<nbSe ; j++){
          parts = x.next().split(",");//pour diviser les chiffres de chaque pair qui sont sépares par une virgule dans le fichier
          pq[i].insert(Integer.parseInt(parts[0]),j); //le premier chiffre de chaque pair dans le fichier correspond au rang donne par l employeur i a l etudiant j
          a[j][i]= Integer.parseInt(parts[1]); //le deuxieme chiffre de chaque paire du fichier correspond au rang donne par un etudiant j a un employeur i
        }
      }

      x.close();
      sue = new Stack(nbSe);
      students = new int[nbSe];
      employers = new int[nbSe];
      for(int i=0 ; i<nbSe ; i++){ //on initialise les elements a -1 puisque personne n est apparié pour le moment
        students[i]= -1;
        employers[i]= -1;
        sue.push(i); //empiler tous les employeurs dans cette pile en debutant par l'employeur 0
      }

  }


  //methode qui permet de créer une file a priorité
  public HeapPriorityQueue <Integer,Integer> createPriorityQueue(int n){
    return new HeapPriorityQueue <>(n);
  }

  //execute l algorithme de Gale-Shapley
  public HeapPriorityQueue <Integer,Integer> execute(){
    while(!sue.isEmpty()){ //on execute tant que tous les employeurs soit appariés
      int e = sue.pop(); // e is looking for a student
      int s = pq[e].removeMin().getValue(); //most preferred student of e
      int ePrime = students[s];
      if(students[s]==-1){ //student is unmatched
        students[s]=e;
        employers[e]=s; //match(e,s)
      }else if(a[s][e]<a[s][ePrime]){ //s prefers e to employer ePrime
        students[s]=e;
        employers[e]=s; //Replace the match
        employers[ePrime]=-1; //now unmatched
        sue.push(ePrime);
      }else{
        sue.push(e);
      }
    }
    HeapPriorityQueue <Integer,Integer> stableMatches = createPriorityQueue(employers.length);
    for(int i=0;i<employers.length;i++){
      stableMatches.insert(i,employers[i]);
    }
    return stableMatches;
  }

  //methode qui permet de sauvegarder les matches dans un fichier
  public void save(File fileName){
    try{
      PrintWriter outpout = new PrintWriter(fileName);
      for(int i=0 ; i<students.length  ; i++){
        outpout.println("Match "+i+": "+employersNames[i]+" - "+ studentsNames[employers[i]] );
      }
      outpout.close();
    }catch(IOException ex){
      System.out.println("ERROR");
    }
  }


  public static void main(String[] args){

      Scanner scan = new Scanner(System.in);  // on cree un object Scanner
      System.out.print("Entrez le nom du fichier: "); // demande a l utilisateur le nom du fichier d entree
      String file = scan.nextLine();  // on lit l entrée de l'utilisateur
      File fileName = new File(file);
      GaleShapley matching = new GaleShapley();
      matching.initialize(fileName);
      matching.execute();
      try{
        File fileNameSave = new File("matches_"+file);
        fileNameSave.createNewFile(); //on crée le fichier dans le format prescrit
        matching.save(fileNameSave);
        System.out.println("les matches ont été sauvergardés avec succés!");
      }catch (IOException e) {
        System.out.println("error");
      }

    }
}
