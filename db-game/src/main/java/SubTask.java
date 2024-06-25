import com.dtarmory.base.BaseArmoryMsg;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SubTask extends BaseArmoryMsg {

    private Long parentId;

    private Map<String, Object> extendInfo = new HashMap<>();
}
