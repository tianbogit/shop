package api.trip.web.db;

import api.trip.web.db.Enums.TripToDb;
import api.trip.web.db.ProductDal.TripProductDal;
import api.trip.web.db.TicketDal.TripTicketDal;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.serviceproxy.ProxyHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * 创建TripDbVerticle
 * @author tianpo
 */
public class TripDbVerticle extends AbstractVerticle {
    private static final Logger LOGGER =  LoggerFactory.getLogger(TripDbVerticle.class);
    private AsyncSQLClient client;

    public static final String CONFIG_PRODUCT_QUEUE = "product.queue";
    public static final String CONFIG_TICKET_QUEUE="ticket.queue";

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        HashMap<TripToDb, String> sqlQueries = loadSqlQueries();
        //执行数据库
        JsonObject mySQLClientConfig = new JsonObject()
                .put("host", "101.200.154.139")
                .put("port", 3306)
                .put("maxPoolSize", 20)
                .put("username", "root")
                .put("password", "123456")
                .put("database", "trip");
        client = MySQLClient.createShared(vertx, mySQLClientConfig);
        //返回实现类参数
        Future productFuture = Future.future();
        TripProductDal.create(client, sqlQueries, ready -> {
            if (ready.succeeded()) {
                System.out.println("db success");
                ProxyHelper.registerService(TripProductDal.class, vertx, ready.result(), CONFIG_PRODUCT_QUEUE);
                productFuture.complete();
            } else {
                productFuture.fail(ready.cause());
            }
        });

        Future ticketFuture = Future.future();
        TripTicketDal.create(client, sqlQueries, ready -> {
            if (ready.succeeded()) {
                System.out.println("db success");
                ProxyHelper.registerService(TripTicketDal.class, vertx, ready.result(), CONFIG_TICKET_QUEUE);
                ticketFuture.complete();
            } else {
                ticketFuture.fail(ready.cause());
            }
        });
        CompositeFuture.all(productFuture,ticketFuture).setHandler(res->{
            if (res.succeeded()){
                startFuture.complete();
            }else {
                startFuture.fail(res.cause());
            }
        });
    }

    private HashMap<TripToDb, String> loadSqlQueries() throws IOException {
        //获取数据库文件信息
        InputStream queryInputStream = getClass().getResourceAsStream("/TripSql.properties");
        Properties properties = new Properties();
        try {
            //把获取的文件信息放入新建的文件对象
            properties.load(queryInputStream);
            queryInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<TripToDb, String> sqlQueries = new HashMap<>();

        //产品操作
        sqlQueries.put(TripToDb.Get_Product_Count,properties.getProperty("Get_Product_Count"));
        sqlQueries.put(TripToDb.Get_ProductsByPage,properties.getProperty("Get_ProductsByPage"));

        //机票操作
        sqlQueries.put(TripToDb.Get_Ticket_Count,properties.getProperty("Get_Ticket_Count"));
        sqlQueries.put(TripToDb.Get_TicketsByPage,properties.getProperty("Get_TicketsByPage"));
        return sqlQueries;
    }
}
