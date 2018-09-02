package au.com.pixelforcesystems.yeahoh.rxjava;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    @GET("citiesJSON")
    Single<CityResponse> queryGeonames(@Query("north") double north, @Query("south") double south,
                                       @Query("east") double east, @Query("west") double west, @Query("lang") String lang);
}
