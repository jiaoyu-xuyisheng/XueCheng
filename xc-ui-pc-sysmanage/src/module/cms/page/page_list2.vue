<template>
    <div id="page_list">
      <el-form :model="params">
        <el-select v-model="params.siteId" placeholder="请选择站点">
          <el-option v-for="item in siteList" :key="item.siteId" :label="item.siteName" :value="item.siteId">
          </el-option>
        </el-select>
        页面别名：<el-input v-model="params.pageAliase" style="width: 100px">
        </el-input>
        <el-button type="primary" v-on:click="query"  style="padding:10px;margin:10px;">查询</el-button>
        <router-link :to="{path:'/cms/page/add'}">
          <el-button  type="primary"  style="padding:10px;margin:10px;">新增页面</el-button>
        </router-link>
      </el-form>


      <el-table
        :data="list1"
        stripe
        style="width: 100%">
        <el-table-column type="index" width="60">
        </el-table-column>
        <el-table-column prop="pageName" label="页面名称" width="120">
        </el-table-column>
        <el-table-column prop="pageAliase" label="别名" width="120">
        </el-table-column>
        <el-table-column prop="pageType" label="页面类型" width="150">
        </el-table-column>
        <el-table-column prop="pageWebPath" label="访问路径" width="250">
        </el-table-column>
        <el-table-column prop="pagePhysicalPath" label="物理路径" width="250">
        </el-table-column>
        <el-table-column prop="pageCreateTime" label="创建时间" width="180" >
        </el-table-column>
        <el-table-column  label="操作" width="180" >
        </el-table-column>
      </el-table>
      <el-pagination
        layout="prev, pager, next"
        :total="total"
        :page-size="params.size"
        :current-page="params.page"
        v-on:current-change="changePage"
        style="float:right">
      </el-pagination>
    </div>
</template>

<script>
  /*编写页面静态部分，即model及vm部分。*/
  import * as cmsApi from '../api/cms'
  export default {
    data() {
      return {
        siteList:[],
        list1:[],
        total:0,
        params:{
          siteId:'',
          pageAliase:'',
          page:1,
          size:10
        }
      }
    },
    methods:{
      getSiteList(){
        cmsApi.getSiteList().then((res)=>{
          this.siteList = res.queryResult.list;
        })
      },
      query:function(){
        // alert('查询')
        //调用服务端的接口
        cmsApi.page_list(this.params.page,this.params.size,this.params).then((res)=>{
          //将res结果数据赋值给数据模型对象
          this.list1 = res.queryResult.list;
          this.total = res.queryResult.total;
        })

      },
      changePage:function(page){//形参就是当前页码
        //调用query方法
        // alert(page)
        this.params.page = page;
        this.query()
      }
    },
    mounted(){
      //当DOM元素渲染完成后调用query
      this.query();
      this.getSiteList();
    }
  }
</script>


<style scoped>
    /*  scoped 组件的局部作用域 */

</style>
