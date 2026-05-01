public class BezpecnostnySpecialista extends Zamestnanec
{
    public BezpecnostnySpecialista(int id, String meno, String priezvisko, int rokNarodenia){
        super(id, meno, priezvisko, rokNarodenia);
    }

    @Override
    public void vykonajAnalyzu(){
        System.out.println("Analyza rizika pre specialistu: " + getMeno() + " " + getPriezvisko());

        if(spolupracovnici.isEmpty())
        {
            System.out.println("Ziadny spolupracovnici - riziko nejde urcit. ");
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
        System.out.println("Pocet sledovanych vazieb: " + spolupracovnici.size());
        System.out.println("Priemerny koeficient rizika: " + vysledok);

        if(vysledok > 2.0){
            System.out.println("VAROVANIE: Vysoke bezpecnostne riziko!");
        }
        else{
            System.out.println("STATUS: Bezpecny. ");
        }
    }
}