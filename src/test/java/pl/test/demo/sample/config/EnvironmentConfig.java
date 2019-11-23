package pl.test.demo.sample.config;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Mutable;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"classpath:environment.properties"})
public interface EnvironmentConfig extends Accessible, Mutable {

    @Key("db.user")
    String getUser();

    @Key("db.pass")
    String getPass();

    @Key("db.host")
    String getHost();

    @Key("db.port")
    int getPort();

}
