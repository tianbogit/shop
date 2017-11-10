package api.trip.web.db.ProductDal;

import api.trip.web.db.Enums.TripToDb;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.sql.SQLConnection;

import java.util.HashMap;
import java.util.List;

/**
 * 旅游产品service
 * @author tianpo
 */
public class TripProductDalImp implements TripProductDal {
    private static final Logger LOGGER =  LoggerFactory.getLogger(TripProductDalImp.class);

    private final HashMap<TripToDb, String> sqlQueries;
    private AsyncSQLClient client;

    TripProductDalImp(AsyncSQLClient client, HashMap<TripToDb, String> sqlQueries, Handler<AsyncResult<TripProductDal>> readyHandler) {
        this.client = client;
        this.sqlQueries = sqlQueries;
        readyHandler.handle(Future.succeededFuture(this));
    }

    //region 获取所有产品
    @Override
    public TripProductDal getProductByPage(int page, int pagesize, int ptype, Handler<AsyncResult<JsonObject>> resultHandler) {
        JsonArray jsonArray = new JsonArray().add(ptype);
        client.getConnection(res -> {
            if (res.succeeded()) {
                SQLConnection connection = res.result();
                connection.queryWithParams(sqlQueries.get(TripToDb.Get_Product_Count), jsonArray, countresult -> {
                    JsonObject response = new JsonObject();
                    if (countresult.succeeded()) {
                        List<JsonArray> results = countresult.result().getResults();
                        JsonObject jsonObject = new JsonObject();
                        Integer count = results.get(0).getInteger(0);
                        jsonObject.put("count", results);
                        if (count > 0) {
                            connection.queryWithParams(sqlQueries.get(TripToDb.Get_ProductsByPage), new JsonArray().add(ptype).add(page).add(pagesize), result -> {
                                connection.close();
                                List products = result.result().getRows();
                                jsonObject.put("products", products);
                                response.put("count", count).put("result", true).put("products", products);
                                resultHandler.handle(Future.succeededFuture(response));
                            });
                        } else {
                        }
                    } else {
                        System.out.println("查数据库失败");
                        resultHandler.handle(Future.failedFuture(res.cause()));
                    }
                });
            }else {
                LOGGER.error("连接数据库失败");
                System.out.println("连接数据库失败");
            }
        });
        return this;
    }
    //endregion
}
