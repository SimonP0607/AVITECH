package com.avitech.sia.security;

public class DevHash {
    public static void main(String[] args) {
        System.out.println("admin123 -> " + Passwords.hash("admin123"));
        System.out.println("super123 -> " + Passwords.hash("super123"));
        System.out.println("oper123  -> " + Passwords.hash("oper123"));
    }
}
