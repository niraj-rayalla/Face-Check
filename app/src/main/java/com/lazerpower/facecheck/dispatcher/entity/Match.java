package com.lazerpower.facecheck.dispatcher.entity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;
import android.widget.SeekBar;

import com.lazerpower.facecheck.Log;
import com.lazerpower.facecheck.db.DatabaseUtils;
import com.lazerpower.facecheck.http.Parser;
import com.lazerpower.facecheck.models.Position;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Created by Niraj & Lazer on 4/6/2015.
 */
public class Match extends Entity {
    @Override
    public ContentValues parse(SQLiteDatabase db, JSONObject json, MergePolicy merge) throws JSONException {
        ContentValues contentValues = new ContentValues();
        new Parser(json, contentValues)
                .parseString("matchId", "id")
                .parseLong("matchCreation")
                .parseInt("matchDuration");

        contentValues.put("json", json.toString());

        long rowId = DatabaseUtils.upsert(
                db, "match", contentValues,
                "id", contentValues.getAsString("id"));

        return contentValues;
    }

    public static class MatchModel {
        private JSONObject mJson;

        private String mId;
        private Date mCreationTime;
        private int mMatchDurationInSeconds;
        private String mMapId;
        private Team[] mTeams;
        private Participant[] mParticipants;
        private Timeline mTimeline;

        public MatchModel(JSONObject json) {
            mJson = json;

            try {
                mId = json.getString("matchId");
                mCreationTime = new Date(json.getLong("matchCreation"));
                mMatchDurationInSeconds = json.getInt("matchDuration");
                mMapId = json.getString("mapId");

                //Teams
                JSONArray teamsJsonArray = json.getJSONArray("teams");
                mTeams = new Team[teamsJsonArray.length()];
                for (int i = 0; i < teamsJsonArray.length(); ++i) {
                    mTeams[i] = new Team(teamsJsonArray.getJSONObject(i));
                }

                //Participants
                JSONArray participantsJsonArray = json.getJSONArray("participants");
                mParticipants = new Participant[participantsJsonArray.length()];
                for (int i = 0; i < participantsJsonArray.length(); ++i) {
                    mParticipants[i] = new Participant(participantsJsonArray.getJSONObject(i));
                }

                //Timeline
                mTimeline = new Timeline(json.getJSONObject("timeline"));
            }
            catch (Exception e) {
                Log.d("Problem creating Match model", e);
            }
        }

        public String getId() {
            return mId;
        }

        public Date getCreationTime() {
            return mCreationTime;
        }

        public int getMatchDurationInSeconds() {
            return mMatchDurationInSeconds;
        }

        public String getMapId() {
            return mMapId;
        }

        public Team[] getTeams() {
            return mTeams;
        }

        public Participant[] getParticipants() {
            return mParticipants;
        }

        public Timeline getTimeline() {
            return mTimeline;
        }

        public Participant getMVP(){

            Team[] teams = getTeams();
            Team Winner = null;

            for(Team team : teams){
                if(team.mWinner)
                {
                    Winner = team;
                    break;
                }
            }

            Participant MVP = getTeamMVP(Winner.mTeamId);

            return MVP;
        }


        public Participant getTeamMVP(int team)
        {
            HashMap<Integer,Integer> mvpScore = new HashMap<>();
            Participant[] players = new Participant[5];
            int placement = 0;

            //Find players on certain team
            for(int i=0; i < 10; i++)
            {
                if(mParticipants[i].mTeamId == team)
                {
                    players[placement] = mParticipants[i];
                    mvpScore.put(mParticipants[i].mParticipantId,0);
                    placement++;
                }
            }



            PriorityQueue<Ranking> rankings = new PriorityQueue<>();


            // MVP KDA ranking
            for (int i = 0; i < 5; i++ ) {
                rankings.add(new Ranking(players[i].mParticipantId, players[i].mStats.mKDA));
            }
            addMVPScore(mvpScore, rankings, new int[]{5,3,1});
            rankings.clear();

            //MVP KILLS
            for (int i = 0; i < 5; i++ ) {
                rankings.add(new Ranking(players[i].mParticipantId, players[i].mStats.mKills));
            }
            addMVPScore(mvpScore, rankings, new int[]{5,3,1});
            rankings.clear();

            //MVP Death
            for (int i = 0; i < 5; i++ ) {
                rankings.add(new Ranking(players[i].mParticipantId, players[i].mStats.mDeaths));
            }
            addMVPScore(mvpScore, rankings, new int[]{-3,-2,-1});
            rankings.clear();

            //MVP Assist
            for (int i = 0; i < 5; i++ ) {
                rankings.add(new Ranking(players[i].mParticipantId, players[i].mStats.mAssists));
            }
            addMVPScore(mvpScore, rankings, new int[]{5,3,1});
            rankings.clear();

            //Total Damaged to Champ
            for (int i = 0; i < 5; i++ ) {
                rankings.add(new Ranking(players[i].mParticipantId, players[i].mStats.mTotalDamChamp));
            }
            addMVPScore(mvpScore, rankings, new int[]{3,2,1});
            rankings.clear();

            //Total Damaged taken
            for (int i = 0; i < 5; i++ ) {
                rankings.add(new Ranking(players[i].mParticipantId, players[i].mStats.mTank));
            }
            addMVPScore(mvpScore, rankings, new int[]{3,1});
            rankings.clear();

            //MinionKills
            for (int i = 0; i < 5; i++ ) {
                rankings.add(new Ranking(players[i].mParticipantId, players[i].mStats.mMinionKill));
            }
            addMVPScore(mvpScore, rankings, new int[]{1});
            rankings.clear();

            //Heal
            for (int i = 0; i < 5; i++ ) {
                rankings.add(new Ranking(players[i].mParticipantId, players[i].mStats.mTotalHeal));
            }
            addMVPScore(mvpScore, rankings, new int[]{3,1});
            rankings.clear();

            //CC
            for (int i = 0; i < 5; i++ ) {
                rankings.add(new Ranking(players[i].mParticipantId, players[i].mStats.mCC));
            }
            addMVPScore(mvpScore, rankings, new int[]{1});
            rankings.clear();

            for (int i = 0; i < 5; i++ ) {
                rankings.add(new Ranking(players[i].mParticipantId, players[i].mStats.mWardPlaced));
            }
            addMVPScore(mvpScore, rankings, new int[]{3,1});
            rankings.clear();

            //Ward Kill
            for (int i = 0; i < 5; i++ ) {
                rankings.add(new Ranking(players[i].mParticipantId, players[i].mStats.mWardKilled));
            }
            addMVPScore(mvpScore, rankings, new int[]{1});
            rankings.clear();

            Set<Integer> keys = mvpScore.keySet();
            Participant MVP = null;
            Integer mvpId = null;
            for (int key : keys )
            {
                if(mvpId==null) {
                    mvpId = key;
                }
                else if(mvpScore.get(key)>mvpScore.get(mvpId))
                {
                    mvpId = key;
                }
            }

            for (int i = 0; i < players.length; i++){
                if (players[i].mParticipantId == mvpId){
                    MVP = players[i];
                }

            }

            return MVP;
        }

        public void addMVPScore(HashMap<Integer,Integer> mvpScores, PriorityQueue<Ranking> ranks,
                           int[] points){

            int i = 0;
            Ranking currentRanking = ranks.poll();
            while(currentRanking != null && i<points.length) {
                mvpScores.put(currentRanking.first,
                        mvpScores.get(currentRanking.first) + points[i]);
                currentRanking = ranks.poll();
                i++;
            }
        }

        public class Ranking extends Pair<Integer, Float> implements Comparable<Ranking> {

            public Ranking(int id,float score){
                super(id, score);
            }

            @Override
            public int compareTo(Ranking another) {

                if(this.second < another.second)
                {
                    return 1;
                }
                else if(this.second > another.second)
                {
                    return 0;
                }
                else
                    return -1;
            }
        }

        //
        //Sub-models
        //

        public static class Team {
            public final int mTeamId;
            public final boolean mWinner;
            public final boolean mFirstBlood;
            public final boolean mFirstTower;
            public final boolean mFirstInhibitor;
            public final boolean mFirstBaron;
            public final boolean mFirstDragon;
            public final int mTowerKills;
            public final int mInhibitorKills;
            public final int mBaronKills;
            public final int mDragonKills;
            public final int mVilemawKills;
            public final int mDominionVictoryScore;

            public final Ban[] mBans;

            public Team(JSONObject teamJsonObject) throws JSONException {
                mTeamId = teamJsonObject.getInt("teamId");
                mWinner = teamJsonObject.getBoolean("winner");
                mFirstBlood = teamJsonObject.getBoolean("firstBlood");
                mFirstTower = teamJsonObject.getBoolean("firstTower");
                mFirstInhibitor = teamJsonObject.getBoolean("firstInhibitor");
                mFirstBaron = teamJsonObject.getBoolean("firstBaron");
                mFirstDragon = teamJsonObject.getBoolean("firstDragon");
                mTowerKills = teamJsonObject.getInt("towerKills");
                mInhibitorKills = teamJsonObject.getInt("inhibitorKills");
                mBaronKills = teamJsonObject.getInt("baronKills");
                mDragonKills = teamJsonObject.getInt("dragonKills");
                mVilemawKills = teamJsonObject.getInt("vilemawKills");
                mDominionVictoryScore = teamJsonObject.getInt("dominionVictoryScore");

                JSONArray bansJsonArray = teamJsonObject.getJSONArray("bans");
                mBans = new Ban[bansJsonArray.length()];
                for (int i = 0; i < bansJsonArray.length(); ++i) {
                    mBans[i] = new Ban(bansJsonArray.getJSONObject(i));
                }
            }
        }

        public static class Ban {
            public final String mChampionId;
            public final int mPickTurn;

            public Ban(JSONObject banJsonObject) throws JSONException {
                mChampionId = banJsonObject.getString("championId");
                mPickTurn = banJsonObject.getInt("pickTurn");
            }
        }

        public static class Participant {
            public final int mTeamId;
            public final int mParticipantId;
            public final String mSpell1Id;
            public final String mSpell2Id;
            public final String mChampionId;
            public final String mHighestAchievedSeasonTier;
            public final ParticipantStats mStats;

            public Participant(JSONObject jsonObject) throws JSONException {
                mTeamId = jsonObject.getInt("teamId");
                mParticipantId = jsonObject.getInt("participantId");
                mSpell1Id = jsonObject.getString("spell1Id");
                mSpell2Id = jsonObject.getString("spell2Id");
                mChampionId = jsonObject.getString("championId");
                mHighestAchievedSeasonTier = jsonObject.getString("highestAchievedSeasonTier");

                mStats = new ParticipantStats(jsonObject.getJSONObject("stats"));
            }
        }

        public static class ParticipantStats {
            public final boolean mWinner;
            public final int mKills;
            public final int mKillSpree;
            public final int mDeaths;
            public final int mAssists;
            public final int mTotalDamChamp;
            public final int mTotalHeal;
            public final int mTank;
            public final int mMinionKill;
            public final int mJungleKill;
            public final int mGoldEarn;
            public final int mGoldSpent;
            public final int mWardPlaced;
            public final int mWardKilled;
            public final int mCC;
            public final float mKDA;

            public ParticipantStats(JSONObject jsonObject) throws JSONException {
                mWinner = jsonObject.getBoolean("winner");
                mKills = jsonObject.getInt("kills");
                mKillSpree = jsonObject.getInt("largestKillingSpree");
                mDeaths = jsonObject.getInt("deaths");
                mAssists = jsonObject.getInt("assists");
                mTotalDamChamp = jsonObject.getInt("totalDamageDealtToChampions");
                mTotalHeal = jsonObject.getInt("totalHeal");
                mTank = jsonObject.getInt("totalDamageTaken");
                mMinionKill = jsonObject.getInt("minionsKilled");
                mJungleKill = jsonObject.getInt("neutralMinionsKilled");
                mGoldEarn = jsonObject.getInt("goldEarned");
                mGoldSpent = jsonObject.getInt("goldSpent");
                mWardPlaced = jsonObject.getInt("wardsPlaced");
                mWardKilled = jsonObject.getInt("wardsKilled");
                mCC = jsonObject.getInt("totalTimeCrowdControlDealt");

                if(mDeaths == 0){
                    mKDA = 100;
                }
                else {
                    mKDA = (mKills + mAssists)/mDeaths;
                }

            }

        }

        public static class Timeline {
            public final int mFrameIntervalInMs;
            public final TimelineFrame[] mFrames;

            public Timeline(JSONObject timelineJsonObject) throws JSONException {
                mFrameIntervalInMs = timelineJsonObject.getInt("frameInterval");

                JSONArray framesJsonArray = timelineJsonObject.getJSONArray("frames");
                mFrames = new TimelineFrame[framesJsonArray.length()];
                for (int i = 0; i < framesJsonArray.length(); ++i) {
                    mFrames[i] = new TimelineFrame(framesJsonArray.getJSONObject(i));
                }
            }
        }

        public static class TimelineFrame {
            public final int mTimestamp;
            public final HashMap<Integer, ParticipantFrame> mParticipantFrames;
            public final ParticipantFrame[] mParticipantFramesArray;
            public final Event[] mEvents;

            public TimelineFrame(JSONObject timelineFrameJsonObject) throws JSONException {
                mTimestamp = timelineFrameJsonObject.getInt("timestamp");

                JSONObject participantFramesJsonObject = timelineFrameJsonObject.getJSONObject("participantFrames");
                Iterator<String> participantFramesIterator = participantFramesJsonObject.keys();
                mParticipantFramesArray = new ParticipantFrame[participantFramesJsonObject.length()];
                mParticipantFrames = new HashMap<>();
                int j = 0;
                while (participantFramesIterator.hasNext()){
                    String participantFrameKey = participantFramesIterator.next();
                    ParticipantFrame participantFrame = new ParticipantFrame(participantFramesJsonObject.getJSONObject(participantFrameKey));
                    mParticipantFrames.put(participantFrame.mId, participantFrame);

                    mParticipantFramesArray[j] = participantFrame;
                    ++j;
                }

                if (timelineFrameJsonObject.has("events")) {
                    JSONArray eventsJsonArray = timelineFrameJsonObject.getJSONArray("events");
                    mEvents = new Event[eventsJsonArray.length()];
                    for (int i = 0; i < eventsJsonArray.length(); ++i) {
                        mEvents[i] = Event.getEvent(eventsJsonArray.getJSONObject(i));
                    }
                }
                else {
                    mEvents = null;
                }
            }
        }

        public static class ParticipantFrame {
            public final int mId;
            public final Position mPosition;
            public final int mCurrentGold;
            public final int mTotalGold;
            public final int mLevel;
            public final int mXp;
            public final int mMinionsKilled;
            public final int mJungleMinionsKilled;
            public final int mDominionScore;
            public final int mTeamScore;

            public ParticipantFrame(JSONObject participantFrameJsonObject) throws JSONException {
                mId = participantFrameJsonObject.getInt("participantId");
                if (participantFrameJsonObject.has("position")) {
                    mPosition = new Position(participantFrameJsonObject.getJSONObject("position"));
                }
                else {
                    mPosition = null;
                }
                mCurrentGold = participantFrameJsonObject.getInt("currentGold");
                mTotalGold = participantFrameJsonObject.getInt("totalGold");
                mLevel = participantFrameJsonObject.getInt("level");
                mXp = participantFrameJsonObject.getInt("xp");
                mMinionsKilled = participantFrameJsonObject.getInt("minionsKilled");
                mJungleMinionsKilled = participantFrameJsonObject.getInt("jungleMinionsKilled");
                mDominionScore = participantFrameJsonObject.getInt("dominionScore");
                mTeamScore = participantFrameJsonObject.getInt("teamScore");
            }
        }

        public abstract static class Event {
            public static final String EVENT_TYPE_CHAMPION_KILL = "CHAMPION_KILL";
            public static final String EVENT_TYPE_BUILDING_KILL = "BUILDING_KILL";
            public static final String EVENT_TYPE_ELITE_MONSTER_KILL = "ELITE_MONSTER_KILL";
            public static final String EVENT_TYPE_ITEM_PURCHASED = "ITEM_PURCHASED";
            public static final String EVENT_TYPE_ITEM_DESTROYED = "ITEM_DESTROYED";
            public static final String EVENT_TYPE_ITEM_SOLD = "ITEM_SOLD";
            public static final String EVENT_TYPE_ITEM_UNDO = "ITEM_UNDO";

            public final String mEventType;
            public final int mTimestamp;

            private Event(JSONObject eventJsonObject) throws JSONException {
                mEventType = eventJsonObject.getString("eventType");
                mTimestamp = eventJsonObject.getInt("timestamp");
            }

            public static Event getEvent(JSONObject eventJsonObject) throws JSONException {
                String eventType = eventJsonObject.getString("eventType");
                if (eventType.equals(EVENT_TYPE_CHAMPION_KILL)) {
                    return new ChampionKillEvent(eventJsonObject);
                }
                else if (eventType.equals(EVENT_TYPE_BUILDING_KILL)) {
                    return new BuildingKillEvent(eventJsonObject);
                }
                else if (eventType.equals(EVENT_TYPE_ELITE_MONSTER_KILL)) {
                    return new EliteMonsterKillEvent(eventJsonObject);
                }
                else if (eventType.equals(EVENT_TYPE_ITEM_PURCHASED)
                         || eventType.equals(EVENT_TYPE_ITEM_DESTROYED)
                         || eventType.equals(EVENT_TYPE_ITEM_SOLD)) {
                    return new ItemEvent(eventJsonObject);
                }
                else if (eventType.equals(EVENT_TYPE_ITEM_UNDO)) {
                    return new ItemUndoEvent(eventJsonObject);
                }

                return null;
            }
        }

        public static class ChampionKillEvent extends Event {
            public final int mKilledId;
            public final int mVictimId;
            public final Position mPosition;

            public ChampionKillEvent(JSONObject eventJsonObject) throws JSONException {
                super(eventJsonObject);

                mKilledId = eventJsonObject.getInt("killerId");
                mVictimId = eventJsonObject.getInt("victimId");
                mPosition = new Position(eventJsonObject.getJSONObject("position"));
            }
        }

        public static class BuildingKillEvent extends Event {
            public final int mKilledId;
            public final int mTeamId;
            public final String mLaneType;
            public final String mBuildingType;
            public final String mTowerType;
            public final Position mPosition;

            public BuildingKillEvent(JSONObject eventJsonObject) throws JSONException {
                super(eventJsonObject);

                mKilledId = eventJsonObject.getInt("killerId");
                mTeamId = eventJsonObject.getInt("teamId");
                mLaneType = eventJsonObject.getString("laneType");
                mBuildingType = eventJsonObject.getString("buildingType");
                mTowerType = eventJsonObject.getString("towerType");
                mPosition = new Position(eventJsonObject.getJSONObject("position"));
            }
        }

        public static class EliteMonsterKillEvent extends Event {
            public final int mKilledId;
            public final String mMonsterType;
            public final Position mPosition;

            public EliteMonsterKillEvent(JSONObject eventJsonObject) throws JSONException {
                super(eventJsonObject);

                mKilledId = eventJsonObject.getInt("killerId");
                mMonsterType = eventJsonObject.getString("monsterType");
                mPosition = new Position(eventJsonObject.getJSONObject("position"));
            }
        }

        /**
         * Basic ItemEvent will handle all types of item events except for the item undo event.
         * For that, ItemUndoEvent should be used.
         */
        public static class ItemEvent extends Event {
            public final int mItemId;
            public final int mParticipantId;

            public ItemEvent(JSONObject eventJsonObject) throws JSONException {
                super(eventJsonObject);

                int itemId = 0;
                try {
                    itemId = eventJsonObject.getInt("itemId");
                }
                catch (Exception e) {
                    //Can be here if the event is an ItemUndoEvent
                }
                mItemId = itemId;
                mParticipantId = eventJsonObject.getInt("participantId");
            }
        }

        public static class ItemUndoEvent extends ItemEvent {
            public final int mItemBefore;
            public final int mItemAfter;

            public ItemUndoEvent(JSONObject eventJsonObject) throws JSONException {
                super(eventJsonObject);

                mItemBefore = eventJsonObject.getInt("itemBefore");
                mItemAfter = eventJsonObject.getInt("itemAfter");
            }
        }
    }
}
