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

package io.phonk.runner.apprunner.api.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.MapBoxTileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Map;

import io.phonk.runner.R;
import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.utils.MLog;

@PhonkClass
public class PMap extends MapView implements PViewMethodsInterface {
    // this is a props proxy for the user
    public final StylePropertiesProxy props = new StylePropertiesProxy();
    // the props are transformed / accessed using the styler object
    public final Styler styler;
    final String TAG = PMap.class.getSimpleName();
    // MyLocationNewOverlay myLocationOverlay;
    final ItemizedOverlayWithFocus<OverlayItem> iconOverlay;
    private final boolean firstMarker = false;
    private final AppRunner mAppRunner;
    private final Context mContext;
    private IMapController mapController = null;
    private MapView mapView = null;
    private ArrayList<OverlayItem> markerList = null;

    public PMap(AppRunner appRunner, Map initProps) {
        super(appRunner.getAppContext());

        mAppRunner = appRunner;
        this.mContext = appRunner.getAppContext();

        styler = new Styler(appRunner, this, props);
        props.eventOnChange = false;
        Styler.fromTo(initProps, props);
        props.eventOnChange = true;
        styler.apply();

        Configuration.getInstance().setUserAgentValue(this.mContext.getApplicationContext().getPackageName());

        // Create the mapview with the custom tile provider array
        this.mapView = this;
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setTilesScaledToDpi(true);
        // mapView.setBuiltInZoomControls(false);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        mapView.setMultiTouchControls(true);
        mapView.setUseDataConnection(true);
        mapView.setClickable(true);
        mapView.setFocusable(true);
        mapView.setDuplicateParentStateEnabled(false);

        mapController = mapView.getController();

        markerList = new ArrayList<>();

        Drawable icon = mContext.getResources().getDrawable(R.drawable.icon);
        iconOverlay = new ItemizedOverlayWithFocus<>(markerList,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(int arg0, OverlayItem arg1) {
                        MLog.d(TAG, "single press");

                        // new InfoWindow()

                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(int arg0, OverlayItem arg1) {
                        MLog.d(TAG, "long press");
                        return false;
                    }
                },
                getContext()
        );


        iconOverlay.setFocusItemsOnTap(true);


        mapView.getOverlays().add(iconOverlay);

    }

    /**
     * Creates a path in which it can be added new point
     * Once the path is created, we can add points to it
     *
     * @param color
     * @return
     */
    @PhonkMethod
    public PMapPath createPath(String color) {
        PMapPath path = new PMapPath();
        path.setColor(Color.parseColor(color));
        mapView.getOverlays().add(path);

        return path;
    }

    /**
     * Removes a path from the map
     *
     * @param path
     * @return
     */
    @PhonkMethod
    public PMapPath removePath(PMapPath path) {
        mapView.getOverlays().remove(path);

        return path;
    }


    /**
     * Set a new tile source such as mapbox and others
     *
     * @param name
     * @param url
     * @return
     */
    @PhonkMethod
    public MapView tileSource(String name, String url) {

        String[] tileSourcesUrl = new String[1];
        tileSourcesUrl[0] = url;
        MapTileProviderBasic tileProvider = new MapTileProviderBasic(mContext);
        ITileSource tileSource = new XYTileSource(name, 3, 10, 256, ".png", tileSourcesUrl);

        tileProvider.setTileSource(tileSource);
        mapView.setTileSource(tileSource);

        return this;
    }

    /**
     * Set a new tile source such as mapbox and others
     *
     * @param type
     * @return
     */
    @PhonkMethod
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

    /**
     * Set a new tile source such as mapbox and others
     *
     * @param accessToken
     * @param mapId
     * @return
     */
    @PhonkMethod
    public MapView mapBoxTileSource(String accessToken, String mapId) {
        final MapBoxTileSource tileSource = new MapBoxTileSource();

        // option 2, provide them programmatically
        tileSource.setAccessToken(accessToken);

        if (mapId != null) {
            tileSource.setMapboxMapid(mapId);
        }
        mapView.setTileSource(tileSource);

        return this;
    }

    /**
     * Add a marker to the map.
     * The params object accepts params as follows
     * {
     * lon: 21.1,
     * lat: 22.2,
     * icon: "myicon.png",
     * title: "hello",
     * description: "Bubble info"
     * }
     *
     * @param params
     * @return
     */
    @PhonkMethod
    public PMapMarker addMarker(Map params) {
        PMapMarker m = new PMapMarker(mapView);

        // location
        GeoPoint loc = new GeoPoint((double) params.get("lat"), (double) params.get("lon"));
        m.setPosition(loc);

        // icon
        String iconName = (String) params.get("icon");
        if (iconName == null) m.setIcon(getResources().getDrawable(R.drawable.marker));
        else {
            m.icon(iconName);
        }

        String title = (String) params.get("title");
        if (title != null) m.title(title);

        String description = (String) params.get("description");
        if (description != null) m.description(description);

        mapView.getOverlays().add(m);
        mapView.invalidate();

        return m;
    }


    /**
     * Clear the map cache"
     *
     * @return
     */
    @PhonkMethod
    public MapView clearCache() {
        mapView.getTileProvider().clearTileCache();

        return this;
    }

    /**
     * Zoom in/out depending on the integer given
     *
     * @param zoom
     * @return
     */
    @PhonkMethod
    public MapView zoom(int zoom) {
        mapController.setZoom(zoom);

        return this;
    }

    /**
     * Show/hide the map controls
     *
     * @param b
     * @return
     */
    @PhonkMethod
    public MapView showControls(boolean b) {
        mapView.setBuiltInZoomControls(b);

        return this;
    }

    /**
     * Enable/Disable the multitouch events in the map
     *
     * @param b
     * @return
     */
    @PhonkMethod
    public MapView multitouch(boolean b) {
        mapView.setMultiTouchControls(b);
        return this;
    }

    /**
     * Move to a specified location
     *
     * @param lat
     * @param lon
     * @return
     */
    @PhonkMethod
    public MapView moveTo(double lat, double lon) {
        GeoPoint point2 = new GeoPoint(lat, lon);
        mapController.animateTo(point2);

        return this;
    }

    /**
     * Set the center of the map with the specified location
     *
     * @param lat
     * @param lon
     * @return
     */
    @PhonkMethod
    public MapView center(double lat, double lon) {
        GeoPoint point2 = new GeoPoint(lat, lon);
        mapController.setCenter(point2);

        return this;
    }


    /**
     * Gets the current center of the map
     *
     * @return
     */
    @PhonkMethod
    public GeoPoint center() {
        return mapView.getBoundingBox().getCenter();
    }


    /**
     * Gets the current zoom of the map
     *
     * @return
     */
    @PhonkMethod
    public float zoom() {
        return mapView.getZoomLevel();
    }


    /**
     * Set the zoom limits
     *
     * @param min
     * @param max
     * @return
     */
    @PhonkMethod
    public MapView zoomLimits(double min, double max) {
        mapView.setMinZoomLevel(min);
        mapView.setMaxZoomLevel(max);

        return this;
    }

    /**
     * Get coordinates from the pixel position of the map
     *
     * @param x
     * @param y
     * @return
     */
    @PhonkMethod
    public org.osmdroid.api.IGeoPoint pixelsToGeo(int x, int y) {
        return mapView.getProjection().fromPixels(x, y);
    }

    /**
     * Get coordinates from the pixel position of the map
     *
     * @param lat
     * @param lon
     * @return
     */
    @PhonkMethod
    public Point geoToPixels(double lat, double lon) {
        GeoPoint point = new GeoPoint(lat, lon);

        return mapView.getProjection().toPixels(point, null);
    }

    /**
     * Enable / disable online map data
     *
     * @param b
     */
    public void useOnlineData(boolean b) {
        mapView.setUseDataConnection(b);
    }

    /*
     * OSMbonus methods
     */

    @Override
    public void set(float x, float y, float w, float h) {
        styler.setLayoutProps(x, y, w, h);
    }

    public class PMapMarker extends Marker {

        public PMapMarker(MapView mapView) {
            super(mapView);
        }

        public PMapMarker position(double lon, double lat) {
            setPosition(new GeoPoint(lon, lat));
            invalidate();

            return this;
        }

        public PMapMarker title(String title) {
            setTitle(title);
            invalidate();
            return this;
        }

        public PMapMarker snippet(String description) {
            setSnippet(description);
            return this;
        }

        public PMapMarker subDescription(String text) {
            setSubDescription(text);
            return this;
        }

        public PMapMarker icon(String iconSrc) {
            Bitmap iconBmp = BitmapFactory.decodeFile(mAppRunner.getProject().getFullPathForFile(iconSrc));
            setIcon(new BitmapDrawable(getResources(), iconBmp));

            return this;
        }

        public PMapMarker image(String imgSrc) {
            Bitmap iconBmp = BitmapFactory.decodeFile(mAppRunner.getProject().getFullPathForFile(imgSrc));
            setImage(new BitmapDrawable(getResources(), iconBmp));

            return this;
        }

        public PMapMarker description(String description) {
            setSnippet(description);
            invalidate();
            return this;
        }

        public PMapMarker onClick(final ReturnInterface callbackfn) {
            this.setOnMarkerClickListener((marker, mapView) -> {
                ReturnObject o = new ReturnObject();
                o.put("marker", PMapMarker.this);
                callbackfn.event(o);
                return false;
            });

            return this;
        }
    }

    public class PMapPath extends Polyline {
        PMapPath() {
            super();
        }

        @PhonkMethod(description = "Add a point to the path", example = "")
        @PhonkMethodParam(params = {"path", "latitude", "longitude"})
        public PMapPath addGeoPoint(double lat, double lon) {
            addPoint(new GeoPoint(lat, lon));
            mapView.invalidate();

            return this;
        }

    }

    @Override
    public void setProps(Map style) {
        styler.setProps(style);
    }

    @Override
    public Map getProps() {
        return props;
    }
}

