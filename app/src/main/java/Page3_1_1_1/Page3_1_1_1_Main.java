package Page3_1_1_1;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hansol.spot_200510_hs.Page0_9_PopUp;
import com.example.hansol.spot_200510_hs.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import DB.DbOpenHelper;
import DB.Like_DbOpenHelper;
import DB.Menu_DbOpenHelper;
import DB.Second_MainDBHelper;
import DB.Train_DbOpenHelper;
import Page1.EndDrawerToggle;
import Page1.Main_RecyclerviewAdapter;
import Page1_schedule.LocationUpdatesService;
import Page1_schedule.Location_Utils;
import Page1_schedule.Page1_Main;
import Page2_1_1.NetworkStatus;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public  class Page3_1_1_1_Main extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, Page3_1_1_1_addCityBottomSheet.onSetList, Page3_1_1_1_trainAdapter.ForProgress {
    TextView title;
    TextView addSpot;
    String split_1 [];
    String date= null, dayPass = null;
    String day1_date, day2_date, day3_date, day4_date, day5_date, day6_date, day7_date;
    Button save_btn;

    //리사이클러뷰 관련
    ArrayList<String> next_data;
    ArrayList<String> next_data_second;
    ArrayList<Page3_1_1_1_dargData> getitem = new ArrayList<>();
    ArrayList<RecycleItem> list = new ArrayList<>();

    //어댑터 관련
    LayoutInflater inflater;
    Page3_1_1_1_trainAdapter adapter;

    //데이터베이스 관련
    Train_DbOpenHelper dbOpenHelper;

    //page1_full_schedule
    ArrayList<Page1_Main.RecycleItem> All_items;
    int dayNumber = 0 ;
    String db_key;

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
    ImageButton logo;

    //프로필 관련
    ImageButton main_schedule;
    ImageButton main_register;
    ImageButton main_like;

    ImageView menu_img;
    TextView menu_text1, menu_text2;

    int[] score = new int[8];
    String mScore[] = new String[8];

    private Like_DbOpenHelper mLikeDpOpenHelper;    // 취향파악 부분
    String  like, nickName, sub;
    ImageButton edit_nickname;


    // 찜한 여행지 저장하는 리스트
    private ArrayList<String > mySpot = new ArrayList<String >();
    private DbOpenHelper mDbOpenHelper;

    private ProgressBar loading_progress;
    private RelativeLayout info_message, info_message2;
    private Button info_dismiss_btn, info_dismiss_btn2;

    private Menu_DbOpenHelper menu_dbOpenHelper;
    private List<String> onoff = new ArrayList<>();

    //위치서비스 관련
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

    //등록한 일정 관련
    private Second_MainDBHelper second_mainDBHelper;
    private String second_key = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page3_1_1_1_main);


        //데이터베이스 연결
        dbOpenHelper = new Train_DbOpenHelper(this);
        dbOpenHelper.open();
        dbOpenHelper.create();


        //값을 받아옴(2) page1_full_schedule에서 옴
        Intent intent = getIntent();
        db_key = null;
        dayNumber = intent.getIntExtra("dayNumber", dayNumber);
        db_key = intent.getStringExtra("key");


        //값을 받아옴(1) page3_1에서 옴
        if(db_key == null) {
            next_data = (ArrayList<String>) intent.getSerializableExtra("next_data");
            date = (intent.getExtras().getString("date")).replaceAll("[^0-9]", "");
            dayPass = intent.getExtras().getString("dayPass");
        }


        save_btn = (Button)findViewById(R.id.page3_save_btn);
        title = (TextView) findViewById(R.id.page3_1_1_1_title);

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

        loading_progress = findViewById(R.id.page3_1_1_1_progress);
        info_message = findViewById(R.id.info_message4);
        info_message2 = findViewById(R.id.info_message5);
        info_dismiss_btn = findViewById(R.id.info_dismiss_btn4);
        info_dismiss_btn2 = findViewById(R.id.info_dismiss_btn5);

        menu_img = (ImageView)findViewById(R.id.menu_userImage);
        menu_text1 = (TextView) findViewById(R.id.menu_text1);
        menu_text2 = (TextView) findViewById(R.id.menu_text2);
        edit_nickname = (ImageButton)findViewById(R.id.menu_edit_btn);

        // 취향파악 DB열기
        mLikeDpOpenHelper = new Like_DbOpenHelper(this);
        mLikeDpOpenHelper.open();
        mLikeDpOpenHelper.create();
        showLikeDB();

        //데베 관련
        second_mainDBHelper = new Second_MainDBHelper(this);
        second_mainDBHelper.open();
        second_mainDBHelper.create();

        //최소 실행 때 보이는 안내창-----------------------------------------------
        SharedPreferences a = getSharedPreferences("info1", MODE_PRIVATE);
        final SharedPreferences.Editor editor = a.edit();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                editor.putInt("info4", 1);
                editor.commit();
            }
        }, 300);

        //첫 실행시 나오는 안내 말풍선
        SharedPreferences preferences =getSharedPreferences("info1", MODE_PRIVATE);
        int firstviewShow = preferences.getInt("info4", 0);

        // 1이 아니라면 취향파악페이지 보여주기 = 처음 실행이라면
        if (firstviewShow != 1) {
            info_message.setVisibility(View.VISIBLE);
            info_message2.setVisibility(View.VISIBLE);
        }

        info_dismiss_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info_message.setVisibility(View.INVISIBLE);
            }
        });

        info_dismiss_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info_message2.setVisibility(View.INVISIBLE);
            }
        });

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
        showDatabase();

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

        //메인로고
        logo = (ImageButton) findViewById(R.id.main_logo_page3_1_1_1);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Page1.Page1.class);
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



        //(1)page3_1 에서 오면
        if(db_key == null) {
            //날짜를 더할때 실제 날짜 반영해서 더해야함
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date_format = null;
            try {
                date_format = dateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date_format);
            day1_date = date;
            calendar.add(Calendar.DATE, 1);
            day2_date = dateFormat.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
            day3_date = dateFormat.format(calendar.getTime());


            //앞에서 전달한 값을 쪼갬
            for (int i = 0; i < next_data.size(); i++) {
                split_1 = next_data.get(i).split(",");
                getitem.add(new Page3_1_1_1_dargData(split_1[0], split_1[1]));
            }

            //리사이클러뷰 리스트에 추가
            list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "1일차", day1_date, "", "",""));

            //환승인지 아닌지 걸러내는 작업
            for (int i = 0; i < next_data.size() - 1; i++) {
                if (!getitem.get(i).getNumber().contains("환승")) {
                    //직행
                    if (!getitem.get(i + 1).getNumber().contains("환승")) {
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i).getName() + "," + getitem.get(i + 1).getName(), getitem.get(i).getName() + "," + getitem.get(i + 1).getName(), day1_date, "", "",""));
                    }

                    //환승 1회
                    else if (!getitem.get(i + 2).getNumber().contains("환승")) {
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i).getName() + "," + getitem.get(i + 1).getName(), getitem.get(i).getName() + ",(환승)" + getitem.get(i + 1).getName(), day1_date, "", "",""));
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i + 1).getName() + "," + getitem.get(i + 2).getName(), "(환승)" + getitem.get(i + 1).getName() + "," + getitem.get(i + 2).getName(), day1_date, "", "",""));
                    }

                    //환승 2회
                    else if (!getitem.get(i + 3).getNumber().contains("환승")) {
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i).getName() + "," + getitem.get(i + 1).getName(), getitem.get(i).getName() + ",(환승)" + getitem.get(i + 1).getName(), day1_date, "", "",""));
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i + 1).getName() + "," + getitem.get(i + 2).getName(), "(환승)" + getitem.get(i + 1).getName() + ",(환승)" + getitem.get(i + 2).getName(), day1_date, "", "",""));
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i + 2).getName() + "," + getitem.get(i + 3).getName(), "(환승)" + getitem.get(i + 2).getName() + "," + getitem.get(i + 3).getName(), day1_date, "", "",""));
                    }

                    //환승 3회
                    else {
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i).getName() + "," + getitem.get(i + 1).getName(), getitem.get(i).getName() + ",(환승)" + getitem.get(i + 1).getName(), day1_date, "", "",""));
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i + 1).getName() + "," + getitem.get(i + 2).getName(), "(환승)" + getitem.get(i + 1).getName() + ",(환승)" + getitem.get(i + 2).getName(), day1_date, "", "",""));
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i + 2).getName() + "," + getitem.get(i + 3).getName(), "(환승)" + getitem.get(i + 2).getName() + ",(환승)" + getitem.get(i + 3).getName(), day1_date, "", "",""));
                        list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, getitem.get(i + 3).getName() + "," + getitem.get(i + 4).getName(), "(환승)" + getitem.get(i).getName() + "," + getitem.get(i + 4).getName(), day1_date, "", "",""));
                    }
                }
            }

            list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "2일차", day2_date, "", "",""));
            list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "3일차", day3_date, "", "",""));



            //5일차면
            if (dayPass.contains("5")) {
                //날짜 더해줌
                calendar.add(Calendar.DATE, 1);
                day4_date = dateFormat.format(calendar.getTime());
                calendar.add(Calendar.DATE, 1);
                day5_date = dateFormat.format(calendar.getTime());

                list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "4일차", day4_date, "", "",""));
                list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "5일차", day5_date, "", "",""));
            }


            //7일차면
            else if (dayPass.contains("7")) {
                //날짜 더해줌
                calendar.add(Calendar.DATE, 1);
                day4_date = dateFormat.format(calendar.getTime());
                calendar.add(Calendar.DATE, 1);
                day5_date = dateFormat.format(calendar.getTime());
                calendar.add(Calendar.DATE, 1);
                day6_date = dateFormat.format(calendar.getTime());
                calendar.add(Calendar.DATE, 1);
                day7_date = dateFormat.format(calendar.getTime());

                list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "4일차", day4_date, "", "",""));
                list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "5일차", day5_date, "", "",""));
                list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "6일차", day6_date, "", "",""));
                list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", "7일차", day7_date, "", "",""));
            }
        }


        //(2) schedule에서 값을 받아오면
        if(db_key != null) {
            getDatabase(db_key);
            title.setText("여행 일정 수정");
            save_btn.setText("수정완료하기");
        }


        // 레이아웃 안에 레이아웃 만들기
        LinearLayout contentsLayout = (LinearLayout) findViewById(R.id.page3_1_1_box_round);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.page3_1_1_1_recyclerview, contentsLayout, true);


        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        RecyclerView recyclerView = findViewById(R.id.scheduleRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // 리사이클러뷰에 Adapter 객체 지정.
        int isNetworkConnect  = NetworkStatus.getConnectivityStatus(Page3_1_1_1_Main.this);
        adapter = new Page3_1_1_1_trainAdapter(list, getSupportFragmentManager(), isNetworkConnect, this);
        recyclerView.setAdapter(adapter);


        // 드래그 이벤트
        ItemTouchHelper.Callback callback = new TrainItemTouchHelper(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        adapter.setTouchHelper(touchHelper);


        // 관광지 추가하기 버튼 누르면
        addSpot = (TextView) findViewById(R.id.page3_1_1_1_1_addbtn);
        addSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Page3_1_1_1_addCityBottomSheet addCityBottomSheet = Page3_1_1_1_addCityBottomSheet.getInstance();
                addCityBottomSheet.show(getSupportFragmentManager(), "AddCityBottomSheet");
            }
        });


        //일정 저장 버튼 누르면 ( 현재날짜+시간을 key로 데베에 저장)
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //일차바 순서가 잘 되어있는지 검사
                List<String> headerPosition = new ArrayList<>();
                boolean headerErro = false;
                for(int i =0; i < list.size(); i++){
                    if( list.get(i).type == 0){
                        headerPosition.add(list.get(i).text);
                    }
                }

                for(int i =0 ; i < headerPosition.size()-1; i++){
                    if(Integer.parseInt(headerPosition.get(i).replaceAll("[^0-9]", "") )> Integer.parseInt(headerPosition.get(i+1).replaceAll("[^0-9]", "") ) ){
                        headerErro = true;
                        HeaderErroDialog();
                        break;
                    }
                }

                //page3_1에서 온 거라면
                if(db_key == null&& !headerErro){
                    //현재시간얻기(데이터베이스의 기본키가 됨)
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddHHmmss");
                    String formatDate = "1"+simpleDateFormat.format(date).trim();

                    dbOpenHelper.open();
                    for(int i=0; i < list.size(); i++){
                        dbOpenHelper.insertColumn(
                                "no",
                                formatDate,
                                list.get(i).date,
                                list.get(i).text,
                                list.get(i).text_shadow,
                                list.get(i).train_time,
                                list.get(i).contentId);
                    }
                    dbOpenHelper.close();

                    Intent intent = new Intent(getApplicationContext(), Page1_Main.class);
                    intent.putExtra("key", formatDate);
                    intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }

                else if (db_key != null && !headerErro){
                    dbOpenHelper.open();
                    dbOpenHelper.deleteColumnByKey(db_key);
                    for(int i=0; i < list.size(); i++){
                        dbOpenHelper.insertColumn(
                                "no",
                                db_key,
                                list.get(i).date,
                                list.get(i).text,
                                list.get(i).text_shadow,
                                list.get(i).train_time,
                                list.get(i).contentId);
                    }
                    dbOpenHelper.close();

                    Intent intent = new Intent(getApplicationContext(), Page1_Main.class);
                    Log.i("키????", db_key);
                    intent.putExtra("key", db_key);
                    intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }

            }
        });
    }



    //관심 관광지 추가 인터페이스
    @Override
    public void onsetlist(String text, String cityname) {
        boolean isAdd = false;
        Log.i("씨티 네임 나 밖에 나갈래", cityname);
        if(cityname.equals("")) cityname = "히히";
        //해당되는 도시 아래에 넣기 위함
        for(int i =0; i < list.size(); i++){
            if(list.get(i).type == Page3_1_1_1_trainAdapter.CHILD){
                String text_split[] = list.get(i).text.split(",");

                //불필요한 공백 제거
                String station = text_split[1].trim();

                if(station.contains(cityname)){
                    list.add(i+1, new RecycleItem(Page3_1_1_1_trainAdapter.CITY, "",  text,  list.get(i).date, "", "",cityname));
                    Log.i("관광지 추가시",   text+"/"+  list.get(i).date+ "/"+cityname );
                    adapter.notifyDataSetChanged();
                    isAdd = true;
                    break;
                }
            }
        }

        //해당되는 도시가 없으면
        if(!isAdd){
            list.add( new RecycleItem(Page3_1_1_1_trainAdapter.CITY, "",  text,  list.get(list.size()-1).date, "", "",cityname));
            adapter.notifyDataSetChanged();
        }
    }

    public void HeaderErroDialog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Page3_1_1_1_Main.this);
        builder.setMessage("n일차 막대 순서를 확인해주세요.");
        builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    @Override
    public void settingProgress(boolean run) {
        if(run){
            loading_progress.setVisibility(View.VISIBLE);
        } else{
            loading_progress.setVisibility(View.INVISIBLE);
        }
    }



    public static class RecycleItem {
        int type;
        String text;
        String text_shadow;
        String date;
        String train_time;
        String station_code;
        String contentId;

        String cityName;
        String title;

        public String getCityName() {
            return cityName;
        }

        public String getTitle() {
            return title;
        }

        public RecycleItem(String title, String cityName) {
            this.title = title;
            this.cityName = cityName;
        }

        public RecycleItem(int type, String text_shadow, String text, String  date, String train_time, String station_code, String contentId){
            this.type = type;
            this.text_shadow = text_shadow;
            this.text = text;
            this.date = date;
            this.train_time = train_time;
            this.station_code = station_code;
            this.contentId = contentId;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setTrain_time(String train_time) {
            this.train_time = train_time;
        }

        public void setStation_code(String station_code) {
            this.station_code = station_code;
        }

        public void setContentId(String contentId) {
            this.contentId = contentId;
        }

    }



    //뒤로가기 화면 전환 없앰
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.backbutton, R.anim.backbutton);
    }


    //데이터베이스 받기(앞에서 저장한 값만 바로 보여줌)
    private void getDatabase(String db_key){
        String db_key2 = db_key.trim();
        Cursor iCursor = dbOpenHelper.selecteNumber(db_key2);
        list.clear();
        List<String> date = new ArrayList<>();
        List<String> daypase = new ArrayList<>();
        List<String> station = new ArrayList<>();
        List<String> time = new ArrayList<>();
        List<String> contendId = new ArrayList<>();

        while(iCursor.moveToNext()) {
            String tempDate     = iCursor.getString(iCursor.getColumnIndex("date"));
            String tempDayPass  = iCursor.getString(iCursor.getColumnIndex("daypass"));
            String tempStation  = iCursor.getString(iCursor.getColumnIndex("station"));
            String tempTime     = iCursor.getString(iCursor.getColumnIndex("time"));
            String tempContentId = iCursor.getString(iCursor.getColumnIndex("contentid"));

            Log.i("로그다", tempDate + "/" + tempDayPass + "/" + tempStation + "/" + tempTime + "/" + tempContentId);
            date.add(tempDate);
            daypase.add(tempDayPass);
            station.add(tempStation);
            time.add(tempTime);
            contendId.add(tempContentId);
        }
        for (int i = 0; i < iCursor.getCount(); i++) {
            //일차
            if ( station.get(i).equals("") && time.get(i).equals("") && contendId.get(i).equals("")) {
                list.add(new RecycleItem(Page3_1_1_1_trainAdapter.HEADER, "", daypase.get(i), date.get(i), "", "",""));
            }

            //기차시간
            else if (contendId.get(i).equals("")) {
                list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CHILD, station.get(i), daypase.get(i), date.get(i), time.get(i), "",""));
            }

            //시티
            else {
                list.add(new RecycleItem(Page3_1_1_1_trainAdapter.CITY, "",daypase.get(i), date.get(i), "", "",contendId.get(i)));
            }
        }

    }
    public void showDatabase(){
        Cursor iCursor = mDbOpenHelper.selectColumns();
        //iCursor.moveToFirst();
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        mySpot.clear();

        while(iCursor.moveToNext()){
            String tempName = iCursor.getString(iCursor.getColumnIndex("name"));

            mySpot.add(tempName);
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
        list.clear();
        getitem.clear();
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

}
