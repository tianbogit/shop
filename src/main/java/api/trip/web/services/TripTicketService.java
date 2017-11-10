package api.trip.web.services;

import api.trip.web.db.ProductDal.TripProductDal;
import api.trip.web.db.TicketDal.TripTicketDal;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * 机票service
 * @author tianpo
 */
public class TripTicketService {
    private TripTicketDal tripTicketDal;
    private Vertx vertx;

    public TripTicketService(Vertx vertx,TripTicketDal tripTicketDal){
        this.vertx = vertx;
        this.tripTicketDal = tripTicketDal;
    }

    //region 获取所有机票
    public void getTicketByPage(RoutingContext routingContext) {
        JsonObject jParam=routingContext.getBodyAsJson();
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8");
        tripTicketDal.getTicketsByPage(jParam.getInteger("page"), jParam.getInteger("pagesize"), res->{
            if (res.succeeded()){
                JsonObject result = res.result();
                routingContext.response().end(result.encode());
            }else {
                routingContext.response().end("-1");
            }
        });

    }
    //endregion
}
