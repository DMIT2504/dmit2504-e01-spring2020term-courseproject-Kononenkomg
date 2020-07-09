package ca.nait.dmit2504.courseproject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface Connector {
    @GET()
    Call<String> StockMetrics(@Url String url);

}
