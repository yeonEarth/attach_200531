package Page3_1_1;

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
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.hansol.spot_200510_hs.Page0_9_PopUp;
import com.example.hansol.spot_200510_hs.R;
import com.example.hansol.spot_200510_hs.send_data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import DB.DbOpenHelper;
import DB.Like_DbOpenHelper;
import DB.Menu_DbOpenHelper;
import Page1.EndDrawerToggle;
import Page1.Main_RecyclerviewAdapter;
import Page1_schedule.LocationUpdatesService;
import Page1_schedule.Location_Utils;
import Page3_1.Page3_1_Main;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class Page3_1_1_Main extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, Page3_1_1_addBottomSheet.onSetList, Page3_1_1_addConformDialog.GoAlgorithPage {

    Page3_1_1_Main page3_1_1_main;
    RecyclerView recyclerView;
    Button  add_city, revise_done;

    //앞에서 전달한 값
    String result, date, dayPass;

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



    //도시추가시 도시가 들어갈 순서를 받기 위한 변수
    int number = 0;

    //리사이클러뷰 관련
    private ArrayList<Page3_1_1_dargData> list = new ArrayList<>();
    private Page3_1_1_adapter adapter = new Page3_1_1_adapter(list);
    boolean checkStart = false;
    List<item_data> add_list = new ArrayList<>();
    ArrayList<String> result_name = new ArrayList<String>();
    ArrayList<String> result_number = new ArrayList<String>();

    //txt값을 분류하기 위한 부분
    private List<String> getdata_list = new ArrayList<String>();
    String[] code_name = null;
    String[] code = new String[237];
    String[] name = new String[237];
    String readStr = "";

    // 찜한 여행지 저장하는 리스트
    private ArrayList<String > mySpot = new ArrayList<String >();
    private DbOpenHelper mDbOpenHelper;

    private RelativeLayout info_message;
    private Button info_dismiss_btn;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page3_1_1_main);

        //앞에서 보내는 값을 받음
        Intent intent = getIntent();
        result =  intent.getStringExtra("result");
        date = intent.getExtras().getString("date");
        dayPass = intent.getExtras().getString("dayPass");


        //리사이클러뷰
        recyclerView = (RecyclerView)findViewById(R.id.page3_1_1_RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(page3_1_1_main));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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
        info_message = findViewById(R.id.info_message3);
        info_dismiss_btn = findViewById(R.id.info_dismiss_btn3);

        menu_img = (ImageView)findViewById(R.id.menu_userImage);
        menu_text1 = (TextView) findViewById(R.id.menu_text1);
        menu_text2 = (TextView) findViewById(R.id.menu_text2);
        edit_nickname = (ImageButton)findViewById(R.id.menu_edit_btn);


        //최소 실행 때 보이는 안내창-----------------------------------------------
        SharedPreferences a = getSharedPreferences("info1", MODE_PRIVATE);
        final SharedPreferences.Editor editor = a.edit();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                editor.putInt("info3", 1);
                editor.commit();
            }
        }, 300);

        //첫 실행시 나오는 안내 말풍선
        SharedPreferences preferences =getSharedPreferences("info1", MODE_PRIVATE);
        int firstviewShow = preferences.getInt("info3", 0);

        // 1이 아니라면 취향파악페이지 보여주기 = 처음 실행이라면
        if (firstviewShow != 1) {
            info_message.setVisibility(View.VISIBLE);
        }

        info_dismiss_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info_message.setVisibility(View.INVISIBLE);
            }
        });

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

        // DB열기
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
        showDatabase();

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
        logo = (ImageButton) findViewById(R.id.main_logo_page3_1_1);

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



        //드래그 기능
        SwipeAndDragHelper swipeAndDragHelper = new SwipeAndDragHelper(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeAndDragHelper);
        adapter.setTouchHelper(touchHelper);


        //리사이클러뷰-드래그 연결
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);


        //앞에서 받아온 알고리즘 값을 정리
        ongetdata(result);


        //환승역은 걸러서 list에 추가해준다.
        for(int i =0; i < add_list.size(); i++){
            if(!add_list.get(i).getNumber().contains("환승")){
                list.add(new Page3_1_1_dargData(add_list.get(i).getName() , number));
                number ++;
            }
        }


        //도시 추가하기 버튼
        add_city = (Button) findViewById(R.id.page3_1_1_listAdd);
        add_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Page3_1_1_addBottomSheet add = Page3_1_1_addBottomSheet.getInstance();
                add.show(getSupportFragmentManager(), "add");
            }
        });


        //도시추가 됐을 때 알고리즘 돌리겠냐는 바텀시트 뜸
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Page3_1_1_addConformDialog conformDialog = Page3_1_1_addConformDialog.getInstance(list.get(list.size()-2).getName(), date, dayPass);
                conformDialog.show(getSupportFragmentManager(), "confirm");
            }
        });


        //수정완료 버튼 누르면, list 순서대로 알고리즘을 돌린다.
        revise_done = (Button) findViewById(R.id.page3_1_1_reviseDone_btn);
        revise_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                settingList();

                //값을 전달할 리스트
                ArrayList<send_data> send_list = new ArrayList<send_data>();

                //선택된 역 이름을 번호와 함께 넘겨준다.
                for(int i=0; i<list.size(); i++){
                    for(int j=0; j<237; j++){
                        if(list.get(i).getName().equals(name[j])){
                            send_list.add(new send_data(code[j],name[j]) );
                        }
                    }
                }

                Intent intent = new Intent(Page3_1_1_Main.this, Page3_1_Main.class);
                intent.putExtra("list", (Serializable) send_list);
                intent.putExtra("date", date);  //날짜
                intent.putExtra("dayPass", dayPass);
                intent.putExtra("reRvise_done", "reRvise_done");
                overridePendingTransition(R.anim.backbutton, R.anim.backbutton);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }



    //알고리즘 값을 정리
    public void ongetdata(String result) {

        //줄바꿈 단위로 나눈 것을 개수/출발/시간/도착 으로 쪼갬
        String[] split_result = result.split("\n");
        for (int i = 0; i < split_result.length; i++) {
            String[] split_result2 = split_result[i].split(",");

            //그냥 지나감
            if (split_result[i].contains("개수") || split_result[i].contains("시간"))
                continue;

            else if (split_result[i].contains("출발")) {
                result_name.add(split_result2[1]);
                if (!checkStart) {
                    result_number.add("출발");
                    checkStart = true;
                } else
                    result_number.add("경유");
            }

            else if (split_result[i].contains("도착") && i == split_result.length - 1) {
                result_name.add(split_result2[1]);
                result_number.add("도착");
            }

            else if (split_result[i].contains("환승")) {
                result_name.add(split_result2[1]);
                result_number.add("환승");
            }
        }

        //리스트에 추가
        for (int i = 0; i < result_name.size(); i++) {
            add_list.add(new item_data(result_number.get(i), result_name.get(i)));
        }

    }



    //도시추가 바텀시트의 인터페이스
    @Override
    public void onsetlist(String text) {
        if(list.size() < 10){
            list.add(list.size()-1, new Page3_1_1_dargData(text , number));
            adapter.notifyDataSetChanged();
        } else {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage("추가할 수 있는 개수를 초과했습니다.");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.show();
        }
    }



    //추가된 역을 반영해서 새로운 알고리즘을 돌리기 위한 인터페이스
    @Override
    public void go_algorithm_page() {
        settingList();

        //값을 전달할 리스트(send_list)와 도착역과 추가역의 순서를 바꿔줄 리스트(send_list_change)
        ArrayList<send_data> send_list = new ArrayList<send_data>();

        //선택된 역 이름을 번호와 함께 넘겨준다.
        for(int i=0; i<list.size(); i++){
            for(int j=0; j<237; j++){
                if(list.get(i).getName().equals(name[j])){
                    send_list.add(new send_data(code[j],name[j]) );
                }
            }
        }



        Intent intent = new Intent(this, Page3_1_Main.class);
        intent.putExtra("list", (Serializable) send_list);
        intent.putExtra("date", date);  //날짜
        intent.putExtra("dayPass", dayPass);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }



    //txt 값 정리
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
        String[] arr = readStr.split("\n");
        for (int i = 0; i < arr.length; i++) {
            code_name = arr[i].split(",");
            code[i] = code_name[0];
            name[i] = code_name[1];
            getdata_list.add(name[i]);
        }
    }



    //뒤로가기 화면 전환 효과 없앰
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.backbutton, R.anim.backbutton);
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





    public static class item_data  {
        String number;
        String name;

        String getNumber() {
            return this.number;
        }

        String getName() {
            return this.name;
        }

        public item_data(String number, String name) {
            this.number  = number;
            this.name  = name;
        }
    }

    public void showDatabase(){
        Cursor iCursor = mDbOpenHelper.selectColumns();
        //iCursor.moveToFirst();
       // Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        mySpot.clear();

        while(iCursor.moveToNext()){
            String tempName = iCursor.getString(iCursor.getColumnIndex("name"));

            mySpot.add(tempName);
        }
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
