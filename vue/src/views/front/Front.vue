<template>
  <div>
    <!--头部-->
    <div style="display: flex; height: 60px; line-height: 60px; border-bottom: 1px solid #eee">
      <div style="width: 300px; display: flex; padding-left: 30px">
        <div style="width: 60px">
          <img src="../../assets/jmu.jpg" alt="" style="width: 50px; position: relative; top: 5px; right: 0">
        </div>
        <div style="flex: 1">欢迎来到医疗图像管理系统</div>
      </div>
      <div style="flex: 1">
        <el-menu :default-active="'/front/home'" class="el-menu-demo" mode="horizontal"
                 router
                 background-color="#FFFFFF"
                 text-color="#0B0000"
                 active-text-color="#00DDFF">
          <el-menu-item index="/front/home">主页</el-menu-item>
          <el-menu-item index="/front/video">视频播放</el-menu-item>
          <el-menu-item index="/front/article">文章列表</el-menu-item>
          <el-submenu index="2">
            <template slot="title">我的工作台</template>
            <el-menu-item index="/front/item1">选项1</el-menu-item>
          </el-submenu>
          <el-menu-item index="3" disabled>消息中心</el-menu-item>
          <el-menu-item><a href="https://www.ele.me" target="_blank">订单管理</a></el-menu-item>
      </el-menu>
      </div>
      <div style="width: 200px">
        <div v-if="!user.username" style="text-align: right; padding-right: 30px">
          <el-button @click="$router.push('/login')">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </div>
        <div v-else>
          <el-dropdown style="width: 150px; cursor: pointer; text-align: right">
            <div style="display: inline-block">
              <img :src="user.avatarUrl" style="width: 30px; border-radius: 50%; position: relative; top: 10px; right: 5px;">
              <span>{{ user.username }}<i class="el-icon-caret-bottom" style="margin-left: 5px"></i></span>
            </div>
            <el-dropdown-menu slot="dropdown" style="width: 70px; text-align: center">
              <el-dropdown-item style="font-size: 14px; padding: 5px 0">
                <router-link to="/front/person">个人信息</router-link>
              </el-dropdown-item>
              <el-dropdown-item style="font-size: 14px; padding: 5px 0">
                <router-link to="/front/password">修改密码</router-link>
              </el-dropdown-item>
              <el-dropdown-item style="font-size: 14px; padding: 5px 0">
                <span style="text-decoration: none" @click="logout">退出</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </div>

    <div style="width: 1000px; margin: 0 auto">
      <router-view @refreshUser="getUser"/>
    </div>

  </div>
</template>

<script>
export default {
  name: "Front",
  data() {
    return {
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
    }
  },
  created() {

  },
  methods: {
    logout() {
      this.$store.commit("logout")
      this.$message.success("退出成功")
    },
    getUser() {
      let username = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).username : "";
      //从后台获取数据
      this.request.get("/user/username/" + username).then(res => {
        //重新赋值后台的最新User数据
        this.user = res.data
      })
    },
  }

}
</script>

<style scoped>
.item{
  display: inline-block;
  width: 100px;

  text-align: center;
  cursor: pointer;
}
.item a{
  color: black;
}
.item:hover{
  background-color: coral;
}
</style>
