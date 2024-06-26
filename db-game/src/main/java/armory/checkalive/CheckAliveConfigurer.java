package armory.checkalive;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.dtarmory.checkalive")
@ComponentScan({"com.dtarmory.checkalive","com.dtarmory.properties"})
public class CheckAliveConfigurer {
}
