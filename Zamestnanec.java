import java.util.*;

public abstract class Zamestnanec implements java.io.Serializable{
    private int id;
    private String meno;
    private String priezvisko;
    private int rokNarodenia;

    protected List <Spolupraca> spolupracovnici = new ArrayList<>();

    public Zamestnanec(int id, String meno, String priezvisko, int rokNarodenia){
        this.id = id;
        this.meno = meno;
        this.priezvisko = priezvisko;
        this.rokNarodenia = rokNarodenia;
    }

    public int getId(){
        return id;
    }

    public String getMeno(){
        return meno;
    }

    public String getPriezvisko(){
        return priezvisko;
    }

    public int getRokNarodenia(){
        return rokNarodenia;
    }

    public List<Spolupraca> getSpolupracovnici() { 
        return spolupracovnici; 
    }

    public void setMeno(String meno){
        this.meno = meno;
    }

    public void setPriezvisko(String priezvisko){
        this.priezvisko = priezvisko;
    }

    public void pridajSpolupracu(Zamestnanec novyKolega, Uroven uroven) {
    if (novyKolega != null && novyKolega != this) {        
        this.spolupracovnici.add(new Spolupraca(novyKolega, uroven));
    } else {
        System.out.println("Chyba: Neplatný objekt spolupracovníka.");
    }
}

    public abstract void vykonajAnalyzu();
}