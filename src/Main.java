import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/*******************************************************
 *
 * Created by julis.wang@beibei.com on 2019/10/24 10:32
 *
 * Description : 通过固定两点位置计算两地距离
 * History   :
 *
 *******************************************************/

public class Main {
    public static void main(String[] args) {
        String[][] data = Data.data;
        int dataSize = data.length;
        for (int i = 0; i < dataSize; i++) {
            String startPlace = data[i][0].replaceAll("\t", "");

            String startLonLat = getLonLat(startPlace);
            String endLonLat = getLonLat(data[i][1]);
            float dis = getDistance(startLonLat, endLonLat);
            System.out.format("%-10s%-10s%s\n", startPlace, data[i][1], dis);
        }
    }

    private static String getLonLat(String address) {
        //返回输入地址address的经纬度信息, 格式是 经度,纬度
        String queryUrl = "http://restapi.amap.com/v3/geocode/geo?key="+Const.KYE+"&address=" + address;
        //高德接品返回的是JSON格式的字符串
        String queryResult = getResponse(queryUrl);

        JSONObject jo = JSON.parseObject(queryResult);
        JSONArray ja = jo.getJSONArray("geocodes");
        if (ja.size() != 0 && ja.get(0) != null && ((JSONObject) ja.get(0)).get("location") != null) {
            return ((JSONObject) ja.get(0)).get("location").toString();
        }
        return "0,0";

    }

    private static float getDistance(String startLonLat, String endLonLat) {
        //返回起始地startAddress与目的地endAddress之间的距离，单位：米
        long result;
        String queryUrl = "http://restapi.amap.com/v3/distance?key="+Const.KYE+"&origins="
                + startLonLat + "&destination=" + endLonLat;
        String queryResult = getResponse(queryUrl);
        JSONObject jo = JSON.parseObject(queryResult);

        JSONArray ja = jo.getJSONArray("results");

        result = Long.parseLong(JSONObject.parseObject(ja.getString(0)).get("distance").toString());
        return result / 1000.0f;

    }

    private static String getResponse(String serverUrl) {
        //用JAVA发起http请求，并返回json格式的结果
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(serverUrl);
            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

}
