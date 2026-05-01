import java.io.IOException;
import java.util.*;

public class Main 
{
    public static void main(String[] args) throws IOException
    {
        DatabazaZamestnancov db = new DatabazaZamestnancov();

        System.out.println("SQL databáza");

        if (!db.nacitajZSQL()) 
        {
            System.out.println("SQL dáta sa nenašli, skúšam načítať data.bin...");
            db.nacitajData();
        }

        Scanner sc = new Scanner(System.in);
        boolean bezi = true;

        System.out.println("----DATABÁZOVY SYSTÉM ZAMESTNANCOV----");

        while(bezi)
        {

            System.out.println("\nMENU:");
            System.out.println("a) Pridať zamestnanca (Analytik/Špecialista)");
            System.out.println("b) Pridať spoluprácu (cez ID)");
            System.out.println("c) Odobrať zamestnanca (podľa ID)");
            System.out.println("d) Výpis informácie o zamestnancovi (cez ID)");
            System.out.println("e) Spustiť dovednosť zamestnanca (Analýza/Riziko)");
            System.out.println("f) Výpis zoznamu abecedne");
            System.out.println("g) Štatistika");
            System.out.println("h) Počet zamestnancov v skupinách");
            System.out.println("i) Uložiť zamestnanca do textového súboru");
            System.out.println("j) Načítať zamestnanca z textového súboru");
            System.out.println("x) Ukončiť");
            System.out.print("Vaša volba: ");

            String volba = sc.nextLine().toLowerCase();

            try
            {
                switch(volba)
                {
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
                    System.out.print("Úroveň (Zla/Priemerna/Dobra): ");
                    String urovenText = sc.nextLine();

                    if (!urovenText.isEmpty())
                    {
                        urovenText = urovenText.substring(0, 1).toUpperCase() + urovenText.substring(1).toLowerCase();
                    }

                    try
                    {
                        Uroven ur = Uroven.valueOf(urovenText);
                        db.pridajSpolupracu(id1, id2, ur);
                    }

                    catch(IllegalArgumentException e){
                        System.out.print("Neplatná úroveň. Použite: Zla, Priemerna alebo Dobra.");
                    }
                    break;

                    case "c":
                        System.out.print("Zadajte ID na vymazanie: ");
                        int idNaZmazanie = Integer.parseInt(sc.nextLine());
                        db.odoberZamestnanca(idNaZmazanie);
                        break;

                    case "d":
                        System.out.print("Zadajte ID zamestnanca pre vyhľadávanie: ");
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

                    case "i":
                        System.out.print("Zadajte ID zamestnanca na uloženie: ");
                        int idNaUlozenie = Integer.parseInt(sc.nextLine());
                        System.out.print("Zadajte názov súboru (v tvare xx.txt) ");
                        String menoSuboruUlozit = sc.nextLine();
                        db.ulozZamestnancaDoSuboru(idNaUlozenie, menoSuboruUlozit);
                        break;

                    case "j":
                        System.out.print("Zadajte názov súboru na načítanie: ");
                        String menoSuboruNacitat = sc.nextLine();
                        db.nacitajZamestnancaZoSuboru(menoSuboruNacitat);
                        break;

                    case "x":
                        db.ulozData();
                        System.out.println("Ukladám dáta do SQL databázy.");
                        db.ulozDoSQL();
                        bezi = false;
                        System.out.println("Program sa ukončuje...");
                        break;

                    default:
                        System.out.println("Neplatná voľba, skúste to znova.");
                }
            }

            catch (NumberFormatException e)
            {
                System.out.println("Zadajte platné číslo.");
            }
            catch (Exception e)
            {
                System.out.println("Vyskytla sa neočakávaná chyba: " + e.getMessage());
            }
        }
        sc.close();
    }
}
