/*
 * Part of Phonk http://www.phonk.io
 * A prototyping platform for Android devices
 *
 * Copyright (C) 2013 - 2017 Victor Diaz Barrales @victordiaz (Protocoder)
 * Copyright (C) 2017 - Victor Diaz Barrales @victordiaz (Phonk)
 *
 * Phonk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Phonk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Phonk. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.phonk.runner.api.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;

import io.phonk.runner.R;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.MLog;

import java.util.ArrayList;

public class PMap extends MapView {

    final String TAG = PMap.class.getSimpleName();

    private IMapController mapController = null;
    private MapView mapView = null;
    // MyLocationNewOverlay myLocationOverlay;
    ItemizedOverlayWithFocus<OverlayItem> iconOverlay;
    private final boolean firstMarker = false;
    private ArrayList<OverlayItem> markerList = null;

    private Context c;

    public PMap(AppRunner appRunner) {
        super(appRunner.getAppContext());
        this.c = appRunner.getAppContext();
        // super(appRunner, pixelTileSize);

        // Create the mapview with the custom tile provider array
        this.mapView = this;
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapView.setTilesScaledToDpi(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setUseDataConnection(true);
        mapView.setClickable(true);
        mapView.setFocusable(true);
        mapView.setDuplicateParentStateEnabled(false);

        mapController = mapView.getController();

        /*
        mapView.setMapListener(new DelayedMapListener(new MapListener() {
            @Override
            public boolean onZoom(final ZoomEvent e) {
                // do something
                MLog.d("map", "zoom " + e.getZoomLevel());
                return true;
            }

            @Override
            public boolean onScroll(final ScrollEvent e) {
                Log.i("zoom", e.getX() + " " + e.getY());
                return true;
            }
        }, 1000));
        */

        markerList = new ArrayList<OverlayItem>();

        Drawable icon = c.getResources().getDrawable(R.drawable.icon);
        iconOverlay = new ItemizedOverlayWithFocus<OverlayItem>(markerList,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemLongPress(int arg0, OverlayItem arg1) {
                        MLog.d(TAG, "long press");
                        return false;
                    }

                    @Override
                    public boolean onItemSingleTapUp(int arg0, OverlayItem arg1) {
                        MLog.d(TAG, "single press");

                        // new InfoWindow()

                        return true;
                    }
                }, getContext());


        iconOverlay.setFocusItemsOnTap(true);


        // myLocationOverlay = new MyLocationNewOverlay(c, mapView);
        // mapView.getOverlays().add(myLocationOverlay);
        mapView.getOverlays().add(iconOverlay);

        /*
        mapView.setMapListener(new DelayedMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                //Log.d(TAG, "qqqqqq");

                //mapView.getBoundingBox().getCenter();

                return true;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                //Log.d(TAG, "qqqqqq");

                //mapView.getBoundingBox().getCenter();


                return true;
            }
        }, 500));
        */

        // myLocationOverlay.enableMyLocation();
        // myLocationOverlay.setDrawAccuracyEnabled(true);

    }


    @ProtoMethod(description = "Creates a path in which it can be added new points", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public MapPath createPath(String color) {
        MapPath path = new MapPath();
        path.setColor(Color.parseColor(color));
        mapView.getOverlays().add(path);

        return path;
    }


    @ProtoMethod(description = "Set a new tile source such as mapbox and others", example = "")
    @ProtoMethodParam(params = {"name", "url"})
    public MapView tileSource(String name, String url) {

        String[] tileSourcesUrl = new String[1];
        tileSourcesUrl[0] = url;
        MapTileProviderBasic tileProvider = new MapTileProviderBasic(c);
        ITileSource tileSource = new XYTileSource(name, 3, 10, 256, ".png", tileSourcesUrl);

        tileProvider.setTileSource(tileSource);
        mapView.setTileSource(tileSource);

        return this;
    }

    public MapView tileSource(String type) {
        OnlineTileSourceBase source;
        if (type.equals("hikebikemap")) {
            source = TileSourceFactory.HIKEBIKEMAP;
        } else {
            source = TileSourceFactory.MAPNIK;
        }

        mapView.setTileSource(source);

        return mapView;
    }

    /*
    @ProtoMethod(description = "Add a new marker", example = "")
    @ProtoMethodParam(params = {"title", "text", "latitude", "longitude"})
    public OverlayItem addMarker(String title, String text, double lat, double lon) {

        OverlayItem olItem = new OverlayItem(title, text, new GeoPoint(lat, lon));
        Drawable newMarker = c.getResources().getDrawable(R.drawable.marker);
        olItem.setMarker(newMarker);
        olItem.setMarkerHotspot(OverlayItem.HotspotPlace.BOTTOM_CENTER);
        markerList.add(olItem);
        iconOverlay.addItem(olItem);
        this.invalidate();

        return olItem;
    }
    */


    public MapMarker addMarker(double lat, double lon) {
        MapMarker m = new MapMarker(mapView);
        m.setPosition(new GeoPoint(lat, lon));
        m.setIcon(getResources().getDrawable(R.drawable.marker));
        // m.setMarkerHotspot(OverlayItem.HotspotPlace.BOTTOM_CENTER);

        mapView.getOverlays().add(m);
        mapView.invalidate();
        return m;
    }


    @ProtoMethod(description = "Clear the map cache", example = "")
    @ProtoMethodParam(params = {""})
    public MapView clearCache() {
        mapView.getTileProvider().clearTileCache();

        return this;
    }

    @ProtoMethod(description = "Zoom in/out depending on the integer given", example = "")
    @ProtoMethodParam(params = {"zoomValue"})
    public MapView zoom(int z) {
        mapController.setZoom(z);

        return this;
    }

    @ProtoMethod(description = "Show/hide the map controls", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public MapView showControls(boolean b) {
        mapView.setBuiltInZoomControls(b);

        return this;
    }

    @ProtoMethod(description = "Enable/Disables the multitouch events in the map", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public MapView multitouch(boolean b) {
        mapView.setMultiTouchControls(b);
        return this;
    }

    @ProtoMethod(description = "Move to a specified location", example = "")
    @ProtoMethodParam(params = {"latitude", "longitude"})
    public MapView moveTo(double lat, double lon) {
        GeoPoint point2 = new GeoPoint(lat, lon);
        mapController.animateTo(point2);

        return this;
    }

    @ProtoMethod(description = "Set the center of the map with the specified location", example = "")
    @ProtoMethodParam(params = {"latitude", "longitude"})
    public MapView center(double lat, double lon) {
        GeoPoint point2 = new GeoPoint(lat, lon);
        mapController.setCenter(point2);

        return this;
    }


    @ProtoMethod(description = "Gets the current center of the map", example = "")
    @ProtoMethodParam(params = {""})
    public GeoPoint center() {
        return mapView.getBoundingBox().getCenter();
    }


    @ProtoMethod(description = "Gets the current zoom of the map", example = "")
    @ProtoMethodParam(params = {""})
    public float zoom() {
        return mapView.getZoomLevel();
    }


    @ProtoMethod(description = "Set the zoom limits", example = "")
    @ProtoMethodParam(params = {"min", "max"})
    public MapView zoomLimits(int min, int max) {
        mapView.setMinZoomLevel(min);
        mapView.setMaxZoomLevel(max);

        return this;
    }


    @ProtoMethod(description = "Get coordinates from the pixel position of the map", example = "")
    @ProtoMethodParam(params = {"x", "y"})
    public org.osmdroid.api.IGeoPoint pixelsToGeo(int x, int y) {
        return mapView.getProjection().fromPixels(x, y);
    }


    @ProtoMethod(description = "Get coordinates from the pixel position of the map", example = "")
    @ProtoMethodParam(params = {"x", "y"})
    public Point geoToPixels(double lat, double lon) {
        GeoPoint point = new GeoPoint(lat, lon);

        return mapView.getProjection().toPixels(point, null);
    }

    public void useOnlineData(boolean b) {
        mapView.setUseDataConnection(b);
    }

    /*
     * OSMbonus methods
     */

    /*
    public void getRoadPath(double lat1, double lon1, double lat2, double lon2) {
        RoadManager roadManager = new OSRMRoadManager(c);

        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(new GeoPoint(lat1, lon1));
        waypoints.add(new GeoPoint(lat2, lon2));

        Road road = roadManager.getRoad(waypoints);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road, c);
        roadOverlay.setWidth(2);
        mapView.getOverlays().add(roadOverlay);
        mapView.invalidate();
    }

    public void addGroundOverlay(double lat, double lon) {
        GroundOverlay myGroundOverlay = new GroundOverlay(c);
        myGroundOverlay.setPosition(new GeoPoint(lat, lon));
        myGroundOverlay.setImage(getResources().getDrawable(R.drawable.protocoder_icon).mutate());
        myGroundOverlay.setDimensions(2000.0f);
        mapView.getOverlays().add(myGroundOverlay);
        mapView.invalidate();
    }
    */

    /*
    public GeoPoint createPoint(double lat, double lon) {
        return new GeoPoint(lat, lon);
    }

    public void loadKml(final String url, final boolean center) {
        final KmlDocument kmlDocument = new KmlDocument();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                kmlDocument.parseKMLUrl(url);

                mapView.post(new Runnable() {
                    @Override
                    public void run() {
                        FolderOverlay kmlOverlay = (FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(mapView, null, null, kmlDocument);
                        mapView.getOverlays().add(kmlOverlay);
                        mapView.invalidate();
                        //if (center) mapView.zoomToBoundingBox(kmlDocument.mKmlRoot.getBoundingBox());

                    }
                });
            }
        });
        t.start();
    }
    */

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        int action = ev.getAction();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                // Disallow ScrollView to intercept touch events.
//                this.getParent().requestDisallowInterceptTouchEvent(true);
//                break;
//
//            case MotionEvent.ACTION_UP:
//                // Allow ScrollView to intercept touch events.
//                this.getParent().requestDisallowInterceptTouchEvent(false);
//                break;
//        }
//
//        // Handle MapView's touch events.
//        super.onTouchEvent(ev);
//        return true;
//    }

    public class MapMarker extends Marker {

        public MapMarker(MapView mapView) {
            super(mapView);
        }

        public MapMarker title(String title) {
            setTitle(title);
            invalidate();
            return this;
        }

        public MapMarker snippet(String description) {
            setSnippet(description);
            return this;
        }

        public MapMarker subDescription(String text) {
            setSubDescription(text);
            return this;
        }

        public MapMarker icon(String iconSrc) {

            return this;
        }

        public MapMarker image(String imgSrc) {

            return this;
        }



    }

    public class MapPath extends Polyline {
        MapPath() {
            super();

        }

        @ProtoMethod(description = "Add a point to the path", example = "")
        @ProtoMethodParam(params = {"path", "latitude", "longitude"})
        public MapPath addGeoPoint(double lat, double lon) {
            addPoint(new GeoPoint(lat, lon));
            mapView.invalidate();

            return this;
        }


        @ProtoMethod(description = "Clear the path", example = "")
        @ProtoMethodParam(params = {"path"})
        public MapPath clear() {
            clearPath();
            mapView.invalidate();

            return this;
        }

    }
}
