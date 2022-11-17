package com.example.smallwhite.io.bio.V2;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author yangqiang
 * @create 2021-09-29 21:16
 */
public class test {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        File file = new File("file:/Users/yangqiang/");
        final Class<?> aClass = Class.forName("/Users/yangqiang/PrimitiveServlet");
        URL url = file.toURI().toURL();
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url});
        urlClassLoader.loadClass("PrimitiveServlet");
    }
}
