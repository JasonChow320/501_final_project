public abstract class Clothes {

    public enum Type {

        t_shirt,
        long_shirt,
        shorts,
        pants,
        shoes,
        jacket
    }

    public enum Size {

        small,
        medium,
        large,
        extra_large
    }

    // public members
    Type type;
    Color color;
    Size size;

    public Clothes(){
        this.type = null;
        this.color = new Color();
        this.size = null;
    }

    /*
     * Public Methods
     */

    // getters
    public Type getType(){
        return this.type;
    }

    public Size getSize(){
        return this.size;
    }

    public Color getColr(){
        return this.color;
    }

    // setters
    public void setType(Type type){

        if (type == null) {
            return;
        }
        this.type = type;
    }

    public void setSize(Size size){

        if (size == null) {
            return;
        }
        this.size = size;
    }

    public void setColor(Color color){

        if (color == null) {
            return;
        }
        this.color = color;
    }
}
