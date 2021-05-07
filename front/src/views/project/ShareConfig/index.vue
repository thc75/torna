<template>
  <div>
    <div class="table-opt-btn">
      <el-button type="primary" size="mini" @click="onAdd">创建分享</el-button>
    </div>
    <el-table
      :data="pageInfo.rows"
      border
      highlight-current-row
    >
      <el-table-column label="分享链接" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link type="primary" :href="buildUrl(scope.row)" target="_blank">{{ buildUrl(scope.row) }}</el-link>
          <span v-if="scope.row.type === getEnums().SHARE_TYPE.ENCRYPT">&nbsp;&nbsp;{{ `密码：${scope.row.password}` }}</span><span v-if="scope.row.remark.length > 0" class="info-tip">【备注】：{{ scope.row.remark }}</span>
        </template>
      </el-table-column>
      <el-table-column label="分享文档" width="100">
        <template slot-scope="scope">
          <span v-if="scope.row.isAll">全部文档</span>
          <el-button v-else type="text" @click="viewDoc(scope.row)">查看</el-button>
        </template>
      </el-table-column>
      <el-table-column label="分享形式" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.type === getEnums().SHARE_TYPE.PUBLIC">公开</el-tag>
          <el-tag v-if="scope.row.type === getEnums().SHARE_TYPE.ENCRYPT" type="warning">加密</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建人" prop="creatorName" width="120" />
      <el-table-column label="状态" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === getEnums().STATUS.ENABLE" type="success">启用</el-tag>
          <el-tag v-if="scope.row.status === getEnums().STATUS.DISABLE" type="danger">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="gmtCreate"
        label="创建时间"
        width="100"
      >
        <template slot-scope="scope">
          <span :title="scope.row.gmtCreate">{{ scope.row.gmtCreate.split(' ')[0] }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        width="200"
      >
        <template slot-scope="scope">
          <el-link type="primary" :underline="false" @click="onTableUpdate(scope.row)">修改</el-link>
          <el-link v-if="scope.row.status === getEnums().STATUS.ENABLE" type="warning" :underline="false" @click="onTableDisable(scope.row)">禁用</el-link>
          <el-link v-else type="success" :underline="false" @click="onTableEnable(scope.row)">启用</el-link>
          <el-popconfirm
            :title="`确定要删除此记录吗？`"
            @confirm="onTableDelete(scope.row)"
          >
            <el-link slot="reference" type="danger">删除</el-link>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      background
      style="margin-top: 5px"
      :current-page="searchFormData.pageIndex"
      :page-size="searchFormData.pageSize"
      :page-sizes="[5, 10, 20, 40]"
      :total="pageInfo.total"
      layout="total, sizes, prev, pager, next"
      @size-change="onSizeChange"
      @current-change="onPageIndexChange"
    />
    <!--dialog-->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      @close="() => {
        resetForm('dialogForm')
        $refs.docTreeRef.setCheckedKeys([])
      }"
    >
      <el-form
        ref="dialogForm"
        :rules="dialogFormRules"
        :model="dialogFormData"
        label-width="120px"
        size="mini"
      >
        <el-form-item label="备注">
          <el-input v-model="dialogFormData.remark" placeholder="选填" show-word-limit maxlength="50" />
        </el-form-item>
        <el-form-item label="分享形式">
          <template>
            <el-radio-group v-model="dialogFormData.type">
              <el-radio :label="1">公开</el-radio>
              <el-radio :label="2">加密</el-radio>
            </el-radio-group>
          </template>
        </el-form-item>
        <el-form-item label="选择文档">
          <el-radio-group v-model="dialogFormData.isAll">
            <el-radio :label="0">部分文档</el-radio>
            <el-radio :label="1">全部文档<span class="normal-text">（整个模块）</span></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-show="dialogFormData.isAll === 0">
          <doc-tree ref="docTreeRef" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="onDialogSave">保 存</el-button>
      </div>
    </el-dialog>
    <el-dialog
      title="分享文档"
      :visible.sync="dialogViewVisible"
      @close="() => { $refs.docTreeViewRef.clear() }"
    >
      <doc-tree ref="docTreeViewRef" view-mode />
    </el-dialog>
  </div>
</template>
<script>
import DocTree from '@/components/DocTree'
export default {
  components: { DocTree },
  props: {

  },
  data() {
    return {
      moduleId: '',
      data: [],
      searchFormData: {
        pageIndex: 1,
        pageSize: 10,
        moduleId: ''
      },
      pageInfo: {
        rows: [],
        total: 0
      },
      dialogVisible: false,
      dialogViewVisible: false,
      dialogTitle: '',
      dialogFormData: {
        id: '',
        type: 1,
        moduleId: '',
        isAll: 0,
        remark: ''
      },
      autoAppend: 1,
      dialogFormRules: {
      }
    }
  },
  methods: {
    reload(moduleId) {
      if (moduleId) {
        this.moduleId = moduleId
      }
      this.loadTable(this.moduleId)
    },
    loadTable(moduleId) {
      if (moduleId) {
        this.searchFormData.moduleId = moduleId
        this.post('/doc/share/page', this.searchFormData, resp => {
          this.pageInfo = resp.data
        })
      }
    },
    onAdd() {
      this.dialogTitle = '新增分享'
      this.dialogVisible = true
      this.dialogFormData = {
        id: '',
        type: 1,
        moduleId: '',
        isAll: 0,
        remark: ''
      }
      this.$nextTick(() => {
        this.reloadDocTree()
      })
    },
    onTableUpdate(row) {
      this.dialogTitle = '修改分享'
      this.dialogVisible = true
      this.$nextTick(() => {
        Object.assign(this.dialogFormData, row)
        if (!row.isAll) {
          this.get('/doc/share/listContent', { id: row.id }, resp => {
            const contentList = resp.data
            const idList = contentList.map(row => row.docId)
            this.reloadDocTree(treeData => {
              for (const data of treeData) {
                for (const content of contentList) {
                  if (data.id === content.docId) {
                    data.isShareFolder = content.isShareFolder === 1
                    break
                  }
                }
              }
            }, tree => {
              tree.setCheckedKeys(idList)
            })
          })
        }
      })
    },
    onTableEnable(row) {
      this.post('/doc/share/enable', row, resp => {
        this.tipSuccess('操作成功')
        this.reload()
      })
    },
    onTableDisable(row) {
      this.post('/doc/share/disable', row, resp => {
        this.tipSuccess('操作成功')
        this.reload()
      })
    },
    onTableDelete(row) {
      const data = {
        id: row.id
      }
      this.post('/doc/share/del', data, () => {
        this.tipSuccess('操作成功')
        this.reload()
      })
    },
    onDialogSave() {
      const data = this.dialogFormData
      data.moduleId = this.moduleId
      const checkedNodes = this.$refs.docTreeRef.getCheckedNodes()
      if (!data.isAll && checkedNodes.length === 0) {
        this.tipError('请勾选文档')
        return
      }
      const content = []
      const treeNode = this.convertTree(checkedNodes)
      const append = (node, isShareFolder) => {
        if (isShareFolder === undefined) {
          isShareFolder = 0
        }
        // 如果是分享整个文件夹，只需要保存文件夹id
        if (isShareFolder) {
          content.push({
            docId: node.id,
            parentId: node.parentId,
            isShareFolder: isShareFolder
          })
        } else {
          // 保存子文件
          // 如果没有设置追加，需要添加所有文档id
          const children = node.children || []
          if (children.length > 0) {
            for (const child of children) {
              append(child, isShareFolder)
            }
          } else {
            content.push({
              docId: node.id,
              parentId: node.parentId,
              isShareFolder: isShareFolder
            })
          }
        }
      }
      for (const node of treeNode) {
        append(node, node.isShareFolder)
      }
      data.content = content
      const uri = this.dialogFormData.id ? '/doc/share/update' : '/doc/share/add'
      this.post(uri, this.dialogFormData, () => {
        this.dialogVisible = false
        this.reload()
      })
    },
    onSizeChange(size) {
      this.searchFormData.pageIndex = 1
      this.searchFormData.pageSize = size
      this.reload()
    },
    onPageIndexChange(pageIndex) {
      this.searchFormData.pageIndex = pageIndex
      this.reload()
    },
    reloadDocTree(beforeFun, afterFun) {
      this.$refs.docTreeRef.load(this.moduleId, beforeFun, afterFun)
    },
    reloadDocTreeView(beforeFun, afterFun) {
      this.$refs.docTreeViewRef.load(this.moduleId, beforeFun, afterFun)
    },
    buildUrl(row) {
      return `${this.getBaseUrl()}/#/share/${row.id}`
    },
    viewDoc(row) {
      this.dialogViewVisible = true
      this.$nextTick(() => {
        this.get('/doc/share/listContent', { id: row.id }, resp => {
          const contentList = resp.data
          const idList = contentList.map(row => row.docId)
          this.reloadDocTreeView(treeData => {
            for (const data of treeData) {
              for (const content of contentList) {
                if (data.id === content.docId) {
                  data.isShareFolder = content.isShareFolder === 1
                  break
                }
              }
            }
          }, tree => {
            tree.setCheckedKeys(idList)
            this.$refs.docTreeViewRef.disable()
          })
        })
      })
    }
  }
}
</script>
