package org.loofer.weread.mvp.model.api.service;

import org.loofer.weread.mvp.model.entity.Gank;
import org.loofer.weread.mvp.model.entity.GankBaseJson;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import rx.Observable;

/**
 * 存放关于用户的一些api
 * Created by jess on 8/5/16 12:05
 * contact with jess.yan.effort@gmail.com
 */
public interface HomeService {

    String HEADER_API_VERSION = "Accept: application/vnd.github.v3+json";

//    @Headers({HEADER_API_VERSION})
    @Headers({ "X-LC-Id: 0azfScvBLCC9tAGRAwIhcC40",
            "X-LC-Key: gAuE93qAusvP8gk1VW8DtOUb",
            "Content-Type: application/json" })
    @GET("http://gank.io/api/data/{category}/{pageSize}/{page}")
    Observable<GankBaseJson<List<Gank>>> getGanks(@Path("category") String category, @Path("pageSize") int pageSize, @Path("page") int page);


}
