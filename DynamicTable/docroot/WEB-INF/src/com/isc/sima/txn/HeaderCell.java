package com.isc.sima.txn;

import java.io.Serializable;

/**
 * Created by f_pAseban on 2018/12/10.
 */
public class HeaderCell implements Serializable {
    private String headerText;
    private int colSpan;
    HeaderCell(String headerText, int colSpan) {
        this.headerText = headerText;
        this.colSpan = colSpan;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public int getColSpan() {
        return colSpan;
    }

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }
}
