package base;

import lombok.Data;

import java.util.Date;

@Data
public abstract class BaseArmoryPO {

    private Long id;
    private Date createdAt;
    private Date updatedAt;
}
