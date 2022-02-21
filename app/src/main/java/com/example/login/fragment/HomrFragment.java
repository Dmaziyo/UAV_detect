package com.example.login.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.login.MQTT.Link_Mqtt;
import com.example.login.MQTT.MQTTService;
import com.example.login.MQTT.MyServiceConnection;
import com.example.login.R;
import com.example.login.UI.MainActivity;
import com.example.login.util.overlayutil.WalkingRouteOverlay;
import com.example.login.utils.MPermissionUtils;
import com.example.login.utils.MapApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class HomrFragment extends Fragment  {
    private boolean isFirstin = true;
    private Activity myActivity;
    private MapApplication myApplication;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private double stratLatitude;//定位纬度
    private double stratLongitude;//定位经度
    private MyLocationListener mLocationListener;
    private LocationClient mLocationClient;
    private RoutePlanSearch mRoutePlanSrch=null;

    private BitmapDescriptor bitmap;//标点的图标
    private double markerLatitude = 0;//标点纬度
    private double markerLongitude = 0;//标点经度
    private ImageButton ibLocation;//重置定位按钮
    private Marker marker;//标点也可以说是覆盖物
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myActivity= (Activity) context;
        myApplication= (MapApplication) myActivity.getApplication();
    }
    ///定位标点
    public void resetLocation(View view) {

        markerLatitude = 0;
        initLocation();
        StarRoute();
        marker.remove();//清除标点
    }
    private void mapOnClick() {
        // 设置marker图标
        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marker);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

            }

            //此方法就是点击地图监听
            @Override
            public void onMapClick(LatLng latLng) {
                //获取经纬度
                markerLatitude = latLng.latitude;
                markerLongitude = latLng.longitude;
                //先清除图层
                mBaiduMap.clear();
                // 定义Maker坐标点
                LatLng point = new LatLng(markerLatitude, markerLongitude);
                // 构建MarkerOption，用于在地图上添加Marker
                MarkerOptions options = new MarkerOptions().position(point)
                        .icon(bitmap);
                // 在地图上添加Marker，并显示
                //mBaiduMap.addOverlay(options);
                marker = (Marker) mBaiduMap.addOverlay(options);
                Bundle bundle = new Bundle();
                bundle.putSerializable("info", "纬度：" + markerLatitude + "   经度：" + markerLongitude);
                marker.setExtraInfo(bundle);//将bundle值传入marker中，给baiduMap设置监听时可以得到它

                //点击地图之后重新定位
                initLocation();
            }
        });

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_homr,container,false);
        mMapView=view.findViewById(R.id.bmapView);
        ibLocation = (ImageButton)view.findViewById(R.id.ib_location);
        mBaiduMap=mMapView.getMap();
        ibLocation.setOnClickListener(this::resetLocation);
        mRoutePlanSrch=RoutePlanSearch.newInstance();
        mRoutePlanSrch.setOnGetRoutePlanResultListener(routePlanSrchlistener);
        initView();
        setViewListener();
        return view;
    }

    private void initView() {

        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        //声明LocationClient类
        LocationClient mLocationClient = new LocationClient(myActivity);
        //注册监听函数
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);
        //回到当前定位
        mMapView.showScaleControl(false);  // 设置比例尺是否可见（true 可见/false不可见）
        //mMapView.showZoomControls(false);  // 设置缩放控件是否可见（true 可见/false不可见）
        mMapView.removeViewAt(1);// 删除百度地图Logo

        mBaiduMap = mMapView.getMap();

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final String info = (String) marker.getExtraInfo().get("info");
                Toast.makeText(myActivity, info, Toast.LENGTH_SHORT).show();
                return true;

            }
        });
        initLocation();//定位
        mapOnClick();
    }

    private void setViewListener() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    //定位
    private void initLocation() {
        //开启定位
        mBaiduMap.setMyLocationEnabled(true);
        //声明LocationClient类
        mLocationClient = new LocationClient(myActivity);
        //注册监听函数
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);

        //==配置参数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系---注意和初始化时的设置对应
        //设置定位间隔为10s，不能小于1s即1000ms
        option.setScanSpan(100000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);

        //调用LocationClient的start()方法，便可发起定位请求
        //start()：启动定位SDK；stop()：关闭定位SDK。调用start()之后只需要等待定位结果自动回调即可。
        mLocationClient.start();
    }


    class MyLocationListener extends BDAbstractLocationListener {
        boolean isZoomMap = true;//标识是否以定位位置为中心缩放地图


        @Override
        public void onReceiveLocation(BDLocation location) {
            // MapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            double resultLatitude;
            double resultLongitude;

            if (markerLatitude == 0) {//自动定位
                resultLatitude = location.getLatitude();
                resultLongitude = location.getLongitude();
                ibLocation.setVisibility(View.GONE);
            } else {//标点定位
                resultLatitude = markerLatitude;
                resultLongitude = markerLongitude;
                ibLocation.setVisibility(View.VISIBLE);
            }
            LatLng latLng = new LatLng(39.963175, 116.400244);//初始画时候的经纬度
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marker);
            OverlayOptions options = new MarkerOptions()
                    .position(latLng)//设置marker的位置
                    .icon(bitmap)    //设置marker的图标
                    .zIndex(9)       //设置marker所在的层级
                    .draggable(true);//设置手势的拖拽
            //在地图上添加Marker
            marker = (Marker) mBaiduMap.addOverlay(options);

            stratLatitude = location.getLatitude();
            //获取经度
            stratLongitude = location.getLongitude();
            Log.e("----------------->", "定位SDK回调 纬度：" + stratLatitude + "经度：" + stratLongitude);
            //第一次定位时调整地图缩放
            if (isZoomMap) {
                //纬度，经度
                 latLng = new LatLng(stratLatitude, stratLongitude);
                // 改变地图状态，使地图以定位地址为目标，显示缩放到恰当的大小
                MapStatus mapStatus = new MapStatus.Builder()
                        .target(latLng)//目标
                        .zoom(16.0f)//缩放
                        .build();
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
                isZoomMap = false;
            }
            //在地图上显示当前位置
            MyLocationData locationData = new MyLocationData.Builder()
                    .latitude(stratLatitude)//纬度
                    .longitude(stratLongitude)//经度
                    .build();
            mBaiduMap.setMyLocationData(locationData);
        }

        }

    //申请权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MPermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden){
            if (mLocationClient.isStarted()) {
                mLocationClient.stop();
            }
        }else{
            if (!mLocationClient.isStarted()) {
                mLocationClient.start();
            }
        }
    }

    //创建路线规划检索结果监听器
    OnGetRoutePlanResultListener routePlanSrchlistener = new OnGetRoutePlanResultListener() {
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            if(walkingRouteResult==null){
                Log.d("数据walk", "数据为空");
            }

            WalkingRouteOverlay overlay = new WalkingRouteOverlay(mMapView.getMap());
            List<WalkingRouteLine> routelines = walkingRouteResult.getRouteLines();
            if (routelines != null && routelines.size() > 0) {
                for (WalkingRouteLine routeLine : routelines
                ) {

                    //为DrivingRouteOverlay实例设置数据
                    overlay.setData(routeLine);
                    //在地图上绘制DrivingRouteOverlay
                    overlay.addToMap();
                }
            }
            Log.d("数据walk", "onGetWalkingRouteResult+error ");
        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
            Log.d("数据walk", "onGetWalkingRouteResult: ");
        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
            Log.d("数据walk", "onGetWalkingRouteResult: ");
        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            Log.d("数据walk", "onGetWalkingRouteResult: ");
        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
            Log.d("数据walk", "onGetWalkingRouteResult: ");
        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
            Log.d("数据walk", "onGetWalkingRouteResult: ");
        }
    };

    public RoutePlanSearch routePlanSearch() {
        return mRoutePlanSrch;
    }

    //开始规划，这里实现多种不同的路线规划方式。
    private void StarRoute() {
        try {
            // RoutePlanSearch mSearch = RoutePlanSearch.newInstance();
            //经纬度规划路线和动态输入规划路线二选一
            // 设置起、终点信息 动态输入规划路线
            LatLng latLngEnd = new LatLng(28.247974 ,113.027071);

            LatLng latLngStart=new LatLng(stratLatitude,stratLongitude);
            PlanNode stNode = PlanNode.withLocation(latLngEnd);
            PlanNode enNode = PlanNode.withLocation(latLngStart);

            mRoutePlanSrch.walkingSearch((new WalkingRoutePlanOption())
                    .from(stNode)
                    .to(enNode));
            Log.i("routeerror","runerror1");
        }catch (Exception e){
            Log.i("routeerror","runerror2");
        }

    }

}