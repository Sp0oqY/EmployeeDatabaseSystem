import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabazaZamestnancov
{
    private List <Zamestnanec> zamestnanci = new ArrayList<>();
    private int dalsieID = 1;

    private static final String DB_URL = "jdbc:h2:./databaza_zamestnancov";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";

    public void pridajZamestnanca(String typ, String meno, String priezvisko, int rokNarodenia)
    {
        Zamestnanec novyZamestnanec;
        int prideleneID = dalsieID++;

        if(typ.equalsIgnoreCase("analytik"))
        {
            novyZamestnanec = new DatovyAnalytik(prideleneID, meno, priezvisko, rokNarodenia);        
        }
        else
        {
            novyZamestnanec = new BezpecnostnySpecialista(prideleneID, meno, priezvisko, rokNarodenia);
        }

        zamestnanci.add(novyZamestnanec);
        System.out.println(Farby.ZELENA + "\nZamestnanec bol pridaný s ID: " + prideleneID + Farby.RESET);
        automatickeUlozenie();
    }

    private void pridajNacitanehoZamestnanca(Zamestnanec z)
    {
        if (z != null)
        {
            zamestnanci.add(z);
            if (z.getId() >= dalsieID)
            {
                dalsieID = z.getId() + 1;
            }
        }
    }

    public Zamestnanec najdiPodlaID(int id)
    {
        for(Zamestnanec z : zamestnanci)
        {
            if(z.getId() == id)
            {
                return z;
            }
        }
        return null;
    }

    public void pridajSpolupracu(int idZamestnanca, int idKolega, Uroven uroven)
    {
        Zamestnanec z1 = najdiPodlaID(idZamestnanca);
        Zamestnanec z2 = najdiPodlaID(idKolega);

        if(z1 != null && z2 != null)
        {
            z1.pridajSpolupracu(z2, uroven);
            z2.pridajSpolupracu(z1, uroven);
            System.out.println(Farby.ZELENA + "\nSpolupráca nastavená." + Farby.RESET);
        }
        else
        {
            System.out.println(Farby.CERVENA + "\nJeden zo zamestnancov nebol nájdený." + Farby.RESET);
        }

        automatickeUlozenie();
    }

    public void odoberZamestnanca(int id)
    {
        Zamestnanec naOdstranenie = najdiPodlaID(id);

        if(naOdstranenie == null)
        {
            System.out.println(Farby.CERVENA + "\nZamestnanec neexistuje." + Farby.RESET);
            return;
        }

        for(Zamestnanec z : zamestnanci)
        {
            z.getSpolupracovnici().removeIf(s -> s.getKolega().getId() == id);
        }

        zamestnanci.remove(naOdstranenie);
        System.out.println(Farby.CERVENA + "\nZamestnanec s ID: " + id + " bol odstránený." + Farby.RESET);

        automatickeUlozenie();
    }

    public void vypisZamestnanca(int id)
    {
        Zamestnanec z = najdiPodlaID(id);

        if (z != null)
        {
            System.out.println(Farby.AZUROVA + "\n---- INFORMÁCIE O ZAMESTNANCOVI ----" + Farby.RESET);
            System.out.println("\nID: " + z.getId());
            System.out.println("Meno: " + z.getMeno() + " " + z.getPriezvisko());
            System.out.println("Rok narodenia: " + z.getRokNarodenia());

            int pocetKolegov = z.getSpolupracovnici().size();
            System.out.println("Počet spolupracovníkov: " + pocetKolegov);
        } 
        else 
        {
            System.out.println(Farby.CERVENA + "Zamestnanec s ID " + id + " nebol nájdený." + Farby.RESET);
        }
    }

    public void spustitDovednost(int id)
    {
        Zamestnanec z = najdiPodlaID(id);
        if (z != null)
        {
            System.out.println(Farby.AZUROVA + "\n---- SPÚŠŤAM ŠPECIÁLNU DOVEDNOSŤ ----" + Farby.RESET);
            z.vykonajAnalyzu(); 
        } 
        else 
        {
            System.out.println(Farby.CERVENA + "\nZamestnanec nenájdený." + Farby.RESET);
        }
    }

    public void vypisAbecedne()
    {
        if (zamestnanci.isEmpty())
        {
            System.out.println(Farby.CERVENA + "\nDatabáza je prázdna." + Farby.RESET);
            return;
        }
    
        List<Zamestnanec> zoradenyZoznam = new ArrayList<>(zamestnanci);
    
        zoradenyZoznam.sort(Comparator.comparing(Zamestnanec::getPriezvisko, String.CASE_INSENSITIVE_ORDER));
    
        System.out.println(Farby.AZUROVA + "\n---- ABECEDNÝ VÝPIS ZAMESTNANCOV ----" + Farby.RESET);
    
        System.out.println(Farby.FIALOVA + "\n[ DÁTOVÍ ANALYTICI ]" + Farby.RESET);
        for (Zamestnanec z : zoradenyZoznam)
        {
            if (z instanceof DatovyAnalytik)
            {
                System.out.println(z.getPriezvisko() + " " + z.getMeno() + " (ID: " + z.getId() + ")");
            }
        }

        System.out.println(Farby.FIALOVA + "\n[ BEZPEČNOSTNÍ ANALYTICI ]" + Farby.RESET);
        for (Zamestnanec z : zoradenyZoznam)
        {
            if (z instanceof BezpecnostnySpecialista)
            {
                System.out.println(z.getPriezvisko() + " " + z.getMeno() + " (ID: " + z.getId() + ")");
            }
        }
    }

    public void vypisStatistiku()
    {
        if(zamestnanci.isEmpty())
        {
            System.out.println(Farby.CERVENA + "\nDatabaza je prázdna a nie je možné spraviť štatistiku." + Farby.RESET);
            return;
        }

        Zamestnanec najviacVazieb = null;
        int maxVazieb = -1;

        int zla = 0;
        int priemerna = 0;
        int dobra = 0;

        for (Zamestnanec z : zamestnanci)
        {
            int pocetVazieb = z.getSpolupracovnici().size();
            if(pocetVazieb > maxVazieb){
                maxVazieb = pocetVazieb;
                najviacVazieb = z;
            }
        
            for(Spolupraca s : z.getSpolupracovnici())
            {
                switch (s.getUroven())
                {
                    case Zla -> zla++;
                    case Priemerna -> priemerna++;
                    case Dobra -> dobra++;
                }
            }
        }

        System.out.println(Farby.AZUROVA + "\n---- CELKOVÉ ŠTATISTIKY ----" + Farby.RESET);

        if(najviacVazieb != null)
        {
            System.out.println("\nZamestnanec s najviac väzbammi: " + najviacVazieb.getMeno() + " " + najviacVazieb.getPriezvisko() + " (Počet: " + maxVazieb + ")");
        }

        System.out.print("Prevažujuca kvalita spolupráce: ");

        if(zla >= priemerna && zla >= dobra)
        {
            System.out.println(Farby.CERVENA + "Zlá" + Farby.RESET);
        }
        else if(priemerna >= zla && priemerna >= dobra)
        {
            System.out.println(Farby.ZLTA + "Priemerná" + Farby.RESET);
        }
        else if(dobra >= priemerna && dobra >= zla)
        {
            System.out.println(Farby.ZELENA + "Dobrá" + Farby.RESET);
        }
    }

    public void vypisPocetZamestnancov()
    {
        int pocetAnalytikov = 0;
        int pocetSpecialistov = 0;

        for(Zamestnanec z : zamestnanci)
        {
            if(z instanceof DatovyAnalytik)
            {
                pocetAnalytikov++;
            }
            else if(z instanceof BezpecnostnySpecialista)
            {
                pocetSpecialistov++;
            }
        }

        System.out.println(Farby.AZUROVA + "\n---- POČET ZAMESTNANCOV V SKUPINÁCH ----" + Farby.RESET);
        System.out.println("\nDátoví analytici: " + pocetAnalytikov);
        System.out.println("Bezpečnostní špecialisti: " + pocetSpecialistov);
        System.out.println("Celkový počet: " + zamestnanci.size());
    }

    private void automatickeUlozenie()
    {
        try (PrintWriter writer = new PrintWriter(new FileWriter("databaza.txt")))
        {
            writer.println("---- AKTUÁLNY STAV DATABÁZY ----");

            for (Zamestnanec z : zamestnanci)
            {
                writer.println("\n--------------------------------------\n");
                writer.println("ID: " + z.getId() + " | " + z.getMeno() + " " + z.getPriezvisko());
                writer.println("Typ: " + (z instanceof DatovyAnalytik ? "Analytik" : "Špecialista"));
                writer.println("Počet kolegov: " + z.getSpolupracovnici().size());
            }
        } 
        catch (IOException e)
        {

        }
    }

    public void ulozData()
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data.bin")))
        {
            
            oos.writeObject(zamestnanci);
            oos.writeInt(dalsieID);

        } catch (IOException e)
        {

        }
    }

    public void nacitajData()
    {
        File subor = new File("data.bin");
        if (!subor.exists()) 
        {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(subor)))
        {
            zamestnanci = (List<Zamestnanec>) ois.readObject();
            dalsieID = ois.readInt();
            
        } 
        catch (Exception e) 
        {
        }
    }

    public void ulozZamestnancaDoSuboru(int id, String menoSuboru)
    {
        Zamestnanec z = najdiPodlaID(id);
        if(z == null)
        {
            System.out.print(Farby.CERVENA + "\nZamestnanec s ID: " + id + " neexistuje." + Farby.RESET);
            return;
        }

        try(PrintWriter writer = new PrintWriter(new FileWriter(menoSuboru)))
        {
            String typ = (z instanceof DatovyAnalytik) ? "ANALYTIK" : "ŠPECIALISTA";
            writer.println(z.getId() + "; " + z.getMeno() + "; " + z.getPriezvisko() + "; " + z.getRokNarodenia() + "; " + typ);
            System.out.println(Farby.ZELENA + "\nZamestnanec uložený do súboru: " + menoSuboru);
        }
        catch(IOException e)
        {
            System.err.println(Farby.CERVENA + "\nChyba pri zpise do súboru: " + e.getMessage() + Farby.RESET);
        }
    }

    public void nacitajZamestnancaZoSuboru(String menoSuboru)
    {
    File subor = new File(menoSuboru);
    if (!subor.exists())
    {
        System.out.println(Farby.CERVENA + "\nSúbor " + menoSuboru + " neexistuje." + Farby.RESET);
        return;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(subor)))
    {
        String riadok = reader.readLine();
        if (riadok != null)
        {
            String[] casti = riadok.split(";");

            if (casti.length == 5)
            {
                int id = Integer.parseInt(casti[0].trim());
                String meno = casti[1].trim();
                String priezvisko = casti[2].trim();
                int rok = Integer.parseInt(casti[3].trim());
                String typ = casti[4].trim();

                if (najdiPodlaID(id) != null)
                {
                    System.out.println(Farby.CERVENA + "\nZamestnanec s ID " + id + " už v databáze existuje." + Farby.RESET);
                    return;
                }

                Zamestnanec novy;
                if (typ.equals("ANALYTIK"))
                {
                    novy = new DatovyAnalytik(id, meno, priezvisko, rok);
                } 
                else
                {
                    novy = new BezpecnostnySpecialista(id, meno, priezvisko, rok);
                }
                pridajNacitanehoZamestnanca(novy);
                System.out.println(Farby.ZELENA + "\nZamestnanec načítaný zo súboru (ID: " + id + ")." + Farby.RESET);
                automatickeUlozenie();
            } 
            else
            {
                System.out.println(Farby.CERVENA + "\nNeplatný formát dát v súbore." + Farby.RESET);
            }
        }
    } 
    catch (Exception e)
    {
        System.err.println(Farby.CERVENA + "\nChyba pri načítavaní zo súboru: " + e.getMessage() + Farby.RESET);
    }
}
    private Connection getSqlConnection() throws SQLException 
    {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

     public void ulozDoSQL()
     {
        String dropSpolupraca = "DROP TABLE IF EXISTS spolupracovnici";
        String dropZamestnanci = "DROP TABLE IF EXISTS zamestnanci";
        
        String createZamestnanci = "CREATE TABLE zamestnanci (" +
                "id INT PRIMARY KEY, " +
                "typ VARCHAR(20), " +
                "meno VARCHAR(50), " +
                "priezvisko VARCHAR(50), " +
                "rok_narodenia INT)";
        
        String createSpolupraca = "CREATE TABLE spolupracovnici (" +
                "zamestnanec_id INT, " +
                "kolega_id INT, " +
                "uroven VARCHAR(20), " +
                "FOREIGN KEY (zamestnanec_id) REFERENCES zamestnanci(id), " +
                "FOREIGN KEY (kolega_id) REFERENCES zamestnanci(id))";

        String insertZamestnanec = "INSERT INTO zamestnanci (id, typ, meno, priezvisko, rok_narodenia) VALUES (?, ?, ?, ?, ?)";
        String insertSpolupraca = "INSERT INTO spolupracovnici (zamestnanec_id, kolega_id, uroven) VALUES (?, ?, ?)";

        try (Connection conn = getSqlConnection();
             Statement stmt = conn.createStatement())
        {
            conn.setAutoCommit(false);

            stmt.execute(dropSpolupraca);
            stmt.execute(dropZamestnanci);
            stmt.execute(createZamestnanci);
            stmt.execute(createSpolupraca);

            try (PreparedStatement psZ = conn.prepareStatement(insertZamestnanec))
            {
                for (Zamestnanec z : zamestnanci)
                {
                    psZ.setInt(1, z.getId());
                    psZ.setString(2, (z instanceof DatovyAnalytik ? "ANALYTIK" : "SPECIALISTA"));
                    psZ.setString(3, z.getMeno());
                    psZ.setString(4, z.getPriezvisko());
                    psZ.setInt(5, z.getRokNarodenia());
                    psZ.addBatch();
                }

                psZ.executeBatch();
            }

            try (PreparedStatement psS = conn.prepareStatement(insertSpolupraca))
            {
                Set<String> ulozeneVazby = new HashSet<>();
                for (Zamestnanec z : zamestnanci)
                {
                    for (Spolupraca s : z.getSpolupracovnici())
                    {
                        int id1 = z.getId();
                        int id2 = s.getKolega().getId();

                        String klucVazby = (id1 < id2) ? id1 + "-" + id2 : id2 + "-" + id1;
                        
                        if (!ulozeneVazby.contains(klucVazby)) {
                            psS.setInt(1, id1);
                            psS.setInt(2, id2);
                            psS.setString(3, s.getUroven().toString());
                            psS.addBatch();
                            ulozeneVazby.add(klucVazby);
                        }
                    }
                }
                psS.executeBatch();
            }

            conn.commit();
            System.out.println(Farby.ZELENA + "Dáta boli úspešne zálohované do SQL databázy." + Farby.RESET);

        }
        catch (SQLException e)
        {
            System.err.println(Farby.CERVENA + "\nChyba pri ukladaní do SQL databázy: " + e.getMessage() + Farby.RESET);
        }
    }

    public boolean nacitajZSQL()
    {
        String selectZamestnanci = "SELECT * FROM zamestnanci";
        String selectSpolupraca = "SELECT * FROM spolupracovnici";
        
        List<Zamestnanec> nacitaniZamestnanci = new ArrayList<>();
        int maxId = 0;

        try (Connection conn = getSqlConnection();
             Statement stmtZ = conn.createStatement();
             ResultSet rsZ = stmtZ.executeQuery(selectZamestnanci))
        {
            while (rsZ.next())
            {
                int id = rsZ.getInt("id");
                String typ = rsZ.getString("typ");
                String meno = rsZ.getString("meno");
                String priezvisko = rsZ.getString("priezvisko");
                int rok = rsZ.getInt("rok_narodenia");

                Zamestnanec z;
                if ("ANALYTIK".equals(typ))
                {
                    z = new DatovyAnalytik(id, meno, priezvisko, rok);
                }
                else
                {
                    z = new BezpecnostnySpecialista(id, meno, priezvisko, rok);
                }
                nacitaniZamestnanci.add(z);
                if (id > maxId) maxId = id;
            }

            if (nacitaniZamestnanci.isEmpty())
            {
                return false;
            }

            this.zamestnanci = nacitaniZamestnanci;

            try (Statement stmtS = conn.createStatement();
                 ResultSet rsS = stmtS.executeQuery(selectSpolupraca))
            {
                
                while (rsS.next())
                {
                    int id1 = rsS.getInt("zamestnanec_id");
                    int id2 = rsS.getInt("kolega_id");
                    String urovenStr = rsS.getString("uroven");
                    Uroven uroven = Uroven.valueOf(urovenStr);

                    Zamestnanec z1 = najdiPodlaID(id1);
                    Zamestnanec z2 = najdiPodlaID(id2);

                    if (z1 != null && z2 != null)
                    {
                        z1.getSpolupracovnici().add(new Spolupraca(z2, uroven));
                        z2.getSpolupracovnici().add(new Spolupraca(z1, uroven));
                    }
                }
            }

            this.dalsieID = maxId + 1;
            automatickeUlozenie();
            System.out.println(Farby.ZELENA + "\n\nÚspešne načítaných " + zamestnanci.size() + " zamestnancov z SQL databázy." + Farby.RESET);
            return true;

        } 
        catch (SQLException e)
        {
            return false;
        } 
        catch (Exception e)
        {
            System.err.println(Farby.CERVENA + "\nChyba pri rekonštrukcii dát z SQL: " + e.getMessage() + Farby.RESET);
            return false;
        }
    }

    public void vypisZSQL() {

    String url = "jdbc:h2:./databaza_zamestnancov";
    String meno = "sa";
    String heslo = "";

    try (Connection conn = DriverManager.getConnection(url, meno, heslo);
         Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT * FROM ZAMESTNANCI"))
        {
            System.out.println(Farby.AZUROVA + "\n---- DÁTA Z SQL DATABÁZY ----" + Farby.RESET);
            
            while (rs.next())
            {
                int id = rs.getInt("id");
                String krstneMeno = rs.getString("meno");
                String priezvisko = rs.getString("priezvisko");
                
                System.out.println("\nID: " + id + " | Meno: " + krstneMeno + " " + priezvisko);
            }
        }
    catch (Exception e)
    {
        System.out.println(Farby.CERVENA + "\nChyba pri čítaní z databázy: " + e.getMessage() + Farby.RESET);
    }
}
}

