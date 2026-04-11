import java.util.*;

public class DatabazaZamestnancov {

    private List <Zamestnanec> zamestnanci = new ArrayList<>();
    private int dalsieID = 1;

    public void pridajZamestnanca(String typ, String meno, String priezvisko, int rokNarodenia){
        
        Zamestnanec novyZamestnanec;
        int prideleneID = dalsieID++;
        if(typ.equalsIgnoreCase("analytik")){
            novyZamestnanec = new DatovyAnalytik(prideleneID, meno, priezvisko, rokNarodenia);        
        }else{
            novyZamestnanec = new BezpecnostnySpecialista(prideleneID, meno, priezvisko, rokNarodenia);
        }

        zamestnanci.add(novyZamestnanec);
        System.out.println("Zamestnanec pridany s ID: " + prideleneID);
    }

    public Zamestnanec najdiPodlaID(int id){
        for(Zamestnanec z : zamestnanci){
            if(z.getId() == id){
                return z;
            }
        }
        return null;
    }

    public void pridajSpolupracu(int idZamestnanca, int idKolega, Uroven uroven){

        Zamestnanec z1 = najdiPodlaID(idZamestnanca);
        Zamestnanec z2 = najdiPodlaID(idKolega);

        if(z1 != null && z2 != null){
            z1.pridajSpolupracu(z2, uroven);
            z2.pridajSpolupracu(z1, uroven);
            System.out.println("Spolupraca nastavena.");
        }else{
            System.out.println("Jeden zo zamestnancov nebol najdeny.");
        }
    }

    public void odoberZamestnanca(int id){

        Zamestnanec naOdstranenie = najdiPodlaID(id);

        if(naOdstranenie == null){
            System.out.println("Zamestnanec neexistuje");
            return;
        }

        for(Zamestnanec z : zamestnanci){
            z.getSpolupracovnici().removeIf(s -> s.getKolega().getId() == id);
        }

        zamestnanci.remove(naOdstranenie);
        System.out.println("Zamestnanec s ID: " + id + " bol odstraneny.");
    }

    public void vypisZamestnanca(int id) {

        Zamestnanec z = najdiPodlaID(id);

        if (z != null) {
            System.out.println("---- INFORMACIE O ZAMESTNANCOVI ----");
            System.out.println("ID: " + z.getId());
            System.out.println("Meno: " + z.getMeno() + " " + z.getPriezvisko());
            System.out.println("Rok narodenia: " + z.getRokNarodenia());

            int pocetKolegov = z.getSpolupracovnici().size();
            System.out.println("Pocet spolupracovnikov: " + pocetKolegov);
        } else {
            System.out.println("Zamestnanec s ID " + id + " nebol najdeny.");
        }
    }

    public void spustitDovednost(int id) {
        Zamestnanec z = najdiPodlaID(id);
        if (z != null) {
            System.out.println("--- SPUSTAM SPECIALNU DOVEDNOST ---");
            z.vykonajAnalyzu(); 
        } else {
            System.out.println("Zamestnanec nenajdeny.");
        }
    }

    public void vypisAbecedne() {
        
        if (zamestnanci.isEmpty()) {
            System.out.println("Databaza je prazdna.");
            return;
        }
    
        List<Zamestnanec> zoradenyZoznam = new ArrayList<>(zamestnanci);
    
        zoradenyZoznam.sort(Comparator.comparing(Zamestnanec::getPriezvisko, String.CASE_INSENSITIVE_ORDER));
    
        System.out.println("---- ABECEDNY VYPIS PODLA ABECEDY ----");
    
        System.out.println("\n[ DATOVI ANALYTICI ]");
        for (Zamestnanec z : zoradenyZoznam) {
            if (z instanceof DatovyAnalytik) {
                System.out.println(z.getPriezvisko() + " " + z.getMeno() + " (ID: " + z.getId() + ")");
            }
        }

        System.out.println("\n[ BEZPECNOSTNI ANALYTICI ]");
        for (Zamestnanec z : zoradenyZoznam) {
            if (z instanceof BezpecnostnySpecialista) {
                System.out.println(z.getPriezvisko() + " " + z.getMeno() + " (ID: " + z.getId() + ")");
            }
        }
    }
}
