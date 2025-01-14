package javatpcproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;


public class JavaTPCProject_E {
    public static void map_service(String point_x, String point_y, String address){
        String URL_STATICMAP = "https://naveropenapi.apigw.ntruss.com/map-static/v2/raster?";
        String client_id = "idx6p03s3a";
        String client_secret="54lrPZEsesDLEmIw6IqoW251VWzU3bHAWi5GBmSZ";
        
        try{
            String pos = URLEncoder.encode(point_x + " " + point_y, "UTF-8");
            String url = URL_STATICMAP;
            //지도의 중심 설정
            url += "center=" + point_x + "," + point_y;
            //줌 레벨 설정 및 사진 파일 크기
            url += "&level=16&w=700&h=500";
            //마커 옵션 및 마커 레이블 설정
            url += "&markers=type:d|size:mid|pos:" + pos +"|label:" + URLEncoder.encode(address, "UTF-8");
            
            URL u = new URL(url);
            HttpURLConnection con = (HttpURLConnection)u.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", client_id);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", client_secret);
            
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode == 200){
                InputStream is = con.getInputStream();
                int read = 0;
                byte[] bytes = new byte[1024];
                //랜덤하게 사진 파일 이름 설정
                String tempname = Long.valueOf(new Date().getTime()).toString();
                File f = new File(tempname + ".jpg");
                f.createNewFile();
                OutputStream outputStream = new FileOutputStream(f);
                while ((read = is.read(bytes))!= -1){
                    outputStream.write(bytes, 0, read);
                }
                is.close();
                
            } else{
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while((inputLine = br.readLine()) != null){
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
            }
            
            
        } catch(Exception E){
            System.out.println(E);
        }
    }
    
    public static void main(String args[]){
        String apiURL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=";
        String client_id = "idx6p03s3a";
        String client_secret="54lrPZEsesDLEmIw6IqoW251VWzU3bHAWi5GBmSZ";
    
        BufferedReader io = new BufferedReader(new InputStreamReader(System.in));
        try{
            System.out.print("주소를 입력하세요 : ");
            String address = io.readLine();
            String addr = URLEncoder.encode(address, "UTF-8"); //문자열 공백제거
            String reqUrl = apiURL + addr; 
            
            URL url = new URL(reqUrl);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", client_id);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", client_secret);
            
            BufferedReader br;
            
            int responseCode = con.getResponseCode(); //응답 코드로 성공여부 확인
            //200은 성공
            if(responseCode == 200){
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            } else{
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            
            String line;
            StringBuffer response = new StringBuffer();
            
            String x=""; String y=""; String z="";
            while((line = br.readLine()) != null){
                response.append(line);
                System.out.println(line);
            }
            br.close();
            
            JSONTokener tokener = new JSONTokener(response.toString());
            JSONObject object = new JSONObject(tokener);
            //System.out.println(object.toString(2));
            
            JSONArray arr = object.getJSONArray("addresses");
            for(int i =0;i<arr.length();i++){
                JSONObject temp = (JSONObject)arr.get(i);
                System.out.println("주소 : " + temp.get("roadAddress"));
                System.out.println("지번주소 : " + temp.get("jibunAddress"));
                System.out.println("경도 : " + temp.get("x"));
                System.out.println("위도 : " + temp.get("y"));
                x=(String)temp.get("x");
                y=(String)temp.get("y");
                z=(String)temp.getString("roadAddress");
                
            }
            map_service(x,y,z);
            
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
}
