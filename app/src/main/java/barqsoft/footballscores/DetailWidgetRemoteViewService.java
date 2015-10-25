package barqsoft.footballscores;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hp on 10/24/2015.
 */
public class DetailWidgetRemoteViewService extends RemoteViewsService {

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

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new RemoteViewsFactory() {

            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {

                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");

                String dateForQuery = mformat.format(date);

                data = getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                        null,
                        null,
                        new String[]{dateForQuery},
                        null);
                Binder.restoreCallingIdentity(identityToken);

            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }

            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),R.layout.widget_scores_list_item);
                views.setTextViewText(R.id.home_name, data.getString(COL_HOME));
                views.setTextViewText(R.id.away_name, data.getString(COL_AWAY));
                views.setTextViewText(R.id.score_textview, Utilies.getScores(data.getInt(COL_HOME_GOALS), data.getInt(COL_AWAY_GOALS)));
                views.setTextViewText(R.id.data_textview, data.getString(COL_MATCHTIME));
                final double match_id = data.getDouble(COL_MATCH_ID);
                views.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(
                        data.getString(COL_HOME)));
                views.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(
                        data.getString(COL_AWAY)));
                Intent fillIntent = new Intent(getApplicationContext(),DetailActivity.class);
                fillIntent.putExtra("id",match_id);
                views.setOnClickFillInIntent(R.id.football_scores_widget_list_item, fillIntent);
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    views.setContentDescription(R.id.football_scores_widget_list_item,"List Item " + (data.getPosition()+1) + " .Click for details" );
                }*/

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(),R.layout.widget_scores_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }
        };
    }


}
