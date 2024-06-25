import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.dtarmory.threelayerschedule")
@ComponentScan({"com.dtarmory.threelayerschedule"})
public class ThreeLayerScheduleTaskConfigurer {
}
