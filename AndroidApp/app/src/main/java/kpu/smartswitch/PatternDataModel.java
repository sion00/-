package kpu.smartswitch;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PatternDataModel {

    public static class PatternInfoList {
        @SerializedName("PatternInfoList")
        public ArrayList<PatternInfo> PatternInfoList;
    }

    public static class PatternInfo {

        @SerializedName("mTime")
        public String time;
        @SerializedName("mMoveTime")
        public double moveTime;
        @SerializedName("mSample")
        public int sample;
    }

}
