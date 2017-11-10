package api.trip.web.db.TicketDal;

import api.trip.web.db.Enums.TripToDb;
import api.trip.web.db.ProductDal.TripProductDal;
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
 * 机票service
 * @author tianpo
 */
public class TripTicketDalImp implements TripTicketDal {
    private static final Logger LOGGER =  LoggerFactory.getLogger(TripTicketDalImp.class);

    private final HashMap<TripToDb, String> sqlQueries;
    private AsyncSQLClient client;

    TripTicketDalImp(AsyncSQLClient client, HashMap<TripToDb, String> sqlQueries, Handler<AsyncResult<TripTicketDal>> readyHandler) {
        this.client = client;
        this.sqlQueries = sqlQueries;
        readyHandler.handle(Future.succeededFuture(this));
    }

    // 分页获取机票数据
    @Override
    public TripTicketDal getTicketsByPage(int page, int pagesize, Handler<AsyncResult<JsonObject>> resultHandler) {
        client.getConnection(res -> {
            if (res.succeeded()) {
                SQLConnection connection = res.result();
                connection.query(sqlQueries.get(TripToDb.Get_Ticket_Count), countresult -> {
                    JsonObject response = new JsonObject();
                    if (countresult.succeeded()) {
                        List<JsonArray> results = countresult.result().getResults();
                        JsonObject jsonObject = new JsonObject();
                        Integer count = results.get(0).getInteger(0);
                        jsonObject.put("count", results);
                        if (count > 0) {
                            connection.queryWithParams(sqlQueries.get(TripToDb.Get_TicketsByPage), new JsonArray().add(page).add(pagesize), result -> {
                                connection.close();
                                List products = result.result().getRows();
                                jsonObject.put("tickets", products);
                                response.put("count", count).put("result", true).put("tickets", products);
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
}
