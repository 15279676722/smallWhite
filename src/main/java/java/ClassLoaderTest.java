package java;

public class ClassLoaderTest {
    public static void main(String[] args) {
        //Prohibited package name: java 会报异常不可以使用这个包名
        System.out.println(ClassLoaderTest.class.getClassLoader());
    }
}
