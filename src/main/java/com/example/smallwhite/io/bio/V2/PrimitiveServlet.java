package com.example.smallwhite.io.bio.V2;

import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author yangqiang
 * @create 2021-09-26 23:37
 */
public class PrimitiveServlet implements Servlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("init");
    }

    @Override
    public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {
        System.out.println("from service");
        PrintWriter out = response.getWriter();
        out.println("Hello. Roses are red.");
        out.print("Violets are blue.");
    }

    @Override
    public void destroy() {
        System.out.println("destroy");
    }

    @Override
    public String getServletInfo() {
        return null;
    }
    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

}
