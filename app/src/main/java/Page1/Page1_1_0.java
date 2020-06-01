package Page1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.hansol.spot_200510_hs.Page0_9_PopUp;
import com.example.hansol.spot_200510_hs.R;

import java.util.ArrayList;
import java.util.List;

import DB.Like_DbOpenHelper;
import DB.Menu_DbOpenHelper;
import DB.Second_MainDBHelper;
import Page1_schedule.LocationUpdatesService;
import Page1_schedule.Location_Utils;
import Page2.Page2;
import Page2_X.Page2_X;
import Page3.Page3_Main;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page1_1_0 extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    Button city, course;

    ImageButton logo;

    //메뉴 관련
    private Context context;
    private ImageButton menu_edit;
    private ImageView userImg;
    private TextView userText1;
    private TextView userText2;
    private RecyclerView recyclerView1;
    private Switch positionBtn;
    private Switch alramBtn;
    Main_RecyclerviewAdapter adapter;
    ArrayList<String> name = new ArrayList<>();
    private Toolbar toolbar2;
    private DrawerLayout drawer;
    private EndDrawerToggle mDrawerToggle;

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

    private Menu_DbOpenHelper menu_dbOpenHelper;
    private List<String> onoff = new ArrayList<>();

    private int spotSize;

    ImageView menu_img;
    TextView menu_text1, menu_text2;

    int[] score = new int[8];
    String mScore[] = new String[8];

    private Like_DbOpenHelper mLikeDpOpenHelper;
    String like, nickName, sub;

    ImageButton edit_nickname;

    //등록한 일정 관련
    private Second_MainDBHelper second_mainDBHelper;
    private String second_key = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page1_1_0);

        city = (Button) findViewById(R.id.page1_1_city);
        course = (Button) findViewById(R.id.page1_1_course);

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

        menu_img = (ImageView)findViewById(R.id.menu_userImage);
        menu_text1 = (TextView) findViewById(R.id.menu_text1);
        menu_text2 = (TextView) findViewById(R.id.menu_text2);
        edit_nickname = (ImageButton)findViewById(R.id.menu_edit_btn);


        logo = (ImageButton) findViewById(R.id.main_logo_page1_1_0);

        Intent intent = getIntent();
        spotSize = intent.getIntExtra("spotSize", 0);

        score = intent.getIntArrayExtra("score");

        //앞에서 주는 score 값이 없으면
        if(score == null){
            score = new int[8];
        }

        // 취향파악 DB열기
        mLikeDpOpenHelper = new Like_DbOpenHelper(this);
        mLikeDpOpenHelper.open();
        mLikeDpOpenHelper.create();
        showLikeDB();

        //데베 관련
        second_mainDBHelper = new Second_MainDBHelper(this);
        second_mainDBHelper.open();
        second_mainDBHelper.create();


        //메인 로고 눌렀을때 메인페이지 이동
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Page1.class);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Logo", "1");
                startActivity(intent);
                overridePendingTransition(0,0);  //순서를 바꿔줌


            }
        });


        // 도시 탐색하기 눌렀을 때
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scheduleIntent = new Intent(getApplicationContext(), Page2_X.class);
                scheduleIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                scheduleIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(scheduleIntent);

            }
        });

        // 추천 코스 눌렀을 때
        course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scheduleIntent = new Intent(getApplicationContext(), Page2_1.class);
                scheduleIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                scheduleIntent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(scheduleIntent);
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

        //등록된 일정이 있는지 검사
        Cursor iCursor = second_mainDBHelper.selectColumns();
        while (iCursor.moveToNext()){
            String Key = iCursor.getString(iCursor.getColumnIndex("userid"));
            second_key = Key;
        }

        //메뉴 안 내용 구성
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Main_RecyclerviewAdapter(name, context, spotSize, second_key);
        recyclerView1.setAdapter(adapter);

        //리사이클러뷰 헤더
        name.add("0");
        name.add("1");
        name.add("2");

        //툴바 타이틀 없애기
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
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
