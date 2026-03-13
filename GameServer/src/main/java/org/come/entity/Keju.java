package org.come.entity;

import org.come.tool.ReadExelTool;

public class Keju implements Cloneable {
    private Integer mid;
    private String wenti;
    private String A;
    private String B;
    private String C;
    private String D;
   public static String[][] result = null;



    public Integer getMid() {
        return mid;
    }

    public String getA() {
        return A;
    }

    public String getB() {
        return B;
    }

    public String getC() {
        return C;
    }

    public String getD() {
        return D;
    }

    public String getWenti() {
        return wenti;
    }

    public void setA(String a) {
        A = a;
    }

    public void setB(String b) {
        B = b;
    }

    public void setC(String c) {
        C = c;
    }

    public void setD(String d) {
        D = d;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public void setWenti(String wenti) {
        this.wenti = wenti;
    }
    public Keju clone(){
        try {
            return (Keju) super.clone();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }
}
