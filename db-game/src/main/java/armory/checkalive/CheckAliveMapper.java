package armory.checkalive;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CheckAliveMapper {

    int tryDbConnection();
}
