const getters = {
  deployUploadApi: state => state.api.deployUploadApi,
  databaseUploadApi: state => state.api.databaseUploadApi,
  size: state => state.api.size,
  sidebar: state => state.api.sidebar,
  device: state => state.api.device,
  token: state => state.api.token,
  visitedViews: state => state.api.visitedViews,
  cachedViews: state => state.api.cachedViews,
  roles: state => state.api.roles,
  user: state => state.api.user,
  loadMenus: state => state.api.loadMenus,
  permission: state => state.api.permission,
  addRouters: state => state.api.addRouters,
  socketApi: state => state.api.socketApi,
  imagesUploadApi: state => state.api.imagesUploadApi,
  baseApi: state => state.api.baseApi,
  fileUploadApi: state => state.api.fileUploadApi,
  updateAvatarApi: state => state.api.updateAvatarApi,
  qiNiuUploadApi: state => state.api.qiNiuUploadApi,
  sqlApi: state => state.api.sqlApi,
  swaggerApi: state => state.api.swaggerApi,
  sidebarRouters: state => state.api.sidebarRouters
}
export default getters
