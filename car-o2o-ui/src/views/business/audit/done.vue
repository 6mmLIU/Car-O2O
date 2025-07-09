<template>
  <div class="app-container">
    <!-- 高级搜索 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.pck_audit_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          /> 
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="daterangeCreateTime"
          style="width: 240px"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 按钮  -->
    <el-row :gutter="10" class="mb8">
    </el-row>
    <!-- 表格数据 -->
    <el-table v-loading="loading" :data="auditList">
      <el-table-column label="套餐名称" align="center" prop="serviceItemName" />
      <el-table-column label="套餐价格" align="center" prop="serviceItemPrice" />
      <el-table-column label="套餐备注" align="center" prop="serviceItemInfo" :show-overflow-tooltip="true"/>
      <el-table-column label="套餐状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pck_audit_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleHistoryTaskOp(scope.row)"
          >审批历史</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="getProcessImgOp(scope.row)"
          >进度查看</el-button>
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

    <!-- 添加或修改套餐审核对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="服务单项id" prop="serviceItemId">
          <el-input v-model="form.serviceItemId" placeholder="请输入服务单项id" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- png 文件展示对话框 -->
    <el-dialog :title="title" :visible.sync="pngOpen" width="800px" append-to-body>
      <div v-html="pngResources"></div>
    </el-dialog>

    <!-- 查看审批历史 -->
    <el-dialog :title="title" :visible.sync="historyTaskOpen" width="800px" append-to-body>
      <el-table :data="historyTaskList">
        <el-table-column label="任务名称" align="center" prop="taskName" />
        <el-table-column label="开始时间" align="center" prop="startTime" />
        <el-table-column label="结束时间" align="center" prop="endTime" />
        <el-table-column label="总耗时" align="center" prop="durationInMillis" />
        <el-table-column label="审批意见" align="center" prop="comment" />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button type="default" @click="historyTaskClose">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAudit, getAudit, processCancel, addAudit, updateAudit, getProcessImg, getHistoryTaskList,listTodoTask,audit,listDoneTask } from "@/api/business/audit";

export default {
  name: "Audit",
  dicts: ['pck_audit_status'],
  data() {
    return {
      // 审批列表 id
      id:null,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      /** 审批对话框显示隐藏 */
      auditOpen:false,
      /** 历史任务集合列表 */
      historyTaskList:[],
      /** 审批历史对话框显示隐藏 */
      historyTaskOpen:false,
      /** 流程图显示隐藏 */
      pngOpen:false,
      /** 流程图数据 */
      pngResources:'',
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 套餐审核表格数据
      auditList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 服务单项id时间范围
      daterangeCreateTime: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        serviceItemId: null,
        status: null,
        createTime: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        status: [
          { required: true, message: "状态【审核中0/审核拒绝1/审核通过2/审核撤销3】不能为空", trigger: "change" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询套餐审核列表 */
    getList() {
      this.loading = true;
      this.queryParams.params = {};
      if (null != this.daterangeCreateTime && '' != this.daterangeCreateTime) {
        this.queryParams.params["beginCreateTime"] = this.daterangeCreateTime[0];
        this.queryParams.params["endCreateTime"] = this.daterangeCreateTime[1];
      }
      listDoneTask(this.queryParams).then(response => {
        this.auditList = response.rows;
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
        serviceItemId: null,
        serviceItemName: null,
        serviceItemInfo: null,
        serviceItemPrice: null,
        instanceId: null,
        creatorId: null,
        info: null,
        status: null,
        createTime: null,
        auditStatus:null,
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
      this.daterangeCreateTime = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加套餐审核";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAudit(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改套餐审核";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAudit(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAudit(this.form).then(response => {
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
      this.download('business/audit/export', {
        ...this.queryParams
      }, `audit_${new Date().getTime()}.xlsx`)
    },
    /** 流程图查看 */
    getProcessImgOp(row){
      // 此处参数直接携带流程实例 id
      getProcessImg(row.instanceId).then(response => {
        this.pngResources = response;
        this.title="流程查看"
        this.pngOpen = true;
      })
    },
     /** 撤销按钮操作 */
    handleCancel(row) {
      const id = row.id
      this.$modal.confirm('是否确认撤销套餐审核编号为"' + id + '"的数据项？').then(() => {
        processCancel(id).then(() => {
          this.getList();
          this.$modal.msgSuccess("流程撤销成功");
        })
      })
    },
    /** 审批历史对话 */
    handleHistoryTaskOp(row){
      getHistoryTaskList(row.instanceId).then(resp => {
        this.historyTaskList = resp.data;
        this.title = "审批历史";
        this.historyTaskOpen = true;
      })
    },
    /** 关闭审批历史对话框 */
    historyTaskClose(){
      this.historyTaskOpen = false;
    },
    /** 打开审批对话框 */
    auditOp(row){
      // 将 id 存起来
      this.id = row.id;
      this.reset();
      this.title = '流程审核'
      this.auditOpen = true;
    },
    /** 关闭审批对话框 */
    auditCancel(){
      this.auditOpen = false;
    },
    /** 审批操作 */
    auditSubmitForm(){
      // 封装参数
      let data = {
        id:this.id,
        auditStatus:this.form.auditStatus,
        info:this.form.info
      }
      // 发送请求
      audit(data).then(() => {
        this.getList();
        this.$modal.msgSuccess("审批完成");
        this.auditOpen = false;
      })
    }
  }
};
</script>
