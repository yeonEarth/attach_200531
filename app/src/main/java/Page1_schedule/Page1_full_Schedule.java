package Page1_schedule;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hansol.spot_200510_hs.Page0_9_PopUp;
import com.example.hansol.spot_200510_hs.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import DB.DbOpenHelper;
import DB.Like_DbOpenHelper;
import DB.Menu_DbOpenHelper;
import DB.Train_DbOpenHelper;
import Page1.EndDrawerToggle;
import Page1.Main_RecyclerviewAdapter;
import Page3_1_1_1.Page3_1_1_1_Main;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page1_full_Schedule extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    Page1_full_ScheduleAdapter1 adapter1;
    ArrayList<Page1_Main.RecycleItem> All_items = new ArrayList<Page1_Main.RecycleItem>();

    //여행 일차를 알기 위함
    int dayNumber = 0 ;
    String db_key;

    //데이터베이스 관련
    private Train_DbOpenHelper mDbOpenHelper;
    private ArrayList<Database_Item> db_data = new ArrayList<Database_Item>();
    private String startDate;
    private List<String> station = new ArrayList<String>();
    private List<String> stationWithTransfer = new ArrayList<String>();

    // 찜한 관광지 DB
    private DbOpenHelper spotDbOpenHelper;
    // 찜한 여행지 저장하는 리스트
    private ArrayList<String > mySpot = new ArrayList<String >();

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page1_full_schedule);

        //앞에서 값을 받아온다.
        Intent get = getIntent();
        db_key = get.getStringExtra("key");


        /*데이터베이스 연결**************************************************/
        mDbOpenHelper = new Train_DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();




        //데이터베이스에 있는 값을 리스트에 추가
        getDatabase(db_key);


        //일차, 기차시간, 관광지 부분 분류
        int dayNumber = 0;
        for(int i = 0; i < db_data.size(); i++){

            //일차
            if(db_data.get(i).text_shadow.length() == 0
                    && db_data.get(i).time.length() == 0
                    && db_data.get(i).contentId.length() == 0){
                All_items.add(new Page1_Main.RecycleItem(Page1_ScheduleAdapter.HEADER, db_data.get(i).date, db_data.get(i).text, "", "", "" ,""));
                dayNumber++;
            }

            //기차시간
            else if(db_data.get(i).text_shadow.length() != 0){
                All_items.add(new Page1_Main.RecycleItem(Page1_ScheduleAdapter.CHILD,  db_data.get(i).date, db_data.get(i).text_shadow, db_data.get(i).time, db_data.get(i).text, "", ""));
            }

            //관심 관광지
            else{
                All_items.add(new Page1_Main.RecycleItem(Page1_ScheduleAdapter.CITY,  db_data.get(i).date, "", "", "", db_data.get(i).text, db_data.get(i).contentId));
            }
        }


        String startDate = db_data.get(0).date;
        String endDate = db_data.get(db_data.size()-1).date;

        // 찜한 관광지DB열기
        spotDbOpenHelper = new DbOpenHelper(this);
        spotDbOpenHelper.open();
        spotDbOpenHelper.create();
        showDatabase();


        //객체 연결
        context = getApplicationContext();
        toolbar2 = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        userImg = (ImageView)findViewById(R.id.menu_userImage);
        userText1 = (TextView)findViewById(R.id.menu_text1);
        userText2 = (TextView)findViewById(R.id.menu_text2);
        positionBtn = (Switch) findViewById(R.id.menu_postion_btn);
        alramBtn = (Switch)findViewById(R.id.menu_alram_btn);
        recyclerView1 = (RecyclerView)findViewById(R.id.menu_recyclerview1);

        menu_img = (ImageView)findViewById(R.id.menu_userImage);
        menu_text1 = (TextView) findViewById(R.id.menu_text1);
        menu_text2 = (TextView) findViewById(R.id.menu_text2);
        edit_nickname = (ImageButton)findViewById(R.id.menu_edit_btn);


        // DB열기
        menu_dbOpenHelper = new Menu_DbOpenHelper(this);
        menu_dbOpenHelper.open();
        menu_dbOpenHelper.create();
        notity_listner("");


        // 취향파악 DB열기
        mLikeDpOpenHelper = new Like_DbOpenHelper(this);
        mLikeDpOpenHelper.open();
        mLikeDpOpenHelper.create();
        showLikeDB();



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

        //메뉴 안 내용 구성
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        adapter2 = new Main_RecyclerviewAdapter(name2, context, mySpot.size());
        recyclerView1.setAdapter(adapter2);

        //리사이클러뷰 헤더
        name2.add("0");
        name2.add("1");
        name2.add("2");

        //툴바 타이틀 없애기
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //메인로고
        logo = (ImageButton) findViewById(R.id.main_logo_page1_full);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Page1.Page1.class);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Logo", "1");
                startActivity(intent);

                overridePendingTransition(0,0);     //순서를 바꿔준다

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


        //날짜를 반영
        TextView textView = (TextView)findViewById(R.id.page1_full_schedule_date);
        textView.setText(startDate.substring(0,4) + "년 "
        + startDate.substring(4, 6) + "월 "
        + startDate.substring(6) + "일 ~ "
        + endDate.substring(4, 6) + "월 "
        + endDate.substring(6) + "일");


        RecyclerView recyclerView = findViewById(R.id.page1_full_schedule_recyclerview1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter1 = new Page1_full_ScheduleAdapter1(All_items, dayNumber);
        recyclerView.setAdapter(adapter1);


        //수정하기 버튼 누르면
        FloatingActionButton editBtn = (FloatingActionButton)findViewById(R.id.page1_full_schedule_editBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Page1_full_Schedule.this, Page3_1_1_1_Main.class);
                intent.putExtra("key", db_key);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

    public void showDatabase(){
        Cursor iCursor = spotDbOpenHelper.selectColumns();
        //iCursor.moveToFirst();
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        mySpot.clear();

        while(iCursor.moveToNext()){
            String tempName = iCursor.getString(iCursor.getColumnIndex("name"));

            mySpot.add(tempName);
        }
    }


    //데이터베이스 받기(앞에서 저장한 값만 바로 보여줌)
    private void getDatabase(String db_key){
        String db_key2 = db_key.trim();
        Cursor iCursor = mDbOpenHelper.selecteNumber(db_key2);
        db_data.clear();

        while(iCursor.moveToNext()){
            String tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
            String tempNumber = iCursor.getString(iCursor.getColumnIndex("number"));
            String tempDate = iCursor.getString(iCursor.getColumnIndex("date"));
            String tempDayPass = iCursor.getString(iCursor.getColumnIndex("daypass"));
            String tempStation = iCursor.getString(iCursor.getColumnIndex("station"));
            String tempTime = iCursor.getString(iCursor.getColumnIndex("time"));
            String tempContentId = iCursor.getString(iCursor.getColumnIndex("contentid"));

            Log.i("로그다",tempDate+"/"+ tempDayPass+"/"+ tempStation+"/"+ tempTime+"/"+ tempContentId );
            db_data.add(new Database_Item(tempDate, tempDayPass, tempStation, tempTime, tempContentId));
        }
        mDbOpenHelper.close();
    }
    //데이터베이스 아이템 변수 선언
    public class Database_Item  {
        String date;
        String text;
        String text_shadow;
        String time;
        String contentId;

        public Database_Item(String date, String text, String text_shadow, String time, String contentId) {
            this.date = date;
            this.text = text;
            this.text_shadow = text_shadow;
            this.time = time;
            this.contentId = contentId;
        }

        public String getDate() {
            return date;
        }

        public String getText() {
            return text;
        }

        public String getText_shadow() {
            return text_shadow;
        }

        public String getTime() {
            return time;
        }

        public String getContentId() {
            return contentId;
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
