package org.example;

import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.jaxb.CityGMLBuilder;
import org.citygml4j.model.citygml.CityGML;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.model.citygml.transportation.TrafficArea;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurface;
import org.citygml4j.model.gml.geometry.primitives.LinearRing;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;
import org.citygml4j.util.walker.GMLWalker;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class GMLParser {
    private static final Logger log = Logger.getLogger(GMLParser.class.getName());
    static String path = "";

    public static Map<String, List<Double>> mapCoordinates = new HashMap<>();
    public static List<Double> listCoordinates = new ArrayList<>();// Initialize the list

    public Map<String, List<Double>> getMapCoordinates() {
        return mapCoordinates;
    }

    public void setCoordinatesMap(Map<String, List<Double>> mapCoordinates) {
        GMLParser.mapCoordinates = mapCoordinates;
    }

    public List<Double> getListCoordinates() {
        return listCoordinates;
    }

    public static void setListCoordinates(List<Double> listCoordinates) {
        GMLParser.listCoordinates = listCoordinates;
    }

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
        GMLParser.path = path;
    }

    public GMLParser(String path){
        setPath(path);
    }

    public static void main(String[] args) {
        try {
            CityGMLContext context = CityGMLContext.getInstance();
            CityGMLBuilder builder = context.createCityGMLBuilder();

            processGMLFile(builder, getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processGMLFile(CityGMLBuilder builder, String path) throws Exception {
        log.info("Processing GML file \n");
        CityGMLInputFactory inputFactory = builder.createCityGMLInputFactory();
        try (CityGMLReader reader = inputFactory.createCityGMLReader(new File(path))) {
            while (reader.hasNext()) {
                CityGML cityGML = reader.nextFeature();
                if (cityGML instanceof CityModel) {
                    processTrafficAreas((CityModel) cityGML);
                }
            }
        }
    }

    private static void processTrafficAreas(CityModel cityModel) {
        log.info("Processing traffic areas \n");
        cityModel.accept(new GMLWalker() {
            @Override
            public void visit(TrafficArea trafficArea) {
                extractAndSetLinearRingData(trafficArea);
                super.visit(trafficArea);
            }
        });
    }

    private static void extractAndSetLinearRingData(TrafficArea trafficArea) {
        log.info("Extracting linear-ring. \n");
        if (trafficArea.isSetLod3MultiSurface()) {
            MultiSurface multiSurface = trafficArea.getLod3MultiSurface().getMultiSurface();
            if (multiSurface != null) {
                for (SurfaceProperty surfaceProperty : multiSurface.getSurfaceMember()) {
                    surfaceProperty.getGeometry().accept(new GMLWalker() {
                        @Override
                        public void visit(LinearRing linearRing) {
                            String id = linearRing.getId();
                            List<Double> listCoordinates = linearRing.toList3d();
                            setValues(listCoordinates, id);
                        }
                    });
                }
            }
        }
    }

    public static void setValues(List<Double> values, String id) {
        log.info("Getting coordinates \n");
        listCoordinates.addAll(values);
        mapCoordinates.put(id, values);
        /*for (Map.Entry<String, List<Double>> entry : coordinatesMap.entrySet()) {
            String key = entry.getKey();
            List<Double> tvalues = entry.getValue();

            System.out.println("Key: " + key);
            System.out.println("Values: " + tvalues);
            System.out.println();  // For better readability
        }*/
    }
}

