import org.jspecify.annotations.NullMarked;

@NullMarked
module com.pivovarit.typesafe.fx {
    exports com.pivovarit.typesafe.fx;
    exports com.pivovarit.typesafe.fx.currency;

    requires static org.jspecify;
}
