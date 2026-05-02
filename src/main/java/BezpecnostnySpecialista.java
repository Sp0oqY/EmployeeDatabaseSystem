

public class BezpecnostnySpecialista extends Zamestnanec
{
    public BezpecnostnySpecialista(int id, String meno, String priezvisko, int rokNarodenia)
    {
        super(id, meno, priezvisko, rokNarodenia);
    }

    @Override
    public void vykonajAnalyzu()
    {
        System.out.println("\nAnalýza rizika pre špecialistu: "+ Farby.ZLTA + getMeno() + " " + getPriezvisko() + Farby.RESET);

        if(spolupracovnici.isEmpty())
        {
            System.out.println(Farby.CERVENA + "\nŽiadni spolupracovníci - riziko sa nedá určiť.");
            return;
        } 

        double celkoveSkore = 0;

        for(Spolupraca s : spolupracovnici)
        {
            switch (s.getUroven()) 
            {
                case Zla -> celkoveSkore += 3;
                case Priemerna -> celkoveSkore += 2;
                default -> celkoveSkore += 1;
            }
        }

        double vysledok = celkoveSkore / spolupracovnici.size();
        System.out.println("\nPočet sledovaných väzieb: " + spolupracovnici.size());
        System.out.println("Priemerný koeficient rizika: " + vysledok);

        if(vysledok > 2.0)
        {
            System.out.println(Farby.CERVENA + "\nVAROVANIE: Vysoké bezpečnostné riziko!" + Farby.RESET);
        }

        else
        {
            System.out.println(Farby.ZELENA + "\nSTATUS: Bezpečný." + Farby.RESET);
        }
    }
}