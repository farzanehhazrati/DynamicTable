package com.isc.sima.export;

import javax.faces.FacesException;

public class CustomExporterFactory {

    public static PDFCustomExporter getExporterForType(String type) {
        PDFCustomExporter exporter = null;

        try {
            ExporterType exporterType = ExporterType.valueOf(type.toUpperCase());

            switch(exporterType) {
               case PDF:
                    exporter = new PDFCustomExporter();
                    break;

            }
        }
        catch(IllegalArgumentException e) {
            throw new FacesException(e);
        }

        return exporter;
    }
}