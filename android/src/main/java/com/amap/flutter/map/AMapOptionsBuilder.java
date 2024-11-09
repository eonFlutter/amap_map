package com.amap.flutter.map;

import android.content.Context;

import androidx.annotation.IntRange;
import androidx.lifecycle.LifecycleOwner;

import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.flutter.map.core.AMapOptionsSink;
import com.amap.flutter.map.core.UISettingsSink;
import com.amap.flutter.map.utils.LogUtil;

import java.util.List;

import io.flutter.plugin.common.BinaryMessenger;

/**
 * @author whm
 * @date 2020/10/29 10:13 AM
 * @mail hongming.whm@alibaba-inc.com
 * @since
 */
class AMapOptionsBuilder implements AMapOptionsSink, UISettingsSink {
    private static final String CLASS_NAME = "AMapOptionsBuilder";
    private final AMapOptions options = new AMapOptions();
    private CustomMapStyleOptions customMapStyleOptions;
    private MyLocationStyle myLocationStyle;

    private boolean showsHeadingIndicator = true;

    private float minZoomLevel = 3;
    private float maxZoomLevel = 20;
    private LatLngBounds latLngBounds;
    private boolean trafficEnabled = true;
    private boolean touchPoiEnabled = true;
    private boolean buildingsEnabled = true;
    private boolean labelsEnabled = true;

    private int logoPosition = AMapOptions.LOGO_POSITION_BOTTOM_LEFT;
    private int logoBottomMargin = 0;
    private int logoLeftMargin = 0;

    private float anchorX = 2.0F;
    private float anchorY = 2.0F;

    private Object initialMarkers;

    private Object initialPolylines;

    private Object initialPolygons;

    private String mapLanguage;

    AMapPlatformView build(int id,
                           Context context,
                           BinaryMessenger binaryMessenger,
                           LifecycleOwner lifecycleProvider) {
        try {
            //iOS端没有放大缩小UI, Android端强制隐藏
            options.zoomControlsEnabled(false);
            final AMapPlatformView aMapPlatformView = new AMapPlatformView(id, context, binaryMessenger, lifecycleProvider, options);


            if (null != customMapStyleOptions) {
                aMapPlatformView.getMapController().setCustomMapStyleOptions(customMapStyleOptions);
            }

            if (null != myLocationStyle) {
                aMapPlatformView.getMapController().setShowsHeadingIndicator(showsHeadingIndicator);
                aMapPlatformView.getMapController().setMyLocationStyle(myLocationStyle);
            }
            if (anchorX >= 0
                    && anchorX <= 1.0
                    && anchorY <= 1.0
                    && anchorY >= 0) {

                aMapPlatformView.getMapController().setScreenAnchor(anchorX, anchorY);
            }

            aMapPlatformView.getMapController().setMinZoomLevel(minZoomLevel);
            aMapPlatformView.getMapController().setMaxZoomLevel(maxZoomLevel);

            if (null != latLngBounds) {
                aMapPlatformView.getMapController().setLatLngBounds(latLngBounds);
            }

            aMapPlatformView.getMapController().setTrafficEnabled(trafficEnabled);
            aMapPlatformView.getMapController().setTouchPoiEnabled(touchPoiEnabled);
            aMapPlatformView.getMapController().setBuildingsEnabled(buildingsEnabled);
            aMapPlatformView.getMapController().setLabelsEnabled(labelsEnabled);

            aMapPlatformView.getMapController().setLogoPosition(logoPosition);
            aMapPlatformView.getMapController().setLogoBottomMargin(logoBottomMargin);
            aMapPlatformView.getMapController().setLogoLeftMargin(logoLeftMargin);


            if (null != initialMarkers) {
                List<Object> markerList = (List<Object>) initialMarkers;
                aMapPlatformView.getMarkersController().addByList(markerList);
            }

            if (null != initialPolylines) {
                List<Object> markerList = (List<Object>) initialPolylines;
                aMapPlatformView.getPolylinesController().addByList(markerList);
            }

            if (null != initialPolygons) {
                List<Object> polygonList = (List<Object>) initialPolygons;
                aMapPlatformView.getPolygonsController().addByList(polygonList);
            }

            aMapPlatformView.getMapController().setMapLanguage(mapLanguage);
            return aMapPlatformView;
        } catch (Throwable e) {
            LogUtil.e(CLASS_NAME, "build", e);
        }
        return null;
    }

    @Override
    public void setCamera(CameraPosition camera) {
        options.camera(camera);
    }

    @Override
    public void setMapType(int mapType) {
        options.mapType(mapType);
    }

    @Override
    public void setCustomMapStyleOptions(CustomMapStyleOptions customMapStyleOptions) {
        this.customMapStyleOptions = customMapStyleOptions;
    }

    @Override
    public void setMyLocationStyle(MyLocationStyle myLocationStyle) {
        this.myLocationStyle = myLocationStyle;
    }

    @Override
    public void setShowsHeadingIndicator(boolean value) {
        this.showsHeadingIndicator = value;
    }

    @Override
    public void setScreenAnchor(float x, float y) {
        anchorX = x;
        anchorY = y;
    }

    @Override
    public void setMinZoomLevel(float minZoomLevel) {
        this.minZoomLevel = minZoomLevel;
    }

    @Override
    public void setMaxZoomLevel(float maxZoomLevel) {
        this.maxZoomLevel = maxZoomLevel;
    }

    @Override
    public void setLatLngBounds(LatLngBounds latLngBounds) {
        this.latLngBounds = latLngBounds;
    }

    @Override
    public void setTrafficEnabled(boolean trafficEnabled) {
        this.trafficEnabled = trafficEnabled;
    }

    @Override
    public void setTouchPoiEnabled(boolean touchPoiEnabled) {
        this.touchPoiEnabled = touchPoiEnabled;
    }

    @Override
    public void setBuildingsEnabled(boolean buildingsEnabled) {
        this.buildingsEnabled = buildingsEnabled;
    }

    @Override
    public void setLabelsEnabled(boolean labelsEnabled) {
        this.labelsEnabled = labelsEnabled;
    }

    @Override
    public void setCompassEnabled(boolean compassEnabled) {
        options.compassEnabled(compassEnabled);
    }

    @Override
    public void setZoomGesturesEnabled(boolean zoomGesturesEnabled) {
        options.zoomGesturesEnabled(zoomGesturesEnabled);
    }

    @Override
    public void setScrollGesturesEnabled(boolean scrollGesturesEnabled) {
        options.scrollGesturesEnabled(scrollGesturesEnabled);
    }

    @Override
    public void setRotateGesturesEnabled(boolean rotateGesturesEnabled) {
        options.rotateGesturesEnabled(rotateGesturesEnabled);
    }

    @Override
    public void setTiltGesturesEnabled(boolean tiltGesturesEnabled) {
        options.tiltGesturesEnabled(tiltGesturesEnabled);
    }

    @Override
    public void setScaleEnabled(boolean scaleEnabled) {
        options.scaleControlsEnabled(scaleEnabled);
    }

    @Override
    public void setLogoPosition(@IntRange(from = AMapOptions.LOGO_POSITION_BOTTOM_LEFT, to = AMapOptions.LOGO_POSITION_BOTTOM_RIGHT) int logoPosition) {
        options.logoPosition(logoPosition);
        this.logoPosition = logoPosition;
    }

    @Override
    public int getLogoPosition() {
        return options.getLogoPosition();
    }

    @Override
    public void setLogoBottomMargin(int pixels) {
        this.logoBottomMargin = pixels;
    }

    @Override
    public void setLogoLeftMargin(int pixels) {
        this.logoLeftMargin = pixels;
    }


    @Override
    public void setInitialMarkers(Object markersObject) {
        this.initialMarkers = markersObject;
    }

    @Override
    public void setInitialPolylines(Object polylinesObject) {
        this.initialPolylines = polylinesObject;
    }

    @Override
    public void setInitialPolygons(Object polygonsObject) {
        this.initialPolygons = polygonsObject;
    }

    @Override
    public void setMapLanguage(String mapLanguage) {
        this.mapLanguage = mapLanguage;
    }

}
