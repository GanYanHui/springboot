<template>
  <div class="wrapper">
    <div style="margin: 200px auto; background-color: #fff; width: 350px; height: 300px; padding: 20px; border-radius: 10px">
      <div style="margin: 20px 0; text-align: center; font-size: 24px"><b>登 录</b></div>
      <el-form :model="user" :rules="rules" ref="userForm">
        <el-form-item prop="username">
          <el-input  placeholder="请输入用户名" size="medium" prefix-icon="el-icon-user" v-model="user.username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input placeholder="请输入密码" size="medium" prefix-icon="el-icon-lock" show-password v-model="user.password"></el-input>
        </el-form-item>
        <el-form-item style="margin: 10px 0; text-align: right">
          <el-button type="primary" size="small"  autocomplete="off" @click="login">登录</el-button>
          <el-button type="warning" size="small"  autocomplete="off" @click="$router.push('/Register')">注册</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import {setRoutes} from "@/router";

export default {
  name: "Login",
  data(){
    return {
      user: {},//{}表示对象，[]表示数组对象
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },//required: true 表示必须的, trigger: 'blur' 表示鼠标失焦时校验
          { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ],
      }
    }
  },
  methods: {
    login() {
      this.$refs['userForm'].validate((valid) => {
        if (valid){// 表单校验合法
          this.request.post("/user/login", this.user).then(res => {
            if(res.code == '200'){
              localStorage.setItem("user", JSON.stringify(res.data))//存储用户信息到浏览器
              localStorage.setItem("menus", JSON.stringify(res.data.menus))//存储用户信息到浏览器
              //动态设置当前用户的路由
              setRoutes()
              this.$message.success("登录成功")
              if(!res.data.role){
                this.$router.push("/front/home")
              }else{
                this.$router.push("/home")
              }
            }else{
              this.$message.error(res.msg)
            }
          })
        }else{
          return false;
        }
      });
    }
  }
}
</script>

<style>
.wrapper {
  background-image: url("../assets/IMG_20220517_184622.jpg");
  position:fixed;
  top: 0;
  left: 0;
  width:100%;
  height:100%;
  min-width: 1000px;
  z-index:-10;
  zoom: 1;
  background-color: #fff;
  background-repeat: no-repeat;
  background-size: cover;
  -webkit-background-size: cover;
  -o-background-size: cover;
  background-position: center 0;
}
</style>
