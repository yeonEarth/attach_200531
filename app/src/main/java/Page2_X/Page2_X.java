package Page2_X;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hansol.spot_200510_hs.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import Page3.Page3_Main;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class Page2_X extends AppCompatActivity {

    private Handler handler =new Handler();
    //svg 지도
    WebView page2_svg;
    //ScrollView scrollView;

    //자동완성 변수
    AutoCompleteTextView searchStation_page2;

    //txt 관련 변수
    int i = 0;
    String readStr = "";
    private List<String> list;  //데이터를 넣을 리스트 변수

    String[] code_name = null;
    String[] code = new String[237];
    String[] name = new String[237];
    String next_text[] = new String[10];  //다음 페이지에 넘길 값 배열


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2_x);

        //검색 리스트 구현 부분
        list = new ArrayList<String>();            //리스트를 생성
        settingList();                             //리스트에 검색될 단어를 추가한다

        //자동완성
        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.searchStation_page2);    //객체 연결
        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list));   //아답터에 연결

        //자동입력에서 항목을 터치했을 때, 키보드가 바로 내려감 + 웹뷰에서 해당역에 출경도 버튼 띄워짐
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (autoCompleteTextView.getText().toString() != null) {
                    page2_svg.loadUrl("javascript:setMessage('" + autoCompleteTextView.getText().toString() + "')");
                }

                //키보드 내림
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
            }
        });

        // svg 지도
        page2_svg = (WebView) findViewById(R.id.page2_svg);

        //웹뷰 자바스크립트 사용가능하도록 선언
        page2_svg.getSettings().setJavaScriptEnabled(true);
        page2_svg.getSettings().setLoadWithOverviewMode(true);
        page2_svg.getSettings().setDisplayZoomControls(false);  //웹뷰 돋보기 없앰
        page2_svg.getSettings().setUseWideViewPort(true);

        //웹뷰 줌기능
        page2_svg.getSettings().setBuiltInZoomControls(true);
        page2_svg.getSettings().setSupportZoom(true);
        page2_svg.setWebViewClient(new WebViewClient());

        //스크롤바 안보이게함
        page2_svg.setHorizontalScrollBarEnabled(false);
        page2_svg.setVerticalScrollBarEnabled(false);
        page2_svg.getSettings().setLoadWithOverviewMode(true);

        //웹뷰를 로드함
        page2_svg.setWebViewClient(new WebViewClient());
        page2_svg.loadUrl("file:///android_asset/index_page2.html");

        //관광지 제공하지 않는역 클릭 시
        page2_svg.setWebChromeClient(new WebChromeClient(){
            public boolean onJsAlert(WebView view,String url, String message, final android.webkit.JsResult result) {
                Toast.makeText(getApplicationContext(), "해당역은 관광지를 제공하지 않습니다.", Toast.LENGTH_LONG).show();
                result.confirm();
                return true;
            }
        });


        출처: https://osankkk.tistory.com/entry/WebView-컨트롤 [준영아빠]



        //자바스크립트에서 메시지 보내면, 그 값을 다음 액티비티로 전달
        page2_svg.addJavascriptInterface(new Object(){
            @JavascriptInterface
            public void send(final String msg){
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        //프로그레스 다이얼로그
                        final ProgressDialog asyncDialog = new ProgressDialog(Page2_X.this);
                        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        asyncDialog.setMessage(msg+"(으)로 이동중입니다..");
                        asyncDialog.show();

                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Page2_X.this, Page2_X_Main.class);
                        intent.putExtra("st_name", msg);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        //0.5초 후, 다이얼로그 없앰
                        Handler mHandler = new Handler();
                        mHandler.postDelayed(new Runnable()  {
                            public void run() {
                                // 시간 지난 후 실행할 코딩
                                asyncDialog.cancel();
                            }
                        }, 500); // 0.5초후


                    }});
            }
        }, "android");


        //웹뷰 화면 비율
        //page2_svg.setInitialScale(230);
    }

    //리스트에 검색될 단어를 추가한다. txt파일을 for문으로 쪼개서 넣음
    private void settingList() {
        AssetManager am = getResources().getAssets();
        InputStream is = null;
        try {
            is = am.open("station3.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String str = null;
            while (((str = reader.readLine()) != null)) {
                readStr += str + "\n";
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] arr = readStr.split("\n");  //한 줄씩 자른다.


        //code,name으로 되어있는 line을 ','를 기준으로 다시 자른다.
        for (int i = 0; i < arr.length; i++) {
            code_name = arr[i].split(",");

            code[i] = code_name[0];
            name[i] = code_name[1];

            list.add(name[i]);


        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
}
