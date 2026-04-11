import java.util.*;

public class Main {
    public static void main(String[] args){

        DatabazaZamestnancov db = new DatabazaZamestnancov();
        Scanner sc = new Scanner(System.in);
        boolean bezi = true;

        System.out.println("----DATABAZOVY SYSTEM ZAMESTNANCOV----");

        while(bezi){

            System.out.println("\nMENU:");
            System.out.println("a) Pridat zamestnanca (Analytik/Specialista)");
            System.out.println("b) Pridat spolupracu (cez ID)");
            System.out.println("c) Odobrat zamestnanca (podla ID)");
            System.out.println("d) Vypis informacie o zamestnancovi (cez ID)");
            System.out.println("e) Spustit dovednost zamestnanca (Analyza/Riziko)");
            System.out.println("f) Vypis zoznamu abecedne");
            System.out.println("g) Statistika");
            System.out.println("h) Pocet zamestnancov v skupinach");
            System.out.println("x) Ukoncit");
            System.out.print("Vasa volba: ");

            String volba = sc.nextLine().toLowerCase();

            switch(volba){
                case "a" :
                System.out.print("Typ (analytik/specialista): ");
                String typ = sc.nextLine();
                System.out.print("Meno: ");
                String meno = sc.nextLine();
                System.out.print("Priezvisko: ");
                String priezvisko = sc.nextLine();
                System.out.print("Rok narodenia: ");
                int rok = Integer.parseInt(sc.nextLine());

                db.pridajZamestnanca(typ, meno, priezvisko, rok);
                break;

                case "b":
                System.out.print("Zadajte ID zamestnanca: ");
                int id1 = Integer.parseInt(sc.nextLine());
                System.out.print("Zadajte ID kolegu: ");
                int id2 = Integer.parseInt(sc.nextLine());
                System.out.print("Uroven (Zla/Priemerna/Dobra): ");
                String urovenText = sc.nextLine();
                if (!urovenText.isEmpty()) {
                     urovenText = urovenText.substring(0, 1).toUpperCase() + urovenText.substring(1).toLowerCase();
                }
                try{
                    Uroven ur = Uroven.valueOf(urovenText);
                    db.pridajSpolupracu(id1, id2, ur);
                }catch(IllegalArgumentException e){
                    System.out.print("Chyba: neplatna uroven! Pouzite: Zla, Priemerna alebo Dobra.");
                }
                break;

                case "c":
                    System.out.print("Zadajte ID na vymazanie: ");
                    int idNaZmazanie = Integer.parseInt(sc.nextLine());
                    db.odoberZamestnanca(idNaZmazanie);
                    break;

                case "d":
                    System.out.print("Zadajte ID zamestnanca pre vyhladavanie: ");
                        int idHladany = Integer.parseInt(sc.nextLine());
                        db.vypisZamestnanca(idHladany);
                    break;

                case "e":
                    System.out.print("Zadajte ID pre spustenie dovednosti: ");
                    int idDovednost = Integer.parseInt(sc.nextLine());
                    db.spustitDovednost(idDovednost);
                    break;

                case "f":
                    db.vypisAbecedne();
                    break;

                case "g":
                    db.vypisStatistiku();
                    break;

                case "h":
                    db.vypisPocetZamestnancov();
                    break;

                case "x":
                    bezi = false;
                    System.out.println("Program sa ukoncuje...");
                    break;

                default:
                    System.out.println("Neplatna volba, skuste to znova.");
            }
            }
            sc.close();
        }
    }
