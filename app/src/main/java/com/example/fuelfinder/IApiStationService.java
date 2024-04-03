package com.example.fuelfinder;

import com.example.fuelfinder.ui.map.apiItems;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface IApiStationService {
    @GET("records?select=cp%2Caddress%2Ccity%2Cautomate_24_24%2Ctimetable%2Cfuel%2Cshortage%2Cupdate%2Cprice_gazole%2Cprice_sp95%2Cprice_sp98%2Cprice_gplc%2Cprice_e10%2Cprice_e85%2Cservices%2Cbrand%2Cname%2Cgeo_point")
    Call<apiItems> getStation(@Query("limit") int limit, @Query("offset") int offset);

    @GET("records?select=cp%2Caddress%2Ccity%2Cautomate_24_24%2Ctimetable%2Cfuel%2Cshortage%2Cupdate%2Cprice_gazole%2Cprice_sp95%2Cprice_sp98%2Cprice_gplc%2Cprice_e10%2Cprice_e85%2Cservices%2Cbrand%2Cname%2Cgeo_point")
    Call<apiItems> getLocatedStation(@Query(value = "where", encoded = true) String withinDistanceQuery, @Query("limit") int limit, @Query("offset") int offset);

}
