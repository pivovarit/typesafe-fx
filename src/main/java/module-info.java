import org.jspecify.annotations.NullMarked;

@NullMarked
module com.pivovarit.money {
    exports com.pivovarit.money;
    exports com.pivovarit.money.currency;
    exports com.pivovarit.money.rate;
    exports com.pivovarit.money.math;

    requires static org.jspecify;
}
