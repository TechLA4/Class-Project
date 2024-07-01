<template>
  <div>
      <i class="el-icon-paperclip" style="margin-right: 18px"></i>
      <span>{{ currentTerm }}</span>
      <el-divider direction="vertical"></el-divider>
      <i class="el-icon-user" style="margin-right: 18px"></i>
      <span>{{ name }}</span>
    <el-divider direction="vertical"></el-divider>
      <span>
        <el-button slot="reference" type="danger" style="margin-top: 8px" round @click="out()">
          退出登录
        </el-button>
      </span>
  </div>
</template>

<script>
import Logout from "@/components/logout";
export default {
  name: "r-header",
  components: {Logout},
  data() {
    return {
      name: null,
      currentTerm: null
    }
  },
  created() {
    this.name = sessionStorage.getItem("name")
    this.currentTerm = sessionStorage.getItem("currentTerm")
  },
  methods: {
    out() {
      sessionStorage.clear();
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>

</style>