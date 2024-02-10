package test;

import java.util.Scanner;

public class Main{
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in); //Scanner 객체 생성
        
        String str = scanner.nextLine();
        int num = scanner.nextInt();
        
        System.out.println(str.charAt(num-1));
    }
}