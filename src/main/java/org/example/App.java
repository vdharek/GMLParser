package org.example;

import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

public class App {
    private static final java.util.logging.Logger log = Logger.getLogger(App.class.getName());
    public static void main( String[] args ){

        String path = "./GmlFiles/Frankfurt_Street_Setting_LOD3.gml";

        GMLValidator gmlValidator = new GMLValidator(Paths.get(path));

        try {
            boolean isValid = gmlValidator.validate();

            if (isValid) {
                log.info("GML file is valid, Proceeding with parsing.");
                GMLParser parser = new GMLParser(path);
                GMLParser.main(new String[]{});
                List<Double> coordinates = parser.getListCoordinates();
                if (coordinates != null && !coordinates.isEmpty()) {
                    log.info("Retrieved coordinates:");
                    for (Double coordinate : coordinates) {
                        System.out.println(coordinate);
                    }
                } else {
                    log.info("No coordinates found");
                }
            } else {
                log.warning("GML file is not valid.");
            }
        }catch (Exception e){
            log.severe("An error occurred during validation: " + e.getMessage());
        }
    }
}
