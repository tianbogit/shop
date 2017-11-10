package api.trip.web.db.TicketDal;

import api.trip.web.db.Enums.TripToDb;
import api.trip.web.db.ProductDal.TripProductDal;
import api.trip.web.db.ProductDal.TripProductDalImp;
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
 * 机票接口
 * @author tianpo
 */
@ProxyGen
public interface TripTicketDal {
    static TripTicketDal create(AsyncSQLClient client, HashMap<TripToDb, String> sqlQueries, Handler<AsyncResult<TripTicketDal>> readyHandler) {
        return  new TripTicketDalImp(client,sqlQueries,readyHandler);
    }
    static TripTicketDal createProxy(Vertx vertx, String address) {
        return ProxyHelper.createProxy(TripTicketDal.class,vertx,address);
    }

    // 分页获取产品
    @Fluent
    TripTicketDal getTicketsByPage (int page, int pagesize,Handler<AsyncResult<JsonObject>> resultHandler);
}
