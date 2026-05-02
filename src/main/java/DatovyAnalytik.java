public class DatovyAnalytik extends Zamestnanec
{
    public DatovyAnalytik(int id, String meno, String priezvisko, int rokNarodenia)
    {
        super(id, meno, priezvisko, rokNarodenia);
    }

    @Override 
    public void vykonajAnalyzu()
    {   
        System.out.println("\nAnalýza pre analyzátora: "+ Farby.ZLTA + getMeno() + " " + getPriezvisko() + Farby.RESET);

        Zamestnanec najlepsiKolega = null;
        int maxSpolocnych = -1;

        for(Spolupraca s : spolupracovnici)
        {
            Zamestnanec kolega = s.getKolega();

            int aktualnaZhoda = spocitajSpolocnych(kolega);

            if(aktualnaZhoda > maxSpolocnych)
            {
                maxSpolocnych = aktualnaZhoda;
                najlepsiKolega = kolega;
            }
        }
        vypisVysledok(najlepsiKolega, maxSpolocnych);        
    }

    private int spocitajSpolocnych(Zamestnanec kolega)
    {
        int body = 0;

        for(Spolupraca mojZoznam : spolupracovnici)
        {
            for(Spolupraca kolegovZoznam : kolega.getSpolupracovnici())
            {
                if (mojZoznam.getKolega().getId() == kolegovZoznam.getKolega().getId())
                {
                    body++;
                }
            }
        }
        return body;
    }

    private void vypisVysledok(Zamestnanec k, int pocet)
    {
        if (k != null)
        {
            System.out.println("\nNajviac spoločných kontaktov má so zamestnancom: " + Farby.FIALOVA + k.getMeno() + " " + k.getPriezvisko() + Farby.RESET);
            System.out.println("Počet spoločnych: " + pocet);
        } 

        else
        {
            System.out.println(Farby.CERVENA + "\nŽiadni spolupracovníci neboli nájdení.");
        }
}
}