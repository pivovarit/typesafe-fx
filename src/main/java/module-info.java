import org.jspecify.annotations.NullMarked;

@NullMarked
module com.pivovarit.typesafe.fx {
    exports com.pivovarit.typesafe.fx;
    exports com.pivovarit.typesafe.fx.currency;
    exports com.pivovarit.typesafe.fx.rate;
    exports com.pivovarit.typesafe.fx.analytics;

    requires static org.jspecify;
}
