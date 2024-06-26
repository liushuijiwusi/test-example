package armory.base;

public enum ArmoryCommonStatus {

    INIT(1, "初始化"),

    PROCESSING(2, "处理中"),

    SUCCESS(3, "成功"),

    FAIL(4, "失败");

    private int status;
    private String desc;

    ArmoryCommonStatus(int status, String desc) {

        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
