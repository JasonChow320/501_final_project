/*
 * Class to test our backend
 */

import java.io.*;

public class Test {

    public Test(){
    }

    public static void run_tests(){

        // first test singleton
        Clothes_Factory clothes_factory = Clothes_Factory.get_instance();

        // print first instance
        System.out.println("First instance addresse: " + clothes_factory.toString() + "\n");

        Clothes_Factory clothes_factory2 = Clothes_Factory.get_instance();

        // print second instance
        System.out.println("Second instance addresse: " + clothes_factory2.toString() + "\n");

        return;
    }

    public static void main(String[] arg){
        run_tests();
    }
}
