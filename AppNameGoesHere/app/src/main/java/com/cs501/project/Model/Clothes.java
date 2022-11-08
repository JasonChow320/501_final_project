public abstract class Clothes {

    public enum Type {

        t_shirt,
        long_shirt,
        shorts,
        pants,
        shoes,
        jacket
    }

    // public members
    Type type;
    Color color;


    public Clothes(){
        this.type = null;
        this.color = new Color();
    }

    // public methods
    public Type getType(){
        return this.type;
    }
}
