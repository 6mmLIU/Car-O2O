# Car-O2O 汽车 O2O 管理平台

## 项目简介
Car-O2O 基于 [RuoYi](https://gitee.com/y_project/RuoYi-Vue) 框架二次开发，版本为 `3.8.3`，面向汽车后市场的线上到线下业务场景，采用 **Spring Boot** + **Vue** 的前后端分离架构，提供服务项、预约、结算等业务功能，并集成系统管理、定时任务、代码生成等基础能力，帮助企业快速构建汽车服务类应用。

## 功能模块
- **系统管理：** 用户、角色、部门、菜单、岗位、字典、参数、通知公告等基础功能
- **业务管理：** 服务项管理、套餐审核、预约管理、结算单管理等汽车业务流程
- **运维支持：** 登录/操作日志、服务监控、缓存监控、定时任务管理等运维功能
- **辅助工具：** 代码生成、在线构建器、Swagger3 接口文档

## 项目结构
```text
Car-O2O/
├── car-o2o-admin            # Spring Boot 启动模块
├── car-o2o-business         # 业务逻辑（Controller、Service、Mapper）
├── car-o2o-business-domain  # 业务实体、DTO、VO
├── car-o2o-common           # 通用工具
├── car-o2o-framework        # 框架核心
├── car-o2o-generator        # 代码生成
├── car-o2o-quartz           # 定时任务
├── car-o2o-system           # 系统管理
├── car-o2o-ui               # 前端 Vue 项目
├── sql                      # 初始化数据库脚本
└── doc                      # 说明文档
```

## 技术栈
- **后端：** Spring Boot、Spring Security、MyBatis、Redis、Quartz、Jackson、Fastjson2
- **前端：** Vue、Element UI、Axios、Vue Router、Vuex
- **数据库：** MySQL + Druid 连接池
- **其他：** Swagger3、PageHelper、Velocity 模板引擎

## 快速开始
### 环境准备
- JDK 1.8+
- Maven 3.5+
- Node.js 8.9+ 与 npm
- MySQL 5.7+

### 初始化数据库
```bash
mysql -u root -p < sql/ry_20220625.sql
mysql -u root -p < sql/quartz.sql
```

### 启动后端
```bash
# 打包整个工程
mvn clean package -DskipTests
# 运行后台服务
java -jar car-o2o-admin/target/car-o2o-admin.jar
```
> 也可以使用 `ry.sh` 或 `ry.bat` 脚本进行启停。

### 启动前端
```bash
cd car-o2o-ui
npm install --registry=https://registry.npmmirror.com
npm run dev
```
访问 `http://localhost:80` 即可体验。

## 核心代码示例
### 服务项控制器
```java
/**
 * 服务项控制器：提供服务项的增删改查接口
 */
@RestController
@RequestMapping("/business/serviceitem")
public class BusServiceItemController extends BaseController {

    @Autowired
    private IBusServiceItemService itemService; // 注入服务项业务对象

    /**
     * 分页查询服务项列表
     *
     * @param busServiceItem 查询条件（根据实体字段自动封装）
     * @return 表格数据对象，包含分页信息和数据列表
     */
    @GetMapping("/list")
    public TableDataInfo list(BusServiceItem busServiceItem) {
        startPage(); // 启动分页
        List<BusServiceItem> list = itemService.selectBusServiceItemList(busServiceItem);
        return getDataTable(list); // 封装为前端表格数据
    }
}
```

### 服务项实体
```java
@Getter
@Setter
public class BusServiceItem extends BaseEntity {

    private static final long serialVersionUID = 1L; // 序列化ID

    // 是否为套餐常量
    public static final Integer CARPACKAGE_NO = 0;  // 不是套餐
    public static final Integer CARPACKAGE_YES = 1; // 是套餐

    // 审核状态常量
    public static final Integer AUDITSTATUS_INIT = 0;       // 初始化
    public static final Integer AUDITSTATUS_AUDITING = 1;   // 审核中
    public static final Integer AUDITSTATUS_APPROVED = 2;   // 审核通过
    public static final Integer AUDITSTATUS_REPLY = 3;      // 审核拒绝
    public static final Integer AUDITSTATUS_NO_REQUIRED = 4;// 无需审核

    // 上架状态常量
    public static final Integer SALESTATUS_OFF = 0; // 下架
    public static final Integer SALESTATUS_ON = 1;  // 上架

    private Long id;                   // 主键ID
    private String name;               // 服务项名称
    private BigDecimal originalPrice;  // 原价
    private BigDecimal discountPrice;  // 折扣价
    private Integer carPackage;        // 是否套餐
    private String info;               // 备注
    private Date createTime;           // 创建时间
    private Integer serviceCatalog;    // 服务分类：0维修/1保养/2其他
    private Integer auditStatus;       // 审核状态
    private Integer saleStatus;        // 上架状态
}
```

## 许可证
本项目基于 [MIT License](LICENSE) 开源，允许个人及企业免费使用。

