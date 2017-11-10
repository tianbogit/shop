package api.trip.web.db.ProductDal;

import api.trip.web.db.Enums.TripToDb;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.serviceproxy.ProxyHelper;

import java.util.HashMap;

/**
 * 旅游产品接口
 * @author tianpo
 */
@ProxyGen
public interface TripProductDal {

    static TripProductDal create(AsyncSQLClient client, HashMap<TripToDb, String> sqlQueries, Handler<AsyncResult<TripProductDal>> readyHandler) {
        return  new TripProductDalImp(client,sqlQueries,readyHandler);
    }
    static TripProductDal createProxy(Vertx vertx, String address) {
        return ProxyHelper.createProxy(TripProductDal.class,vertx,address);
    }

    //分页获取产品
    @Fluent
    TripProductDal getProductByPage (int page, int pagesize, int ptype,Handler<AsyncResult<JsonObject>> resultHandler);

}
