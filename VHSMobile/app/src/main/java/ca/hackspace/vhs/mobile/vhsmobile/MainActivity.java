package ca.hackspace.vhs.mobile.vhsmobile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Member;

import ca.hackspace.vhs.mobile.nomos.Nomos;
import ca.hackspace.vhs.mobile.nomos.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int LOGIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Nomos.Services().SetClient("3", "4adcd386dda238a03d896c646ba6b91aeebc3d2e5034ed45c623f4b183b49a30"); //fixme move these to secrets

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("http://isvhsopen.com/");

        NfcManager manager = (NfcManager) getApplicationContext().getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();

        if (adapter != null && adapter.isEnabled()) {
            adapter.enableReaderMode(this, new NfcAdapter.ReaderCallback() {
                @Override
                public void onTagDiscovered(Tag tag) {
                    Intent memberCardActivity = new Intent(MainActivity.this.getApplicationContext(), MemberCardActivity.class);
                    memberCardActivity.putExtra("tag", tag);
                    startActivity(memberCardActivity);
                }
            }, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
        }

        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        TextView email = (TextView) nav.getHeaderView(0).findViewById(R.id.textView_username);
        email.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!Nomos.Services().IsAuthenticated()) {
                        Intent intent = new Intent(v.getContext(), LoginActivity.class);
                        startActivityForResult(intent, LOGIN);
                        //startActivity(intent);

                    } else {
                        LoadUserDetails();
                    }

                    return true;
                }

                return false;
            }
        });
    }

    private void LoadUserDetails() {
        if (Nomos.Services().IsAuthenticated()) {
            Nomos.Services().GetCurrentUser(new User.IUserResultHandler() {
                @Override
                public void handle(User user) {
                    NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
                    TextView username = (TextView) nav.getHeaderView(0).findViewById(R.id.textView_username);
                    TextView email = (TextView) nav.getHeaderView(0).findViewById(R.id.textView_email);
                    ImageView image = (ImageView) nav.getHeaderView(0).findViewById(R.id.imageView);

                    username.setText(user.username);
                    email.setText(user.email);
                    image.setImageDrawable(null);
                    image.setImageURI(Uri.parse("https://www.gravatar.com/avatar/" + Nomos.md5(user.email)));
                }

                @Override
                public void error(Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN && resultCode != RESULT_CANCELED && Nomos.Services().IsAuthenticated()) {
            LoadUserDetails();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.is_vhs_open) {
            WebView webView = (WebView) findViewById(R.id.webView);
            webView.loadUrl("http://isvhsopen.com/");
            //setContentView(R.layout.activity_main);
        } else if (id == R.id.nav_nfc) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
