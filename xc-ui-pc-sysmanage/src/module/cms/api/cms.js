/**
 * Created by xuyisheng on 2020/2/2.
 */
import http from './../../../base/api/public'
import querystring from 'querystring'
let sysConfig = require('@/../config/sysConfig')
let apiUrl = sysConfig.xcApiUrlPre;

export const page_list=(page,size,params) =>{
  let query =querystring.stringify(params)
  return http.requestQuickGet(apiUrl+'/cms/page/list/'+page+'/'+size+'/?'+query)
}

export const getSiteList=() =>{
  return http.requestQuickGet(apiUrl+'/cms/site/list/0/10')
}

export const getTemplateList=() =>{
  return http.requestQuickGet(apiUrl+'/cms/template/list/0/10')
}

//新增页面
export const page_add = params =>{
  return http.requestPost(apiUrl+'/cms/page/add',params)
}


//根据id查询页面
export const page_get = id =>{
  return http.requestQuickGet(apiUrl+'/cms/page/get/'+id)
}
//修改页面提交
export const page_edit = (id,params) =>{
  return http.requestPut(apiUrl+'/cms/page/edit/'+id,params)
}

export const page_del = id =>{
  return http.requestDelete(apiUrl+'/cms/page/del/'+id)
}

export const page_postPage= id =>{
  return http.requestDelete(apiUrl+'/cms/page/del/'+id)
}

