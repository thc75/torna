<template>
  <div>
    <h1 style="margin-bottom: 0">{{ docInfo.name }}</h1>
    <span class="doc-modify-info">
        {{ docInfo.creatorName }} {{ $ts('createdOn') }} {{ docInfo.gmtCreate }}，
        {{ docInfo.modifierName }} {{ $ts('lastModifiedBy') }} {{ docInfo.gmtModified }}
    </span>
    <div v-html="docInfo.description"></div>
  </div>
</template>
<script>
export default {
  props: {
    docInfo: Object
  }
}
</script>
