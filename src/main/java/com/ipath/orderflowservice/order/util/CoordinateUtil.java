package com.ipath.orderflowservice.order.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CoordinateUtil {
    // WGS84标准参考椭球中的地球长半径(单位:米)
    private static final double EARTH_RADIUS_WGS84 = 6378137.0;

    /**
     * 计算两个坐标的距离(粗略计算，单位:米)
     * 计算公式参照 google map 的距离计算
     *
     * @param lat1 坐标1纬度
     * @param lng1 坐标1经度
     * @param lat2 坐标2纬度
     * @param lng2 坐标2经度
     * @return
     */
    public static double distance(String lng1, String lat1, String lng2, String lat2) {
        try {
            double radLat1 = Math.toRadians(Double.valueOf(lat1));
            double radLat2 = Math.toRadians(Double.valueOf(lat2));

            double a = radLat1 - radLat2;
            double b = Math.toRadians(Double.valueOf(lng1)) - Math.toRadians(Double.valueOf(lng2));

            double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                    Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));

            return Math.round(s * EARTH_RADIUS_WGS84);
        }catch (Exception e){
            log.error("distance ===> {}",e.getMessage());
        }
        return 900;
    }

    /**
     * 获取两点间的直线距离 公里
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static Integer getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = getRadian(lat1);
        double radLat2 = getRadian(lat2);
        double a = radLat1 - radLat2;
        double b = getRadian(lng1) - getRadian(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1)
                * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378.137;
        return Double.valueOf(s).intValue();
    }

    /**
     * 获取两点间的直线距离 公里
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static Integer getDistanceMeter(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = getRadian(lat1);
        double radLat2 = getRadian(lat2);
        double a = radLat1 - radLat2;
        double b = getRadian(lng1) - getRadian(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1)
                * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378.137 * 1000;
        return Double.valueOf(s).intValue();
    }

    private static double getRadian(double degree) {
        return degree * Math.PI / 180.0;
    }
//    public static void main(String[] args) {
//        double distance = CoordinateUtil.distance(38.855271, 121.526744, 38.855310, 121.526744);
//        System.out.println(distance);
//    }

}
