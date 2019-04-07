package com.isc.sima.txn;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Z_Babakhani
 * Date: 10/26/14
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataTableRow implements Serializable {

    private HashMap hs;
    private String id;
    DataTableRow(){
        hs=new HashMap();
        id="";
    }
    public HashMap getHs() {
        return hs;
    }

    public void setHs(HashMap hs) {
        this.hs = hs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




}