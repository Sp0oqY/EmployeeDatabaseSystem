public class Main {
    public static void main(String[] args) {

        DatovyAnalytik miro = new DatovyAnalytik(1, "Miro", "Kastier", 2004);
        DatovyAnalytik viki = new DatovyAnalytik(2, "Viki", "Pavolova", 2004);
        BezpecnostnySpecialista bibi = new BezpecnostnySpecialista(3, "Bibi", "Pavolova", 2001);


        miro.pridajSpolupracu(viki, Uroven.Dobra);
        miro.pridajSpolupracu(bibi, Uroven.Priemerna);

        viki.pridajSpolupracu(miro, Uroven.Dobra);
        viki.pridajSpolupracu(bibi, Uroven.Zla);

        bibi.pridajSpolupracu(miro, Uroven.Zla);

        System.out.println("--- TEST ANALYTIKA ---");
        miro.vykonajAnalyzu();
        
        System.out.println("\n--- TEST ŠPECIALISTU ---");
        bibi.vykonajAnalyzu();
    }
}