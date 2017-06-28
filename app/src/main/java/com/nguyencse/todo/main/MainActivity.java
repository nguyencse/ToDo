package com.nguyencse.todo.main;

import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nguyencse.todo.adapter.ViewPagerAdapter;
import com.nguyencse.todo.custom.EditTextCSE;
import com.nguyencse.todo.custom.TextViewCSE;
import com.nguyencse.todo.database.DatabaseHandler;
import com.nguyencse.todo.main.fragments.DashboardFragment;
import com.nguyencse.todo.objects.Task;
import com.nguyencse.todo.R;
import com.nguyencse.todo.general.CommonField;
import com.nguyencse.todo.general.CommonMethod;
import com.nguyencse.todo.main.fragments.AddTaskFragment;
import com.nguyencse.todo.main.fragments.ChattingFragment;
import com.nguyencse.todo.main.fragments.MoreFragment;
import com.nguyencse.todo.objects.User;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static FirebaseUser user;
    private FirebaseAuth mAuth;

    private DrawerLayout drlMenuProfile;
    private ImageButton btnMenuProfile;
    private ImageButton btnMenuRight;
    private LinearLayout lnlMenuProfile;

    private TabLayout tabsMain;
    public static ViewPager vpMain;
    private TextViewCSE txtScreenTitle;
    private TextViewCSE txtProfileUsername;
    private TextViewCSE txtProfileEmail;
    public static DatabaseHandler dbTodo;

    private TextViewCSE txtProfileUpdateUsername;
    private EditTextCSE edtProfileUpdateUsername;
    private TextViewCSE txtProfileUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbTodo = new DatabaseHandler(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        drlMenuProfile = (DrawerLayout) findViewById(R.id.drlMenuProfile);
        btnMenuProfile = (ImageButton) findViewById(R.id.btn_menu_profile);
        btnMenuRight = (ImageButton) findViewById(R.id.btn_menu_right);
        lnlMenuProfile = (LinearLayout) findViewById(R.id.lnlMenuProfile);
        txtScreenTitle = (TextViewCSE) findViewById(R.id.txt_screen_title);
        txtProfileUsername = (TextViewCSE) findViewById(R.id.txt_profile_username);
        txtProfileEmail = (TextViewCSE) findViewById(R.id.txt_profile_email);
        txtProfileUpdate = (TextViewCSE) findViewById(R.id.txt_profile_update);
        edtProfileUpdateUsername = (EditTextCSE) findViewById(R.id.edt_profile_update_username);
        txtProfileUpdateUsername = (TextViewCSE) findViewById(R.id.txt_profile_update_username);

        FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    txtProfileUsername.setText(user.getUsername());
                    txtProfileEmail.setText(user.getEmail());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        txtProfileUsername.setText(user.getDisplayName());
        txtProfileEmail.setText(user.getEmail());

        tabsMain = (TabLayout) findViewById(R.id.tabs_main);
        vpMain = (ViewPager) findViewById(R.id.vp_main);
        vpMain.setOffscreenPageLimit(CommonField.FRAGMENT_COUNT);

        setupViewPager(vpMain);
        tabsMain.setupWithViewPager(vpMain);
        setupTabIcons();

        if (tabsMain.getSelectedTabPosition() == 0) {
            txtScreenTitle.setText(getString(R.string.dashboard));
        }

        tabsMain.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int posTab = tab.getPosition();

                switch (posTab) {
                    case 0:
                        txtScreenTitle.setText(getString(R.string.dashboard));
                        break;
                    case 1:
                        txtScreenTitle.setText(getString(R.string.add_task));
                        break;
                    case 2:
                        txtScreenTitle.setText(getString(R.string.chatting));
                        break;
                    case 3:
                        txtScreenTitle.setText(getString(R.string.more));
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Reading all contacts
//        Log.d("CSE_TAKS_Reading: ", "Reading all contacts..");
        List<Task> taskList = dbTodo.getAllTasks();

        for (Task task : taskList) {
            String log = "Key: " + task.getKey() + " ,Date: " + task.getDate() + " ,Time: " + task.getTime() + " ,Name: " + task.getName()
                    + " ,Detail: " + task.getDetail() + " ,Status: " + task.getStatus();
            // Writing tasks to log
//            Log.d("CSE_TASK: ", log);
        }

        btnMenuProfile.setOnClickListener(this);
        btnMenuRight.setOnClickListener(this);
        txtProfileUpdate.setOnClickListener(this);
        txtProfileUpdateUsername.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_menu_profile:
                drlMenuProfile.openDrawer(lnlMenuProfile);
                break;
            case R.id.btn_menu_right:
                CommonMethod.logout(this);
                break;
            case R.id.txt_profile_update:
                edtProfileUpdateUsername.setVisibility(View.VISIBLE);
                txtProfileUpdateUsername.setVisibility(View.VISIBLE);
                break;
            case R.id.txt_profile_update_username:
                edtProfileUpdateUsername.setVisibility(View.GONE);
                txtProfileUpdateUsername.setVisibility(View.GONE);

                if (TextUtils.isEmpty(String.valueOf(edtProfileUpdateUsername.getText().toString()))) {
                    Toast.makeText(this, getString(R.string.this_field_must_not_be_empty), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid())
                            .setValue(new User(user.getUid(), edtProfileUpdateUsername.getText().toString(), user.getEmail(), ""));
                    txtProfileUsername.setText(edtProfileUpdateUsername.getText().toString());
                }
                break;
        }
    }

    private void setupTabIcons() {
        TextViewCSE tabDashboard = (TextViewCSE) LayoutInflater.from(this).inflate(R.layout.custom_item_tab, null, false);
        tabDashboard.setText(getString(R.string.dashboard));
        tabDashboard.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_dashboard, 0, 0);
        tabsMain.getTabAt(0).setCustomView(tabDashboard);

        TextViewCSE tabAddingTask = (TextViewCSE) LayoutInflater.from(this).inflate(R.layout.custom_item_tab, null, false);
        tabAddingTask.setText(getString(R.string.add_task));
        tabAddingTask.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_add, 0, 0);
        tabsMain.getTabAt(1).setCustomView(tabAddingTask);

        TextViewCSE tabChatting = (TextViewCSE) LayoutInflater.from(this).inflate(R.layout.custom_item_tab, null, false);
        tabChatting.setText(getString(R.string.chatting));
        tabChatting.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_chatting, 0, 0);
        tabsMain.getTabAt(2).setCustomView(tabChatting);

        TextViewCSE tabFriends = (TextViewCSE) LayoutInflater.from(this).inflate(R.layout.custom_item_tab, null, false);
        tabFriends.setText(getString(R.string.more));
        tabFriends.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_more_horiz_white, 0, 0);
        tabsMain.getTabAt(3).setCustomView(tabFriends);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DashboardFragment(), "Dashboard");
        adapter.addFragment(new AddTaskFragment(), "Add Task");
        adapter.addFragment(new ChattingFragment(), "Chatting");
        adapter.addFragment(new MoreFragment(), "More");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.warning))
                .setMessage(getString(R.string.confirm_exit_app))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }
}
