package com.example.fuelfinder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class apiItems implements Serializable
{
    @SerializedName("total_count")
    @Expose
    public Integer totalCount;
    @SerializedName("results")
    @Expose
    public List<Station> stations = new ArrayList<Station>();
    private final static long serialVersionUID = 5205227008753705009L;

    /**
     * No args constructor for use in serialization
     *
     */
    public apiItems() {
    }

    /**
     *
     * @param totalCount
     * @param stations
     */
    public apiItems(Integer totalCount, List<Station> stations) {
        super();
        this.totalCount = totalCount;
        this.stations = stations;
    }

    public apiItems withTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public apiItems withResults(List<Station> stations) {
        this.stations = stations;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(apiItems.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("totalCount");
        sb.append('=');
        sb.append(((this.totalCount == null)?"<null>":this.totalCount));
        sb.append(',');
        sb.append("stations");
        sb.append('=');
        sb.append(((this.stations == null)?"<null>":this.stations));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}