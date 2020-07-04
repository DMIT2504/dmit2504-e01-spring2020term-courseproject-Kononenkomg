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

    @FormUrlEncoded
    @POST("Lab02Post.jsp")
    Call<String> postArchivedItem(@Field("LIST_TITLE") String list_title,
                                  @Field("CONTENT") String content,
                                  @Field("COMPLETED_FLAG") int completed_flag,
                                  @Field("ALIAS") String alias,
                                  @Field("PASSWORD") String password,
                                  @Field("CREATED_DATE") String created_date);


}
