package com.example.fuelfinder;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IApiStationService {
    @GET("records?select=cp%2Caddress%2Ccom_arm_name%2Cautomate_24_24%2Ctimetable%2Cfuel%2Cshortage%2Cupdate%2Cprice_gazole%2Cprice_sp95%2Cprice_sp98%2Cprice_gplc%2Cprice_e10%2Cprice_e85%2Cservices%2Cbrand%2Cname%2Cgeo_point")
    Call<apiItems> get20Station(@Query("limit") int limit,@Query("offset") int offset);

    //https://public.opendatasoft.com/api/explore/v2.1/catalog/datasets/prix-des-carburants-j-1/

}