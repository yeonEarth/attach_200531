package Page3;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidtagview.ColorFactory;
import com.example.androidtagview.TagContainerLayout;
import com.example.androidtagview.TagView;
import com.example.hansol.spot_200510_hs.Page0_9_PopUp;
import com.example.hansol.spot_200510_hs.R;
import com.example.hansol.spot_200510_hs.send_data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import DB.DbOpenHelper;
import DB.Like_DbOpenHelper;
import DB.Menu_DbOpenHelper;
import DB.Page3_DbOpenHelper;
import DB.Second_MainDBHelper;
import Page1.EndDrawerToggle;
import Page1.Main_RecyclerviewAdapter;
import Page1.Page1;
import Page1_schedule.LocationUpdatesService;
import Page1_schedule.Location_Utils;
import Page2.Page2;
import Page3_1.Page3_1_Main;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page3_Main extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    //태그 관련 변수
    private TagContainerLayout mTagContainerLayout1;
    boolean startOk = false;
    boolean endOk = false;
    boolean middleOk = false;
    boolean dateOk = false;
    boolean isOk = false;

    //page2 액티비티에서 받은 값 관련(코스 전체추가 버튼)
    ArrayList<String> course = null;
    ArrayList<String> course2 =null;
    ArrayList<String> items3 = null;

    //메뉴바 프로필 관련
    ImageView menu_img;
    TextView menu_text1, menu_text2;

    int[] score = new int[8];
    String mScore[] = new String[8];

    private Like_DbOpenHelper mLikeDpOpenHelper;
    String like, nickName, sub;
    ImageButton edit_nickname;


    //메뉴 관련
    private Context context;
    private ImageButton menu_edit;
    private ImageView userImg;
    private TextView userText1;
    private TextView userText2;
    private RecyclerView recyclerView1;
    private Switch positionBtn;
    private Switch alramBtn;
    Main_RecyclerviewAdapter adapter2;
    ArrayList<String> name2 = new ArrayList<>();
    private Toolbar toolbar2;
    private DrawerLayout drawer;
    private EndDrawerToggle mDrawerToggle;
    NestedScrollView nestedScrollView;


    //svg 지도
    WebView page3_svg;
    RelativeLayout page3_svg_bg;

    //이용권
    Button dayPass_3, dayPass_5, dayPass_7;
    String dayPass = "";

    // 날짜 관련 변수들
    Calendar myCalendar = Calendar.getInstance();
    LinearLayout editDate;
    SimpleDateFormat sdf;
    String t_year, t_month, t_day;
    TextView set_year, set_month, set_day;
    String date;

    //txt 관련 변수
    int i = 0;
    String readStr = "";
    private List<String> list;  //데이터를 넣을 리스트 변수
    Handler handler = new Handler();
    String[] code_name = null;
    String[] code = new String[237];
    String[] name = new String[237];


    //날짜 값을 받아온다.
    DatePickerDialog.OnDateSetListener myDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };


    //다음 페이지로 값을 전달할 list
    ArrayList <send_data> send_list = new ArrayList<send_data>();

    //데이터베이스 관련--------------------------------------여기추가
    Page3_DbOpenHelper page3_dbOpenHelper;
    String forDB = "";
    String daypassFromDB = null;
    ArrayList<String> stationFromDB;

    boolean fromPage2 = false;

    ImageButton logo;

    //위치서비스 관련
    private Menu_DbOpenHelper menu_dbOpenHelper;
    private List<String> onoff = new ArrayList<>();
    private MyReceiver myReceiver;
    private boolean mBound = false;
    private LocationUpdatesService mService = null;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    // 찜한 관광지 DB
    private DbOpenHelper mDbOpenHelper;
    // 찜한 여행지 저장하는 리스트
    private ArrayList<String > mySpot = new ArrayList<String >();

    //등록한 일정 관련
    private Second_MainDBHelper second_mainDBHelper;
    private String second_key = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page3_main);

        //page2에서 값을 받아온다
        Intent intent = getIntent();
        daypassFromDB = intent.getStringExtra("daypassFromDB");
        course = (ArrayList<String>)intent.getSerializableExtra("page2_course") ;
        course2 = (ArrayList<String>)intent.getSerializableExtra("page2_course2") ;
        stationFromDB = (ArrayList<String>)intent.getSerializableExtra("stationFromDB") ;

        //데베 생성
        page3_dbOpenHelper = new Page3_DbOpenHelper(this);
        page3_dbOpenHelper.open();
        page3_dbOpenHelper.create();

        //데베 관련
        second_mainDBHelper = new Second_MainDBHelper(this);
        second_mainDBHelper.open();
        second_mainDBHelper.create();


        Intent intent3 = getIntent();
        items3 = (ArrayList<String>)intent3.getSerializableExtra("items3") ;

        //객체 연결
        context = getApplicationContext();
        toolbar2 = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        userImg = (ImageView)findViewById(R.id.menu_userImage);
        userText1 = (TextView)findViewById(R.id.menu_text1);
        userText2 = (TextView)findViewById(R.id.menu_text2);
        positionBtn = (Switch)findViewById(R.id.menu_postion_btn);
        alramBtn = (Switch)findViewById(R.id.menu_alram_btn);
        recyclerView1 = (RecyclerView)findViewById(R.id.menu_recyclerview1);

        nestedScrollView = (NestedScrollView)findViewById(R.id.nestScrollView_page3);

        menu_img = (ImageView)findViewById(R.id.menu_userImage);
        menu_text1 = (TextView) findViewById(R.id.menu_text1);
        menu_text2 = (TextView) findViewById(R.id.menu_text2);
        edit_nickname = (ImageButton)findViewById(R.id.menu_edit_btn);


        // DB열기
        menu_dbOpenHelper = new Menu_DbOpenHelper(this);
        menu_dbOpenHelper.open();
        menu_dbOpenHelper.create();
        notity_listner("");



        //위치 스위치 관련
        myReceiver = new MyReceiver();
        setButtonsState(Location_Utils.requestingLocationUpdates(this));
        positionBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                } else {
                    mService.removeLocationUpdates();
                }
            }
        });




        //알림 스위치 버튼
        setButtonsState_notity();
        alramBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    menu_dbOpenHelper.open();
                    menu_dbOpenHelper.deleteAllColumns();
                    menu_dbOpenHelper.insertColumn("true", "0");
                    //  menu_dbOpenHelper.close();

                }else {
                    menu_dbOpenHelper.open();
                    menu_dbOpenHelper.deleteAllColumns();
                    menu_dbOpenHelper.insertColumn("false", "0");
                    //  menu_dbOpenHelper.close();
                }
            }
        });





        mDrawerToggle = new EndDrawerToggle(this,drawer,toolbar2,R.string.open_drawer,R.string.close_drawer){
            @Override //드로어가 열렸을때
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override //드로어가 닫혔을때
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        setSupportActionBar(toolbar2);
        drawer.addDrawerListener(mDrawerToggle);

        // DB열기
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
        //showDatabase();

        // 취향파악 DB열기
        mLikeDpOpenHelper = new Like_DbOpenHelper(this);
        mLikeDpOpenHelper.open();
        mLikeDpOpenHelper.create();
        showLikeDB();


        //등록된 일정이 있는지 검사
        Cursor iCursor = second_mainDBHelper.selectColumns();
        while (iCursor.moveToNext()){
            String Key = iCursor.getString(iCursor.getColumnIndex("userid"));
            second_key = Key;
        }

        //메뉴 안 내용 구성
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        adapter2 = new Main_RecyclerviewAdapter(name2, context, mySpot.size(), second_key);
        recyclerView1.setAdapter(adapter2);



        //리사이클러뷰 헤더
        name2.add("0");
        name2.add("1");
        name2.add("2");

        //툴바 타이틀 없애기
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        logo = (ImageButton) findViewById(R.id.main_logo_page3);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Page1.class);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Logo", "1");
                startActivity(intent);

                overridePendingTransition(0,0);
            }
        });

        // 프로필편집 버튼 눌렀을 때
        edit_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Page0_9_PopUp.class);

                intent.putExtra("서브이름", sub);
                intent.putExtra("닉네임", nickName);
                intent.putExtra("Page9",score);
                intent.addFlags(intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 1);
            }
        });



        //이용권 버튼,텍스트
        dayPass_3 = (Button) findViewById(R.id.dayPass_3);
        dayPass_5 = (Button) findViewById(R.id.dayPass_5);
        dayPass_7 = (Button) findViewById(R.id.dayPass_7);
        editDate = (LinearLayout) findViewById(R.id.page3_date);


        //이용권 버튼 클릭시 테두리와 글자색 바꾸기
        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.dayPass_3:
                        dateOk = true;
                        dayPass = "3일권";
                        dayPass_3.setSelected(true);
                        dayPass_5.setSelected(false);
                        dayPass_7.setSelected(false);
                        dayPass_3.setTextColor(getResources().getColorStateList(R.color.btn_ticket_text));
                        break;
                    case R.id.dayPass_5:
                        dateOk = true;
                        dayPass = "5일권";
                        dayPass_5.setSelected(true);
                        dayPass_3.setSelected(false);
                        dayPass_7.setSelected(false);
                        dayPass_5.setTextColor(getResources().getColorStateList(R.color.btn_ticket_text));
                        break;
                    case R.id.dayPass_7:
                        dateOk = true;
                        dayPass = "7일권";
                        dayPass_7.setSelected(true);
                        dayPass_3.setSelected(false);
                        dayPass_5.setSelected(false);
                        dayPass_7.setTextColor(getResources().getColorStateList(R.color.btn_ticket_text));
                        break;
                }
            }
        };

        //데베에서 값을 받으면 이용권 세팅
        if (daypassFromDB != null) {
            switch (daypassFromDB) {
                case "3일권":
                    dateOk = true;
                    dayPass = "3일권";
                    dayPass_3.setSelected(true);
                    dayPass_3.setTextColor(getResources().getColorStateList(R.color.btn_ticket_text));
                    break;
                case "5일권":
                    dateOk = true;
                    dayPass = "5일권";
                    dayPass_5.setSelected(true);
                    dayPass_5.setTextColor(getResources().getColorStateList(R.color.btn_ticket_text));
                    break;
                case "7일권":
                    dateOk = true;
                    dayPass = "7일권";
                    dayPass_7.setSelected(true);
                    dayPass_7.setTextColor(getResources().getColorStateList(R.color.btn_ticket_text));
                    break;
            }
        }


        dayPass_3.setOnClickListener(onClickListener);
        dayPass_5.setOnClickListener(onClickListener);
        dayPass_7.setOnClickListener(onClickListener);

        set_year = (TextView) findViewById(R.id.page3_setYear);
        set_month = (TextView) findViewById(R.id.page3_setMonth);
        set_day = (TextView) findViewById(R.id.page3_setDay);


        // 현재 날짜 출력
        String myFormat = String.format("yyyy년");
        sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        t_year = sdf.format(myCalendar.getTime());
        set_year.setText(t_year);

        String myFormat2 = String.format("MM월");
        sdf = new SimpleDateFormat(myFormat2, Locale.KOREA);
        t_month = sdf.format(myCalendar.getTime());
        set_month.setText(t_month);

        String myFormat3 = String.format("dd일");
        sdf = new SimpleDateFormat(myFormat3, Locale.KOREA);
        t_day = sdf.format(myCalendar.getTime());
        set_day.setText(t_day);



        // 출발 날짜 클릭 시 DatePicker 보여주기
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(Page3_Main.this, myDatePicker,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        //자동완성-검색 리스트 구현 부분
        list = new ArrayList<String>();            //리스트를 생성
        settingList();                             //리스트에 검색될 단어를 추가한다


        //자동완성
        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.searchStation_page3);    //객체 연결
        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list));   //아답터에 연결


        //자동입력 누르면 스크롤이 맨 아래로 내려감
        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                nestedScrollView.post(new Runnable() {
                    @Override
                    public void run() {

                        //키보드 올라와 있으면 작동 안함
                        //nestedScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        autoCompleteTextView.setText("");
                    }
                });
                return false;
            }
        });


        //자동입력에서 항목을 터치했을 때, 키보드가 바로 내려감 + 웹뷰에서 해당역에 출경도 버튼 띄워짐
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (autoCompleteTextView.getText().toString() != null) {
                    page3_svg.loadUrl("javascript:setMessage('" + autoCompleteTextView.getText().toString() +"' , '0')");

                    //자동입력에서 입력되면 지도 줌아웃됨--------------------------------------------------------------------여기추가
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        page3_svg.zoomBy(0.1f);
                    }

                    Log.i("svg 스케일", String.valueOf(page3_svg.getScale()));

                    //키보드 내림
                    InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mInputMethodManager.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);

                }
            }
        });


        // svg 지도
        page3_svg = (WebView) findViewById(R.id.page3_svg);
        page3_svg_bg = (RelativeLayout) findViewById(R.id.page3_svg_bg);

        //지도를 줌인 할 때, 스크롤뷰(부모뷰)의 영향을 받지 않는다. + 테두리가 생긴다.
        page3_svg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                page3_svg_bg.setSelected(true);
                return false;
            }
        });


        //지도가 아닌 배경을 눌렀을때 지도의 테두리를 없애줌 + 키보드 내림
        nestedScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                page3_svg_bg.setSelected(false);
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
                return false;
            }
        });


        //웹뷰 자바스크립트 사용가능하도록 선언
        page3_svg.getSettings().setJavaScriptEnabled(true);
        page3_svg.getSettings().setLoadWithOverviewMode(true);
        page3_svg.getSettings().setDisplayZoomControls(false);  //웹뷰 돋보기 없앰
        page3_svg.getSettings().setUseWideViewPort(true);
        page3_svg.getSettings().setBuiltInZoomControls(true);   //웹뷰 줌기능
        page3_svg.getSettings().setSupportZoom(true);
        page3_svg.setWebViewClient(new WebViewClient());
        page3_svg.setHorizontalScrollBarEnabled(false);         //스크롤바 안보이게함
        page3_svg.setVerticalScrollBarEnabled(false);

        page3_svg.setWebChromeClient(new WebChromeClient() {
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(Page3_Main.this)
                        .setTitle("안내")
                        .setMessage("해당역은 내일로 정차역이 아닙니다.")
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        result.confirm();
                                    }
                                }).setCancelable(false).create().show();
                return true;
            }
        });

        //웹뷰를 로드함
        page3_svg.loadUrl("file:///android_asset/index.html");


        //태그뷰 관련
        final List<String> list1 = new ArrayList<String>();
        mTagContainerLayout1 = (TagContainerLayout) findViewById(R.id.tagcontainerLayout1);


        //태그뷰를 커스텀
        mTagContainerLayout1.setDragEnable(false);                                           //태그뷰에서 드래그 안씀
        mTagContainerLayout1.setTheme(ColorFactory.NONE);                                    //테마를 커스텀
        mTagContainerLayout1.setTagBorderRadius(100f);                                       //태그 아이템의 모서리를 둥글게
        mTagContainerLayout1.setTagBorderColor(Color.parseColor("#FE9D0D"));     //태그 아이템의 테두리 색
        mTagContainerLayout1.setTagBackgroundColor(Color.parseColor("#FE9D0D")); //태그 아이템의 배경 색
        mTagContainerLayout1.setCrossAreaPadding(38f);                                       // X<-의 크기를 조절
        mTagContainerLayout1.setCrossColor(Color.parseColor("#FE9D0D"));         // X<-의 색
        mTagContainerLayout1.setBackgroundColor(Color.parseColor("#7FFFFFFF"));    //태그뷰 배경 색
        mTagContainerLayout1.setBorderColor(Color.parseColor("#FFFFFF"));        //태그뷰의 모서리를 둥글게
        //박스 생기는 거 나중에  애니메이션 주자.


        //태그뷰 글꼴
        Typeface typeface = Typeface.createFromAsset(getAssets(), "notosans_regular.ttf");
        mTagContainerLayout1.setTagTypeface(typeface);
        mTagContainerLayout1.setTagTextColor(Color.parseColor("#ffffff"));
        mTagContainerLayout1.setTagTextSize(45);
        mTagContainerLayout1.setTagBdDistance(5f);      //태그 아이템의 밑위 간격
        mTagContainerLayout1.setTags(list1);            //위에서 커스텀한 태그뷰를 set



        //태그뷰를 선택했을 때, 메소드들
        mTagContainerLayout1.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) { }         //태그뷰를 단순 터치

            @Override
            public void onTagLongClick(int position, String text) { }     //태그뷰를 롱터치

            @Override
            public void onSelectedTagDrag(int position, String text) { }   //태그뷰를 드래그

            @Override
            public void onTagCrossClick(int position) {                    // X <- 눌러서 지우는 메소드
                if (position < mTagContainerLayout1.getChildCount()) {

                    //경유만 삭제할 수 있도록 함
                    if(position != 0 & position != mTagContainerLayout1.size()){
                        mTagContainerLayout1.removeTag(position);
                        page3_svg.loadUrl("javascript:deletePin_middle('"+"2_"+list1.get(position)+"')");   //svg 지도에서 경유 아이콘 삭제
                        list1.remove(position);

                        //경유역이 모두 삭제되면
                        if(list1.size() == 2) middleOk = false;
                    }
                }
            }
        });

        if (course!=null) {
            for (int i = 0; i < course.size(); i++) {
                mTagContainerLayout1.setCrossColor(Color.parseColor("#1B503D"));
                mTagContainerLayout1.setTagBackgroundColor(Color.parseColor("#4DD9A9"));
                mTagContainerLayout1.setTagBorderColor(Color.parseColor("#4DD9A9"));
                mTagContainerLayout1.addTag(course.get(i));
                list1.add(course.get(i));
                middleOk = true;

            }
            //지도에 동그라미 그려주는 부분
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < course.size(); i++) {
                        page3_svg.loadUrl("javascript:setMessage('" + course.get(i) + "' , '2')");
                    }
                }
            }, 300);
        }

        if (course2!=null) {
            for (int i = 0; i < course2.size(); i++) {
                mTagContainerLayout1.setCrossColor(Color.parseColor("#1B503D"));
                mTagContainerLayout1.setTagBackgroundColor(Color.parseColor("#4DD9A9"));
                mTagContainerLayout1.setTagBorderColor(Color.parseColor("#4DD9A9"));
                mTagContainerLayout1.addTag(course2.get(i));
                list1.add(course2.get(i));
                middleOk = true;

            }
            //지도에 동그라미 그려주는 부분
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < course2.size(); i++) {
                        page3_svg.loadUrl("javascript:setMessage('" + course2.get(i) + "' , '2')");
                    }
                }
            }, 300);
        }

        if (items3!=null) {
            for (int i = 0; i < items3.size(); i++) {
                mTagContainerLayout1.setCrossColor(Color.parseColor("#1B503D"));
                mTagContainerLayout1.setTagBackgroundColor(Color.parseColor("#4DD9A9"));
                mTagContainerLayout1.setTagBorderColor(Color.parseColor("#4DD9A9"));
                mTagContainerLayout1.addTag(items3.get(i));
                list1.add(items3.get(i));
                middleOk = true;
            }
            //지도에 동그라미 그려주는 부분
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < items3.size(); i++) {
                        page3_svg.loadUrl("javascript:setMessage('" + items3.get(i) + "' , '2')");
                    }
                }
            }, 300);
        }

        //page1 intent 부분
        if (stationFromDB!=null) {
            for (int i = 0; i < stationFromDB.size(); i++) {
                if (i == 0) {
                    mTagContainerLayout1.setTagBackgroundColor(Color.parseColor("#FE9D0D"));
                    mTagContainerLayout1.setTagBorderColor(Color.parseColor("#FE9D0D"));
                    mTagContainerLayout1.addTag(stationFromDB.get(i));
                    list1.add(stationFromDB.get(i));
                    startOk = true;
                } else if(i < stationFromDB.size()-1) {
                    mTagContainerLayout1.setCrossColor(Color.parseColor("#1B503D"));
                    mTagContainerLayout1.setTagBackgroundColor(Color.parseColor("#4DD9A9"));
                    mTagContainerLayout1.setTagBorderColor(Color.parseColor("#4DD9A9"));
                    mTagContainerLayout1.addTag(stationFromDB.get(i));
                    list1.add(stationFromDB.get(i));
                    middleOk = true;
                }  else {
                    mTagContainerLayout1.setCrossColor(Color.parseColor("#FE800D"));
                    mTagContainerLayout1.setTagBackgroundColor(Color.parseColor("#FE800D"));
                    mTagContainerLayout1.setTagBorderColor(Color.parseColor("#FE800D"));
                    mTagContainerLayout1.addTag(stationFromDB.get(i));
                    list1.add(stationFromDB.get(i));
                    endOk = true;
                }
            }
            //지도에 동그라미 그려주는 부분
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < stationFromDB.size(); i++) {
                        if (i == 0) {
                            page3_svg.loadUrl("javascript:setMessage('" + stationFromDB.get(i).trim() + "' , '1')");
                        } else if(i < stationFromDB.size()-1) {
                            page3_svg.loadUrl("javascript:setMessage('" + stationFromDB.get(i) + "' , '2')");
                        }  else {
                            page3_svg.loadUrl("javascript:setMessage('" + stationFromDB.get(i) + "' , '3')");
                        }
                    }
                }
            }, 300);
        }

        //지도에서 역 누르면 태그뷰에 추가되는 부분-----------------------------여기추가
        page3_svg.addJavascriptInterface(new Object(){
            @JavascriptInterface
            public void send(final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            page3_svg.zoomBy(0.1f);
                        }
                    }
                });
            }
        }, "android2");




        //지도에서 역 누르면 태그뷰에 추가되는 부분
        page3_svg.addJavascriptInterface(new Object(){
            @JavascriptInterface
            public void send(final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run(){

                        //번호_역이름  <-이렇게 전달되는 걸 '_' 단위로 쪼개서 isStart에 넣는다.
                        String[] isStart = msg.split("_");

                        //태그뷰에 아이템이 하나도 없을 때, 출발역이 먼저 선택되어야 한다.
                        if(list1.size() == 0){

                            //출발역인 경우에
                            if (msg.contains("1")) {
                                //태그 아이템의 색을 지정, 태그뷰에 추가
                                mTagContainerLayout1.setTagBackgroundColor(Color.parseColor("#FE9D0D"));
                                mTagContainerLayout1.setTagBorderColor(Color.parseColor("#FE9D0D"));
                                mTagContainerLayout1.addTag(isStart[1]);
                                list1.add(isStart[1]);
                                startOk = true;
                            }

                            //경유역일 때
                            else if (msg.contains("2")){
                                page3_svg.loadUrl("javascript:deletePin_middle('"+"2_"+isStart[1]+"')");
                                startFirst_Show();
                            }

                            //도착역일 때
                            else {
                                page3_svg.loadUrl("javascript:deletePin_end()");
                                startFirst_Show();
                            }
                        }

                        else{
                            for(i=0;i < list1.size(); i++) {

                                //추가할 자리가 없을 때 (태그뷰 아이템 개수 제한 ( 최대 역 개수 = 10개)
                                if (list1.size() == 10 && msg.contains("2")) {
                                    no_Show();
                                    break;
                                }

                                //출발역인 경우에 기존의 출발역 정보를 지우고 새로 갱신한다.
                                if (msg.contains("1")) {
                                    int length = 0;
                                    int startNumber = 0;
                                    if(endOk){
                                        length = list1.size()-1;
                                    } else{
                                        length = list1.size();
                                    }
                                    if(startOk){
                                        startNumber = 1;
                                    } else {
                                        startNumber = 0;
                                    }
                                    for(int i = startNumber; i < length; i++) {
                                        if(isStart[1].equals(list1.get(i))){
                                            page3_svg.loadUrl("javascript:deletePin('1')");   //svg 지도에서 경유 아이콘 삭제
                                            isOk = true;
                                            break;
                                        } else{
                                            isOk =false;
                                        }
                                    }
                                    if(!isOk) {
                                        if (startOk) {
                                            mTagContainerLayout1.removeTag(0);
                                            list1.remove(0);
                                        }
                                        mTagContainerLayout1.setCrossColor(Color.parseColor("#FE9D0D"));
                                        mTagContainerLayout1.setTagBackgroundColor(Color.parseColor("#FE9D0D"));
                                        mTagContainerLayout1.setTagBorderColor(Color.parseColor("#FE9D0D"));
                                        mTagContainerLayout1.addTag(isStart[1], 0);    //첫번째 자리에 추가 또는 갱신
                                        list1.add(0, isStart[1]);                         //첫번째 자리에 추가 또는 갱신
                                        startOk = true;
                                        break;
                                    }
                                    else
                                        Toast.makeText(getApplicationContext(), "경유역에 동일한 역이 있습니다. \n다른 역을 선택해주세요.", Toast.LENGTH_SHORT).show();
                                    break;
                                }


                                //경유역인 경우에, 출발역과 도착역 사이에 계속 추가가 되어야 한다.
                                else if (msg.contains("2")) {
                                    middleOk = true;
                                    int length = 0;
                                    int startNumber = 0;
                                    if(endOk){
                                        length = list1.size()-1;
                                    } else{
                                        length = list1.size();
                                    }
                                    if(startOk){
                                        startNumber = 1;
                                    } else {
                                        startNumber = 0;
                                    }
                                    for(int i = startNumber; i < length; i++) {
                                        if (isStart[1].equals(list1.get(i))) {
                                            page3_svg.loadUrl("javascript:deletePin_middle('"+"2_"+list1.get(i)+"')");   //svg 지도에서 경유 아이콘 삭제
                                            isOk = true;
                                            break;
                                        } else {
                                            isOk = false;
                                        }
                                    }

                                    if(!isOk) {
                                        //도착역이 선택 되어있으면, 도착역을 뒤로 밀어준다.
                                        if (endOk) {
                                            //도착역의 색상
                                            mTagContainerLayout1.setTagBackgroundColor(Color.parseColor("#FE800D"));
                                            mTagContainerLayout1.setTagBorderColor(Color.parseColor("#FE800D"));

                                            //도착역을 뒤로 밀고, 기존 도착역을 삭제한다.
                                            mTagContainerLayout1.addTag(list1.get(list1.size() - 1), mTagContainerLayout1.size());
                                            list1.add(list1.size(), list1.get(list1.size() - 1));
                                            mTagContainerLayout1.removeTag(mTagContainerLayout1.size() - 1);
                                            list1.remove(list1.size() - 1);

                                            //경유역을 도착역 앞에 생성한다.
                                            mTagContainerLayout1.setCrossColor(Color.parseColor("#1B503D"));         // X<-의 색
                                            mTagContainerLayout1.setTagBackgroundColor(Color.parseColor("#4DD9A9"));
                                            mTagContainerLayout1.setTagBorderColor(Color.parseColor("#4DD9A9"));
                                            mTagContainerLayout1.addTag(isStart[1], mTagContainerLayout1.size() - 1);
                                            list1.add(list1.size() - 1, isStart[1]);
                                        } else {
                                            mTagContainerLayout1.setCrossColor(Color.parseColor("#1B503D"));         // X<-의 색
                                            mTagContainerLayout1.setTagBackgroundColor(Color.parseColor("#4DD9A9"));
                                            mTagContainerLayout1.setTagBorderColor(Color.parseColor("#4DD9A9"));
                                            mTagContainerLayout1.addTag(isStart[1]);
                                            list1.add(isStart[1]);
                                        }
                                        break;
                                    } else
                                        Toast.makeText(getApplicationContext(), "동일한 역이 있습니다. \n다른 역을 선택해주세요.", Toast.LENGTH_SHORT).show();
                                    break;
                                }


                                //도착역인 경우에, 항상 뒤에 추가가 된다.
                                else if (msg.contains("3")) {
                                    int length = 0;
                                    int startNumber = 0;
                                    if(endOk){
                                        length = list1.size()-1;
                                    } else{
                                        length = list1.size();
                                    }
                                    if(startOk){
                                        startNumber = 1;
                                    } else {
                                        startNumber = 0;
                                    }
                                    for(int i = startNumber; i < length; i++) {
                                        if (isStart[1].equals(list1.get(i))) {
                                            page3_svg.loadUrl("javascript:deletePin('3')");   //svg 지도에서 경유 아이콘 삭제
                                            isOk = true;
                                            break;
                                        } else {
                                            isOk = false;
                                        }
                                    }

                                    if(!isOk) {
                                        //기존의 도착역을 삭제하고 갱신한다.
                                        if (endOk) {
                                            mTagContainerLayout1.removeTag(mTagContainerLayout1.size() - 1);
                                            list1.remove(list1.size() - 1);
                                        }
                                        mTagContainerLayout1.setCrossColor(Color.parseColor("#FE800D"));         // X<-의 색
                                        mTagContainerLayout1.setTagBackgroundColor(Color.parseColor("#FE800D"));
                                        mTagContainerLayout1.setTagBorderColor(Color.parseColor("#FE800D"));
                                        mTagContainerLayout1.addTag(isStart[1], mTagContainerLayout1.size());         //항상 뒤에 추가 됨
                                        list1.add(list1.size(), isStart[1]);                                           //항상 뒤에 추가 됨
                                        endOk = true;
                                        break;
                                    }
                                    else
                                        Toast.makeText(getApplicationContext(), "경유역에 동일한 역이 있습니다. \n다른 역을 선택해주세요.", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                        }

                    }
                });
            }
        }, "android");



        //다음 페이지에 값을 넘김
        final Button send_btn = (Button)findViewById(R.id.page3_search_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //출발, 도착, 경유역이 선택이 안되어 있으면 오류메시지
                if(startOk==false || endOk == false || middleOk ==false || dateOk == false){
                    err_show();
                } else {
                    //선택된 역 이름을 번호와 함께 넘겨준다.
                    for(int i=0; i<list1.size(); i++){
                        for(int j=0; j<237; j++){
                            if(list1.get(i).equals(name[j])){
                                send_list.add(new send_data(code[j],name[j]) );

                                //선택된 역을 하나의 string 으로 넣어줌
                                forDB = forDB + "," + name[j];
                            }
                        }
                    }

                    //값을 데베에 저장
                    page3_dbOpenHelper.open();
                    page3_dbOpenHelper.deleteAllColumns();
                    page3_dbOpenHelper.insertColumn( dayPass, forDB);
                    Log.i("이렇게 저장됨", dayPass+forDB);

                    Intent intent = new Intent(getApplicationContext(), Page3_1_Main.class);
                    intent.putExtra("list", (Serializable) send_list);           //추가된 역
                    intent.putExtra("date", t_year+t_month+t_day);
                    intent.putExtra("dayPass", dayPass);                     //일권
                    intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            }
        });
    }


    //선택된 날짜를 적용
    private void updateLabel() {

        String myFormat = String.format("yyyy년");
        sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        t_year = sdf.format(myCalendar.getTime());
        set_year.setText(t_year);

        String myFormat2 = String.format("MM월");
        sdf = new SimpleDateFormat(myFormat2, Locale.KOREA);
        t_month = sdf.format(myCalendar.getTime());
        set_month.setText(t_month);

        String myFormat3 = String.format("dd일");
        sdf = new SimpleDateFormat(myFormat3, Locale.KOREA);
        t_day = sdf.format(myCalendar.getTime());
        set_day.setText(t_day);

    }



    //경유 추가하는데 자리가 없으면 없다고 다이얼로그 띄움
    void no_Show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("추가할 수 있는 개수를 초과했습니다.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }


    //태그뷰의 첫번재의 입력값은 출발역이어야 함
    void startFirst_Show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("출발역을 먼저 선택해 주세요.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }


    //입력되지 않은 항목이 있으면 다이얼로그 띄움
    void err_show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        setTheme(R.style.AlertDialogStyle);
        builder.setTitle("Error");
        builder.setMessage("입력되지 않은 항목이 있습니다. 한번 더 확인 해 주세요.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
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


    public void showLikeDB() {
        Cursor likeCursor = mLikeDpOpenHelper.selectColumns();
        Log.d("showLikeDB", "DB Size : " + likeCursor.getCount());

        while (likeCursor.moveToNext()) {
            String tempLike = likeCursor.getString(likeCursor.getColumnIndex("userid"));
            String tempNickname = likeCursor.getString(likeCursor.getColumnIndex("nickname"));
            String tempSub = likeCursor.getString(likeCursor.getColumnIndex("sub"));
            like = tempLike;
            nickName = tempNickname;
            sub = tempSub;
            Log.d("nickkkk",nickName);
        }

        menu_text1.setText(sub);
        menu_text2.setText(nickName);

        // DB에 값이 있다면
        if (like != null) {
            // mScore에 일단 값을 쪼개서 저장하고
            mScore = like.split(" ");
//            Log.i("mScore", like);
            for (int i = 0 ; i < mScore.length ; i++) {
//                Log.i("mScore", mScore[i]);
                score[i] = Integer.parseInt(mScore[i]); // Int로 캐스팅
//                Log.i("score", String.valueOf(score[i]));
            }

            if (score[2] == 0 && score[3] == 0) {
                menu_img.setBackgroundResource(R.drawable.ic_ant);
            }

            if (score[2] == 1 && score[3] == 1) {
                menu_img.setBackgroundResource(R.drawable.ic_sloth);
            }

            if (score[2] != score[3]) {
                if (score[6] == 0) {
                    menu_img.setBackgroundResource(R.drawable.ic_otter);
                } else if (score[2] == 1 ) {

                    menu_img.setBackgroundResource(R.drawable.ic_soul);

                } else if (score[2] == 0) {

                    menu_img.setBackgroundResource(R.drawable.ic_excel);

                }
            }

            if (score[1] == 0) {
                if (score[4] == 0 && score[5] == 1) {
                    menu_img.setBackgroundResource(R.drawable.ic_sprout);
                }
                else if (score[4] == 1&&score[5] == 0) {
                    menu_img.setBackgroundResource(R.drawable.ic_chick);

                }
            }

            if (score[1] == 4&&score[5] == 0) {
                menu_img.setBackgroundResource(R.drawable.ic_chick);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                //String result2 = data.getStringExtra("result2");
                menu_text2.setText(result);
                nickName = result;
                //db_nickName = nickName;

            }
        }
    }


    public void notity_listner(String sort){
        Cursor iCursor = menu_dbOpenHelper.selectColumns();

        while(iCursor.moveToNext()){
            String  id = iCursor.getString(iCursor.getColumnIndex("userid"));
            Log.i("갑자기 왜 안돼", String.valueOf(iCursor.getCount()) + "/" + id);
            onoff.add(id);
        }

        //최초 실행을 위함
        if(iCursor.getCount() == 0){
            menu_dbOpenHelper.insertColumn("true", "0");
            onoff.add("true");
        }
    }


    /*위치&알림 스위치 버튼 관련***********************************************************************************************/
    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }



    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
    }



    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
        send_list.clear();
    }


    @Override
    protected void onStop() {
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    //위치 스위치 상태
    private void setButtonsState(boolean requestingLocationUpdates ) {
        if (requestingLocationUpdates) {
            positionBtn.setChecked(true);
        } else if( !requestingLocationUpdates){
            positionBtn.setChecked(false);
        }
    }



    //알림 스위치 상태
    private void setButtonsState_notity() {
        if (onoff.get(0).equals("true")) {
            alramBtn.setChecked(true);
        } else {
            alramBtn.setChecked(false);
        }

    }



    //포그라운드와 연결 ( 핸드폰 껐을 때도 돌아가도록 하는 부분)
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
            }
        }
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }




}


