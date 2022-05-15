<template>
  <div>
    <div style="margin: 10px 0">
      <el-carousel height="450px" :interval="10000">
        <el-carousel-item v-for="item in imgs" :key="item">
          <img :src="item" alt="" style="width: 100%">
        </el-carousel-item>
      </el-carousel>
    </div>

    <h1>等待管理员分配角色</h1>
<!--    <div style="margin: 10px 0">-->
<!--      <el-row :gutter="10">-->
<!--        <el-col :span="6" v-for="item in files" :key="item.id" style="margin-bottom: 10px">-->
<!--          <div style="border: 1px solid #ccc; padding-bottom: 10px">-->
<!--            <img :src="item.url" alt="" style="width: 100%">-->
<!--            <div style="color: #666; padding: 10px">{{ item.name }}</div>-->
<!--          </div>-->
<!--        </el-col>-->
<!--      </el-row>-->
<!--    </div>-->
  </div>
</template>

<script>
export default {
  name: "FrontHome",
  data() {
    return {
      imgs: [
        'https://www.jmu.edu.cn/images/20220126150200745BL1.jpg',
        'https://www.jmu.edu.cn/imges/20211229101519849z4M.png',
        'https://www.jmu.edu.cn/images/20211207153344601U8A.jpg',
        'https://www.jmu.edu.cn/images/20200215130427599d87.jpg',
        'https://www.jmu.edu.cn/images/2022041415545806270N.png',
        'https://www.jmu.edu.cn/images/202111051028059166j7.jpg',
        'https://www.jmu.edu.cn/images/20220402214848750p3r.jpg',
      ],
      files: []
    }
  },
  created() {
    this.request.get("/echarts/file/front/all").then(res => {
      console.log(res.data)
      this.files = res.data.filter(v => v.type === 'png' || v.type === 'jpg' || v.type === 'webp')
    })
  },
  methods: {

  }
}
</script>

<style scoped>

</style>
