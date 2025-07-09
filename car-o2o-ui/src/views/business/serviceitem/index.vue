<template>
  <div class="app-container">
    <!--高级搜索-->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="服务名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入服务名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="审核状态" prop="auditStatus">
        <el-select
          v-model="queryParams.auditStatus"
          placeholder="请选择审核状态"
          clearable
        >
          <el-option
            v-for="dict in dict.type.item_serv_audit_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="上架状态" prop="saleStatus">
        <el-select
          v-model="queryParams.saleStatus"
          placeholder="请选择上架状态"
          clearable
        >
          <el-option
            v-for="dict in dict.type.item_serv_up_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>


      <el-form-item label="服务原价" prop="originalPrice">
        <el-input
          v-model="queryParams.originalPrice"
          placeholder="请输入服务原价"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="服务折扣价" prop="discountPrice">
        <el-input
          v-model="queryParams.discountPrice"
          placeholder="请输入服务折扣价"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否套餐" prop="carPackage">
        <el-radio-group v-model="form.carPackage" :disabled="carPackage">
          <el-radio
            v-for="dict in dict.type.item_serv_package"
            :key="dict.value"
            :label="dict.value"
          >{{ dict.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="服务分类" prop="serviceCatalog">
        <el-radio-group v-model="form.serviceCatalog">
          <el-radio
            v-for="dict in dict.type.item_serv_type"
            :key="dict.value"
            :label="dict.value"
          >{{ dict.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <!--中间的四个按钮-->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['business:serviceitem:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
        @click="startAuditFun(selectedRow)"
        >
        发起审核
        </el-button>
      </el-col>

    </el-row>

    <el-table v-loading="loading" :data="serviceitemList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="服务项名称" align="center" prop="name"/>
      <el-table-column label="服务项原价" align="center" prop="originalPrice"/>
      <el-table-column label="服务项折扣价" align="center" prop="discountPrice"/>
      <el-table-column label="是否套餐" align="center" prop="carPackage">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.item_serv_package" :value="scope.row.carPackage"/>
        </template>
      </el-table-column>
      <el-table-column label="备注信息" align="center" prop="info" :show-overflow-tooltip="true"/>
      <el-table-column label="服务分类" align="center" prop="serviceCatalog">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.item_serv_type" :value="scope.row.serviceCatalog"/>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="auditStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.item_serv_audit_status" :value="scope.row.auditStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="上架状态" align="center" prop="saleStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.item_serv_up_status" :value="scope.row.saleStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['business:serviceitem:edit']" :disabled="scope.row.saleStatus == 1
          || (scope.row.saleStatus == 0
              && scope.row.carPackage == 1
              && scope.row.auditStatus == 1)"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="saleOn(scope.row)"
            v-if="scope.row.saleStatus == 0"
            :disabled="scope.row.saleStatus == 1
          || (scope.row.saleStatus == 0
              && scope.row.carPackage == 1
              && scope.row.auditStatus != 2)"
          >上架
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="saleOff(scope.row)"
            v-if="scope.row.saleStatus == 1"
            :disabled="scope.row.saleStatus == 0"
          >下架
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['business:serviceitem:remove']"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />


    <!-- 添加或修改服务项对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="服务名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入服务项名称"/>
        </el-form-item>
        <el-form-item label="服务原价" prop="originalPrice">
          <el-input v-model="form.originalPrice" placeholder="请输入服务项原价"/>
        </el-form-item>
        <el-form-item label="折扣价" prop="discountPrice">
          <el-input v-model="form.discountPrice" placeholder="请输入服务项折扣价"/>
        </el-form-item>
        <el-form-item label="是否套餐" prop="carPackage">
          <el-radio-group v-model="form.carPackage" :disabled="carPackage">
            <el-radio
              v-for="dict in dict.type.item_serv_package"
              :key="dict.value"
              :label="parseInt(dict.value)"
            >{{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="服务分类" prop="serviceCatalog">
          <el-radio-group v-model="form.serviceCatalog">
            <el-radio
              v-for="dict in dict.type.item_serv_type"
              :key="dict.value"
              :label="parseInt(dict.value)"
            >{{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注信息" prop="info">
          <el-input v-model="form.info" placeholder="请输入备注信息"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>


    <!--发起审核对话框-->
    <el-dialog :title="title" :visible.sync="startAuditOpen" width="500px" append-to-body>
      <el-form ref="form" :model="auditInfo" label-width="80px">
        <el-form-item label="套餐名称" prop="name">
          <el-input v-model="auditInfo.busServiceItem.name" disabled/>
        </el-form-item>
        <el-form-item label="套餐原价" prop="originalPrice">
          <el-input v-model="auditInfo.busServiceItem.originalPrice" disabled/>
        </el-form-item>
        <el-form-item label="套餐折扣价" prop="discountPrice">
          <el-input v-model="auditInfo.busServiceItem.discountPrice" disabled/>
        </el-form-item>
        <el-form-item label="审核流程名" prop="info">
          <el-input value="服务套餐审核流程" disabled/>
        </el-form-item>

        <el-form-item label="店长" prop="shopOwner">
          <el-select v-model="auditInfo.shopOwnerId" placeholder="请选择店长">
            <el-option
              v-for="item in shopOwnerList"
              :key="item.userId"
              :label="item.nickName"
              :value="item.userId"
            />
          </el-select>
        </el-form-item>


        <el-form-item label="财务" prop="financeId" v-if="auditInfo.busServiceItem.discountPrice>=3000">
          <el-select v-model="auditInfo.financeId" placeholder="请选择财务">
            <el-option
              v-for="item in financeList"
              :key="item.userId"
              :label="item.nickName"
              :value="item.userId"
            />
          </el-select>
        </el-form-item>


        <el-form-item label="备注信息" prop="info">
          <el-input v-model="auditInfo.info" placeholder="请输入备注信息"/>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="startAuditSubmit">确 定</el-button>
        <el-button @click="startAuditClose">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listServiceitem,
  getServiceitem,
  delServiceitem,
  addServiceitem,
  updateServiceitem,
  saleOnOp,
  saleOffOp,
  getAuditInfo,
  startAudit
} from "@/api/business/serviceitem";

export default {
  name: "Serviceitem",
  dicts: ['item_serv_package', 'item_serv_type', 'item_serv_audit_status', 'item_serv_up_status'],
  data() {
    return {
      id: null,
      selectedRow: null,
      //控制是否套餐禁用and可用
      carPackage: false,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 服务项表格数据
      serviceitemList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      startAuditOpen: false,
      auditInfo: {
        busServiceItem: {
          name: '',
          originalPrice: 0,
          discountPrice: 0
        },
        info: '',
        shopOwnerId: '',
        financeId: '',
        discountPriceLimit: 0
      },
      shopOwnerList: [],
      financeList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        originalPrice: null,
        discountPrice: null,
        carPackage: null,
        serviceCatalog: null,
        auditStatus: null,
        saleStatus: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          {required: true, message: "服务项名称不能为空", trigger: "blur"}
        ],
        originalPrice: [
          {required: true, message: "服务项原价不能为空", trigger: "blur"}
        ],
        discountPrice: [
          {required: true, message: "服务项优惠价格不能为空", trigger: "blur"}
        ],
        carPackage: [
          {required: true, message: "是否套餐必选", trigger: "blur"}
        ],
        info: [
          {required: true, message: "服务项备注不能为空", trigger: "blur"}
        ],
        serviceCatalog: [
          {required: true, message: "服务项分类不能为空", trigger: "blur"}
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    startAuditSubmit() {
      var data = {
        serviceItemId: this.id,
        shopOwnerId: this.auditInfo.shopOwnerId,
        financeId: this.auditInfo.financeId,
        info: this.auditInfo.info
      }
      startAudit(data).then(resp => {
        this.startAuditOpen = false;
        this.getList();
        this.$modal.msgSuccess("发起审核成功");
      });
    },
    startAuditClose() {
      this.startAuditOpen = false;
    },
    startAuditFun(row) {
      this.id=row.id;
      this.reset();
      const id = row.id || this.ids
      getAuditInfo(id).then(resp => {
        console.log('API返回的完整数据:', resp);
        console.log('店长列表数据:', resp.data.shopOwnerList);
        this.auditInfo.busServiceItem.name = row.name;
        this.auditInfo.busServiceItem.originalPrice = row.originalPrice;
        this.auditInfo.busServiceItem.discountPrice = row.discountPrice;
        this.shopOwnerList = resp.data.shopOwnerList;
        this.financeList = resp.data.financeList;
        this.title = "发起审核";
        this.startAuditOpen = true;
      });
    },
    /** 查询服务项列表 */
    getList() {
      this.loading = true;
      listServiceitem(this.queryParams).then(response => {
        this.serviceitemList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        name: null,
        originalPrice: null,
        discountPrice: null,
        carPackage: null,
        info: null,
        createTime: null,
        serviceCatalog: null,
        auditStatus: null,
        saleStatus: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length;
      this.selectedRow = selection.length === 1 ? selection[0] : null;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.carPackage = false;
      this.title = "添加服务项";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.carPackage = true;
      const id = row.id || this.ids
      getServiceitem(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改服务项";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateServiceitem(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addServiceitem(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除服务项编号为"' + ids + '"的数据项？').then(function () {
        return delServiceitem(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('business/serviceitem/export', {
        ...this.queryParams
      }, `serviceitem_${new Date().getTime()}.xlsx`)
    },
    /**上架按钮操作 */
    saleOn(row) {
      this.$modal.confirm('是否确认上架服务项编号为"' + row.id + '"的商品？').then(function () {
        return saleOnOp(row.id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("上架成功");
      }).catch(() => {
      });
    },
    /**下架按钮操作 */
    saleOff(row) {
      this.$modal.confirm('是否确认下架服务项编号为"' + row.id + '"的商品？').then(function () {
        return saleOffOp(row.id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("下架成功");
      }).catch(() => {
      });
    }
  }
};
</script>
