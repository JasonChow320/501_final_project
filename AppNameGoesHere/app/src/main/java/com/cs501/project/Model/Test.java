package com.cs501.project.Model;/*
 * Class to test our backend
 */

import com.cs501.project.Model.Clothes_Factory;
import com.cs501.project.Model.Pants;
import com.cs501.project.Model.Shirt;
import com.cs501.project.Model.Shoes;
import com.cs501.project.Model.Shorts;
import com.cs501.project.Model.T_shirt;

import java.io.*;

public class Test {

    public Test(){
    }

    public static void run_tests(){

        // test clothes factory
        Clothes_Factory factory = new Clothes_Factory();

        // test pants class
        Pants pants = factory.get_pants();
        System.out.println(pants);

        // test shirt class
        Shirt shirt = factory.get_long_sleeve();
        System.out.println(shirt);

        // test tshirt class
        T_shirt tshirt = factory.get_tshirt();
        System.out.println(tshirt);

        // test shoes class
        Shoes shoes = factory.get_shoes();
        System.out.println(shoes);

        // test shorts class
        Shorts shorts = factory.get_shorts();
        System.out.println(shorts);

        return;
    }

    public static void main(String[] arg){
        run_tests();
    }
}
