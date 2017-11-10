package api.trip.web.http;

import api.trip.web.db.ProductDal.TripProductDal;
import api.trip.web.db.TicketDal.TripTicketDal;
import api.trip.web.services.TripProductService;
import api.trip.web.services.TripTicketService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * 创建httpserver，注册路由
 * @author tianpo
 */
public class HttpServerVerticle  extends AbstractVerticle {

    public static final String CONFIG_HTTP_SERVER_PORT = "8080";
    public static final String CONFIG_PRODUCT_QUEUE = "product.queue";
    public static final String CONFIG_TICKET_QUEUE="ticket.queue";

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);
    private TripProductDal productDal;
    private TripProductService productService;
    private TripTicketDal ticketDal;
    private TripTicketService ticketService;
    private  Router router;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        /*
        * 创建产品服务代理
        * */
        String productQueue = config().getString(CONFIG_PRODUCT_QUEUE, "product.queue");
        productDal = TripProductDal.createProxy(vertx, productQueue);
        productService = new TripProductService(vertx,productDal);

         /*
        * 创建机票代理
        * */
        String ticketQueue = config().getString(CONFIG_TICKET_QUEUE, "ticket.queue");
        ticketDal = TripTicketDal.createProxy(vertx, ticketQueue);
        ticketService = new TripTicketService(vertx,ticketDal);

        HttpServer server = vertx.createHttpServer();
        router = Router.router(vertx);
        Set<String> allowHeaders = new HashSet<>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");
        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.GET);
        allowMethods.add(HttpMethod.POST);
        allowMethods.add(HttpMethod.DELETE);
        allowMethods.add(HttpMethod.PATCH);
        router.route().handler(CorsHandler.create("*").allowedHeaders(allowHeaders).allowedMethods(allowMethods));
        router.route().handler(BodyHandler.create());
        router.get("/app/*").handler(StaticHandler.create().setCachingEnabled(false));

        //admin登录
        router.get("/api/login").handler(this::loginHandler);

        //分页获取产品
        router.post("/api/getProductByPage").handler(productService::getProductByPage);
        //分页获取机票
        router.post("/api/getTicketByPage").handler(ticketService::getTicketByPage);

        int portNumber = config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080);
        server.requestHandler(router::accept)
                .listen(portNumber, ar -> {
                    if (ar.succeeded()) {
                        LOGGER.info("HTTP server running on port " + portNumber);
                        System.out.println(portNumber);
                        startFuture.complete();
                    } else {
                        LOGGER.error("Could not start a HTTP server", ar.cause());
                        startFuture.fail(ar.cause());
                    }
                });
    }

    private void loginHandler(RoutingContext routingContext) {
        routingContext.response().end("{\"code\":200,\"msg\":\"登录成功\",\"user\":\"admin\"}");
    }
}
