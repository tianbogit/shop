package api.trip.web.services;

import api.trip.web.db.ProductDal.TripProductDal;
import com.sun.javafx.scene.layout.region.Margins;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import scala.Int;

import java.util.logging.Logger;

/**
 * 产品service
 * @author tianpo
 */
public class TripProductService {
    private TripProductDal productDal;
    private Vertx vertx;

    public TripProductService(Vertx vertx,TripProductDal productDal){
        this.vertx = vertx;
        this.productDal = productDal;
    }

    //region 获取所有产品
    public void getProductByPage(RoutingContext routingContext) {
        JsonObject jParam=routingContext.getBodyAsJson();
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8");
        productDal.getProductByPage(jParam.getInteger("page"), jParam.getInteger("pagesize"),jParam.getInteger("ptype"), res->{
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
