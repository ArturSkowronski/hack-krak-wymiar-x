package demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo.Fragments.MainFragment;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
        if (savedInstanceState == null) {

            MainFragment fragment = new MainFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("amount", 0);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    private void registerReceiver(){

    }
}
