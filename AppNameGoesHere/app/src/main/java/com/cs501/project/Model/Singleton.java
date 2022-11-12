/*
 * This class applies the Singleton design pattern and return all Singleton objects
 */

public class Singleton {

    private static Clothes_factory factory;

    private Singleton(){}

    public static Clothes_factory getInstance_Clothes_Factory() {

        if (this.factory == null){
            this.factory = new Clothes_factory();
        }
        return factory;
    }
}
