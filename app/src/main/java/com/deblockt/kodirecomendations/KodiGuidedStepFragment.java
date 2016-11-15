package com.deblockt.kodirecomendations;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;

import java.util.List;

/**
 * Created by thomas on 09/04/16.
 */
public class KodiGuidedStepFragment extends GuidedStepFragment {

    private static final long INITIAL_DELAY = 10;
    private long QUIT = 1;


    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        String title = "Activation";//getString(R.string.guidedstep_first_title);
        String breadcrumb = "Recomendation Kodi"; //getString(R.string.guidedstep_first_breadcrumb);
        String description = "Les recomandations pour Kodi sont maintenant activées. ";//getString(R.string.guidedstep_first_description);
    //    Drawable icon = getActivity().getDrawable(R.drawable.guidedstep_main_icon_1);
        return new GuidanceStylist.Guidance(title, description, breadcrumb, null);
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        // Add "Continue" user action for this step
        actions.add(new GuidedAction.Builder(this.getActivity())
                .id(QUIT)
                .title("Fermer")
                .description("Fermer l'application")
                .hasNext(true)
                .build());
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {

        if (action.getId() == QUIT) {
            Context context = this.getActivity();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent recommendationIntent = new Intent(context, UpdateRecommendationsService.class);
            PendingIntent alarmIntent = PendingIntent.getService(context, 0, recommendationIntent, 0);

            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    INITIAL_DELAY,
                    AlarmManager.INTERVAL_HALF_HOUR,
                    alarmIntent);

            this.getActivity().startService(recommendationIntent);


            getActivity().finish();
        }
    }
}
