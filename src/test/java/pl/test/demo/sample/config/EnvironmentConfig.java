package pl.test.demo.sample.config;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Mutable;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({ "classpath:env.${active.environment}.properties"})
public interface EnvironmentConfig extends Accessible, Mutable {

    @Key("env.current")
    String currentEnv();

    @Key("my.property")
    int myProperty();

}
