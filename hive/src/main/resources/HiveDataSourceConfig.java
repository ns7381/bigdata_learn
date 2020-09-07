import com.zaxxer.hikari.HikariDataSource;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class HiveDataSourceConfig {

    @Value("${app.hive.url}")
    private String dbUrl;
    @Value("${app.hive.username}")
    private String username;
    @Value("${app.hive.password}")
    private String password;
    @Value(value = "${kerberos.enable}")
    private Boolean kerberosEnable;
    @Value(value = "${kerberos.principal}")
    private String kerberosPrincipal;
    @Value(value = "${kerberos.keyPath}")
    private String kerberosKeyPath;
    @Value(value = "${kerberos.confPath}")
    private String kerberosConfPath;


    private static final String  DB_DRIVER_CLASS = "org.apache.hive.jdbc.HiveDriver";
    private static final Integer DB_MIN_IDLE = 3;
    private static final Integer DB_MAX_ACTIVE = 20;
    private static final Integer DB_CONNECTION_TIMEOUT = 60000;
    private static final Integer DB_MAX_LIFETIME = 200000;

    /**
     * execute every 10h
     */
    @Scheduled(fixedRate = 1000 * 60 * 60 * 10)
    public void login() {
        if (!kerberosEnable) {
            return;
        }
        System.setProperty("java.security.krb5.conf", kerberosConfPath);
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set(CommonConfigurationKeys.HADOOP_SECURITY_AUTHORIZATION, "true");
        conf.set(CommonConfigurationKeys.HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        UserGroupInformation.setConfiguration(conf);
        try {
            UserGroupInformation.loginUserFromKeytab(kerberosPrincipal, kerberosKeyPath);
        } catch (IOException e) {
            throw new RTRuntimeException(e);
        }
    }

    @Bean(name = "hiveDataSource")
    public DataSource dataSource() {
        login();
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dbUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(DB_DRIVER_CLASS);
        dataSource.setReadOnly(false);

        dataSource.setMaximumPoolSize(DB_MAX_ACTIVE);
        dataSource.setMinimumIdle(DB_MIN_IDLE);
        dataSource.setMaxLifetime(DB_MAX_LIFETIME);
        dataSource.setConnectionTimeout(DB_CONNECTION_TIMEOUT);
        return dataSource;
    }
    @Bean(name = "hiveTemplate")
    public JdbcTemplate hiveJdbcTemplate(@Qualifier("hiveDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
