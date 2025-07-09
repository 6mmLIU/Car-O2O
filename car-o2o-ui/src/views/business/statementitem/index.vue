<template>
  <div>
    <!-- 两列栅格 -->
    <el-row :gutter="20" class="top">
      <!-- ===== 左半部分 ===== -->
      <el-col :span="statement.status==0 ? 15 : 22">
        <!-- 客户信息 -->
        <el-row class="leftTop">
          <el-descriptions :column="2">
            <el-descriptions-item label="客户姓名">{{
              statement.customerName
            }}</el-descriptions-item>
            <el-descriptions-item label="联系方式">{{
              statement.customerPhone
            }}</el-descriptions-item>
            <el-descriptions-item label="车牌号码">{{
              statement.licensePlate
            }}</el-descriptions-item>
            <el-descriptions-item label="汽车类型">{{
              statement.carSeries
            }}</el-descriptions-item>
            <el-descriptions-item label="服务类型">
              <dict-tag
                :options="dict.type.appoint_serv_type"
                :value="statement.serviceType"
              />
            </el-descriptions-item>
            <el-descriptions-item label="到店时间">{{
              statement.actualArrivalTime
            }}</el-descriptions-item>
            <!-- 修改：使用计算属性显示总消费金额，而不是statement.totalAmount -->
            <el-descriptions-item label="总消费金额">{{
              totalSum
            }}</el-descriptions-item>
            <!-- 修改：使用计算属性显示实付价格，而不是直接计算 -->
            <el-descriptions-item label="实付价格">{{
              actualPrice
            }}</el-descriptions-item>

            <!-- 优惠价格 -->
            <el-descriptions-item label="优惠价格">
              <el-col :span="8">
                <!-- 修改：添加数值输入验证 -->
                <el-input
                  v-model="statement.discountAmount"
                  size="mini"
                  type="number"
                  :min="0"
                  @blur="handleDiscountChange"
                  v-if="statement.status==0"
                >
                </el-input>
                <span v-if="statement.status==1">{{ statement.discountAmount }}</span>
              </el-col>
            </el-descriptions-item>
          </el-descriptions>
        </el-row>

        <!-- 明细 + 操作 -->
        <el-row class="leftBottom">
          <!-- 按钮行 -->
          <el-row class="btnbox"  v-if="statement.status==0">
            <el-col :span="2">
              <el-button
                type="primary"
                plain
                icon="el-icon-plus"
                size="small"
                @click="itemSaveOp"
                >保存</el-button
              >
            </el-col>

            <el-col :span="2" class="paybtnclass">
              <el-button
                type="warning"
                plain
                icon="el-icon-shopping-bag-1"
                size="small"
                @click="payOp"
                >支付</el-button
              >
            </el-col>

            <el-col :span="21">
              <right-toolbar
                :showSearch.sync="showSearch"
                @queryTable="getList"
              />
            </el-col>
          </el-row>

          <!-- 表格 -->
          <el-table
            :data="statementItemList"
            style="width: 100%"
            class="tablebox"
          >
            <el-table-column prop="itemName" label="服务名称" />
            <el-table-column prop="itemPrice" label="服务价格" />
            <el-table-column prop="itemQuantity" label="数量" />
            <el-table-column label="操作" v-if="statement.status==0">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  icon="el-icon-plus"
                  type="primary"
                  @click="plusHandle(scope.$index)"
                >
                </el-button>
                <el-button
                  size="mini"
                  icon="el-icon-minus"
                  type="danger"
                  @click="minusHandle(scope.$index)"
                >
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="page">
            显示第 1 到第 {{ statementItemList.length }} 条记录，总共
            {{ statementItemList.length }} 条记录
          </div>
        </el-row>
      </el-col>

      <!-- ===== 右半部分 ===== -->
      <el-col :span="7" v-if="statement.status==0">
        <!-- ===== 右上部分 ===== -->
        <el-row class="rightTop">
          <!-- 高级搜索 -->
          <el-form
            :model="queryParams"
            ref="queryForm"
            size="small"
            :inline="true"
          >
            <el-form-item label="服务名称" prop="name">
              <el-input
                v-model="queryParams.name"
                placeholder="请输入服务名称"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>

            <el-form-item label="是否套餐" prop="carPackage">
              <!-- 统一用 queryParams 保存搜索条件 -->
              <el-select v-model="queryParams.carPackage" clearable>
                <el-option
                  v-for="dict in dict.type.item_serv_package"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="服务分类" prop="serviceCatalog">
              <el-select v-model="queryParams.serviceCatalog" clearable>
                <el-option
                  v-for="dict in dict.type.item_serv_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>

            <el-form-item label=" ">
              <el-button
                type="success"
                icon="el-icon-search"
                size="medium"
                round
                @click="handleQuery"
              >
                搜索
              </el-button>
            </el-form-item>
          </el-form>
        </el-row>

        <!-- ===== 右下部分 ===== -->
        <el-row class="rightBottom">
          <el-table :data="serviceitemList">
            <el-table-column label="服务名称" align="center" prop="name" />
            <el-table-column
              label="服务价格"
              align="center"
              prop="discountPrice"
            />
            <el-table-column
              label="操作"
              align="center"
              class-name="small-padding fixed-width"
            >
              <template slot-scope="scope">
                <el-button
                  size="small"
                  icon="el-icon-plus"
                  type="primary"
                  @click="serviceItemPlus(scope.row)"
                ></el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="page">
            显示第 1 到第 {{ serviceitemList.length }} 条记录， 总共
            {{ serviceitemList.length }} 条记录
          </div>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getServiceitemList } from "@/api/business/serviceitem";
import { getStatement } from "@/api/business/statement";
import { itemSave,getStatementItemListByStatementId,pay} from "@/api/business/statementItem";
import stata from "highlight.js/lib/languages/stata";
export default {
  name: "item",
  dicts: ["item_serv_package", "item_serv_type", "appoint_serv_type"],
  data() {
    return {
      // 修改：使用正确的路由参数名 statementId 而不是 id
      statementId: this.$route.params.statementId,

      //结算单明细列表
      statementItemList: [],

      //服务项集合列表
      serviceitemList: [],
      //结算单对象
      statement: {
        totalAmount: 0, // 总消费金额
        discountAmount: 0, // 优惠价格
        customerName: "",
        customerPhone: "",
        licensePlate: "",
        carSeries: "",
        serviceType: "",
        actualArrivalTime: "",
      },
      //服务项高级搜索参数
      queryParams: {
        name: null,
        carPackage: null,
        serviceCatalog: null,
        saleStatus: 1,
      },

      /* ===== 其他 ===== */
      discount: null, // 优惠价格输入框
      showSearch: true,
    };
  },
  created() {
    this.getInit();
  },
  computed: {
    // 总金额 - 新增：计算属性，实时计算结算单明细的总金额
    totalSum() {
      let sum = 0;
      if (this.statementItemList && this.statementItemList.length > 0) {
        this.statementItemList.forEach((ele) => {
          const price = parseFloat(ele.itemPrice) || 0;
          const quantity = parseInt(ele.itemQuantity) || 0;
          sum += price * quantity;
        });
      }
      return sum;
    },
    // 实付价格 - 新增：计算属性，总金额减去优惠金额
    actualPrice() {
      const total = this.totalSum || 0;
      const discount = parseFloat(this.statement.discountAmount) || 0;
      const result = total - discount;
      return result > 0 ? result : 0;
    },
  },
  methods: {
    /* 初始化 */
    getInit() {
      this.getServiceitemListOp();
      // 加载结算单列表数据(集合)

      //加载结算单对象(对象)
      this.getStatementOp();
       this.getStatementItemListOp(); // 新增这行
    },

    getList() {
      this.getServiceitemListOp();
    },

    /* 加载服务项集合列表 */
    getServiceitemListOp() {
      getServiceitemList(this.queryParams).then((resp) => {
        this.serviceitemList = resp.data;
      });
    },
    //保存按钮(保存明细项,更新结算单)
    itemSaveOp() {
      this.$modal.confirm("是否需要保存当前明细").then(() => {
        //封装参数
          let data={
        statementItemList: this.statementItemList,
        discountAmount: this.statement.discountAmount
      }
      //发送请求
      itemSave(data).then(()=>{
          this.$modal.msgSuccess("保存成功")
      })
    })
    },
    //支付按钮
    payOp(){
      this.$modal.confirm("您确定要支付改结算单吗?").then(() => {
      //发送请求
        pay(this.statementId).then(()=>{
          this.$modal.msgSuccess("支付成功")
          //刷新结算单数据 
          this.getStatementOp();
      })
    })
    },

    /*加载结算单*/
    getStatementOp() {
      getStatement(this.statementId)
        .then((resp) => {
          if (resp.data) {
            // 修改：确保所有数值字段都有默认值，防止undefined错误
            this.statement = {
              totalAmount: parseFloat(resp.data.totalAmount) || 0,
              discountAmount: parseFloat(resp.data.discountAmount) || 0,
              customerName: resp.data.customerName || "",
              customerPhone: resp.data.customerPhone || "",
              licensePlate: resp.data.licensePlate || "",
              carSeries: resp.data.carSeries || "",
              serviceType: resp.data.serviceType || "",
              actualArrivalTime: resp.data.actualArrivalTime || "",
              ...resp.data,
            };
          }
        })
        .catch((error) => {
          console.error("获取结算单数据失败:", error);
          // 保持默认值
        });
    },
 /**加载结算单明细列表 */
          getStatementItemListOp() {
  getStatementItemListByStatementId(this.statementId).then((resp) => {
    this.statementItemList = resp.data || [];
  }).catch((error) => {
    console.error("获取结算单明细列表失败:", error);
    this.statementItemList = [];
  });
},
    /* 搜索 */
    handleQuery() {
      this.getServiceitemListOp();
    },

    // 新增：处理优惠价格输入变化，确保输入有效数值
    handleDiscountChange() {
      const discount = parseFloat(this.statement.discountAmount);
      if (isNaN(discount) || discount < 0) {
        this.statement.discountAmount = 0;
      } else {
        this.statement.discountAmount = discount;
      }
    },

    //点击右侧加号
    serviceItemPlus(row) {
      //新增||更新 数量
      //根据传入的itemId寻找结算单明细中是否存在对应的itemId
      let index = this.getStatementItemListIndexByServiceIteId(row.id);
      if (index >= 0) {
        //如果存在则数量加1
        this.statementItemList[index].itemQuantity++;
      } else {
        //如果不存在则新增一条结算单明细
        this.statementItemList.push(this.createStatementItem(row));
      }
    },
    //根据传入的serviceItemId对比当前 结算单 statementItemList 是否存在,并返回索引
    getStatementItemListIndexByServiceIteId(serviceItemId) {
      //定义初始索引为-1,若没找到则直接将-1返回
      let index = -1;
      //循环遍历结算单明细中是否存在传入的serviceItemId
      if (this.statementItemList.length < 1) {
        //说明是空集合,结算单明细列表中不存在数据.直接返回-1
        return index;
      }
      //说明结算单明细列表集合中存在数据 需要遍历判定
      this.statementItemList.forEach((e, i) => {
        //结算单明细列表中的某一个itemId(结算单明细id)与传入的服务项id(serviceItemId)相等则找到了
        if (e.itemId === serviceItemId) {
          index = i; //记录索引
          return;
        }
      });
      return index;
    },

    //点击右侧加号后封装结算单明细对象数据
    createStatementItem(row) {
      let obj = {
        statementId: this.statementId,
        itemId: row.id,
        itemName: row.name,
        // 修改：确保价格字段正确转换为数值类型
        itemPrice: parseFloat(row.discountPrice) || 0,
        itemQuantity: 1,
      };
      return obj;
    },
    //点击左下结算单部分的加号
    plusHandle(index) {
      this.statementItemList[index].itemQuantity++;
    },
    //点击左下结算单部分的减号
    minusHandle(index) {
      //判断剩余数量
      if (this.statementItemList[index].itemQuantity == 1) {
        //如果数量等于1则删除该条结算单明细
        this.statementItemList.splice(index, 1);
        return;
      } else {
        this.statementItemList[index].itemQuantity--;
      }
    },

    // 新增：数据提交前的验证方法，确保所有数值字段都不是undefined
    validateData() {
      // 确保关键数值字段不为 undefined 或 null
      if (
        this.statement.totalAmount === undefined ||
        this.statement.totalAmount === null
      ) {
        this.statement.totalAmount = 0;
      }
      if (
        this.statement.discountAmount === undefined ||
        this.statement.discountAmount === null
      ) {
        this.statement.discountAmount = 0;
      }

      // 转换为数值类型
      this.statement.totalAmount = parseFloat(this.statement.totalAmount) || 0;
      this.statement.discountAmount =
        parseFloat(this.statement.discountAmount) || 0;
    },

    // 新增：保存方法，包含数据验证和格式化
    saveStatement() {
      this.validateData(); // 保存前验证数据

      // 构建提交数据，确保数值类型正确
      const submitData = {
        id: parseInt(this.statementId) || 0,
        totalAmount: this.totalSum, // 使用计算属性
        discountAmount: parseFloat(this.statement.discountAmount) || 0,
        customerName: String(this.statement.customerName || ""),
        customerPhone: String(this.statement.customerPhone || ""),
        licensePlate: String(this.statement.licensePlate || ""),
        carSeries: String(this.statement.carSeries || ""),
        serviceType: String(this.statement.serviceType || ""),
        actualArrivalTime: String(this.statement.actualArrivalTime || ""),
        statementItemList: this.statementItemList,
      };

      console.log("提交数据:", submitData);

      // 这里调用保存API
      // saveStatementAPI(submitData).then(response => {
      //   this.$message.success('保存成功');
      // }).catch(error => {
      //   this.$message.error('保存失败: ' + error.message);
      // });
    },
  },
};
</script>

<style scoped>
.top {
  margin-top: 3%;
  margin-left: 20px;
}

/* 卡片化外观 */
.leftTop,
.leftBottom,
.rightTop,
.rightBottom {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12), 0 0 6px rgba(0, 0, 0, 0.04);
  border-radius: 4px;
}

.leftTop {
  padding: 3% 5%;
}
.leftBottom {
  margin-top: 16px;
  padding: 16px 24px;
}
.tablebox {
  margin: 24px 2%;
}
.paybtnclass {
  margin-left: 1%;
}
.rightTop {
  padding: 3% 5%;
}
.rightBottom {
  padding: 3% 5%;
}
.leftTop,
.leftBottom,
.rightTop {
  margin-left: 5%;
}
.rightBottom {
  margin-top: 4%;
  padding: 6% 5%;
  margin-left: 5%;
}
.page {
  line-height: 34px;
  font-size: 13px;
  color: #676a6c;
}
</style>
