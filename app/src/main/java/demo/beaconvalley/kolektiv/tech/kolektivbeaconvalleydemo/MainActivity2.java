package demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo.Fragments.MainFragment;


public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            MainFragment fragment = new MainFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("amount", 3);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
    }
}
