package Stego;

import java.io.IOException;

public class Main {
    public static void main(String []args) {
        String name = "C:/Users/aser/Documents/MATLAB/lena.bmp";
        Stegosystem s = new Stegosystem(name, 2);
        //C:/Users/aser/Desktop/лабы/vvod.txt
        s.imageWB("C:/Users/aser/Documents/MATLAB/lena_grey.bmp", "bmp");
        System.out.println("PSNR: "+s.getPSNR());
        System.out.println("Количество вставленной информации: "+s.getBit()+" бит");
        System.out.println("Максимальное возможное количество: "+s.maxBit()+" бит");


        int [][] k = s.palette();
        name = "C:/Users/aser/Documents/MATLAB/lena_grey.bmp";
        Stegosystem s1 = new Stegosystem(name, k);
        System.out.println("Получение скрытых данных");
        System.out.println(s1.getSecret());
    }
}
