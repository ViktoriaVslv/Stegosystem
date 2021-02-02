package Stego;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Stegosystem {

    private int [][] Y;
    private int [][] Y1;
    private int H;
    private  int W;
    private int type;
    private int [][] palette;
    private int [] secret;

    public Stegosystem(String name, int mode){
        try {
            File file = new File(name);
            BufferedImage image = ImageIO.read(file);

            H = image.getHeight();
            W = image.getWidth();
            type =image.getType();

            Y = new int[H][W];
            Y1 = new int[H][W];

            for(int i = 0; i < H; i++){
                for(int j = 0; j < W; j++){
                    Color color = new Color(image.getRGB(i, j));
                    int R = color.getRed();
                    int G = color.getGreen();
                    int B = color.getBlue();
                    Y[i][j] = (int)(0.299*R+ 0.587*G+ 0.114*B);
                    Y1[i][j] = (int)(0.299*R+ 0.587*G+ 0.114*B);
                }
            }

            palette = this.getPalette();
            sortPalette();
            if(mode==1)
                enterSecret();
            else
                enterSecretFile();

            setSecret();
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }
    private void enterSecret() throws IOException{
        Scanner in = new Scanner(System.in);
        System.out.println("Введите текст для вставки");
        String a = in.nextLine();
        a +="*@*";
        byte[] str = a.getBytes();
        in.close();
        int[] num = new int[str.length];
        for (int i = 0; i < str.length; i++)
            num[i] = str[i] + 128;
        secret = byteToBit(num);
    }
    private void enterSecretFile() throws IOException{
        Scanner in = new Scanner(System.in);
        System.out.println("Введите путь к файлу с секретом");
        String filename = in.nextLine();
        FileInputStream fileIn = new FileInputStream(filename);
        ArrayList<Integer> bb = new ArrayList<Integer>();
        while (fileIn.available() > 0)
            bb.add(fileIn.read());
        fileIn.close();
        int size = bb.size();
//        if((bb.size()+3)*8>H*W)
//            size = (H*W-24)/8;
        String a ="*@*";
        byte[] str = a.getBytes();

        int [] text = new int[size+3];
        for(int i=0; i<size; i++)
            text [i] = bb.get(i)+128;
        text [text.length-3] = str[0]+128;
        text [text.length-2] = str[1]+128;
        text [text.length-1] = str[2]+128;
        secret = byteToBit(text);
    }

    public Stegosystem(String name, int [][] k){
        try {
            palette = k;
            sortPalette();

            File file = new File(name);
            BufferedImage image = ImageIO.read(file);

            H = image.getHeight();
            W = image.getWidth();
            type = image.getType();
            Y = new int[H][W];

            for (int i = 0; i < H; i++) {
                for (int j = 0; j < W; j++) {
                    Color color = new Color(image.getRGB(i, j));
                    Y[i][j] = color.getRed();
                }
            }
            getSecret1();
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void setSecret(){
        int m = 0;
        int Lm = secret.length;
        for(int i = 0; i < H; i++){
            for(int j = 0; j < W; j++){
                if(m >= Lm)
                    return;
                int n = -1;
                int tilda = 0;
                for (int k = 0; k < palette.length; k++){
                    if(palette[k][1] == Y1[i][j]){
                        n = palette[k][0];
                        tilda = k;
                        break;
                    }
                }
                if (n%2 != secret[m]){
                    int L1 = 0;
                    int L2 = 0;
                    for (int d = 0; d < palette.length; d++){
                        L1 = -1000;
                        if (tilda - d >= 0) {
                            if (palette[tilda - d][0] % 2 == secret[m]) {
                                L1 = palette[tilda - d][1];
                                break;
                            }
                        }
                    }
                    for (int d = 0; d < palette.length; d++){
                        L2 = 1000;
                        if (tilda+d < palette.length) {
                            if (palette[tilda + d][0] % 2 == secret[m]) {
                                L2 = palette[tilda + d][1];
                                break;
                            }
                        }
                    }
                    if(Y1[i][j]-L1 <= L2-Y1[i][j])
                        Y1[i][j] = L1;
                    else
                        Y1[i][j] = L2;
                }
                m++;
            }
        }
    }
    private  void getSecret1(){
        ArrayList<Integer> M = new ArrayList<>();
        for(int i = 0; i < H; i++){
            for(int j = 0; j < W; j++) {
                for (int x = 0; x < palette.length; x++) {
                    if(palette[x][1] == Y[i][j])
                        M.add(palette[x][0]%2);
                }
            }
        }
        int [] mes = new int[M.size()];
        for(int i=0; i<M.size();i++)
            mes[i] = M.get(i);
        secret = bitToByte(mes);
    }
    public String getSecret() {
        byte[] end = "*@*".getBytes();
        byte[] str = new byte[secret.length];
        int ind = 0;
        for (int i = 0; i < secret.length - end.length; i++){
            str[i] = (byte) secret[i];
            if(str[i] == end[0] && str[i+1] == end[1] && str[i+2] == end[2])
                ind = i;
        }
        for (int i = 0; i < str.length; i++){
            if(str[i] == end[0] && str[i+1] == end[1] && str[i+2] == end[2])
                ind = i;
        }
        if(ind==0){
            return new String(str);
        }

        byte [] res = new byte[ind];
        for(int i = 0; i < ind; i++)
            res[i] = str[i];

        return new String(res);
    }

    private void sortPalette(){
        for (int i = 0; i < palette.length; i++) {
            for (int j = 0; j < palette.length-1; j++) {
                if (palette[j][1] > palette[j + 1][1]) {
                    int a =  palette[j][0];
                    int b = palette[j][1];
                    palette[j][0] = palette[j + 1][0];
                    palette[j][1] = palette[j + 1][1];
                    palette[j + 1][0] = a;
                    palette[j + 1][1] = b;
                }
            }
        }
    }
    private int[][] getPalette(){
        int [] gradient = new int[256];
        for(int i = 0; i < H; i++){
            for(int j = 0; j < W; j++) {
                gradient[Y[i][j]] = 1;
            }
        }
        int size = 0;
        for (int i=0; i <256; i++){
            if (gradient[i]==1)
                size++;
        }

        int [][] res = new int [size][2];
        int x = 0;
        for(int i = 0; i < H; i++) {
            for (int j = 0; j < W; j++) {
                if (x >= size){
                    return res;
                }
                if (x == 0){
                    res[x][0] = x;
                    res[x][1] = Y[i][j];
                    x++;
                }
                else {
                    int flag = 0;
                    for(int k = 0; k < x; k++){
                        if (res[k][1] == Y[i][j]) {
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 0){
                        res[x][0] = x;
                        res[x][1] = Y[i][j];
                        x++;
                    }
                }
            }
        }
        return res;
    }

    public int [][] palette(){return palette;}

    public void imageWB(String name, String format){
        BufferedImage result = new BufferedImage(W, H, type);
        for(int i = 0; i < H; i++){
            for(int j = 0; j < W; j++) {
                Color newColor = new Color(Y1[i][j], Y1[i][j], Y1[i][j]);
                result.setRGB(i, j, newColor.getRGB());
            }
        }
        try {
            File output = new File(name);
            ImageIO.write(result, format, output);
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private int[] byteToBit(int []value){
        int[] res = new int[8 * value.length];
        for (int i = 0; i < value.length; i++) {
            int by = value[i];
            int[] tmp = new int[8];
            for (int j = 0; j < 8; j++) {
                tmp[j] = by % 2;
                by = (by - by % 2) / 2;
            }
            for (int j = 0; j < 8; j++) {
                res[i * 8 + j] = tmp[7 - j];
            }
        }
        return res;
    }

    private int[] bitToByte(int []value){
        int []res=new int [value.length/8];
        for (int i=0; i<res.length;i++){
            int by=0;
            for (int j=0; j<8;j++) {
                by+=value[i*8+j]* Math.pow(2, 7-j);
            }
            res[i]=by-128;
        }
        return res;
    }
    public  double getPSNR(){
        double psnr = 0;
        double sum =0;
        for(int i = 0; i < H; i++) {
            for (int j = 0; j < W; j++) {
                sum += Math.pow(Y[i][j]-Y1[i][j],2);
            }
        }
        psnr = 10*Math.log10((H*W*Math.pow(255,2))/sum);
        return psnr;
    }
    public int getBit(){
        int res = H*W;
        if(secret.length < res)
            res = secret.length-24;
        return res;
    }
    public  int maxBit(){return  W*H;}
}
