public class Spolupraca implements java.io.Serializable{

    private Zamestnanec kolega;
    private Uroven uroven;

    public Spolupraca(Zamestnanec kolega, Uroven uroven){
        this.kolega = kolega;
        this.uroven = uroven;
    }

    public Zamestnanec getKolega() {
        return kolega;
    }

    public Uroven getUroven() {
        return uroven;
    }
}   