package com.deblockt.kodirecomendations;

import android.app.Activity;
import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;

/**
 * Created by thomas on 09/04/16.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        GuidedStepFragment.addAsRoot(this, new KodiGuidedStepFragment(), R.id.guid_step);
    }
}
