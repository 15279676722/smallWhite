package com.example.smallwhite.jvm.chapter02;

import sun.misc.Launcher;

import java.net.URL;
import java.util.Arrays;

public class ClassLoaderTest1 {
    public static void main(String[] args) {
        System.out.println("*****启动类加载器********");

        Arrays.stream(Launcher.getBootstrapClassPath().getURLs()).forEach(System.out::println);

        System.out.println("*****扩展类加载器********");

        Arrays.stream(System.getProperty("java.ext.dirs").split(";")).forEach(System.out::println);

    }
}
