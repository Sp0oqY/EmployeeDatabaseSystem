public class DatovyAnalytik extends Zamestnanec{
    
    public DatovyAnalytik(int id, String meno, String priezvisko, int rokNarodenia){
            super(id, meno, priezvisko, rokNarodenia);
    }

    @Override 
    public void vykonajAnalyzu(){
        
        System.out.println("Analyza pre analyzatora: " + getMeno() + " " + getPriezvisko());

        Zamestnanec najlepsiKolega = null;
        int maxSpolocnych = -1;

        for(Spolupraca s : spolupracovnici){
            Zamestnanec kolega = s.getKolega();

            int aktualnaZhoda = spocitajSpolocnych(kolega);

            if(aktualnaZhoda > maxSpolocnych){
                maxSpolocnych = aktualnaZhoda;
                najlepsiKolega = kolega;
            }
        }

        vypisVysledok(najlepsiKolega, maxSpolocnych);        
    }

    private int spocitajSpolocnych(Zamestnanec kolega) {
        int body = 0;

        for(Spolupraca mojZoznam : spolupracovnici) {
            for(Spolupraca kolegovZoznam : kolega.getSpolupracovnici())
            {
                if (mojZoznam.getKolega() == kolegovZoznam.getKolega())
                {
                    body++;
                }
            }
        }

        return body;

    }

    private void vypisVysledok(Zamestnanec k, int pocet) {
        if (k != null) {
            System.out.println("Najviac spolocnych kontaktov ma s : " + k.getMeno() + " " + k.getPriezvisko());
            System.out.println("Pocet spolecnych: " + pocet);
        } else {
            System.out.println("Ziadny spoluparacvnici neboli najdeni!");
        }
}
}