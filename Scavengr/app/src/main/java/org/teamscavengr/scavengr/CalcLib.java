package org.teamscavengr.scavengr;

import android.location.Location;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public static double distanceFromLatLng(double startLat ,double startLng, double endLat, double endLng) {
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

    public static double distanceFromLatLng(Location start, Location end) {
        return distanceFromLatLng(start.getLatitude(), start.getLongitude(),
                end.getLatitude(),
                end.getLongitude());
    }

    public static LatLng maxDistanceFromCentroid(LatLng centroid, List<LatLng> points) {
        double diffLat = 0;
        double diffLng = 0;
        if (points.size() == 1) {
            return new LatLng(.0002, .0002);
        }
        for (LatLng point: points) {
            if (diffLat < Math.abs(point.latitude - centroid.latitude)) {
                diffLat = Math.abs(point.latitude - centroid.latitude);
            }
            if (diffLng < Math.abs(point.longitude - centroid.longitude)) {
                diffLng = Math.abs(point.longitude - centroid.longitude);
            }
        }
        return new LatLng(diffLat, diffLng);
    }

    /**
     * Gets the average of LatLng.
     *
     * @param points List of LatLng points to get the average of.
     * @return LatLng representing the average latLng of the points
     */
    public static LatLng CentralAverage(List<LatLng> points) {
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
       if (hunt.getNumberOfTasks() == 1) {
           Task onlyTask = hunt.getTasks().get(0);
           Double radius = onlyTask.getRadius();
           Location answerLocation = onlyTask.getLocation();

           Random random = new Random();
//           int percentage = random.nextInt((100 - -100) + 1) + -100;
//           float multiplier = ((float)percentage)/100f;

           double newLat = answerLocation.getLatitude();
           double newLong = answerLocation.getLongitude();

           LatLng centroid = new LatLng(newLat, newLong);
           return  new Pair<LatLng, Double>(centroid, radius*2);
       }

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
        if (hunt.getNumberOfTasks() == 1) {
            radius = hunt.getTasks().get(0).getRadius();
        } else {
            for (int i = 0; i < hunt.getNumberOfTasks(); i++) {
                Log.d("MEGAN", "Task Location: " + lats.get(i) + lngs.get(i));
                double potentialRadius = CalcLib.distanceFromLatLng(new LatLng(lats.get(i), lngs.get(i)),
                        centroid);

                if (potentialRadius > radius) {
                    radius = potentialRadius;
                }
            }
        }
        return  new Pair<LatLng, Double>(centroid, Math.max(200, radius*1.1));
    }
}
