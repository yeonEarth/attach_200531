package com.example.hansol.spot_200510_hs;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import DB.Like_DbOpenHelper;

public class Page0_9_PopUp extends AppCompatActivity {
    LinearLayout linearLayout;

    Button close_btn, check_btn;
    TextView subTitle;
    EditText nickName;
    ImageView profile;

    String name, sub, editName, fav;

    int[] score = new int[8];

    // 키보드 내릴때
    InputMethodManager keyboard;
    private Like_DbOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page0_9_pop_up);

        close_btn = (Button) findViewById(R.id.page0_9_close_btn);
        check_btn = (Button) findViewById(R.id.page0_9_check_btn);
        subTitle = (TextView) findViewById(R.id.page0_9_subname);
        nickName = (EditText) findViewById(R.id.page0_9_name);
        profile = (ImageView) findViewById(R.id.page0_9_profile);
        keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        linearLayout = (LinearLayout) findViewById(R.id.page0_9_ll);

        linearLayout.setOnClickListener(listener);

        mDbOpenHelper = new Like_DbOpenHelper(Page0_9_PopUp.this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        Intent intent = getIntent();
        name = intent.getStringExtra("닉네임");
        sub = intent.getStringExtra("서브이름");
        score = intent.getIntArrayExtra("Page9");

        // 앞에서 받아온 걸로 적용
        nickName.setText(name);
        subTitle.setText(sub);

        //앞에서 받아온 스코엉어를 스트링으로
        for (int i = 0 ; i < score.length -1  ; i++) {
            fav += score[i] + " ";
        }


        //메뉴 사진
        if (score[2] == 0 && score[3] == 0) {
            profile.setBackgroundResource(R.drawable.ic_ant);
        }

        if (score[2] == 1 && score[3] == 1) {
            profile.setBackgroundResource(R.drawable.ic_sloth);
        }

        if (score[2] != score[3]) {
            if (score[6] == 0) {
                profile.setBackgroundResource(R.drawable.ic_otter);
            } else if (score[2] == 1 ) {

                profile.setBackgroundResource(R.drawable.ic_soul);

            } else if (score[2] == 0) {

                profile.setBackgroundResource(R.drawable.ic_excel);

            }
        }

        if (score[1] == 0) {
            if (score[4] == 0 && score[5] == 1) {
                profile.setBackgroundResource(R.drawable.ic_sprout);
            }
            else if (score[4] == 1&&score[5] == 0) {
                profile.setBackgroundResource(R.drawable.ic_chick);

            }
        }
        if (score[1] == 4&&score[5] == 0) {
            profile.setBackgroundResource(R.drawable.ic_chick);
        }

        nickName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // 키보드의 확인 버튼 눌렀을 때
                if (i == EditorInfo.IME_ACTION_DONE) {
                    // 키보드 내리기
                    keyboard.hideSoftInputFromWindow(nickName.getWindowToken(), 0);
                }
                return false;
            }
        });

        nickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            // 입력 끝났을 때
            @Override
            public void afterTextChanged(Editable editable) {
                // 다시 메인으로 보낼 수정된 이름
                editName = nickName.getText().toString();
                sub = subTitle.getText().toString();
                Log.i("보낼 이름", editName);
            }
        });

        // X버튼 눌렀을 때
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0,0);
            }
        });

        // 등록 버튼 눌렀을 때
        check_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDbOpenHelper.open();
                mDbOpenHelper.deleteAllColumns();
                mDbOpenHelper.insertLikeColumn(fav.replaceAll("null",""), editName, sub);
                mDbOpenHelper.close();

                // 공백 체크
                if (nickName.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent checkIntent = new Intent();
                    checkIntent.putExtra("result", editName);
                    checkIntent.putExtra("result2", sub);
                    setResult(RESULT_OK, checkIntent);
                    finish();
                    overridePendingTransition(0,0);
                }
            }
        });
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            linearOnClick();
        }
    };

    public void linearOnClick() {
        keyboard.hideSoftInputFromWindow(nickName.getWindowToken(),0);
    }

    // 백버튼 막기
    @Override
    public void onBackPressed() {
        return;
    }
}
