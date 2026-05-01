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
        System.out.println("Zamestnanec pridany s ID: " + prideleneID);
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
            System.out.println("Spolupraca nastavena.");
        }
        else
        {
            System.out.println("Jeden zo zamestnancov nebol najdeny.");
        }

        automatickeUlozenie();
    }

    public void odoberZamestnanca(int id)
    {
        Zamestnanec naOdstranenie = najdiPodlaID(id);

        if(naOdstranenie == null)
        {
            System.out.println("Zamestnanec neexistuje");
            return;
        }

        for(Zamestnanec z : zamestnanci)
        {
            z.getSpolupracovnici().removeIf(s -> s.getKolega().getId() == id);
        }

        zamestnanci.remove(naOdstranenie);
        System.out.println("Zamestnanec s ID: " + id + " bol odstraneny.");

        automatickeUlozenie();
    }

    public void vypisZamestnanca(int id)
    {
        Zamestnanec z = najdiPodlaID(id);

        if (z != null)
        {
            System.out.println("---- INFORMACIE O ZAMESTNANCOVI ----");
            System.out.println("ID: " + z.getId());
            System.out.println("Meno: " + z.getMeno() + " " + z.getPriezvisko());
            System.out.println("Rok narodenia: " + z.getRokNarodenia());

            int pocetKolegov = z.getSpolupracovnici().size();
            System.out.println("Pocet spolupracovnikov: " + pocetKolegov);
        } 
        else 
        {
            System.out.println("Zamestnanec s ID " + id + " nebol najdeny.");
        }
    }

    public void spustitDovednost(int id)
    {
        Zamestnanec z = najdiPodlaID(id);
        if (z != null)
        {
            System.out.println("--- SPUSTAM SPECIALNU DOVEDNOST ---");
            z.vykonajAnalyzu(); 
        } 
        else 
        {
            System.out.println("Zamestnanec nenajdeny.");
        }
    }

    public void vypisAbecedne()
    {
        if (zamestnanci.isEmpty())
        {
            System.out.println("Databaza je prazdna.");
            return;
        }
    
        List<Zamestnanec> zoradenyZoznam = new ArrayList<>(zamestnanci);
    
        zoradenyZoznam.sort(Comparator.comparing(Zamestnanec::getPriezvisko, String.CASE_INSENSITIVE_ORDER));
    
        System.out.println("---- ABECEDNY VYPIS PODLA ABECEDY ----");
    
        System.out.println("\n[ DATOVI ANALYTICI ]");
        for (Zamestnanec z : zoradenyZoznam)
        {
            if (z instanceof DatovyAnalytik)
            {
                System.out.println(z.getPriezvisko() + " " + z.getMeno() + " (ID: " + z.getId() + ")");
            }
        }

        System.out.println("\n[ BEZPECNOSTNI ANALYTICI ]");
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
            System.out.println("Databaza je prazdna a nie je mozne spravit statistiku.");
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

        System.out.println("---- CELKOVE STATISTIKY ----");

        if(najviacVazieb != null)
        {
            System.out.println("Zamestnanec s najviac vazbammi: " + najviacVazieb.getMeno() + " " + najviacVazieb.getPriezvisko() + "(pocet: " + maxVazieb + ")");
        }

        System.out.print("Prevazujuca kvalita spoluprace: ");

        if(zla >= priemerna && zla >= dobra)
        {
            System.out.println("zla");
        }
        else if(priemerna >= zla && priemerna >= dobra)
        {
            System.out.println("priemerna");
        }
        else if(dobra >= priemerna && dobra >= zla)
        {
            System.out.println("dobra");
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

        System.out.println("---- POCET ZAMESTNANCOV V SKUPINACH ----");
        System.out.println("Datovi analytici: " + pocetAnalytikov);
        System.out.println("Bezpecnostni specialisti: " + pocetSpecialistov);
        System.out.println("Celkovy pocet: " + zamestnanci.size());
    }

    private void automatickeUlozenie()
    {
        try (PrintWriter writer = new PrintWriter(new FileWriter("databaza.txt")))
        {
            writer.println("---- AKTUALNY STAV DATABAZE ----");

            for (Zamestnanec z : zamestnanci)
            {
                writer.println("ID: " + z.getId() + " | " + z.getMeno() + " " + z.getPriezvisko());
                writer.println("Typ: " + (z instanceof DatovyAnalytik ? "Analytik" : "Specialista"));
                writer.println("Pocet kolegov: " + z.getSpolupracovnici().size());
                writer.println("--------------------------------------");
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
            System.out.println("Zamestnanec s ID: " + id + " neexistuje.");
            return;
        }

        try(PrintWriter writer = new PrintWriter(new FileWriter(menoSuboru)))
        {
            String typ = (z instanceof DatovyAnalytik) ? "ANALYTIK" : "SPECIALISTA";
            writer.println(typ + "; " + z.getId() + "; " + z.getMeno() + "; " + z.getPriezvisko() + "; " + z.getRokNarodenia());
            System.out.println("Zamestnanec uložený do súboru: " + menoSuboru);
        }
        catch(IOException e)
        {
            System.err.println("Chyba pri zpise do súboru: " + e.getMessage());
        }
    }

    public void nacitajZamestnancaZoSuboru(String menoSuboru)
    {
    File subor = new File(menoSuboru);
    if (!subor.exists())
    {
        System.out.println("Súbor " + menoSuboru + " neexistuje.");
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
                String typ = casti[0].trim();
                int id = Integer.parseInt(casti[1].trim());
                String meno = casti[2].trim();
                String priezvisko = casti[3].trim();
                int rok = Integer.parseInt(casti[4].trim());

                if (najdiPodlaID(id) != null)
                {
                    System.out.println("Zamestnanec s ID " + id + " už v databáze existuje.");
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
                System.out.println("Zamestnanec načítaný zo súboru (ID: " + id + ").");
                automatickeUlozenie();
            } 
            else
            {
                System.out.println("Neplatný formát dát v súbore.");
            }
        }
    } 
    catch (Exception e)
    {
        System.err.println("Chyba pri načítavaní zo súboru: " + e.getMessage());
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
            System.out.println("Dáta boli úspešne zálohované do SQL databázy.");

        }
        catch (SQLException e)
        {
            System.err.println("Chyba pri ukladaní do SQL databázy: " + e.getMessage());
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
            System.out.println("Úspešne načítaných " + zamestnanci.size() + " zamestnancov z SQL databázy.");
            return true;

        } 
        catch (SQLException e)
        {
            return false;
        } 
        catch (Exception e)
        {
            System.err.println("Chyba pri rekonštrukcii dát z SQL: " + e.getMessage());
            return false;
        }
    }
}

