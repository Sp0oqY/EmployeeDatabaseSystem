import java.io.IOException;
import java.util.Scanner;

public class Main 
{
    public static void main(String[] args) throws IOException
    {
        DatabazaZamestnancov db = new DatabazaZamestnancov();

        if (!db.nacitajZSQL()) 
        {
            System.out.println(Farby.CERVENA + "\nSQL dáta sa nenašli, skúšam načítať data.bin..." + Farby.RESET);
            db.nacitajData();
        }

        Scanner sc = new Scanner(System.in);
        boolean bezi = true;

        while(bezi)
        {
            System.out.println(Farby.MODRA + "\n---- DATABÁZOVY SYSTÉM ZAMESTNANCOV ----" + Farby.RESET);
            System.out.println("\na) Pridať zamestnanca (Analytik/Špecialista)");
            System.out.println("b) Pridať spoluprácu (podľa ID)");
            System.out.println("c) Odobrať zamestnanca (podľa ID)");
            System.out.println("d) Výpis informácie o zamestnancovi (podľa ID)");
            System.out.println("e) Spustiť dovednosť zamestnanca (Analýza/Riziko)");
            System.out.println("f) Abecedný výpis zoznamu");
            System.out.println("g) Štatistika");
            System.out.println("h) Počet zamestnancov v skupinách");
            System.out.println("i) Uložiť zamestnanca do textového súboru");
            System.out.println("j) Načítať zamestnanca z textového súboru");
            System.out.println("k) Výpis dát z SQL databázy");
            System.out.println(Farby.CERVENA + "x) Ukončiť" + Farby.RESET);
            System.out.print("\nVyberte si z možností: ");

            String volba = sc.nextLine().toLowerCase();

            try
            {
                switch(volba)
                {
                    case "a" :
                    System.out.print("\nTyp (analytik/specialista): ");
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
                    System.out.print("\nZadajte ID zamestnanca: ");
                    int id1 = Integer.parseInt(sc.nextLine());
                    System.out.print("Zadajte ID kolegu: ");
                    int id2 = Integer.parseInt(sc.nextLine());
                    System.out.print("Úroveň (" + Farby.CERVENA + "Zlá" + Farby.RESET + " / " + Farby.ZLTA + "Priemerná" + Farby.RESET + " / " + Farby.ZELENA +  "Dobrá" + Farby.RESET + "): ");
                    String urovenText = sc.nextLine();

                    if (!urovenText.isEmpty())
                    {
                        urovenText = urovenText.substring(0, 1).toUpperCase() + urovenText.substring(1).toLowerCase().trim();
                    }

                    try
                    {
                        Uroven ur = Uroven.valueOf(urovenText);
                        db.pridajSpolupracu(id1, id2, ur);
                    }

                    catch(IllegalArgumentException e){
                        System.out.print(Farby.CERVENA + "\nNeplatná úroveň. Použite: Zla, Priemerna alebo Dobra." + Farby.RESET);
                    }
                    break;

                    case "c":
                        System.out.print("\nZadajte ID na vymazanie: ");
                        int idNaZmazanie = Integer.parseInt(sc.nextLine());
                        db.odoberZamestnanca(idNaZmazanie);
                        break;

                    case "d":
                        System.out.print("\nZadajte ID zamestnanca pre vyhľadávanie: ");
                            int idHladany = Integer.parseInt(sc.nextLine());
                            db.vypisZamestnanca(idHladany);
                        break;

                    case "e":
                        System.out.print("\nZadajte ID pre spustenie dovednosti: ");
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
                        System.out.print("\nZadajte ID zamestnanca na uloženie: ");
                        int idNaUlozenie = Integer.parseInt(sc.nextLine());
                        System.out.print("Zadajte názov súboru (v tvare xx.txt) ");
                        String menoSuboruUlozit = sc.nextLine();
                        db.ulozZamestnancaDoSuboru(idNaUlozenie, menoSuboruUlozit);
                        break;

                    case "j":
                        System.out.print("\nZadajte názov súboru na načítanie: ");
                        String menoSuboruNacitat = sc.nextLine();
                        db.nacitajZamestnancaZoSuboru(menoSuboruNacitat);
                        break;
                    
                    case "k":
                        db.vypisZSQL();
                        break;

                    case "x":
                        db.ulozData();
                        System.out.println(Farby.AZUROVA + "\nUkladám dáta do SQL databázy...\n" + Farby.RESET);
                        db.ulozDoSQL();
                        bezi = false;
                        System.out.println(Farby.CERVENA + "\nProgram sa ukončuje..." + Farby.RESET);
                        break;

                    default:
                        System.out.println(Farby.CERVENA + "\nNeplatná voľba, skúste to znova." + Farby.RESET);
                }
            }

            catch (NumberFormatException e)
            {
                System.out.print(Farby.CERVENA + "\nZadajte platné číslo." + Farby.RESET);
            }
            catch (Exception e)
            {
                System.out.print(Farby.CERVENA + "\nVyskytla sa neočakávaná chyba: " + e.getMessage() + Farby.RESET);
            }
        }
        sc.close();
    }
}
