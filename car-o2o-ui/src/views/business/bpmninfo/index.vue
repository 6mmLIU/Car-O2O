<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="流程类型" prop="bpmnType">
        <el-select v-model="queryParams.bpmnType" placeholder="请选择流程类型" clearable>
          <el-option
            v-for="dict in dict.type.flow_bpmn_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="操作时间">
        <el-date-picker
          v-model="daterangeDeployTime"
          style="width: 240px"
          value-format="yyyy-MM-dd"
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

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >流程文件部署
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="bpmninfoList">
      <el-table-column label="流程类型" align="center" prop="bpmnType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.flow_bpmn_type" :value="scope.row.bpmnType"/>
        </template>
      </el-table-column>
      <el-table-column label="流程定义key" align="center" prop="processDefinitionKey"/>
      <el-table-column label="版本号" align="center" prop="version"/>
      <el-table-column label="描述信息" align="center" prop="info"/>
      <el-table-column label="部署时间" align="center" prop="deployTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.deployTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>

      <el-table-column label="流程文件" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-search"
            @click="showBpmn(scope.row)"
          >查看
          </el-button>
        </template>
      </el-table-column>

      <el-table-column label="流程图" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-search"
            @click="showPng(scope.row)"
          >查看
          </el-button>
        </template>


      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-remove"
            @click="handleCancel(scope.row)"
          >撤销
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

    <!-- 添加或修改流程定义明细对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="流程类型" prop="bpmnType">
          <el-select v-model="form.bpmnType" placeholder="请选择流程类型" clearable>
            <el-option
              v-for="item in dict.type.flow_bpmn_type"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="请选择文件：">
          <el-upload
            ref="upfile"
            action="upload"
            style="display: inline"
            :auto-upload="false"
            :on-change="handleChange"
            :file-list="fileList"
          >
            <el-button type="success">选择文件</el-button>
          </el-upload>
        </el-form-item>

        <el-form-item label="描述信息" prop="info">
          <el-input v-model="form.info" placeholder="请输入描述信息"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <el-dialog :title="title" :visible.sync="xmlOpen" width="1200px" append-to-body>
      <div v-text="xmlResource"></div>
    </el-dialog>
    <el-dialog :title="title" :visible.sync="pngOpen" width="1200px" append-to-body>
      <div v-html="pngResource"></div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listBpmninfo,
  getBpmninfo,
  delBpmninfo,
  addBpmninfo,
  updateBpmninfo,
  getResourceByType,
  cancelRequest
} from "@/api/business/bpmninfo";

export default {
  name: "Bpmninfo",
  dicts: ['flow_bpmn_type'],
  data() {
    return {
      //
      // 选择文件的列表
      fileList: [],
      //选择的文件
      file: null,
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
      // 流程定义明细表格数据
      bpmninfoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 描述信息时间范围
      daterangeDeployTime: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        bpmnType: null,
        deployTime: null,
        info: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        bpmnType: [
          {required: true, message: "流程类型不能为空", trigger: "change"}
        ],
        info: [
          {required: true, message: "描述信息不能为空", trigger: "blur"}
        ]
      },
      xmlOpen: false,
      pngOpen: false,
      xmlResource: '',
      pngResource: ''

    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询流程定义明细列表 */
    getList() {
      this.loading = true;
      this.queryParams.params = {};
      if (null != this.daterangeDeployTime && '' != this.daterangeDeployTime) {
        this.queryParams.params["beginDeployTime"] = this.daterangeDeployTime[0];
        this.queryParams.params["endDeployTime"] = this.daterangeDeployTime[1];
      }
      listBpmninfo(this.queryParams).then(response => {
        this.bpmninfoList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    handleCancel(row) {
      this.$modal
        .confirm('是否撤销流程定义?')
        .then(() => {
          cancelRequest(row.id).then((resp) => {
            if (resp.code == 200) {
              //刷新
              this.getList();
              this.$modal.msgSuccess( '撤销成功');
            } else {
              this.$modal.msgSuccess( '撤销失败');
            }
          });
        });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    showBpmn(row) {
      getResourceByType(row.id, "xml")
        .then(resp => {
          this.title = "流程定义bpmn文件";
          this.xmlOpen = true;
          this.xmlResource = resp;
        });
    },
    showPng(row) {
      getResourceByType(row.id, "png")
        .then(resp => {
          this.title = "流程定义png文件";
          this.pngOpen = true;
          this.pngResource = resp;
        });
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        bpmnLabel: null,
        bpmnType: null,
        processDefinitionKey: null,
        deployTime: null,
        version: null,
        info: null
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
      this.daterangeDeployTime = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加流程定义明细";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getBpmninfo(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改流程定义明细";
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除流程定义明细编号为"' + ids + '"的数据项？').then(function () {
        return delBpmninfo(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /**文件选择事件 */
    handleChange(file, fileList) {
      //console.log(file)
      //console.log(fileList)
      //文件上传前端一般我们会直接封装一个formdata对象,该对象可以不对参数进行编码和格式改变
      this.file = file

    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          //文件上传前端一般我们会直接封装一个formdata对象,该对象可以不对参数进行编码和格式改变
          //forData我们可以看作key->value格式
          let formData = new FormData();
          //将我们接收的参数都封装到formData属性中
          formData.append("file", this.file.raw);
          formData.append("bpmnType", this.form.bpmnType);
          formData.append("info", this.form.info);
          addBpmninfo(formData).then(response => {
            this.$modal.msgSuccess("部署成功");
            this.open = false;
            this.getList();
          });
        }

      });
    },
  }
};
</script>
