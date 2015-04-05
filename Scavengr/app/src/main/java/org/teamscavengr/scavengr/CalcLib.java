package org.teamscavengr.scavengr;

import android.location.Location;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by erosales on 3/15/15.
 */
public class CalcLib {

    /**
     * Gets the distance between two points in meters
     *
     * @param startLat starting latitude.
     * @param startLng starting longitude.
     * @param endLat ending latitude.
     * @param endLng ending longitude.
     * @return distance between both points in meters.
     */
    public static double distanceFromLatLng(float startLat ,float startLng, float endLat, float endLng) {
        int R = 6371000; // metres
        double phi1 = Math.toRadians(startLat);
        double phi2 = Math.toRadians(endLat);
        double deltaPhi = Math.toRadians(endLat-startLat);
        double deltaLambda = Math.toRadians(endLng-startLng);

        double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) +
                Math.cos(phi1) * Math.cos(phi2) *
                        Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double d = R * c;
        return d;
    }

    /**
     * Gets the distance between two points in meters
     *
     * @param start starting latitude and longitude.
     * @param end ending latitude and longitude.
     * @return distance between both points in meters.
     */
    public static double distanceFromLatLng(LatLng start ,LatLng end) {
        double startLat = start.latitude;
        double startLng = start.longitude;
        double endLat = end.latitude;
        double endLng = end.longitude;
        int R = 6371000; // metres
        double phi1 = Math.toRadians(startLat);
        double phi2 = Math.toRadians(endLat);
        double deltaPhi = Math.toRadians(endLat-startLat);
        double deltaLambda = Math.toRadians(endLng-startLng);

        double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) +
                Math.cos(phi1) * Math.cos(phi2) *
                        Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double d = R * c;
        return d;
    }

    /**
     * Gets the average of LatLng.
     *
     * @param points List of LatLng points to get the average of.
     * @return LatLng representing the average latLng of the points
     */
    public static LatLng CentralAverage(ArrayList<LatLng> points) {
        double avgLat = 0;
        double avgLng = 0;
        int size = 0;
        for (LatLng point : points) {
            if (size == 0) {
                avgLat = point.latitude;
                avgLng = point.longitude;
                size = 1;
            } else {
                avgLat *= (size - 1.0)/ size;
                avgLat += (1.0/size) * point.latitude;
                avgLng *= (size - 1.0)/ size;
                avgLng += (1.0/size) * point.longitude;
                size += 1;
            }
        }
       return new LatLng(avgLat, avgLng);
    }

    public static Pair<LatLng, Double> calculateCentroidAndRadius (Hunt hunt) {
        double radius = 0.0;
        double centroidLat = 0.0;
        double centroidLng = 0.0;
        ArrayList<Double> lats = new ArrayList<Double>();
        ArrayList<Double> lngs = new ArrayList<Double>();

        for (Task task : hunt.getTasks()){
            Location taskLocation = task.getLocation();
            double lat = taskLocation.getLatitude();
            double lng = taskLocation.getLongitude();
            centroidLat += lat;
            centroidLng += lng;
            lats.add(lat);
            lngs.add(lng);
        }

        centroidLat = centroidLat/hunt.getNumberOfTasks();
        centroidLng = centroidLng/hunt.getNumberOfTasks();
        LatLng centroid = new LatLng(centroidLat, centroidLng);

        for (int i = 0; i < hunt.getNumberOfTasks(); i++){
            double potentialRadius = CalcLib.distanceFromLatLng(new LatLng(lats.get(i), lngs.get(i)),
                    centroid);
            if (potentialRadius > radius){
                radius = potentialRadius;
            }
        }
        return  new Pair<LatLng, Double>(centroid, radius);
    }
}
