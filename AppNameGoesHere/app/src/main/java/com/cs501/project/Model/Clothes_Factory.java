/*
 * This class creates Clothes for the backend
 * 
 * Follows the Factory design pattern, also uses Singleton pattern
 */

public class Clothes_Factory {

    private static Clothes_Factory factory;

    // private constructor to force the use of get_instance() 
    private Clothes_Factory(){
    }

    public static Clothes_Factory get_instance(){

        if (factory == null){
            factory = new Clothes_Factory();
        }
        return factory;
    }

    /*
     * Public Methods
     */
    public T_shirt get_tshirt(){
        return new T_shirt();
    }

    public Shirt get_shirt(){
        return new Shirt();
    }

    public Shoes get_shoes(){
        return new Shoes();
    }

    public Pants get_pants(){
        return new Pants();
    }

    public Shorts get_shorts(){
        return new Shorts();
    }
}
