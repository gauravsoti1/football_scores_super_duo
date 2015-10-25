package barqsoft.footballscores;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by hp on 10/24/2015.
 */
public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    public static final String LOG_TAG = DetailActivity.class.getSimpleName();
    public static final int DETAIL_SCORES_LOADER = 123;
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 0;
    public static final int COL_MATCH_ID = 8;
    public static final int COL_MATCHTIME = 2;
    double match_id;
    private ImageView mHomeCrest;
    private TextView mHomeName;
    private TextView mScoreTextview;
    private TextView mDataTextview;
    private ImageView mAwayCrest;
    private TextView mAwayName;
    private TextView mLeagueTextview;
    private TextView mMatchdayTextview;
    private Button mShareButton;
    private static final String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        match_id = getIntent().getDoubleExtra("id", 0);
        getLoaderManager().initLoader(DETAIL_SCORES_LOADER, null, this);
        findViews();
    }

    private void findViews() {
        mHomeCrest = (ImageView)findViewById( R.id.home_crest );
        mHomeName = (TextView)findViewById( R.id.home_name );
        mScoreTextview = (TextView)findViewById( R.id.score_textview );
        mDataTextview = (TextView)findViewById( R.id.data_textview );
        mAwayCrest = (ImageView)findViewById( R.id.away_crest );
        mAwayName = (TextView)findViewById( R.id.away_name );
        mLeagueTextview = (TextView)findViewById( R.id.league_textview );
        mMatchdayTextview = (TextView)findViewById( R.id.matchday_textview );
        mShareButton = (Button)findViewById( R.id.share_button );

        mShareButton.setOnClickListener(this);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri ss = DatabaseContract.scores_table.buildScoreWithId();
        return new CursorLoader(DetailActivity.this,
                DatabaseContract.scores_table.buildScoreWithId(),
                null,
                null,
                new String[]{Double.toString(match_id)},
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        data.moveToFirst();
        final String home_team = data.getString(COL_HOME);
        final int match_day = data.getInt(COL_MATCHDAY);
        final String away_team = data.getString(COL_AWAY);
        final int away_goals = data.getInt(COL_AWAY_GOALS);
        final int home_goals = data.getInt(COL_HOME_GOALS);
        final String date = data.getString(COL_DATE);
        final int league = data.getInt(COL_LEAGUE);
        final String match_time = data.getString(COL_MATCHTIME);

        mAwayCrest.setImageResource(Utilies.getTeamCrestByTeamName(away_team));
        mHomeCrest.setImageResource(Utilies.getTeamCrestByTeamName(home_team));
        mScoreTextview.setText(Utilies.getScores(home_goals, away_goals));
        mMatchdayTextview.setText(Utilies.getMatchDay(match_day, league));
        mMatchdayTextview.setContentDescription("Match Day is " + Utilies.getMatchDay(match_day, league) );
        mAwayName.setText(away_team);
        mHomeName.setText(home_team);
        mDataTextview.setText(match_time);
        Log.d(LOG_TAG,"League = " + league + " " + Utilies.getLeague(league));
        mLeagueTextview.setText(Utilies.getLeague(league));


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View v) {
        if ( v == mShareButton ) {
            startActivity(createShareForecastIntent(mHomeName.getText()+" "
                    +mScoreTextview.getText()+" "+mAwayName.getText() + " "));
        }
    }

    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }
}
