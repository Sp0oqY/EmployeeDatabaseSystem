public class Spolupraca implements java.io.Serializable
{
    final private Zamestnanec kolega;
    final private Uroven uroven;

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