<template>
  <div>
    <div style="margin: 10px 0">
      <el-input style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search" v-model="name"></el-input>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>
    <div style="margin: 10px 0">
      <el-upload :action="'http://' + serverIp + ':9090/image/upload'" :show-file-list="false"
                 :on-success="handleImageUploadSuccess" style="display: inline-block">
          <el-button type="primary" class="ml-5" :disabled="user.role !== 'ROLE_MEDICAL_EXAMINER'">上传图片 <i class="el-icon-top"></i></el-button>
      </el-upload>
      <el-popconfirm
          class="ml-5"
          confirm-button-text='确定'
          cancel-button-text='我再想想'
          icon="el-icon-info"
          icon-color="red"
          title="您确定批量删除这些数据吗？"
          @confirm="delBatch"
      >
        <el-button type="danger" slot="reference" :disabled="user.role !== 'ROLE_ADMIN'">批量删除 <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>

    </div>
    <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"  @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="40"></el-table-column>
      <el-table-column prop="id" label="ID" width="40"></el-table-column>
      <el-table-column prop="name" label="图像名称"></el-table-column>
      <el-table-column prop="type" label="图像类型" width="70"></el-table-column>
      <el-table-column prop="presize" label="原图像大小(KB)" width="120"></el-table-column>
      <el-table-column prop="nextsize" label="压缩后图像大小(KB)" width="140"></el-table-column>
      <el-table-column prop="encriptsize" label="加密后图像大小(KB)" width="140"></el-table-column>
      <el-table-column prop="uploadTime" label="上传时间" :formatter="formatter"></el-table-column>
      <el-table-column label="下载">
        <template slot-scope="scope">
          <el-button type="primary" @click="lookImage(scope.row.id)" :disabled="user.role !== 'ROLE_DOCTOR'">下载</el-button>
          <el-button type="warning" @click="preview(scope.row.id)" :disabled="user.role !== 'ROLE_DOCTOR'">预览</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作"  width="260" align="center">
        <template slot-scope="scope">
<!--          <el-button type="success" @click="handleEdit(scope.row)" :disabled="user.role !== 'ROLE_ADMIN'">编辑<i class="el-icon-edit ml-5"></i></el-button>-->
          <el-button type="success" @click="handleEncrypt(scope.row)" :disabled="user.role !== 'ROLE_MEDICAL_EXAMINER'">加密<i class="el-icon-lock ml-5"></i></el-button>
          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='我再想想'
              icon="el-icon-info"
              icon-color="red"
              title="您确定删除吗？"
              @confirm="del(scope.row.id)"
          >
            <el-button type="danger" slot="reference" :disabled="user.role !== 'ROLE_ADMIN'">删除 <i class="el-icon-remove-outline"></i></el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div style="padding: 10px 0">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[2, 5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <!-- 点击编辑弹出对话框 -->
    <el-dialog title="图像信息" :visible.sync="dialogFormVisible" width="33%">
      <el-form label-width="140px" size="small">
        <el-form-item label="图像名称">
          <el-input v-model="form.name" autocomplete="off" disabled></el-input>
        </el-form-item>
        <el-form-item label="图像类型">
          <el-input v-model="form.type" autocomplete="off" disabled></el-input>
        </el-form-item>
        <el-form-item label="原图像大小(KB)">
          <el-input v-model="form.presize" autocomplete="off" disabled></el-input>
        </el-form-item>
        <el-form-item label="压缩后图像大小(KB)">
          <el-input v-model="form.nextsize" autocomplete="off" disabled></el-input>
        </el-form-item>
        <el-form-item label="加密后图像大小(KB)">
          <el-input v-model="form.encriptsize" autocomplete="off" disabled></el-input>
        </el-form-item>
        <el-form-item label="图像字节流">
          <el-input v-model="form.img" autocomplete="off" disabled></el-input>
        </el-form-item>
        <el-form-item label="上传时间">
          <el-input v-model="form.uploadTime" autocomplete="off" disabled></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="noSave">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 点击弹出密钥选择框 -->
    <el-dialog title="密钥选择" :visible.sync="dialogKeyVisible" width="33%">
      <el-form label-width="140px" size="small">
        <el-form-item label="图像名称">
          <el-input v-model="form.name" autocomplete="off" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="上传时间">
          <el-input v-model="form.uploadTime" autocomplete="off" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="密钥选择">
          <el-select clearable v-model="form.doctorId" placeholder="请选择医生" style="width: 100%">
            <el-option v-for="item in doctors" :key="item.id" :label="item.username" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="noSave">取 消</el-button>
        <el-button type="primary" @click="save">加密</el-button>
      </div>
    </el-dialog>


  </div>
</template>

<script>
import {serverIp} from "../../public/config";
import { formatDate } from "../utils/formatter";

export default {
  name: "MyImage",
  data() {
    return {
      serverIp: serverIp,
      tableData: [],
      name: '',
      multipleSelection: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogFormVisible: false,
      dialogKeyVisible: false,
      form: {},
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      doctors: [],
      uploadTime: "",
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.request.get("/image/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })

      this.request.get("/user/role/ROLE_DOCTOR").then(res => {
        this.doctors = res.data
      })

    },
    changeEnable(row) {
      this.request.post("/image/update", row).then(res => {
        if (res.code === '200') {
          this.$message.success("操作成功")
        }
      })
    },
    del(id) {
      this.request.delete("/image/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    delBatch() {
      let ids = this.multipleSelection.map(v => v.id)  // [{}, {}, {}] => [1,2,3]
      this.request.post("/image/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
    },
    reset() {
      this.name = ""
      this.load()
    },
    handleSizeChange(pageSize) {
      console.log(pageSize)
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      console.log(pageNum)
      this.pageNum = pageNum
      this.load()
    },
    handleImageUploadSuccess(res) {
      if (res.code === '200') {
        this.$message.success("上传成功")
      }else{
        this.$message.error(res.msg)
      }
      this.load()
    },
    formatter(row,column){
      return formatDate('YYYY-mm-dd HH:MM',row[column.property]);
    },
    lookImage(id){
      window.open("http://localhost:9090/image/download/" + id + "&" + this.user.id)
    },
    preview(id){
      window.open("http://localhost:9090/image/download/" + id + "&" + this.user.id)
    },
    handleEdit(row){//编辑
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
    },
    handleEncrypt(row){
      this.form = JSON.parse(JSON.stringify(row))
      this.form.doctors = this.doctors
      this.dialogKeyVisible = true
    },
    save(){//新增或修改后保存
      this.request.post("/image", this.form).then(res => {
        if(res.code === '200'){
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.dialogKeyVisible = false
          this.load()
        }else{
          this.$message.error("保存失败")
        }
      })
    },
    noSave(){
      this.dialogFormVisible = false
      this.dialogKeyVisible = false
      this.load()
    },
  }
}
</script>

<style>

</style>
