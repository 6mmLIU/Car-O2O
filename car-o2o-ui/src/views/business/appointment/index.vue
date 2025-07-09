<template>
  <div class="app-container">
    <!--高级查询部分-->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="客户联系方式" prop="customerPhone">
        <el-input
          v-model="queryParams.customerPhone"
          placeholder="请输入客户联系方式"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="车牌号码" prop="licensePlate">
        <el-input
          v-model="queryParams.licensePlate"
          placeholder="请输入车牌号码"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['business:appointment:add']"
        >新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
<!-- 表格部分 -->
<el-table v-loading="loading" :data="appointmentList" @selection-change="handleSelectionChange">
  <el-table-column label="主键" align="center" prop="id" />

  <el-table-column label="微信用户ID" align="center" >
    <template slot-scope="scope">
      <span>{{ scope.row.wxUserId | nameFormat}}</span>
    </template>
  </el-table-column>

  <el-table-column label="联系方式" align="center" prop="customerPhone" width="130"/>
  
  <el-table-column label="预约时间" align="center" prop="appointmentTime" width="130">
    <template slot-scope="scope">
      <span>{{ parseTime(scope.row.appointmentTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
    </template>
  </el-table-column>

  <el-table-column label="实际到店时间" align="center" prop="actualArrivalTime" width="180">
    <template slot-scope="scope">
      <span>{{ parseTime(scope.row.actualArrivalTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
    </template>
  </el-table-column>

  <el-table-column label="车牌号码" align="center" prop="licensePlate" />
  <el-table-column label="汽车类型" align="center" prop="carSeries" />

  <el-table-column label="服务类型" align="center" prop="serviceType">
    <template slot-scope="scope">
      <dict-tag :options="dict.type.appoint_serv_type" :value="scope.row.serviceType"/>
    </template>
  </el-table-column>

  <el-table-column label="备注信息" align="center" prop="info" />

  <el-table-column label="状态" align="center" prop="status">
    <template slot-scope="scope">
      <dict-tag :options="dict.type.appoint_serv_status" :value="scope.row.status"/>
    </template>
  </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            :disabled="scope.row.status !=0"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['business:appointment:edit']"
          >编辑</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-s-shop"
            :disabled="scope.row.status !=0"
            @click="handleArrived(scope.row)"
            v-hasPermi="['business:appointment:edit']"
          >到店</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-reading"
            @click="handleStatement(scope.row.id)"
            :disabled="scope.row.status ==0 || scope.row.status ==2 || scope.row.status==3"
            v-hasPermi="['business:appointment:edit']"
          >结算单</el-button>
          <el-dropdown size="mini" @command="(command) => handleCommand(command, scope.row)">
                <span class="el-dropdown-link">
                  <i class="el-icon-d-arrow-right el-icon--right"></i>更多
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item  icon="el-icon-remove" command="handleCancel"
                  :disabled="scope.row.status!=0">取消</el-dropdown-item>
                  <el-dropdown-item  icon="el-icon-delete" command="handleDelete"
                  :disabled="scope.row.status!=0">删除</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
        </template>
      </el-table-column>
    </el-table>
    <!--分页-->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改养修信息预约对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="微信用户ID" prop="wxUserId">
          <el-input v-model="form.wxUserId" placeholder="请输入微信用户ID" />
        </el-form-item>
        <el-form-item label="联系方式" prop="customerPhone">
          <el-input v-model="form.customerPhone" placeholder="请输入客户联系方式" />
        </el-form-item>
        <el-form-item label="预约时间" prop="appointmentTime">
          <el-date-picker clearable
            v-model="form.appointmentTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择预约时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="车牌号码" prop="licensePlate">
          <el-input v-model="form.licensePlate" placeholder="请输入车牌号码" />
        </el-form-item>
        <el-form-item label="汽车类型" prop="carSeries">
          <el-input v-model="form.carSeries" placeholder="请输入汽车类型" />
        </el-form-item>
        <el-form-item label="服务类型" prop="serviceType">
          <el-select v-model="form.serviceType" placeholder="请选择服务类型">
            <el-option
              v-for="dict in dict.type.appoint_serv_type"
              :key="dict.value"
              :label="dict.label"
:value="parseInt(dict.value)"
            ></el-option>
          </el-select>
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
import { listAppointment, getAppointment, delAppointment, addAppointment, updateAppointment ,arrivedOp,cancelOp,createOrSelectStatement} from "@/api/business/appointment";
import handlebars from "highlight.js/lib/languages/handlebars";

export default {
  name: "Appointment",
  dicts: ['appoint_serv_status', 'appoint_serv_type'],
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
      // 养修信息预约表格数据
      appointmentList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        customerPhone: null,
        licensePlate: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
  customerPhone: [
    { required: true, message: '手机号不能为空', trigger: 'blur' }
  ],
  appointmentTime: [
    { required: true, message: '预约时间不能为空', trigger: 'blur' }
  ],
  licensePlate: [
    { required: true, message: '车牌号不能为空', trigger: 'blur' }
  ],
  carSeries: [
    { required: true, message: '汽车类型不能为空', trigger: 'blur' }
  ],
  serviceType: [
    { required: true, message: '服务类型不能为空', trigger: 'blur' }
  ]
  
}
    };
  },
  created() {
    this.getList();
  },
  methods: {

    /** 查询养修信息预约列表 */
    getList() {
      this.loading = true;
      listAppointment(this.queryParams).then(response => {
        this.appointmentList = response.rows;
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
        wxUserId: null,
        customerPhone: null,
        appointmentTime: null,
        actualArrivalTime: null,
        licensePlate: null,
        carSeries: null,
        serviceType: null,
        createTime: null,
        info: null,
        status: 0
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
      this.title = "添加养修信息预约";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAppointment(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改养修信息预约";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAppointment(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAppointment(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('business/appointment/export', {
        ...this.queryParams
      }, `appointment_${new Date().getTime()}.xlsx`)
    },
     // 更多操作触发
  handleCommand(command, row) { 
      switch (command) {
        case "handleCancel":
          this.handleCancel(row);
          break;
        case "handleDelete":
          this.handleDelete(row);
          break;
        default:
          break;
      }
    },
       /** 取消按钮操作 */
       handleCancel(row) {
        this.$modal.confirm('是否确认取消养修信息预约编号为"' + row.id + '"的数据项?').then(function () {
      return cancelOp(row.id);
      //发送到后台
    }).then(()=>{
      this.getList();
      this.$modal.msgSuccess("取消预约成功");
    }).catch(() => {});
     
    },
       /** 删除按钮操作 */
       handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除养修信息预约编号为"' + ids + '"的数据项？').then(function() {
        return delAppointment(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
/** 结算单按钮 */
handleStatement(id) {
  this.$modal.confirm("您是否要创建或查看结算单？")
    .then(() => {
      createOrSelectStatement(id).then(resp => {
        let statementId=resp.data;
        // 通过动态路由跳转到结算单详情页面
        this.$router.push({
          path: "/business/statementItem/index/" + statementId
        });
      });
    });
},
    /**到店功能 */
  handleArrived(row){
    this.$modal.confirm('是否确认用户已经到店？').then(function () {
      return arrivedOp(row.id);
      //发送到店请求
    }).then(()=>{
      this.getList();
      this.$modal.msgSuccess("到店操作成功");
    }).catch(() => {});
  }
  },
  
  filters: {
    nameFormat(wxUserId){
      if (wxUserId) {
        return wxUserId;
        
      }else{
        return "客服录入";
      }
    }
  }
 
};
</script>
