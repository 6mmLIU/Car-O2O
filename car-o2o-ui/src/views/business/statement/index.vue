<template>
  <div class="app-container">
    <!--高级查询-->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="到店时间" prop="actualArrivalTime">
  <el-date-picker
    v-model="dateRange"
    type="daterange"
    format="yyyy-MM-dd"
    value-format="yyyy-MM-dd"
    range-separator="至"
    start-placeholder="开始日期"
    end-placeholder="结束日期">
  </el-date-picker>

</el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
<!--按钮-->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['business:statement:add']"
        >新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="statementList">
      <el-table-column label="客户姓名" align="center" prop="customerName" />
      <el-table-column label="联系方式" align="center" prop="customerPhone" />
      <el-table-column label="到店时间" align="center" prop="actualArrivalTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.actualArrivalTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="车牌号码" align="center" prop="licensePlate" />
      <el-table-column label="汽车类型" align="center" prop="carSeries" />
      <el-table-column label="服务类型" align="center" prop="serviceType" >
          <template slot-scope="scope">
         <dict-tag :options="dict.type.appoint_serv_type" :value="scope.row.serviceType" />
          </template>
          </el-table-column> 
      <el-table-column label="预约用户" align="center" prop="appointmentId" >
        <template slot-scope="scope">
          <span>{{ scope.row.appointmentId ?"是" : "否" }}</span>
        </template>
      </el-table-column> 
      <el-table-column label="结算状态" align="center" prop="status" >
        <template slot-scope="scope">
          <dict-tag :options="dict.type.statement_status" :value="scope.row.status" />
        </template>
        </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
             :disabled="scope.row.status == 1"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['business:statement:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleDetail(scope.row)"
          >明细</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['business:statement:remove']"
          >删除</el-button>
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

    <!-- 添加或修改结算单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="客户姓名" prop="customerName">
          <el-input v-model="form.customerName" placeholder="请输入客户姓名" />
        </el-form-item>
        <el-form-item label="联系方式" prop="customerPhone">
          <el-input v-model="form.customerPhone" placeholder="请输入客户联系方式" />
        </el-form-item>
        <el-form-item label="到店时间" prop="actualArrivalTime">
          <el-date-picker clearable
            v-model="form.actualArrivalTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择实际到店时间 yyyy-MM-dd HH:mm:ss" >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="车牌号码" prop="licensePlate">
          <el-input v-model="form.licensePlate" placeholder="请输入车牌号码" />
        </el-form-item>
        <el-form-item label="汽车类型" prop="carSeries">
          <el-input v-model="form.carSeries" placeholder="请输入汽车类型" />
        </el-form-item>
        <el-form-item label="服务类型" prop="serviceType">
         <el-col :span="12">

    <el-select v-model="form.serviceType" placeholder="请选择服务类型" >
      <el-option
        v-for="dict in dict.type.appoint_serv_type"
        :key="dict.value"
        :label="dict.label"
        :value="parseInt(dict.value)">
      </el-option>
    </el-select>

</el-col>

        </el-form-item>
        <el-form-item label="备注信息" prop="info">
          <el-input v-model="form.info" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listStatement, getStatement, delStatement, addStatement, updateStatement } from "@/api/business/statement";
export default {
  name: "Statement",
  dicts: ['appoint_serv_type', 'statement_status'],
  data() {
    return {
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
      // 结算单表格数据
      statementList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      //接收时间高级查询参数
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        actualArrivalTime: null,
        isDelete: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
          customerName: [
          { required: true, message: "客户姓名不能为空", trigger: "blur" },

        ],
        customerPhone: [
          { required: true, message: "客户联系方式不能为空", trigger: "blur" },

        ],
      actualArrivalTime: [
          { required: true, message: "实际到店时间不能为空", trigger: "blur" },
    
        ],licensePlate: [
          { required: true, message: "车牌号码不能为空", trigger: "blur" },

        ],carSeries: [
          { required: true, message: "汽车类型不能为空", trigger: "blur" },

        ],serviceType: [
          { required: true, message: "服务类型不能为空", trigger: "blur" },

        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询结算单列表 */
    getList() {
      this.loading = true;
      if (this.dateRange) {
  this.queryParams["params"] = {};
  this.queryParams.params["beginTime"] = this.dateRange[0];
  this.queryParams.params["endTime"] = this.dateRange[1];
}

      listStatement(this.queryParams).then(response => {
        this.statementList = response.rows;
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
        customerName: null,
        customerPhone: null,
        actualArrivalTime: null,
        licensePlate: null,
        carSeries: null,
        serviceType: null,
        appointmentId: null,
        status: 0,
        payTime: null,
        payeeId: null,
        totalAmount: null,
        totalQuantity: null,
        discountAmount: null,
        createTime: null,
        info: null,
        isDelete: null
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
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加结算单";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getStatement(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改结算单";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateStatement(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addStatement(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除结算单编号为"' + ids + '"的数据项？').then(function() {
        return delStatement(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('business/statement/export', {
        ...this.queryParams
      }, `statement_${new Date().getTime()}.xlsx`)
    },
    /** 明细按钮操作 */
    handleDetail(row) {
     this.$router.push({
      path:"/business/statementItem/index/" + row.id,
     })
    }
  }
};
</script>
